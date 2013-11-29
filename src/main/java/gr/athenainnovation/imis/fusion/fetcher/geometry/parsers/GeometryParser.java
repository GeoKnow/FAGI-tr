package gr.athenainnovation.imis.fusion.fetcher.geometry.parsers;

import com.vividsolutions.jts.geom.Geometry;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.util.Map;

/**
 * Interface for parsing geometry to internal reprsentation.
 * @author Thomas Maroulis
 */
public interface GeometryParser {
    
    /**
     * Parses geometry from given geometry literals.
     * @param objectVariableMap geometry literal variable map
     * @return geometry
     */
    Geometry parseGeometry(Map<String,Variable> objectVariableMap);
}