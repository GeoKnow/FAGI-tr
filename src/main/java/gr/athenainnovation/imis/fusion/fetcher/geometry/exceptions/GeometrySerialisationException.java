package gr.athenainnovation.imis.fusion.fetcher.geometry.exceptions;

import com.vividsolutions.jts.geom.Geometry;

/**
 * GeometrySerialistionException signifies a failure when attempting to serialise a {@link Geometry} object.
 * @author Thomas Maroulis
 */
public class GeometrySerialisationException extends RuntimeException {
    public GeometrySerialisationException() {super();};
    public GeometrySerialisationException(final String message) {super(message);}
    public GeometrySerialisationException(final Throwable throwable) {super(throwable);}
    public GeometrySerialisationException(final String message, final Throwable throwable) {super(message, throwable);}
}
