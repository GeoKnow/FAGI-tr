package gr.athenainnovation.imis.fusion.fetcher.core;

import com.google.common.base.Optional;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.vividsolutions.jts.geom.Geometry;
import gr.athenainnovation.imis.fusion.fetcher.geometry.GeometryProcessor;
import gr.athenainnovation.imis.fusion.fetcher.gui.workers.Dataset;
import gr.athenainnovation.imis.fusion.fetcher.gui.workers.FetcherWorker;
import gr.athenainnovation.imis.fusion.fetcher.gui.workers.MatchedRule;
import gr.athenainnovation.imis.fusion.fetcher.rules.RulePattern;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import gr.athenainnovation.imis.fusion.fetcher.rules.parser.RulePatternParser;
import gr.athenainnovation.imis.fusion.fetcher.rules.parser.RuleQueryUtils;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoUpdateRequest;
import virtuoso.jena.driver.VirtuosoUpdateFactory;

/**
 * This class is responsible for fetching geometry related triples from a source dataset and then a). copying them to a local graph A, and b). transforming them according to a target rule and then entering
 * them to a local graph B. The list of linked nodes to fetch can be optionally specified.
 * @author Thomas Maroulis
 */
public class GeometryFetcher {
    private static final Logger LOG = Logger.getLogger(GeometryFetcher.class);
    
    private FetcherWorker callback;
    
    private Dataset dataset;
    private MatchedRule targetRule;
    private RulePatternParser targetRulePatternParser;
    private GeometryProcessor targetRuleGeometryProcessor;
    private final Optional<List<String>> linkedNodes;
    
    /**
     * Constructs a new instance of GeometryFetcher tied to specific dataset, target rule and linked nodes list.
     * @param dataset a dataset object containing all information pertaining to source and destination datasets.
     * @param targetRule optional of a rule for transforming geometric triples to. If absent only entries to the unmodified graph will take place and entries to the transformed graph will be omitted.
     * @param callback callback
     * @param linkedNodes optional of a list of node URIs to be fetched. If absent all root nodes (as defined by the dataset) will be fetched.
     */
    public GeometryFetcher(final Dataset dataset, final Optional<MatchedRule> targetRule, final FetcherWorker callback, final Optional<List<String>> linkedNodes) {
        this.dataset = dataset;
        this.callback = callback;
        this.linkedNodes = linkedNodes;
        
        if(targetRule.isPresent()) {
            this.targetRule = targetRule.get();
            targetRulePatternParser = new RulePatternParser(this.targetRule.getRule());
            if(!this.targetRule.getGeometryProcessor().isPresent()) {
                throw new IllegalArgumentException("Target rule has no geometry processor.");
            }
            targetRuleGeometryProcessor = this.targetRule.getGeometryProcessor().get();
        }
    }
    
