package gr.athenainnovation.imis.fusion.fetcher.geometry.parsers;

import com.google.common.base.Optional;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import gr.athenainnovation.imis.fusion.fetcher.geometry.exceptions.GeometryParseException;
import gr.athenainnovation.imis.fusion.fetcher.rules.RuleTriple;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;

/**
 * Geometry parser for lat/long type geometry.
 * @author Thomas Maroulis
 */
public class LatLongParser implements GeometryParser {
    private static final Logger LOG = Logger.getLogger(LatLongParser.class);
    
    private static final int SRID = 4326;
    
    @Override
    public Geometry parseGeometry(final Map<String,Variable> objectVariableMap) throws GeometryParseException, NumberFormatException {
        Optional<String> latitude = Optional.absent();
        Optional<String> longitude = Optional.absent();
        
        for(Entry<String, Variable> varEntry : objectVariableMap.entrySet()) {
            switch (varEntry.getValue().getType()) {
                case RuleTriple.LAT:
                    latitude = varEntry.getValue().getContent();
                    break;
                case RuleTriple.LONG:
                    longitude = varEntry.getValue().getContent();
                    break;
                case RuleTriple.LAT_LONG:
                    final Optional<String> content = varEntry.getValue().getContent();
                    if(content.isPresent()) {
                        final String[] splitContent = content.get().split("(\\s|,)");
                        if(splitContent.length < 2) {
                            throw new NumberFormatException("Failed to split lat/long pair: " + content.get());
                        }
                        latitude = Optional.of(splitContent[0]);
                        longitude = Optional.of(splitContent[1]);
                    }
                    break;
                default:
                    LOG.info("Found non Lat/Long type variable. Ignoring. Type found: " + varEntry.getValue().getType());
                    break;
            }
        }
        
        if(latitude.isPresent() && longitude.isPresent()) {
            final Optional<Geometry> parsedGeom = parse(Double.parseDouble(latitude.get()), Double.parseDouble(longitude.get()));
            if(parsedGeom.isPresent()) {
                return parsedGeom.get();
            }
        }
        throw new GeometryParseException("Failed to parse Lat/Long geometry.");
    }
    
    private Optional<Geometry> parse(final double latitude, final double longitude) {
        final GeometryFactory factory = new GeometryFactory(new PrecisionModel(), SRID);
       
        final Coordinate coordinate[] = {new Coordinate(latitude, longitude)};
        final CoordinateSequence coordinateSequence = new CoordinateArraySequence(coordinate);
        
        final Optional<Geometry> geometry = Optional.fromNullable((Geometry) factory.createPoint(coordinateSequence));
        
        if(!geometry.isPresent()) {
            LOG.warn("Failed to parse lat/long pair: " + latitude + "/" + longitude);
        }
        
        return geometry;
    }
}