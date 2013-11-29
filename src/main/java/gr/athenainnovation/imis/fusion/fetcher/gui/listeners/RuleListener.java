package gr.athenainnovation.imis.fusion.fetcher.gui.listeners;

import gr.athenainnovation.imis.fusion.fetcher.gui.workers.MatchedRule;
import java.util.Map;

/**
 * Matched rule listener.
 * @author Thomas Maroulis
 */
public interface RuleListener {
    void setSourceMatchedRules(Map<String, MatchedRule> sourceMatchedRules);
    void setTargetMatchedRules(Map<String, MatchedRule> targetMatchedRules);
    void setSourceMatchedRulesReady(boolean sourceMatchedRulesReady);
    void setTargetMatchedRulesReady(boolean targetMatchedRulesReady);
}
