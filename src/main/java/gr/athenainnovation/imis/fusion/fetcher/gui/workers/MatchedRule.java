package gr.athenainnovation.imis.fusion.fetcher.gui.workers;

import com.google.common.base.Optional;
import gr.athenainnovation.imis.fusion.fetcher.geometry.GeometryProcessor;
import gr.athenainnovation.imis.fusion.fetcher.rules.Rule;
import gr.athenainnovation.imis.fusion.fetcher.rules.RulePattern;
import java.util.Comparator;

/**
 * Represents  a rule that has been matched against a dataset.
 * @author Thomas Maroulis
 */
public class MatchedRule {
    private final Rule rule;
    private Optional<Integer> timesMatched = Optional.absent();
    private Optional<RulePattern> rulePattern = Optional.absent();
    private Optional<String> exceptionMessageThrown = Optional.absent();
    private Optional<GeometryProcessor> geometryProcessor = Optional.absent();
    private boolean toBeRetained = false;

    /**
     * Constructor for scenarios where matching completed without error.
     * @param rule rule
     */
    public MatchedRule(final Rule rule) {
        if(rule == null) {
            throw new IllegalArgumentException("Rule cannot be null.");
        }
        
        this.rule = rule;
    }
    
    /**
     * Constructor for scenarios where matching threw an exception.
     * @param rule rule
     * @param exceptionMessageThrown exception message
     */
    public MatchedRule(final Rule rule, final String exceptionMessageThrown) {
        if(rule == null) {
            throw new IllegalArgumentException("Rule cannot be null.");
        }
        
        this.rule = rule;
        this.exceptionMessageThrown = Optional.of(exceptionMessageThrown);
    }
    
    /**
     *
     * @return rule
     */
    public Rule getRule() {
        return rule;
    }

    /**
     *
     * @return times rule has been matched
     */
    public Optional<Integer> getTimesMatched() {
        return timesMatched;
    }

    /**
     *
     * @return rule pattern
     */
    public Optional<RulePattern> getRulePattern() {
        return rulePattern;
    }

    /**
     *
     * @return exception message
     */
    public Optional<String> getExceptionMessageThrown() {
        return exceptionMessageThrown;
    }
    
    /**
     *
     * @return geometry processor
     */
    public Optional<GeometryProcessor> getGeometryProcessor() {
        return geometryProcessor;
    }
    
    /**
     *
     * @return true if the triples matched by this rule are to be retained/transformed, false otherwise
     */
    public boolean isToBeRetained() {
        return toBeRetained;
    }    

    /**
     *
     * @param timesMatched times rule has been matched against dataset
     */
    public void setTimesMatched(final Optional<Integer> timesMatched) {
        this.timesMatched = timesMatched;
    }

    /**
     *
     * @param rulePattern rule pattern
     */
    public void setRulePattern(final Optional<RulePattern> rulePattern) {
        this.rulePattern = rulePattern;
    }

    /**
     *
     * @param exceptionMessageThrown exception message
     */
    public void setExceptionMessageThrown(final Optional<String> exceptionMessageThrown) {
        this.exceptionMessageThrown = exceptionMessageThrown;
    }
    
    /**
     *
     * @param geometryProcessor geometry processor
     */
    public void setGeometryProcessor(final Optional<GeometryProcessor> geometryProcessor) {
        this.geometryProcessor = geometryProcessor;
    }
    
    /**
     *
     * @param toBeRetained true if triples matched by this rule are to be retained/transformed, false otherwise
     */
    public void setToBeRetained(final boolean toBeRetained) {
        this.toBeRetained = toBeRetained;
    }
    
    /**
     * Comparator for object on times matched.
     */
    public static final Comparator<MatchedRule> TIMES_MATCHED_COMPARATOR = new Comparator<MatchedRule>() {

        @Override
        public int compare(final MatchedRule object1, final MatchedRule object2) {
            Integer firstValue = 0;
            Integer secondValue = 0;
            if(object1.timesMatched.isPresent()) {
                firstValue = object1.timesMatched.get();
            }
            if(object2.timesMatched.isPresent()) {
                secondValue = object2.timesMatched.get();
            }
            
            //Descending order
            return secondValue.compareTo(firstValue);
        }
    };
}