    /**
     *  Fetches all triples matched by the given rule and then a). copies them into the unmodified graph, and b). transforms them according to the target rule and enters them into the transformed graph.
     * @param sourceMatchedRule rule for matching a set of triples in the source graph.
     */
    
    
    //fetchGeometries with Virtuoso Insert Support ->fetchGeometriesHelper got VirtGraph conn parameter instead of Connection conn
        public void fetchGeometries(final MatchedRule sourceMatchedRule) {  
        
        final RulePatternParser rulePatternParser = new RulePatternParser(sourceMatchedRule.getRule());
        
        try{
        VirtGraph conn = getVirtuosoConnection(dataset.getDBConnectionParameters().getUrl(), 
                    dataset.getDBConnectionParameters().getUsername(), dataset.getDBConnectionParameters().getPassword());       
            
            if(linkedNodes.isPresent()) {
                for(String subject : linkedNodes.get()) {
                    fetchGeometriesHelper(rulePatternParser, sourceMatchedRule, Optional.of(subject), conn);
                }
            }
            else {
                Optional<String> subject = Optional.absent();
                fetchGeometriesHelper(rulePatternParser, sourceMatchedRule, subject, conn);
            }
        }
        catch (com.hp.hpl.jena.update.UpdateException ex){
        LOG.warn(ex.getMessage(), ex);
        callback.printExceptionMessage(ex.getMessage());
        throw new RuntimeException(ex);
        }
    }  
        
        
    private void fetchGeometriesHelper(final RulePatternParser sourceRulePatternParser, final MatchedRule sourceMatchedRule, final Optional<String> subject, final VirtGraph conn) {

        RulePattern sourceRulePattern = sourceRulePatternParser.getRulePattern();
        String selectAllQuery = RuleQueryUtils.formSelectAllQuery(sourceRulePattern);
        
        if(subject.isPresent()) {
            Variable subjectVar = sourceRulePattern.getSubjectVariable();
            subjectVar.setContent(subject);
            sourceRulePattern.setSubjectVariable(subjectVar);
            
            selectAllQuery = selectAllQuery.replace(subjectVar.getName(), "<" + subject.get() + ">");
        }
                
        QueryExecution queryExecution = null;
        
        //long selectStartTime;
        try {
            
            //writeUnmodified with second graph param
            writeUnmodified2(dataset.getUnmodifiedLocalGraph(), conn, dataset.getGraph());
            
            final Query query = QueryFactory.create(selectAllQuery);
            
            if(!dataset.getGraph().isEmpty()) {
                queryExecution = QueryExecutionFactory.sparqlService(dataset.getEndpoint(), query, dataset.getGraph());                
                
                //start select time
                //selectStartTime = System.nanoTime();
            }
            else { 
                //start select time
                //selectStartTime = System.nanoTime();
                queryExecution = QueryExecutionFactory.sparqlService(dataset.getEndpoint(), query);
            }           
                
            final ResultSet resultSet = queryExecution.execSelect();
            //end select time
            //long selectEndTime = System.nanoTime();
            
            //double selectElapsedTime = (selectEndTime - selectStartTime)/1E9;
            //System.out.println("SELECT elapsed time:   " + selectElapsedTime);
            
            QuerySolution querySolution;
            //start transform time
            long transformStartTime = System.nanoTime();
            while(resultSet.hasNext()) {
                
                querySolution = resultSet.next();                                
                RulePattern rulePatternWithContent = getVariableContent(subject, sourceRulePattern, querySolution);                
                               
                writeTransformed(dataset.getTransformedLocalGraph(), rulePatternWithContent, sourceMatchedRule.getGeometryProcessor().get(), conn);
                      
            }
            //transform ended time
            long transformEndTime = System.nanoTime();
            //elapsed time for transforming and writing to modified graph
            double transformElapsedTime = (transformEndTime - transformStartTime)/1E9; //convert nanoseconds to seconds
            System.out.println("Transform Elapsed time:\n" + transformElapsedTime);
            
        }
        finally {
            if(queryExecution != null) {
                queryExecution.close();
            }
        }
    }
    
    //writeUnmodified with second graph added
    private void writeUnmodified2(String graph, VirtGraph conn, String fromGraph) {
      String unmodifiedUpdateQueryString = RuleQueryUtils.formUpdateQueryForUnmodified(graph, fromGraph);
      executeUpdateOnLocalDB2(unmodifiedUpdateQueryString, conn);
    }
   
    
    //last argument became VirtGraph conn instead of Connection conn, executeUpdateOnLocalDB commented out
    private void writeTransformed(String graph, RulePattern sourceRulePattern, GeometryProcessor sourceGeometryProcessor, VirtGraph conn) {
        if(targetRulePatternParser != null) {
            RulePattern targetRulePattern = targetRulePatternParser.getRulePattern();
            populateTargetPatternWithVariables(targetRulePattern, targetRuleGeometryProcessor, sourceRulePattern, sourceGeometryProcessor);
            
            String updateQuery = RuleQueryUtils.formUpdateQuery(targetRulePattern, graph);          

            executeUpdateOnLocalDB2(updateQuery, conn);
                 
        }
    }                   
    
