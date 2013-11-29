package gr.athenainnovation.imis.fusion.fetcher.geometry.crs;

import com.google.common.base.Optional;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.util.Map;

/**
 * Abstract class providing CRS parsing functionality.
 * @author Thomas Maroulis
 */
 public abstract class AbstractCRSParser {
     
     /**
      * URI for CRS84.
      */
    public static final String CRS84 ="http://www.opengis.net/def/crs/OGC/1.3/CRS84";
    
    /**
     *  Parse Parses Coordinate Reference System (CRS) from given geometry literals.
     * @param objectVariableMap geometry literal variable map
     * @return geometry CRS
     */
    public abstract GeometryCRS parseCRS(Map<String,Variable> objectVariableMap);
    
    /**
     * Get CRS URI.
     * @param geometryCRS geometry CRS
     * @return geometry CRS URI
     */
    public Optional<String> getCRSUri(final GeometryCRS geometryCRS) {
        Optional<String> output;
        
        if("EPSG".equals(geometryCRS.getAuthority())) {
            if(geometryCRS.getSRID() == 4326 && geometryCRS.isLongitudeFirst()) {
                output = Optional.of("");
            }
            else {
                output = Optional.of("http://www.opengis.net/def/crs/epsg/0/" + geometryCRS.getSRID());
            }
        }
        else {
            output = Optional.absent();
        }
        
        return output;
    }
}
