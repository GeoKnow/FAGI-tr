package gr.athenainnovation.imis.fusion.fetcher.rules.parser;

import com.google.common.base.Optional;
import gr.athenainnovation.imis.fusion.fetcher.rules.Rule;
import gr.athenainnovation.imis.fusion.fetcher.rules.RuleTriple;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

/**
 * Parser for rule configuration files
 * @author Thomas Maroulis
 */
public class RuleConfigParser {

    private final Logger log = Logger.getLogger(RuleConfigParser.class);
    
    private Map<String, String> classRules = new HashMap<>();
    private Map<String, String> propertyRules = new HashMap<>();
    private Map<String, String[]> objectRules = new HashMap<>();
    private Map<String, Rule> tripleRules = new HashMap<>();
    
    /**
     * Prefix for class rules.
     */
    public final String CLASS_RULE_PREFIX = "c_";
    /**
     * Prefix for property rules.
     */
    public final String PROPERTY_RULE_PREFIX = "p_";
    /**
     * Prefix for object rules.
     */
    public final String OBJECT_RULE_PREFIX = "o_";
    /**
     * Prefix for user defined triple rules.
     */
    public final String TRIPLE_USER_RULE_PREFIX = "u_";
    /**
     * Prefix for default triple rules.
     */
    public final String TRIPLE_DEFAULT_RULE_PREFIX = "d_";
    
    /**
     * Parse all rule files.
     * @param classRulesFile class rules file
     * @param propertyRulesFile property rules file
     * @param objectRulesFile object rules file
     * @param tripleDefaultRulesFile default triple rules file
     * @param tripleUserRulesFile user defined triple rules file
     */
    public void readAllRules(String classRulesFile, String propertyRulesFile, String objectRulesFile, String tripleDefaultRulesFile, String tripleUserRulesFile) {
        readClassRules(classRulesFile);
        readPropertyRules(propertyRulesFile);
        readObjectRules(objectRulesFile);
        readTripleRules(tripleDefaultRulesFile, TRIPLE_DEFAULT_RULE_PREFIX);
        readTripleRules(tripleUserRulesFile, TRIPLE_USER_RULE_PREFIX);
    }

