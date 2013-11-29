package gr.athenainnovation.imis.fusion.fetcher.geometry.exceptions;

import com.vividsolutions.jts.geom.Geometry;

/**
 * GeometryParseException signifies a failure when parsing the serialisation of a geometry into a {@link Geometry} object.
 * @author Thomas Maroulis
 */
public class GeometryParseException extends RuntimeException {
    public GeometryParseException() {super();}
    public GeometryParseException(final String message) {super(message);}
    public GeometryParseException(final Throwable throwable) {super(throwable);}
    public GeometryParseException(final String message, final Throwable throwable) {super(message, throwable);}
}
