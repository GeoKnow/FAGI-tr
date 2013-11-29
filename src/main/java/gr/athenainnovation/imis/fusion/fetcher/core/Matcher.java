package gr.athenainnovation.imis.fusion.fetcher.core;

import com.google.common.base.Optional;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import gr.athenainnovation.imis.fusion.fetcher.geometry.EastingNorthingProcessor;
import gr.athenainnovation.imis.fusion.fetcher.geometry.GMLProcessor;
import gr.athenainnovation.imis.fusion.fetcher.geometry.GeometryProcessor;
import gr.athenainnovation.imis.fusion.fetcher.geometry.LatLongProcessor;
import gr.athenainnovation.imis.fusion.fetcher.geometry.WKTProcessor;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GeometryCRS;
import gr.athenainnovation.imis.fusion.fetcher.rules.RuleTriple;
import gr.athenainnovation.imis.fusion.fetcher.rules.RulePattern;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import gr.athenainnovation.imis.fusion.fetcher.rules.parser.RulePatternParser;
import gr.athenainnovation.imis.fusion.fetcher.rules.parser.RuleQueryUtils;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;

/**
 * This class is responsible for matching rules against a given dataset and providing metadata relevant to the rule.
 * @author Thomas Maroulis
 */
public class Matcher {
    private static final Logger LOG = Logger.getLogger(Matcher.class);
    
    private final String endpoint, graph;
    
    /**
     * Constructs a new Matcher for a given SPARQL endpoint and graph.
     * @param endpoint a SPARQL endpoint for the target dataset
     * @param graph a graph URI for the target dataset
     * @throws IllegalArgumentException if a null endpoint is provided
     */
    public Matcher(final String endpoint, final String graph) throws IllegalArgumentException {
        if(endpoint == null) {
            throw new IllegalArgumentException("Received null while expecting SPARQL endpoint.");
        }
        
        this.endpoint = endpoint;
        this.graph = graph;
    }
    
    /**
     * Parses a rule to determine the appropriate GeometryProcessor.
     * @param ruleID the ID of the rule to be parsed
     * @param rulePattern the rule to be parsed
     * @return optional of the appropriate GeometryProcessor for the geometry serialisation specified in the rule or absent if it cannot be determined
     */
    public Optional<GeometryProcessor> getRuleGeometryProcessor(final String ruleID, final RulePattern rulePattern) {
        GeometryProcessor geometryProcessor = null;
        
        boolean latFound = false;
        boolean longFound = false;
        boolean eastingFound = false;
        boolean northingFound = false;
        
        for(String variableName : rulePattern.getObjectVariables().keySet()) {
            final Variable variable = rulePattern.getObjectVariables().get(variableName);
            
            if(variable.getType() != null) {
                switch (variable.getType()) {
                    case RuleTriple.WKT:
                        checkIfProcessorInitialised(geometryProcessor, ruleID);
                        geometryProcessor = new WKTProcessor();
                        break;
                    case RuleTriple.GML:
                        checkIfProcessorInitialised(geometryProcessor, ruleID);
                        geometryProcessor = new GMLProcessor();
                        break;
                    case RuleTriple.LAT_LONG:
                        checkIfProcessorInitialised(geometryProcessor, ruleID);
                        geometryProcessor = new LatLongProcessor();
                        break;
                    case RuleTriple.LAT:
                        checkIfProcessorInitialised(geometryProcessor, ruleID);
                        if(longFound) {
                            geometryProcessor = new LatLongProcessor();
                        }
                        latFound = true;
                        break;
                    case RuleTriple.LONG:
                        checkIfProcessorInitialised(geometryProcessor, ruleID);
                        if(latFound) {
                            geometryProcessor = new LatLongProcessor();
                        }
                        longFound = true;
                        break;
                    case RuleTriple.EASTING:
                        checkIfProcessorInitialised(geometryProcessor, ruleID);
                        if(northingFound) {
                            geometryProcessor = new EastingNorthingProcessor();
                        }
                        eastingFound = true;
                        break;
                    case RuleTriple.NORTHING:
                        checkIfProcessorInitialised(geometryProcessor, ruleID);
                        if(eastingFound) {
                            geometryProcessor = new EastingNorthingProcessor();
                        }
                        northingFound = true;
                        break;
                    default:
                        break;
                }
            }
        }
        
        Optional<GeometryProcessor> processor;
        
        if(geometryProcessor == null) {
            LOG.info("Failed to determine geometry type for rule: " + ruleID);
            processor = Optional.absent();
        }
        else {
            processor = Optional.of(geometryProcessor);
        }
        
        return processor;
    }
    
    private void checkIfProcessorInitialised(final GeometryProcessor geometryProcessor, final String ruleID) {
        if(geometryProcessor != null) {
                    throw new IllegalArgumentException("Multiple geometry types found in rule: " + ruleID);
        }
    }
    
