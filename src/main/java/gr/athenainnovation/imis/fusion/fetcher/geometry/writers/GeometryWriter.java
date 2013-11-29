package gr.athenainnovation.imis.fusion.fetcher.geometry.writers;

import com.vividsolutions.jts.geom.Geometry;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GeometryCRS;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.util.Map;

/**
 * Interface for writing geometry serialisation from internal representation.
 * @author Thomas Maroulis
 */
public interface GeometryWriter {
    
    /**
     * Write geometry serialisation.
     * @param geometry geometry
     * @param geometryCRS geometry CRS
     * @param objectVariableMap  geometry literal variable map
     * @return geometry literal variable map with geometry serialisation added
     */
    Map<String,Variable> writeGeometry(Geometry geometry, GeometryCRS geometryCRS, Map<String,Variable> objectVariableMap);
}