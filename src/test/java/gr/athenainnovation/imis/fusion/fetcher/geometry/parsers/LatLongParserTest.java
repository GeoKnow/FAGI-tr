package gr.athenainnovation.imis.fusion.fetcher.geometry.parsers;

import com.google.common.base.Optional;
import com.vividsolutions.jts.geom.Geometry;
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
public class LatLongParserTest {

    /**
     * Test of parse method, of class LatLongParser.
     */
    @Test
    public void testParse() {
        System.out.println("parse");
        double latitude = 6.0;
        double longitude = 4.0;
        
        Map<String,Variable> variableMap1 = new HashMap<>();
        Variable var1X = new Variable("?x", RuleTriple.LAT_LONG);
        var1X.setContent(Optional.of(latitude + " " + longitude));
        variableMap1.put("?x", var1X);
        
        Map<String,Variable> variableMap2 = new HashMap<>();
        Variable var2X = new Variable("?x", RuleTriple.LAT);
        var2X.setContent(Optional.of(latitude + ""));
        Variable var2Y = new Variable("?y", RuleTriple.LONG);
        var2Y.setContent(Optional.of(longitude + ""));
        variableMap2.put("?x", var2X);
        variableMap2.put("?y", var2Y);
        
        double expectedLat = 6.0;
        double expectedLong = 4.0;
        
        LatLongParser parser = new LatLongParser();
        
        Geometry result1 = parser.parseGeometry(variableMap1);
        assertEquals(expectedLat, result1.getCoordinate().x, 0);
        assertEquals(expectedLong, result1.getCoordinate().y, 0);
        
        Geometry result2 = parser.parseGeometry(variableMap2);
        assertEquals(expectedLat, result2.getCoordinate().x, 0);
        assertEquals(expectedLong, result2.getCoordinate().y, 0);
    }
}