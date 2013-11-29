package gr.athenainnovation.imis.fusion.fetcher.geometry.crs;

import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.WKTCRSParser;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GeometryCRS;
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
public class WKTCRSParserTest {
    
    public WKTCRSParserTest() {
    }

    /**
     * Test of parseCRS method, of class WKTCRSParser.
     */
    @Test
    public void testParseCRS() {
        System.out.println("WKTCRSParser.parseCRS");
        String serialisation = "<http://www.opengis.net/def/crs/epsg/0/4326>POINT(6 4)";
        Map<String,Variable> variableMap = new HashMap<>();
        Variable var = new Variable("?x", RuleTriple.WKT);
        var.setContent(Optional.of(serialisation));
        variableMap.put("?x", var);
        
        WKTCRSParser parser = new WKTCRSParser();
        
        GeometryCRS expectedCRS = new GeometryCRS("EPSG", 4326);
        GeometryCRS result = parser.parseCRS(variableMap);
        
        assertEquals(expectedCRS.getAuthority(), result.getAuthority());
        assertEquals(expectedCRS.getSRID(), result.getSRID());
        assertEquals(expectedCRS.isLongitudeFirst(), result.isLongitudeFirst());
    }
}