    //executeUpdateOnLocalDB
    private void executeUpdateOnLocalDB2(String updateQuery, VirtGraph conn) {
       try
       {
        VirtuosoUpdateRequest vur = VirtuosoUpdateFactory.create(updateQuery, conn);
        vur.exec();
       }
       catch (com.hp.hpl.jena.update.UpdateException ex)
       { 
           LOG.warn(ex.getMessage() + " | " + updateQuery, ex);
           callback.printExceptionMessage(ex.getMessage() + " | " + updateQuery);
       }           
               
    }    

    private VirtGraph getVirtuosoConnection (String url, String username, String password){
        VirtGraph conn = new VirtGraph ("jdbc:virtuoso://" + url + "/CHARSET=UTF-8", username, password);
        return conn;
    }
    
    private void populateTargetPatternWithVariables(RulePattern updateRulePattern, GeometryProcessor targetGeometryProcessor, RulePattern sourceRulePattern, GeometryProcessor sourceGeometryProcessor) {
        updateRulePattern.setSubjectVariable(sourceRulePattern.getSubjectVariable());
        
        //source rule pattern
        updateRulePattern.setPredicateVariables(sourceRulePattern.getPredicateVariables());
        
        Geometry geometry = sourceGeometryProcessor.parseGeometry(sourceRulePattern.getObjectVariables());
        
        updateRulePattern.setObjectVariables(targetGeometryProcessor.writeGeometry(geometry, updateRulePattern.getObjectVariables()));
                
    }
    
    private RulePattern getVariableContent(final Optional<String> subject, final RulePattern sourceRulePattern, final QuerySolution querySolution) {
        RulePattern rulePattern = new RulePattern(sourceRulePattern);
        
        setSubjectVariableContent(subject, rulePattern.getSubjectVariable(), querySolution);
        setPredicateVariableContent(rulePattern.getPredicateVariables(), querySolution);
        
        setObjectVariableContent(rulePattern.getObjectVariables(), querySolution);
        return rulePattern;
    }
    
    private void setSubjectVariableContent(final Optional<String> subject, final Variable subjectVariable, final QuerySolution querySolution) {        
        if(subject.isPresent()) {
            subjectVariable.setContent(subject);
        }
        else {
            String subjectVariableContent = querySolution.getResource(subjectVariable.getName()).getURI();
            subjectVariable.setContent(Optional.fromNullable(subjectVariableContent));
        }
    }
    
    private void setPredicateVariableContent(final Map<String, Variable> predicateVariableMap, final QuerySolution querySolution) {        
        for(Entry<String, Variable> varEntry : predicateVariableMap.entrySet()) {
            String predicateVariableContent = querySolution.getResource(varEntry.getKey()).getURI();
            
            Variable predicateVariable = varEntry.getValue();
            predicateVariable.setContent(Optional.fromNullable(predicateVariableContent));
            
            predicateVariableMap.put(varEntry.getKey(), predicateVariable);

        }
    }
    
    private void setObjectVariableContent(final Map<String, Variable> objectVariableMap, final QuerySolution querySolution) {
        for(Entry<String, Variable> varEntry : objectVariableMap.entrySet()) {
            Variable objectVariable = varEntry.getValue();
            
            String objectVariableContent = null;
            
            RDFNode node = querySolution.get(varEntry.getKey());
            if(node.isLiteral()) {
                objectVariableContent = node.asLiteral().getLexicalForm();
            }
            else if (node.isResource()) {
                objectVariableContent = node.asResource().getURI();
            }
            
            if(objectVariableContent != null) {
                objectVariable.setContent(Optional.fromNullable(objectVariableContent));
            }
            
            objectVariableMap.put(varEntry.getKey(), objectVariable);
        }
    }
}