package gr.athenainnovation.imis.fusion.fetcher.rules;

/**
 * Represents an unparsed rule for matching triples against a dataset as it is read from the configuration files.
 * @author Thomas Maroulis
 */
public class RuleTriple {
    private String subject, predicate, object, datatype, type;
    
    /**
     * Variable type representing the subject variable that serves as the root node. There should only be a single SUBJECT_VARIABLE in every rule.
     */
    public static final String SUBJECT_VARIABLE = "subject";
    /**
     * Variable type representing a variable whose value will be a WKT geometry literal.
     */
    public static final String WKT = "&wkt";
    /**
     * Variable type representing a variable whose value will be a GML geometry literal.
     */
    public static final String GML = "&gml";
    /**
     * Variable type representing a variable whose value will be a latitude literal.
     */
    public static final String LAT = "&lat";
    /**
     * Variable type representing a variable whose value will be a longitude literal.
     */
    public static final String LONG = "&long";
    /**
     * Variable type representing a variable whose value will be an altitude literal.
     */
    public static final String ALT = "&alt";
    /**
     * Variable type representing a variable whose value will be a comma or space separated latitude/longitude value pair literal.
     */
    public static final String LAT_LONG = "&lat_long";
    /**
     * Variable type representing a variable whose value will be an easting type literal.
     */
    public static final String EASTING = "&east";
    /**
     * Variable type representing a variable whose value will be a northing type literal.
     */
    public static final String NORTHING = "&north";
    /**
     * Variable type reprsenting a variable whose value will have no special meaning attached to it.
     */
    public static final String GENERIC = "generic";

    /**
     *
     * @param subject triple subject
     * @param predicate triple predicate
     * @param object triple object
     * @param datatype object datatype
     * @param type object type
     */
    public RuleTriple(final String subject, final String predicate, final String object, final String datatype, final String type) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.datatype = datatype;
        this.type = type;
    }

    /**
     *
     * @return triple subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     *
     * @return triple predicate
     */
    public String getPredicate() {
        return predicate;
    }

    /**
     *
     * @return triple object
     */
    public String getObject() {
        return object;
    }

    /**
     *
     * @return object datatype
     */
    public String getDatatype() {
        return datatype;
    }
    
    /**
     *
     * @return object type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param subject triple subject 
     */
    public void setSubject(final String subject) {
        this.subject = subject;
    }

    /**
     *
     * @param predicate triple predicate
     */
    public void setPredicate(final String predicate) {
        this.predicate = predicate;
    }

    /**
     *
     * @param object triple object
     */
    public void setObject(final String object) {
        this.object = object;
    }

    /**
     *
     * @param datatype object datatype
     */
    public void setDatatype(final String datatype) {
        this.datatype = datatype;
    }
    
    /**
     *
     * @param type object type
     */
    public void setType(final String type) {
        this.type = type;
    }
}