    /**
     * Get number of times rule is matched against dataset by executing a COUNT DISTINCT query.
     * @param countDistinctQuery query string
     * @return number of times rule has been matched
     */
    public Integer getRuleMatchCount(final String countDistinctQuery) {
        if(countDistinctQuery == null) {
            throw new IllegalArgumentException("SPARQL query cannot be null.");
        }
        
        QueryExecution queryExecution = null;
        
        try {
            final Query query = QueryFactory.create(countDistinctQuery);
            
            if(graph == null) {
                queryExecution = QueryExecutionFactory.sparqlService(endpoint, query);
            }
            else {
                queryExecution = QueryExecutionFactory.sparqlService(endpoint, query, graph);
            }
            
            final ResultSet resultSet = queryExecution.execSelect();
            final QuerySolution querySolution = resultSet.next();        
            final String variable = querySolution.varNames().next();
            final Integer count = querySolution.getLiteral(variable).getInt();
            
            return count;
        }
        catch (RuntimeException ex) {
            LOG.warn("Failed to execute query: " + countDistinctQuery + " with: " + ex.getMessage(), ex);
            throw ex;
        }
        finally {
            if(queryExecution != null) {
                queryExecution.close();
            }
        }
    }
    
    /**
     * Get sample values for the variables specified in the rule by executing a SELECT ... LIMIT 1 query.
     * @param rulePatternParser rule pattern parser
     * @return the rule with variable content set
     */
    public RulePattern getRuleSampleVariableContent(final RulePatternParser rulePatternParser) {
        if(rulePatternParser == null) {
            throw new IllegalArgumentException("Rule pattern parser cannot be null.");
        }
        
        final RulePattern rulePattern = rulePatternParser.getRulePattern();
        final String selectOneQuery = RuleQueryUtils.formSelectOneQuery(rulePattern);
        QueryExecution queryExecution = null;
        
        try {
            final Query query = QueryFactory.create(selectOneQuery);
            
            if(graph == null) {
                queryExecution = QueryExecutionFactory.sparqlService(endpoint, query);
            }
            else {
                queryExecution = QueryExecutionFactory.sparqlService(endpoint, query, graph);
            }
            
            final ResultSet resultSet = queryExecution.execSelect();
            QuerySolution querySolution;
            
            if(!resultSet.hasNext()) {
                throw new RuntimeException("Query returned empty resultSet.");
            }
            
            querySolution = resultSet.next();
            final RulePattern rulePatternWithContent = getContentFromSolution(querySolution, rulePattern);
            
            return rulePatternWithContent;
        }
        catch(RuntimeException ex) {
            LOG.warn("Failed to execute query: " + selectOneQuery + " with: " + ex.getMessage(), ex);
            throw ex;
        }
        finally {
            if(queryExecution != null) {
                queryExecution.close();
            }
        }
    }
    
    /**
     * Parses object variable content in rule to determine matched geometry serialisation CRS.
     * @param rulePattern rule pattern
     * @param geometryProcessor geometryProcessor for the geometry serialisation matched by the rule
     * @return geometry CRS
     */
    public GeometryCRS getCRSFromContent(final RulePattern rulePattern, final GeometryProcessor geometryProcessor) {
        return geometryProcessor.parseCRS(rulePattern.getObjectVariables());
    }
    
    private RulePattern getContentFromSolution(final QuerySolution querySolution, final RulePattern rulePattern) {
        final RulePattern localRulePattern = new RulePattern(rulePattern);
        
        Variable subjectVariable = localRulePattern.getSubjectVariable();
        subjectVariable = getSubjectNodeContent(querySolution, subjectVariable);
        localRulePattern.setSubjectVariable(subjectVariable);
        
        Map<String,Variable> predicateVariableMap = localRulePattern.getPredicateVariables();
        predicateVariableMap = getPredicateNodeContent(querySolution, predicateVariableMap);
        localRulePattern.setPredicateVariables(predicateVariableMap);

        Map<String,Variable> objectVariableMap = localRulePattern.getObjectVariables();
        objectVariableMap = getObjectNodeContent(querySolution, objectVariableMap);
        localRulePattern.setObjectVariables(objectVariableMap);

        return localRulePattern;
    }
    
    private Variable getSubjectNodeContent(final QuerySolution querySolution, final Variable var) {
        final Resource node = querySolution.getResource(var.getName());
        Optional<String> content;
        
        if(node.isAnon()) {
            content = Optional.of("anon");
        }
        else {
            content = Optional.fromNullable(node.getURI());
        }
        
        var.setContent(content);
        
        return var;
    }
    
    private Map<String, Variable> getPredicateNodeContent(final QuerySolution querySolution, final Map<String, Variable> varMap) {
        for(Entry<String, Variable> varEntry : varMap.entrySet()) {
            final Variable var = varEntry.getValue();
            
            final Resource node = querySolution.getResource(varEntry.getKey());
            Optional<String> content = Optional.absent();
            
            if(node != null) {
                content = Optional.fromNullable("<" + node.asResource().getURI() + ">");
            }
            
            var.setContent(content);
            varMap.put(varEntry.getKey(), var);
        }
        
        return varMap;
    }
    
    private Map<String, Variable> getObjectNodeContent(final QuerySolution querySolution, final Map<String, Variable> varMap) {
        for(Entry<String, Variable> varEntry : varMap.entrySet()) {
            final Variable var = varEntry.getValue();
            
            final RDFNode node = querySolution.get(varEntry.getKey());
            Optional<String> content = Optional.absent();
            
            if(node != null) {
                if(node.isLiteral()) {
                    content = Optional.fromNullable(node.asLiteral().getLexicalForm());
                }
                else if(node.isResource()) {
                    if(node.asResource().isAnon()) {
                        content = Optional.of("anon");
                    }
                    else {
                        content = Optional.fromNullable(node.asResource().getURI());
                    }
                }
            }
            var.setContent(content);
            varMap.put(varEntry.getKey(), var);
        }
        
        return varMap;
    }
}