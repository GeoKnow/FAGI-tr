package gr.athenainnovation.imis.fusion.fetcher.gui.workers;

/**
 * Container object holding locations of rule configuration files.
 * @author Thomas Maroulis
 */
public class RuleConfig {  
    private static final String CLASS_RULES_FILE = "rules/class";
    private static final String PROPERTY_RULES_FILE = "rules/property";
    private static final String OBJECT_RULES_FILE = "rules/object";
    private static final String TRIPLE_DEFAULT_RULES_FILE = "rules/triple_default";
    private static final String TRIPLE_USER_RULES_FILE = "rules/triple_user";
    
    /**
     * Represents source dataset.
     */
    public static final int SOURCE_DATASET = 0;
    /**
     * Represents target dataset.
     */
    public static final int TARGET_DATASET = 1;

    /**
     *
     * @return class rules configuration file
     */
    public String getClassRulesFile() {
        return CLASS_RULES_FILE;
    }

    /**
     *
     * @return property rules configuration file
     */
    public String getPropertyRulesFile() {
        return PROPERTY_RULES_FILE;
    }

    /**
     *
     * @return object rules configuration file
     */
    public String getObjectRulesFile() {
        return OBJECT_RULES_FILE;
    }

    /**
     *
     * @return default full triple configuration file
     */
    public String getTripleDefaultRulesFile() {
        return TRIPLE_DEFAULT_RULES_FILE;
    }

    /**
     *
     * @return user added full triple configuration file
     */
    public String getTripleUserRulesFile() {
        return TRIPLE_USER_RULES_FILE;
    }
}