package gr.athenainnovation.imis.fusion.fetcher.geometry;

import com.vividsolutions.jts.geom.Geometry;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.AbstractCRSParser;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GeometryCRS;
import gr.athenainnovation.imis.fusion.fetcher.geometry.parsers.GeometryParser;
import gr.athenainnovation.imis.fusion.fetcher.geometry.writers.GeometryWriter;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.util.Map;

/**
 * Container object for providing geometry processing functionality. This class cannot be instantiated and should be extended for each geometry type.
 * @author Thomas Maroulis
 */
public class GeometryProcessor {

    protected AbstractCRSParser crsParser;
    protected GeometryParser geometryParser;
    protected GeometryWriter geometryWriter;
    
    protected boolean geometryCRSSet = false;
    protected GeometryCRS geometryCRS;
    
    protected GeometryProcessor() {
        //Constructor is protected to defeat direct instantiation.
    }

    /**
     * Parses Coordinate Reference System (CRS) from given geometry literals.
     * @param objectVariableMap geometry literal variable map
     * @return geometry CRS
     */
    public GeometryCRS parseCRS(final Map<String, Variable> objectVariableMap) {
        return crsParser.parseCRS(objectVariableMap);
    }

    /**
     * Parses geometry from given geometry literals.
     * @param objectVariableMap geometry literal variable map
     * @return geometry
     */
    public Geometry parseGeometry(final Map<String, Variable> objectVariableMap) {
        return geometryParser.parseGeometry(objectVariableMap);
    }

    /**
     * Writes serialisation of geometry.
     * @param geometry geometry
     * @param objectVariableMap geometry literal variable map
     * @return geometry literal variable map with geometry serialisation added
     * @throws IllegalStateException if no CRS has been specified
     */
    public Map<String, Variable> writeGeometry(final Geometry geometry, final Map<String, Variable> objectVariableMap) throws IllegalStateException {
        if(geometryCRSSet) {
            return geometryWriter.writeGeometry(geometry, geometryCRS, objectVariableMap);
        }
        else {
            throw new IllegalStateException("CRS not specified for writer.");
        }
    }
    
    /**
     *
     * @return true if CRS has been set for this writer, false otherwise
     */
    public boolean isCRSSet() {
        return geometryCRSSet;
    }
    
    /**
     * Sets geometry CRS for this writer.
     * @param geometryCRS geometry CRS
     */
    public void setGeometryCRS(final GeometryCRS geometryCRS) {
        this.geometryCRS = geometryCRS;
        geometryCRSSet = true;
    }
    
    /**
     *
     * @return geometry CRS
     */
    public GeometryCRS getGeometryCRS() {
        return geometryCRS;
    }
}