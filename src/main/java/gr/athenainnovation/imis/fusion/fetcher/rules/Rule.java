package gr.athenainnovation.imis.fusion.fetcher.rules;

import java.util.Arrays;

/**
 * Represents a rule for matching triples in a dataset.
 * @author Thomas Maroulis
 */
public class Rule {
    private final String name, description;
    private final RuleTriple[] triples;

    /**
     * 
     * @param name rule name
     * @param description rule description
     * @param triples rule triples
     */
    public Rule(final String name, final String description, final RuleTriple[] triples) {
        this.name = name;
        this.description = description;
        this.triples = (RuleTriple[]) Arrays.copyOf(triples, triples.length);
    }

    /**
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @return triples
     */
    public RuleTriple[] getTriples() {
        return (RuleTriple[]) Arrays.copyOf(triples, triples.length);
    }
}