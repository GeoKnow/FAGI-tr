package gr.athenainnovation.imis.fusion.fetcher.geometry.exceptions;

/**
 * CRSParseException signifies a failure when parsing the Coordinate Reference System from the serialisation of a geometry.
 * @author Thomas Maroulis
 */
public class CRSParseException extends RuntimeException {
    public CRSParseException() {super();}
    public CRSParseException(final String message) {super(message);};
    public CRSParseException(final Throwable throwable) {super(throwable);}
    public CRSParseException(final String message, final Throwable throwable) {super(message, throwable);}
}
