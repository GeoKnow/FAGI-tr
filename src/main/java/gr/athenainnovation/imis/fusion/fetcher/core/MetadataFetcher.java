package gr.athenainnovation.imis.fusion.fetcher.core;

import com.google.common.base.Optional;
import gr.athenainnovation.imis.fusion.fetcher.gui.workers.Dataset;
import gr.athenainnovation.imis.fusion.fetcher.gui.workers.FetcherWorker;
import gr.athenainnovation.imis.fusion.fetcher.gui.workers.MatchedRule;
import gr.athenainnovation.imis.fusion.fetcher.rules.RuleTriple;
//import virtuoso.jdbc3;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import virtuoso.jdbc4.VirtuosoExtendedString;
import virtuoso.jdbc4.VirtuosoRdfBox;
import virtuoso.jdbc4.VirtuosoResultSet;
//VirtGraph
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoUpdateFactory;
import virtuoso.jena.driver.VirtuosoUpdateRequest;

/**
 *  This class is responsible for fetching all metadata triples (non geometry related) from a source dataset and copying them first to the specified by the dataset local unmodified graph and second, if the
 *  populateTransformationGraph flag is set, to the local transformed graph.
 *  @author Thomas Maroulis
 */
public class MetadataFetcher {
    private final static Logger LOG = Logger.getLogger(MetadataFetcher.class);
    
    private final FetcherWorker callback;
    private final Dataset dataset;
    private final Optional<List<String>> linkedNodes;
    
    private final String SUBJECT_VAR = "?s";
    private final String PREDICATE_VAR = "?p";
    private final String OBJECT_VAR = "?o";
    
    private Integer totalCount;
    private Integer count = 1;
    
    private boolean populateTransformedGraph = false; //true if targetRule is Present
    //private boolean blankNodeTripleIsToBeExcluded = false; //true if blank node from unmodified graph does not need to be copied in the transformed

    /**
     *  Constructs a new instance of MetadataFetcher tied to a specific dataset. linked nodes list and with the specified populateTransformationGraph flag.
     * @param dataset a dataset object containing all information pertaining to source and destination datasets.
     * @param callback callback
     * @param populateTransformedGraph true if triples are to be copied to the transformed graph, false otherwise
     * @param linkedNodes optional of a list of node URIs to be fetched. If absent all root nodes (as defined by the dataset) will be fetched.
     */
    public MetadataFetcher(final Dataset dataset, final FetcherWorker callback, final boolean populateTransformedGraph, final Optional<List<String>> linkedNodes) {
        this.callback = callback;
        this.dataset = dataset;
        this.populateTransformedGraph = populateTransformedGraph;
        this.linkedNodes = linkedNodes;
    }
    
