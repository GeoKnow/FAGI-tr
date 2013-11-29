package gr.athenainnovation.imis.fusion.fetcher.geometry.crs;

import com.google.common.base.Optional;
import gr.athenainnovation.imis.fusion.fetcher.rules.RuleTriple;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Thomas Maroulis
 */
public class LatLongCRSParserTest {
    
    public LatLongCRSParserTest() {
    }

    /**
     * Test of parseCRS method, of class LatLongCRSParser.
     */
    @Test
    public void testParseCRS() {
        System.out.println("LatLongCRSParser.parseCRS");
        double latitude = 6.0;
        double longitude = 4.0;
        Map<String,Variable> variableMap = new HashMap<>();
        Variable varX = new Variable("?x", RuleTriple.LAT);
        varX.setContent(Optional.of(latitude + ""));
        Variable varY = new Variable("?y", RuleTriple.LONG);
        varY.setContent(Optional.of(longitude + ""));
        variableMap.put("?x", varX);
        variableMap.put("?y", varY);
        
        LatLongCRSParser parser = new LatLongCRSParser();
        
        GeometryCRS expectedCRS = new GeometryCRS("EPSG", 4326);
        GeometryCRS result = parser.parseCRS(variableMap);
        
        assertEquals(expectedCRS.getAuthority(), result.getAuthority());
        assertEquals(expectedCRS.getSRID(), result.getSRID());
        assertEquals(expectedCRS.isLongitudeFirst(), result.isLongitudeFirst());
    }
}