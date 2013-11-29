package gr.athenainnovation.imis.fusion.fetcher.gui.workers;

import com.google.common.base.Optional;
import gr.athenainnovation.imis.fusion.fetcher.core.Matcher;
import gr.athenainnovation.imis.fusion.fetcher.geometry.GeometryProcessor;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GeometryCRS;
import gr.athenainnovation.imis.fusion.fetcher.rules.Rule;
import gr.athenainnovation.imis.fusion.fetcher.rules.RulePattern;
import gr.athenainnovation.imis.fusion.fetcher.rules.parser.RulePatternParser;
import gr.athenainnovation.imis.fusion.fetcher.rules.parser.RuleQueryUtils;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;

/**
 * This worker handles matching a set of rules against a dataset.
 * @author Thomas Maroulis
 */
public class MatcherWorker extends SwingWorker<Map<String, MatchedRule>, Void>{
    private final Logger log = Logger.getLogger(MatcherWorker.class);
    
    private Map<String, Rule> rules;
    private String endpoint, graph;
    
    /**
     * 
     * @param rules map of rules to be matched
     * @param endpoint SPARQL endpoint
     * @param graph graph URI
     */
    public MatcherWorker(Map<String, Rule> rules, String endpoint, String graph) {
        super();
        this.rules = rules;
        this.endpoint = endpoint;
        this.graph = graph;
    }

    @Override
    protected Map<String, MatchedRule> doInBackground() {
        Matcher matcher = new Matcher(endpoint, graph);
        Map<String, MatchedRule> matchedRulesMap = new HashMap<>();
        int rulesExecuted = 0;
        
        for(String ruleID : rules.keySet()) {
            try {                
                final RulePatternParser rulePatternParser = new RulePatternParser(rules.get(ruleID));
                final RulePattern rulePattern = rulePatternParser.getRulePattern();
                
                int count = matcher.getRuleMatchCount(RuleQueryUtils.formCountDistinctQuery(rulePattern));

                MatchedRule matchedRule = new MatchedRule(rules.get(ruleID));
                matchedRule.setTimesMatched(Optional.of(count));
                
                Optional<GeometryProcessor> geometryProcessor = matcher.getRuleGeometryProcessor(ruleID, rulePattern);
                
                if(count > 0) {
                    RulePattern rulePatternWithContent = matcher.getRuleSampleVariableContent(rulePatternParser);
                    matchedRule.setRulePattern(Optional.of(rulePatternWithContent));
                    
                    if(geometryProcessor.isPresent() && !geometryProcessor.get().isCRSSet()) {
                        GeometryCRS geometryCRS = matcher.getCRSFromContent(rulePatternWithContent, geometryProcessor.get());
                        geometryProcessor.get().setGeometryCRS(geometryCRS);
                    }
                }
                matchedRule.setGeometryProcessor(geometryProcessor);
                
                matchedRulesMap.put(ruleID, matchedRule);
            }
            catch (RuntimeException ex) {
                log.warn(ex.getMessage(), ex);
                MatchedRule matchedRule = new MatchedRule(rules.get(ruleID), ex.getMessage());
                matchedRulesMap.put(ruleID, matchedRule);
            }
            
            setProgress(++rulesExecuted);
        }
        
        return matchedRulesMap;
    }
}