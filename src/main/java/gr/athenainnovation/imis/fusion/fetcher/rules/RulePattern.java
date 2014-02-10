package gr.athenainnovation.imis.fusion.fetcher.rules;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a rule pattern for matching triples in a dataset.
 * @author Thomas Maroulis
 */
public class RulePattern {
    private Variable subjectVariable;
    private Map<String, Variable> predicateVariables;
    private Map<String, Variable> objectVariables;
    private String selectPattern;
    private String updatePattern;
    
    /**
     * 
     */
    public RulePattern() {
        subjectVariable = null;
        predicateVariables = new LinkedHashMap<>();
        objectVariables = new LinkedHashMap<>();
        selectPattern = "";
        updatePattern = "";
    }
    
    /**
     * Copy constructor
     * @param rulePattern existing rule pattern to be copied
     */
    public RulePattern(final RulePattern rulePattern) {
        subjectVariable = new Variable(rulePattern.getSubjectVariable());
        predicateVariables = new LinkedHashMap<>(rulePattern.getPredicateVariables());
        objectVariables = new LinkedHashMap<>(rulePattern.getObjectVariables());
        selectPattern = rulePattern.getSelectPattern();
        updatePattern = rulePattern.getUpdatePattern();
    }

    /**
     *
     * @return subject variable
     */
    public Variable getSubjectVariable() {
        return subjectVariable;
    }
    
    /**
     *
     * @return predicate variable map
     */
    public Map<String, Variable> getPredicateVariables() {
        return predicateVariables;
    }

    /**
     *
     * @return object variable map
     */
    public Map<String, Variable> getObjectVariables() {
        return objectVariables;
    }
    
    /**
     *
     * @return select query pattern
     */
    public String getSelectPattern() {
        return selectPattern;
    }
    
    /**
     *
     * @return update query pattern
     */
    public String getUpdatePattern() {
        return updatePattern;
    }

    /**
     *
     * @param subjectVariable subject variable
     */
    public void setSubjectVariable(final Variable subjectVariable) {
        this.subjectVariable = subjectVariable;
    }
    
    /**
     *
     * @param predicateVariables predicate variable map
     */
    public void setPredicateVariables(final Map<String, Variable> predicateVariables) {
        this.predicateVariables = predicateVariables;
    }

    /**
     *
     * @param objectVariables object variable map
     */
    public void setObjectVariables(final Map<String, Variable> objectVariables) {
        this.objectVariables = objectVariables;
    }
    
    /**
     *
     * @param selectPattern select query pattern
     */
    public void setSelectPattern(final String selectPattern) {
        this.selectPattern = selectPattern;
    }
    
    /**
     *
     * @param updatePattern update query pattern
     */
    public void setUpdatePattern(final String updatePattern) {
        this.updatePattern = updatePattern;
    }
}
