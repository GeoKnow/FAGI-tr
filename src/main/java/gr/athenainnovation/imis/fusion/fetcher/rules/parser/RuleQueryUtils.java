package gr.athenainnovation.imis.fusion.fetcher.rules.parser;

import gr.athenainnovation.imis.fusion.fetcher.rules.RulePattern;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * Utilities for forming queries from rules.
 * @author Thomas Maroulis
 */
public final class RuleQueryUtils {
    
    private RuleQueryUtils() {
        // Private to defeat instantiation.
    }
    
    /**
     *
     * @param rulePattern
     * @return count distinct query
     */
    public static String formCountDistinctQuery(final RulePattern rulePattern) {
        return "SELECT (COUNT (DISTINCT " + rulePattern.getSubjectVariable().getName() + ") as ?count)\nWHERE\n{\n" + rulePattern.getSelectPattern() + "}";
    }
    
    /**
     *
     * @param rulePattern
     * @return select all query
     */
    public static String formSelectAllQuery(final RulePattern rulePattern) {
        return "SELECT * WHERE { " + rulePattern.getSelectPattern()+ "}";
    }
    
    /**
     *
     * @param rulePattern
     * @return select one query
     */
    public static String formSelectOneQuery(final RulePattern rulePattern) {
        return formSelectAllQuery(rulePattern) + " LIMIT 1";
    }
    
    /**
     *
     * @param rulePattern
     * @param graph
     * @return update query
     */
    public static String formUpdateQuery(final RulePattern rulePattern, final String graph) {
        if(graph == null || graph.isEmpty()) {
            throw new IllegalArgumentException("Graph cannot be null or empty.");
        }
        
        String updateTriplePattern = rulePattern.getUpdatePattern();
        
        if(!rulePattern.getSubjectVariable().getContent().isPresent()) {
            throw new IllegalArgumentException("Subject variable " + rulePattern.getSubjectVariable().getName() + " content not present.");
        }
        updateTriplePattern = updateTriplePattern.replace(rulePattern.getSubjectVariable().getName(), "<" + rulePattern.getSubjectVariable().getContent().get() + ">");
        
        for(Entry<String, Variable> predicateVariableEntry : rulePattern.getPredicateVariables().entrySet()) {
            if(!predicateVariableEntry.getValue().getContent().isPresent()) {
                throw new IllegalArgumentException("Predicate variable " + predicateVariableEntry.getKey() + " content not present.");
            }
            updateTriplePattern = updateTriplePattern.replace(predicateVariableEntry.getKey(), "<" + predicateVariableEntry.getValue().getContent().get() + ">");
        }
        
        for(Entry<String, Variable> objectVariableEntry : rulePattern.getObjectVariables().entrySet()) {
            if(!objectVariableEntry.getValue().getContent().isPresent()) {
                throw new IllegalArgumentException("Object variable " + objectVariableEntry.getKey() + " content not present.");
            }
            
            if(objectVariableEntry.getValue().getDatatype().isPresent()) {
                updateTriplePattern = updateTriplePattern.replace(objectVariableEntry.getKey(), "\"" + objectVariableEntry.getValue().getContent().get() + "\"^^<" + 
                        objectVariableEntry.getValue().getDatatype().get() + ">");
            }
            else {
                updateTriplePattern = updateTriplePattern.replace(objectVariableEntry.getKey(), "\"" + objectVariableEntry.getValue().getContent().get() + "\"");
            }
        }
        
        return "SPARQL WITH <" + graph + "> INSERT { " + updateTriplePattern + "} WHERE {}";
    }
    
    /**
     *
     * @param rulePattern
     * @return list of triples with variables substituted with their values
     */
    public static List<String> substituteVariableContentIntoPattern(final RulePattern rulePattern) {        
        List<String> tokens = Arrays.asList(rulePattern.getUpdatePattern().split(" \\. "));
        List<String> output = new ArrayList<>();
        
        for(Iterator<String> iter = tokens.iterator(); iter.hasNext();) {
            String line = iter.next().trim();

            String subjectVar = rulePattern.getSubjectVariable().getName();
            if(rulePattern.getSubjectVariable().getContent().isPresent())
                line = line.replace(subjectVar, rulePattern.getSubjectVariable().getContent().get());
            else
                line = line.replaceAll(subjectVar, "N/A");

            for(Entry<String, Variable> predicateVarEntry : rulePattern.getPredicateVariables().entrySet()) {
                if(predicateVarEntry.getValue().getContent().isPresent())
                        line = line.replace(predicateVarEntry.getKey(), predicateVarEntry.getValue().getContent().get());
                else
                    line = line.replaceAll(predicateVarEntry.getKey(), "N/A");
            }

            for(Entry<String, Variable> objectVarEntry : rulePattern.getObjectVariables().entrySet()) {
                if(objectVarEntry.getValue().getContent().isPresent())
                    line = line.replace(objectVarEntry.getKey(), objectVarEntry.getValue().getContent().get());
                else
                    line = line.replaceAll(objectVarEntry.getKey(), "N/A");
            }
            output.add(line);
        }
        return output;
    }
}