    /**
     *  Fetches metadata triples from the source graph and enters it in the local unmodified graph and, if the populateTransformedGraph is set, into the local transformed graph.
     * @param matchedRules a map of matched rules that specify the triple patterns to be excluded as non-metadata
     */
    public void fetchMetadata(Map<String, MatchedRule> matchedRules) {
        String restriction = getGeometryExclusionQueryRestriction(matchedRules, dataset.getGraph());
        
        try (Connection conn = getConnectionToDB(dataset.getDBConnectionParameters().getUrl(),
                    dataset.getDBConnectionParameters().getUsername(), dataset.getDBConnectionParameters().getPassword())) {
            
            if(linkedNodes.isPresent()) {
                totalCount = getNumberOfStatementsToExplore(restriction, conn, linkedNodes.get());
                
                for(String subject : linkedNodes.get()) {
                    String bracketedSubject = "<" + subject + ">";
                    fetchMetadataHelper(restriction, Optional.of(bracketedSubject), conn);
                }
            }
            else {
                if(!dataset.getRootNodeNS().isEmpty()) {
                    restriction = restriction.substring(0,restriction.lastIndexOf("}")).concat("FILTER (REGEX(?s, \""+ dataset.getRootNodeNS() + "\", \"i\")) }");
                }
                
                totalCount = getNumberOfStatementsToExplore(restriction, conn);
                Optional<String> subject = Optional.absent();
                fetchMetadataHelper(restriction, subject, conn);
            }
        }
        catch (RuntimeException | SQLException | ClassNotFoundException ex) {
            LOG.warn(ex.getMessage(), ex);
            callback.printExceptionMessage(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
    
    private void fetchMetadataHelper(String restriction, Optional<String> subject, Connection conn) {
        String selectQuery;
        if(subject.isPresent()) {
            selectQuery = formSelectQuery(restriction.replace("?s", subject.get()));
        }
        else {
            selectQuery = formSelectQuery(restriction);
        }
        
        //virtuoso connection
        VirtGraph vConn = getVirtuosoConnection(dataset.getDBConnectionParameters().getUrl(), 
        dataset.getDBConnectionParameters().getUsername(), dataset.getDBConnectionParameters().getPassword());        
        
        long metadataStartTime = System.nanoTime();    
        try (Statement statement = conn.createStatement();       
                
        VirtuosoResultSet resultSet = (VirtuosoResultSet) statement.executeQuery(selectQuery)) {
            while(resultSet.next()) {
                callback.publishFetcherProgress((int) (0.5 + (100 * (double) count++ / (double) totalCount)));
                
                String subjectContent;
                if(subject.isPresent()) {
                    subjectContent = subject.get();
                }
                else {
                    subjectContent = virtuosoDatatypeToString(resultSet.getObject(1));
                }
                String predicateContent = virtuosoDatatypeToString(resultSet.getObject(2));
                String objectContent = virtuosoDatatypeToString(resultSet.getObject(3));
                
                if(subjectContent.isEmpty() || predicateContent.isEmpty() || objectContent.isEmpty()) {
                    LOG.warn("Found empty value in triple: " + subjectContent + " | " + predicateContent + " | " + objectContent);
                }
                             
                String genericInsertQuery, specificInsertQuery;
                
                if(populateTransformedGraph){ 
                    
                    genericInsertQuery = formInsertQuery(dataset.getTransformedLocalGraph());
                    
                    if (!blankNodeTripleIsToBeExcluded(subjectContent, objectContent)){
                    specificInsertQuery = substituteVariableContentIntoQuery(genericInsertQuery, subjectContent, predicateContent, objectContent);
                    
                    //executeUpdateOnLocalDB(specificInsertQuery, conn);                    
                    executeUpdateOnLocalDB2(specificInsertQuery, vConn);
                    }

                }
            }
            long metadataEndTime = System.nanoTime();
            double metadataElapsedTime = (metadataEndTime - metadataStartTime)/1E9;
            System.out.println("METADATA elapsed time:   " + metadataElapsedTime);
        }        
        catch (SQLException ex) {
            LOG.warn(ex.getMessage() + " | " + selectQuery, ex);
            callback.printExceptionMessage(ex.getMessage() + " | " + selectQuery);
        }
    }
    
    //execute on localDB 2
    private void executeUpdateOnLocalDB2(String specificInsertQuery, VirtGraph vConn) {
        try{
        VirtuosoUpdateRequest vur = VirtuosoUpdateFactory.create(specificInsertQuery, vConn);
        vur.exec();
        }
        catch (com.hp.hpl.jena.update.UpdateException ex){
            LOG.warn(ex.getMessage(), ex);
            callback.printExceptionMessage(ex.getMessage());
            //throw new RuntimeException(ex);
        }
    }
    
    private Integer getNumberOfStatementsToExplore(final String restriction, final Connection conn) {
        Integer numOfStatements;
        
        String queryString = formTotalCountQuery(restriction);
        
        try (Statement statement = conn.createStatement();
                VirtuosoResultSet resultSet = (VirtuosoResultSet) statement.executeQuery(queryString);){            
            if(resultSet.next()) {
                numOfStatements = resultSet.getInt(1);
            }
            else {
                numOfStatements = -1;
            }
        }
        catch (SQLException ex) {
            LOG.warn(ex.getMessage(), ex);
            callback.printExceptionMessage(ex.getMessage());
            numOfStatements = -1;
        }
        
        return numOfStatements;
    }
    
    private Integer getNumberOfStatementsToExplore(final String restriction, final Connection conn, final List<String> links) {
        Integer numOfStatements = 0;
        
        String queryString = formTotalCountQuery(restriction);
        
        for(String subject : links) {
            String modifiedQueryString = queryString.replace("?s", "<" + subject + ">");
            
            try (Statement statement = conn.createStatement();
                    VirtuosoResultSet resultSet = (VirtuosoResultSet) statement.executeQuery(modifiedQueryString);) {
                if(resultSet.next()) {
                        numOfStatements += resultSet.getInt(1);
                    }
            }
            catch (SQLException ex) {
                LOG.warn(ex.getMessage(), ex);
                callback.printExceptionMessage(ex.getMessage());
                numOfStatements = -1;
            }
        }
        
        return numOfStatements;
    }
    
    private Connection getConnectionToDB(String url, String username, String password) throws SQLException, ClassNotFoundException {
        Class.forName("virtuoso.jdbc4.Driver");
        String urlDB = "jdbc:virtuoso://" + url + "/CHARSET=UTF-8";               
        return DriverManager.getConnection(urlDB, username, password);
    }
    
        private VirtGraph getVirtuosoConnection (String url, String username, String password){
        VirtGraph Vconn = new VirtGraph ("jdbc:virtuoso://" + url + "/CHARSET=UTF-8", username, password);
        return Vconn;
    }

    
    private String getGeometryExclusionQueryRestriction(Map<String, MatchedRule> matchedRules, String graph) {        
        String restriction = "FROM <" + graph + "> WHERE { ?s ?p ?o . ";
        
        for(MatchedRule matchedRule : matchedRules.values()) {
            if(matchedRule.getTimesMatched().isPresent() && matchedRule.getTimesMatched().get() > 0) {
                RuleTriple firstTriple = matchedRule.getRule().getTriples()[0];
                //check for predicate in the second triple of the rule, to be filtered out
                if (matchedRule.getRule().getTriples().length > 1){
                   RuleTriple secondTriple = matchedRule.getRule().getTriples()[1]; 

                   String regex = "\"" + secondTriple.getPredicate().trim().substring(1, secondTriple.getPredicate().trim().length()-1) + "\"";
                   restriction = restriction.concat("FILTER (!regex(?p, " + regex + ", \"i\")) ");
                }//this if is to be expanded to work with rules that have more triple predicates to exclude

                String regex = "\"" + firstTriple.getPredicate().trim().substring(1, firstTriple.getPredicate().trim().length()-1) + "\"";
                restriction = restriction.concat("FILTER (!regex(?p, " + regex + ", \"i\")) ");
                
            }
        }
        
        restriction = restriction.concat("}");
        return restriction;
    }
    
    private String formTotalCountQuery(String restriction) {
        return "SPARQL SELECT (COUNT (?p) as ?count) " + restriction;
    }
    
    private String formSelectQuery(String restriction) {
        return "SPARQL SELECT ?s ?p ?o " + restriction;
    }
    
    private String formInsertQuery(String graph) {               
        //return "SPARQL WITH  <" + graph + "> INSERT { ?s ?p ?o } WHERE {}";
        return "INSERT INTO GRAPH  <" + graph + "> { ?s ?p ?o }"; 
    }
    
    private String substituteVariableContentIntoQuery(String query, String subject, String predicate, String object) {                     
        
        query = query.replace(SUBJECT_VAR, subject);
        query = query.replace(PREDICATE_VAR, predicate);
        query = query.replace(OBJECT_VAR, object);
        return query;
    }
    
    
    private boolean blankNodeTripleIsToBeExcluded(String subject, String object){
        return subject.contains("nodeID://b");
    }

    private String virtuosoDatatypeToString(Object object) {
        if(object instanceof VirtuosoExtendedString) {
            VirtuosoExtendedString vs=  (VirtuosoExtendedString) object;
            if (vs.iriType == VirtuosoExtendedString.IRI && (vs.strType & 0x01) == 0x01) {
                return "<" + vs.str +">";
            }
            else if (vs.iriType == VirtuosoExtendedString.BNODE) { 
                return "<" + vs.str +">";
            }
            else return "\""+vs.str+"\"";
        }
        else if(object instanceof VirtuosoRdfBox) {
            VirtuosoRdfBox rb = (VirtuosoRdfBox) object;
            String content = "\"" +rb.toString() + "\"";
            if(rb.getLang() != null && !rb.getLang().isEmpty()) {
                content = content.concat("@" + rb.getLang());
            }
            if(rb.getType() != null && !rb.getType().isEmpty()) {
                content = content.concat("^^<" + rb.getType() +">");
                
                if("http://www.w3.org/2001/XMLSchema#boolean".equals(rb.getType())) {
                    content = content.replace("yes", "true");
                    content = content.replace("no", "false");
                }
            }
            return content;
        }
        else if(object instanceof Timestamp) {
            Timestamp timestamp = (Timestamp) object;
            return parseTimestamp(timestamp);
        }
        else {
            return "\"" + object.toString() + "\"";
        }
    }
    
    private String parseTimestamp(Timestamp timestamp) {
        // Parse timestamp to XSDdatetime format
        StringBuilder sb;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            Date date = sdf.parse(timestamp.toString());
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            sb = new StringBuilder(sdf.format(date));
            
            return "\"" + sb.toString() + "\"";
        }
        catch (ParseException ex) {
            LOG.warn("Could not parse timestamp to XSDdatetime format. Printing lexical representation of Timestamp object directly.", ex);
            return "\"" + timestamp.toString() + "\"";
        }
    }
}