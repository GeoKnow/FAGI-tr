package gr.athenainnovation.imis.fusion.fetcher.geometry.crs;

import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.util.Map;

/**
 * CRS parser for easting/northing type geometry.
 * @author Thomas Maroulis
 */
public class EastingNorthingCRSParser extends AbstractCRSParser {

    @Override
    public GeometryCRS parseCRS(final Map<String, Variable> objectVariableMap) {
        return new GeometryCRS("EPSG", 27700);
    }
}