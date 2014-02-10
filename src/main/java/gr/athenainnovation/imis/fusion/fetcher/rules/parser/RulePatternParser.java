package gr.athenainnovation.imis.fusion.fetcher.rules.parser;

import gr.athenainnovation.imis.fusion.fetcher.rules.Rule;
import gr.athenainnovation.imis.fusion.fetcher.rules.RulePattern;
import gr.athenainnovation.imis.fusion.fetcher.rules.RuleTriple;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.util.Map;


/**
 * Rule pattern parser.
 * @author Thomas Maroulis
 */
public class RulePatternParser {
     
    private static final String DOT = " . ";
    
    private String triplePattern = "";
    private String filterPattern = "";
    
    private static final String NEW_VARIABLE_PREFIX = "?var";
    private int newVariableIndex = 0;
    
    private boolean subjectVariableInitialised = false;
    
    private final RulePattern rulePattern;
    
    /**
     *
     * @param rule rule
     */
    public RulePatternParser(final Rule rule) {    
        //gets constructed for every rule, after the begining of rule matching
        if(rule == null) {
            throw new IllegalArgumentException("Rule cannot be null.");
        }
        
        rulePattern = new RulePattern();
        
        for(RuleTriple triple : rule.getTriples()) {
            formTriple(triple);           
        }
        
        final String selectPattern = triplePattern + " " + filterPattern; 
        final String updatePattern = triplePattern;
       
        rulePattern.setSelectPattern(selectPattern);
        rulePattern.setUpdatePattern(updatePattern);
    }
    
    /**
     *
     * @return rule pattern
     */
    public RulePattern getRulePattern() {
        return rulePattern;
    }
    
    private void formTriple(final RuleTriple triple) {
        if(triple == null || triple.getSubject() == null || triple.getPredicate() == null || triple.getObject() == null) {
            throw new IllegalArgumentException("Found null triple.");
        }

        addSubject(triple.getSubject());
        addPredicate(triple.getPredicate());
        addObject(triple.getObject(), triple.getDatatype(), triple.getType());
    }

    private void addSubject(final String subject) {
        if(isVariable(subject)) {
            triplePattern = triplePattern.concat(subject + " ");

            if(!subjectVariableInitialised || rulePattern.getSubjectVariable().getName().equals(subject)) {
                rulePattern.setSubjectVariable(new Variable(subject, RuleTriple.SUBJECT_VARIABLE));
                subjectVariableInitialised = true;
            }
            else {
                throw new IllegalArgumentException("There can only be one triple subject variable: " + subject);
            }
        }
        else if(isAnon(subject)) {
            triplePattern = triplePattern.concat(subject + " ");
        }
        else {
            throw new IllegalArgumentException("Illegal triple subject: " + subject);
        }
    }

    private void addPredicate(final String predicate) {
        if(isURI(predicate)) {
            addURI(predicate);
        }
        else if(isRegex(predicate)) {
            addPredicateRegex(predicate);
        }
        else {
            throw new IllegalArgumentException("Illegal triple predicate: " + predicate);
        }
    }

    private void addObject(final String object, final String datatype, final String type) {
        // Check if object is an anon node (":_"), a variable ("?"), a special value ("&") or a regex ("\"" ... "\"")
        if(isAnon(object)) {
            triplePattern = triplePattern.concat(object + DOT);           
            addDatatypeFilter(object, datatype);
        }
        else if(isVariable(object)) {
            triplePattern = triplePattern.concat(object + DOT);
            final Map<String,Variable> objectVars = rulePattern.getObjectVariables();
            objectVars.put(object, new Variable(object, type));
            rulePattern.setObjectVariables(objectVars);

            addDatatypeFilter(object, datatype);
        }
        else if(isRegex(object)) {
            addObjectRegex(object, datatype, type);
            triplePattern = triplePattern.concat(DOT);
        }
        else {
            throw new IllegalArgumentException("Illegal triple object: " + object);
        }
    }

    private void addURI(final String uri) {
        //replace { } with < > in uri 
        triplePattern = triplePattern.concat("<" + uri.substring(1, uri.length()-1) + "> ");
    }

    private void addPredicateRegex(final String regex) {
        final String newVariable = NEW_VARIABLE_PREFIX + newVariableIndex++;
        triplePattern = triplePattern.concat(newVariable + " ");
        filterPattern = filterPattern.concat("FILTER regex(" + newVariable + ", " + regex + ", \"i\") ");
        
        final Map<String, Variable> predicateVariables = rulePattern.getPredicateVariables();
        predicateVariables.put(newVariable, new Variable(newVariable, RuleTriple.GENERIC));
    }
    
    private void addObjectRegex(final String regex, final String datatype, final String type) {
        final String newVariable = NEW_VARIABLE_PREFIX + newVariableIndex++;
        triplePattern = triplePattern.concat(newVariable + " ");
        // origin filterPattern = filterPattern.concat("FILTER regex(" + newVariable + ", " + regex + ", \"i\") ");
        filterPattern = filterPattern.concat("FILTER regex(str(" + newVariable + "), " + regex + ", \"i\") ");
        
        addDatatypeFilter(newVariable, datatype);
        
        final Map<String, Variable> objectVariables = rulePattern.getObjectVariables();
        final Variable variable = new Variable(newVariable, type);
        
        if(!"none".equals(datatype) && isURI(datatype)) {
            variable.setDatatype(datatype.substring(1, datatype.length()-1));
        }
        
        objectVariables.put(newVariable, variable);
    }

    private void addDatatypeFilter(final String variable, final String datatype) {
        if(!"none".equals(datatype) && isURI(datatype)) {
            filterPattern = filterPattern.concat("FILTER (datatype(" + variable + ") = <" + datatype.substring(1, datatype.length()-1) + ">) ");
        }
    }

    private boolean isVariable(final String value) {
        return value.startsWith("?");
    }

    private boolean isAnon(final String value) {
        return value.startsWith("_:");
    }

    private boolean isURI(final String value) {
        return value.startsWith("{") && value.endsWith("}");
    }

    private boolean isRegex(final String value) {
        return value.startsWith("\"") && value.endsWith("\"");
    }
}