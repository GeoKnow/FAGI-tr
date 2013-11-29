package gr.athenainnovation.imis.fusion.fetcher.geometry.parsers;

import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.CRSTranformer;
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
import org.opengis.geometry.DirectPosition;

/**
 * Geometry parser for easting/northing type geometry.
 * @author Thomas Maroulis
 */
public class EastingNorthingParser implements GeometryParser {
    private static final Logger LOG = Logger.getLogger(EastingNorthingParser.class);
    
    @Override
    public Geometry parseGeometry(final Map<String,Variable> objectVariableMap) throws GeometryParseException, NumberFormatException {
        Optional<String> easting = Optional.absent();
        Optional<String> northing = Optional.absent();
        
        for(Entry<String, Variable> varEntry : objectVariableMap.entrySet()) {
            switch (varEntry.getValue().getType()) {
                case RuleTriple.EASTING:
                    easting = varEntry.getValue().getContent();
                    break;
                case RuleTriple.NORTHING:
                    northing = varEntry.getValue().getContent();
                    break;
                default:
                    LOG.info("Found non Easting/Northing type variable. Ignoring. Type found: " + varEntry.getValue().getType());
                    break;
            }
        }
            
        if(easting.isPresent() && northing.isPresent()) {            
            final Optional<Geometry> parsedGeom = parse(Double.parseDouble(easting.get()), Double.parseDouble(northing.get()));
            if(parsedGeom.isPresent()) {
                return parsedGeom.get();
            }
        }
        throw new GeometryParseException("Failed to parse Easting/Northing geometry.");
    }
    
    private Optional<Geometry> parse(final double easting, final double northing) {
        Optional<Geometry> parsedGeometry;
        final Optional<DirectPosition> latLong = CRSTranformer.transformEastingNorthingToInternalCRS(easting, northing);
        
        if(latLong.isPresent()) {
            final GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

            final Coordinate coordinate[] = {new Coordinate(latLong.get().getOrdinate(0), latLong.get().getOrdinate(1))};
            final CoordinateSequence coordinateSequence = new CoordinateArraySequence(coordinate);

            final Optional<Geometry> geometry = Optional.fromNullable((Geometry) factory.createPoint(coordinateSequence));

            if(!geometry.isPresent()) {
                LOG.warn("Failed to parse easting/northing pair: " + easting + "/" + northing);
            }
            
            parsedGeometry = geometry;
        }
        else {
            LOG.warn("Failed to parse easting/northing pair: " + easting + "/" + northing);
            parsedGeometry = Optional.absent();
        }

        return parsedGeometry;
    }
}
