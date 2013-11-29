package gr.athenainnovation.imis.fusion.fetcher.rules;

import com.google.common.base.Optional;

/**
 * Represents a variable with its name, its geometric type and optional its value (content) and datatype. 
 * @author Thomas Maroulis
 */
public class Variable {
    private final String name;
    private Optional<String> content;
    private Optional<String> datatype;
    private final String type;
    
    /**
     * 
     * @param name variable name
     * @param type variable geometric type for parsing
     */
    public Variable(final String name, final String type) {
        this.name = name;
        this.content = Optional.absent();
        this.datatype = Optional.absent();
        this.type = type;
    }
    
    /**
     * Copy constructor
     * @param variable existing variable to be copied
     */
    public Variable(final Variable variable) {
        this.name = variable.getName();
        this.content = variable.getContent();
        this.type = variable.getType();
    }

    /**
     *
     * @return variable name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return variable content (value)
     */
    public Optional<String> getContent() {
        return content;
    }

    /**
     *
     * @return variable geometric type
     */
    public String getType() {
        return type;
    }
    
    
    /**
     *
     * @return datatype URI
     */
    public Optional<String> getDatatype() {
        return datatype;
    }
    
    /**
     *
     * @param datatype datatype URI
     */
    public void setDatatype(final String datatype) {
        this.datatype = Optional.of(datatype);
    }

    /**
     *
     * @param content variable content (value)
     */
    public void setContent(final Optional<String> content) {
        this.content = content;
    }
}