    private void readClassRules(String file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String currentLine;

            while ((currentLine = br.readLine()) != null) {
                currentLine = currentLine.trim();
                if (!currentLine.isEmpty() && !currentLine.startsWith("#")) {
                    parseClassRule(currentLine);
                }
            }
        } catch (IOException ex) {
            log.error("Error when parsing: " + file, ex);
            throw new RuntimeException(ex);
        }
    }

    private void readPropertyRules(String file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String currentLine;

            while ((currentLine = br.readLine()) != null) {
                currentLine = currentLine.trim();
                if (!currentLine.isEmpty() && !currentLine.startsWith("#")) {
                    parsePropertyRule(currentLine);
                }
            }
        } catch (IOException ex) {
            log.error("Error when parsing: " + file, ex);
            throw new RuntimeException(ex);
        }
    }

    private void readObjectRules(String file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String currentLine;

            while ((currentLine = br.readLine()) != null) {
                currentLine = currentLine.trim();
                if (!currentLine.isEmpty() && !currentLine.startsWith("#")) {
                    parseObjectRule(currentLine);
                }
            }
        } catch (IOException ex) {
            log.error("Error when parsing: " + file, ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     *
     * @param file coniguration file path
     * @param type prefix for rule. Should be either {@link RuleConfigParser#COMBINED_RULE_PREFIX} for combined type rules or {@link RuleConfigParser#KNOWN_RULE_PREFIX} for known type rules
     */
    private void readTripleRules(String file, String type) {
        try (BufferedReader br = new BufferedReader((new FileReader(file)))) {
            String currentLine;

            while ((currentLine = br.readLine()) != null) {
                currentLine = currentLine.trim();
                if (!currentLine.isEmpty() && !currentLine.startsWith("#")) {
                    // Check if rule is all in one line
                    if (currentLine.endsWith(".")) {
                        String token[] = currentLine.substring(0, currentLine.lastIndexOf(".")).trim().split(" ", 4);

                        if (token.length == 4) {
                            parseTripleRules(token[0], token[1], token[2], token[3], type);
                        } else {
                            log.warn("Invalid rule: " + currentLine);
                        }
                    }
                    // Rule is in multiple lines
                    else {
                        String token[] = currentLine.split(" ", 3);
                        if (token.length == 3) {
                            String id = token[0];
                            String description = token[1];
                            String num = token[2];
                            String triples = "";

                            while ((currentLine = br.readLine()) != null) {
                                currentLine = currentLine.trim();
                                if (!currentLine.isEmpty() && !currentLine.startsWith("#")) {
                                    triples = triples.concat(currentLine + "\n");
                                    if (currentLine.endsWith(".")) {
                                        triples = triples.substring(0, triples.lastIndexOf(".")).trim();
                                        break;
                                    }
                                }
                            }

                            parseTripleRules(id, description, num, triples, type);
                        } else {
                            log.warn("Invalid rule: " + currentLine);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            log.error("Error when parsing: " + file, ex);
            throw new RuntimeException(ex);
        }
    }

    private void parseClassRule(String currentLine) {
        String token[] = currentLine.split(" ", 2);
        
        String id = token[0];
        String value = token[1];
        
        if(isID(id)) id = getID(id);
        else id = "";
        
        if(isRegex(value)) value = getRegex(value);
        else if (isURI(value)) value = getURI(value);
        else value = "";

        if(!id.isEmpty() && !value.isEmpty()) {
            if(classRules.get(id) == null) classRules.put(id, value);
            else log.warn("Duplicate rule id found: " + id);
        }
        else log.warn("Failed to parse class rule: " + currentLine);
    }

    private void parsePropertyRule(String currentLine) {
        String token[] = currentLine.split(" ", 2);
        
        String id = token[0];
        String value = token[1];
        
        if(isID(id)) id = getID(id);
        else id = "";
        
        if(isRegex(value)) value = getRegex(value);
        else if (isURI(value)) value = getURI(value);
        else value = "";

        if(!id.isEmpty() && !value.isEmpty()) {
            if(propertyRules.get(id) == null) propertyRules.put(id, value);
            else log.warn("Duplicate rule id found: " + id);
        }
        else log.warn("Failed to parse property rule: " + currentLine);
    }

    private void parseObjectRule(String currentLine) {
        String token[] = currentLine.split(" ", 4);
        
        String id = token[0];
        String regex = token[1];
        String datatype = token[2];
        String type = token[3];
        
        if(isID(id)) id = getID(id);
        else id = "";
        
        if(isRegex(regex)) regex = getRegex(regex);
        else regex = "";
        
        if(isURI(datatype)) datatype = getURI(datatype);
        else if(!"none".equals(datatype)) datatype = "";

        if(!id.isEmpty() && !regex.isEmpty() && !datatype.isEmpty() && !type.isEmpty()) {
            if(objectRules.get(id) == null) objectRules.put(id, new String[]{regex, datatype, type});
            else log.warn("Duplicate rule id found: " + id);
        }
        else log.warn("Failed to parse object rule: " + currentLine);
    }

    private void parseTripleRules(String id, String description, String numUnparsed, String triples, String idPrefix) {
        int num;
        try {
            num = Integer.parseInt(numUnparsed);
        } catch (NumberFormatException ex) {
            log.warn("Failed to parse number of triples (\"" + numUnparsed + "\") in rule: " + id + " " + numUnparsed + "\n" + triples, ex);
            return;
        }

        if(!TRIPLE_USER_RULE_PREFIX.equals(idPrefix) && !TRIPLE_DEFAULT_RULE_PREFIX.equals(idPrefix)) {
            log.warn("Invalid rule id prefix (type): " + idPrefix);
            return;
        }

        if(isIDOfType(id, idPrefix)) id = getID(id);
        else id = "";
        if(id.isEmpty()) return;
        
        if(isDescription(description)) description = getDescription(description);
        else description = "N/A";

        String tripleTokens[] = triples.split("\n");
        
        if(tripleTokens.length != num) {
            log.warn("Invalid number of triples (\"" + num + "\") in rule: " + id + " " + numUnparsed + "\n" + triples);
            return;
        }

        RuleTriple tripleExpandedTokens[] = new RuleTriple[num];

        int count = 0;
        for(String indivTriple : tripleTokens) {
            String splitIndivTriple[] = indivTriple.split(" ", 3);
            if (splitIndivTriple.length != 3) {
                log.warn("Invalid triple: " + indivTriple);
                return;
            }

            RuleTriple expandedTriple = new RuleTriple(splitIndivTriple[0], splitIndivTriple[1], splitIndivTriple[2], "none", "");

            if(!dereferenceTripleProperty(expandedTriple)) return;
            if(!dereferenceTripleObject(expandedTriple)) return;

            tripleExpandedTokens[count] = expandedTriple;
            count++;
        }

        if(TRIPLE_USER_RULE_PREFIX.equals(idPrefix) || TRIPLE_DEFAULT_RULE_PREFIX.equals(idPrefix)) {
            if(tripleRules.get(id) == null) tripleRules.put(id, new Rule(id, description, tripleExpandedTokens));
            else log.warn("Duplicate rule id found: " + id);
        }
    }

    /**
     * Check if triple property is a regex or a reference to a property rule
     *
     * @param expandedTriple
     * @return true if successful, false if there has been an error
     */
    private Boolean dereferenceTripleProperty(RuleTriple expandedTriple) {
        if(isRegex(expandedTriple.getPredicate())) {
            expandedTriple.setPredicate(getRegex(expandedTriple.getPredicate()));
            return true;
        }
        else if(isURI(expandedTriple.getPredicate())) {
            expandedTriple.setPredicate(getURI(expandedTriple.getPredicate()));
            return true;
        }
        else if(isIDOfType(expandedTriple.getPredicate(), PROPERTY_RULE_PREFIX)) {
            String propertyId = getID(expandedTriple.getPredicate());
            Optional<String> regex = getPropertyById(propertyId);
            
            if(regex.isPresent()) {
                 expandedTriple.setPredicate(regex.get());
                 return true;
            }
            else {
                log.warn("Property rule id not found: " + propertyId);
                return false;
            }
        } else {
            log.warn("Invalid triple property: " + expandedTriple.getPredicate());
            return false;
        }
    }

    /**
     * Check if triple object is a regex, a reference to an object rule or class rule, a special value or a variable
     *
     * @param expandedTriple
     * @return true if successful, false if there has been an error
     */
    private Boolean dereferenceTripleObject(RuleTriple expandedTriple) {
        if(isRegex(expandedTriple.getObject())) {
            expandedTriple.setObject(getRegex(expandedTriple.getObject()));
            return true;
        }
        else if(isIDOfType(expandedTriple.getObject(), OBJECT_RULE_PREFIX)) {
            String objectId = getID(expandedTriple.getObject());
            Optional<String[]> value = getObjectById(objectId);
            
            if (value.isPresent()) {
                expandedTriple.setObject(value.get()[0]);
                expandedTriple.setDatatype(value.get()[1]);
                expandedTriple.setType(value.get()[2]);
                return true;
            }
            else {
                log.warn("Object rule id not found: " + objectId);
                return false;
            }
        }
        else if(isIDOfType(expandedTriple.getObject(), CLASS_RULE_PREFIX)) {
            String classid = getID(expandedTriple.getObject());
            Optional<String> value = getClassById(classid);
            
            if (!value.isPresent()) {
                log.warn("Class rule id not found: " + classid);
                return false;
            }
            
            if(!expandedTriple.getPredicate().matches("(^http://www.w3.org/1999/02/22-rdf-syntax-ns#type$|^a$)")) {
                log.warn("Triple object is a reference to a class rule, but triple property is not \"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\" or \"a\": " + expandedTriple.getPredicate());
                return false;
            }
            
            expandedTriple.setObject(value.get());
            return true;
        }
        else if(isVariable(expandedTriple.getObject()) || isAnon(expandedTriple.getObject())) {
            // It's either a variable or an anonymous node. Nothing more to do here.
            return true;
        }
        else {
            log.warn(("Invalid triple object: " + expandedTriple.getObject()));
            return false;
        }
    }

    private String getID(String id) {
        return id.substring(1, id.length() - 1);
    }

    private String getRegex(String value) {
        return "\"" + StringEscapeUtils.escapeJava(value.substring(1, value.length() - 1)) + "\"";
    }
    
    private String getDescription(String value) {
        return value.substring(1, value.length() - 1).replaceAll("_", " ");
    }
    
    private String getURI(String uri) {
        return "{" + StringEscapeUtils.escapeJava(uri.substring(1, uri.length() - 1)) + "}";
    }

    private Optional<String> getClassById(String id) {
        if(isIDPrefix(id, CLASS_RULE_PREFIX)) {
            return Optional.fromNullable(classRules.get(id));
        } else {
            log.warn("Not valid id for class rule: " + id);
            return Optional.absent();
        }
    }

    private Optional<String> getPropertyById(String id) {
        if (isIDPrefix(id, PROPERTY_RULE_PREFIX)) {
            return Optional.fromNullable(propertyRules.get(id));
        } else {
            log.warn("Not valid id for property rule: " + id);
            return Optional.absent();
        }
    }

    private Optional<String[]> getObjectById(String id) {
        if (isIDPrefix(id, OBJECT_RULE_PREFIX)) {
            return Optional.fromNullable(objectRules.get(id));
        } else {
            log.warn("Not valid id for object rule: " + id);
            return Optional.absent();
        }
    }
    
    private boolean isID(String value) {
        return (value.startsWith("<") && value.endsWith(">"));
    }
    
    private boolean isIDOfType(String value, String type) {
        return value.startsWith("<" + type) && value.endsWith(">");
    }
    
    private boolean isIDPrefix(String id, String prefix) {
        return id.startsWith(prefix);
    }
    
    private boolean isURI(String value) {
        return (value.startsWith("{") && value.endsWith("}"));
    }
    
    private boolean isRegex(String value) {
        return (value.startsWith("\"") && value.endsWith("\""));
    }
    
    private boolean isDescription(String value) {
        return (value.startsWith("\"") && value.endsWith("\""));
    }
    
    private boolean isVariable(String value) {
        return value.startsWith("?");
    }
    
    private boolean isAnon(String value) {
        return value.startsWith("_:");
    }

    /**
     *
     * @return class rules
     */
    public Map<String, String> getClassRules() {
        return classRules;
    }

    /**
     *
     * @return property rules
     */
    public Map<String, String> getPropertyRules() {
        return propertyRules;
    }

    /**
     *
     * @return object rules
     */
    public Map<String, String[]> getObjectRules() {
        return objectRules;
    }

    /**
     *
     * @return triple rules
     */
    public Map<String, Rule> getTripleRules() {
        return tripleRules;
    }
}