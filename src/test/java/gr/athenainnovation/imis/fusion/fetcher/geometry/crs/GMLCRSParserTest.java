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
public class GMLCRSParserTest {
    
    public GMLCRSParserTest() {
    }

    /**
     * Test of parseCRS method, of class GMLCRSParser.
     */
    @Test
    public void testParseCRS() {
        System.out.println("GMLCRSParser.parseCRS");
        String serialisation = "<gml:Point srsName=\"EPSG:4326\"><gml:coordinates>6.0,4.0</gml:coordinates></gml:Point>";
        Map<String,Variable> variableMap = new HashMap<>();
        Optional<String> content = Optional.of(serialisation);
        Variable var = new Variable("?x", RuleTriple.GML);
        var.setContent(content);
        variableMap.put("?x", var);
        
        GMLCRSParser parser = new GMLCRSParser();
        
        GeometryCRS expectedCRS = new GeometryCRS("EPSG", 4326);
        GeometryCRS result = parser.parseCRS(variableMap);
        
        assertEquals(expectedCRS.getAuthority(), result.getAuthority());
        assertEquals(expectedCRS.getSRID(), result.getSRID());
        assertEquals(expectedCRS.isLongitudeFirst(), result.isLongitudeFirst());
    }
}