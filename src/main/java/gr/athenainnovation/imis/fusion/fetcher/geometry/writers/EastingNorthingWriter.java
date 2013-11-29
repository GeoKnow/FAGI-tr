package gr.athenainnovation.imis.fusion.fetcher.geometry.writers;

import com.vividsolutions.jts.geom.Geometry;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GeometryCRS;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.util.Map;

/**
 * Geometry writer for easting/northing type geometry.
 * @author Thomas Maroulis
 */
public class EastingNorthingWriter implements GeometryWriter {

    @Override
    public Map<String, Variable> writeGeometry(Geometry geometry, GeometryCRS geometryCRS, Map<String, Variable> objectVariableMap) {
        // TODO Not implemented method: writeGeometry
        throw new UnsupportedOperationException("Not supported yet.");
    }
}