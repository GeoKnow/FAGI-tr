package gr.athenainnovation.imis.fusion.fetcher.geometry.writers;

import com.google.common.base.Optional;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GeometryCRS;
import gr.athenainnovation.imis.fusion.fetcher.rules.RuleTriple;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;

/**
 * Geometry writer for lat/long type geometry.
 * @author Thomas Maroulis
 */
public class LatLongWriter implements GeometryWriter {
    private static final Logger LOG = Logger.getLogger(LatLongWriter.class);
    
    @Override
    public Map<String, Variable> writeGeometry(final Geometry geometry, final GeometryCRS geometryCRS, final Map<String, Variable> objectVariableMap) throws RuntimeException {
        if(!(geometry instanceof Point)) {
            throw new IllegalArgumentException("Lat/Long writer given non-point geometry.");
        }
        if(geometryCRS != null && !( "EPSG".equals(geometryCRS.getAuthority()) && geometryCRS.getSRID() == 4326 && !geometryCRS.isLongitudeFirst())) {
            throw new IllegalArgumentException("Lat/Long writer only accepts EPSG:4326.");
        }
        
        final Double latitude = getLat(Optional.of(geometry));
        final Double longitude = getLong(Optional.of(geometry));
        
        for(Entry<String, Variable> varEntry : objectVariableMap.entrySet()) {
            switch (varEntry.getValue().getType()) {
                case RuleTriple.LAT:
                    {
                        final Variable variable = varEntry.getValue();
                        variable.setContent(Optional.of(Double.toString(latitude)));
                        objectVariableMap.put(varEntry.getKey(), variable);
                        break;
                    }
                case RuleTriple.LONG:
                    {
                        final Variable variable = varEntry.getValue();
                        variable.setContent(Optional.of(Double.toString(longitude)));
                        objectVariableMap.put(varEntry.getKey(), variable);
                        break;
                    }
                case RuleTriple.LAT_LONG:
                    {
                        final Variable variable = varEntry.getValue();
                        variable.setContent(Optional.of(latitude + " " + longitude));
                        objectVariableMap.put(varEntry.getKey(), variable);
                        break;
                    }
                default:
                    LOG.info("Found non Lat/Long type variable. Ignoring. Type found: " + varEntry.getValue().getType());
                    break;
            }
        }
        
        return objectVariableMap;
    }
    
    private Double getLat(final Optional<Geometry> geometry) {
        return geometry.get().getCoordinate().x;
    }
    
    private Double getLong(final Optional<Geometry> geometry) {
        return geometry.get().getCoordinate().y;
    }
}