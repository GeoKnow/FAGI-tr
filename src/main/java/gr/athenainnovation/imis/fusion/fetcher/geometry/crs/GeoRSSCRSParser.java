package gr.athenainnovation.imis.fusion.fetcher.geometry.crs;

import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.util.Map;

/**
 * CRS parser for GeoRSS type geometry.
 */
public class GeoRSSCRSParser extends AbstractCRSParser {

    @Override
    public GeometryCRS parseCRS(Map<String,Variable> objectVariableMap) {
        return new GeometryCRS("EPSG", 4326);
    }
}

