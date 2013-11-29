package gr.athenainnovation.imis.fusion.fetcher.geometry.parsers;

import gr.athenainnovation.imis.fusion.fetcher.geometry.parsers.WKTParser;
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
public class WKTParserTest {

    /**
     * Test of parse method, of class WKTParser.
     */
    @Test
    public void testParse() {
        System.out.println("WKTParser.parse");
        String serialisation1 = "POINT(4 6)";
        String serialisation2 = "<http://www.opengis.net/def/crs/OGC/1.3/CRS84>POINT(4 6)";
        String serialisation3 = "<http://www.opengis.net/def/crs/epsg/0/4326>POINT(6 4)";
        
        Variable var1 = new Variable("?x", RuleTriple.WKT);
        var1.setContent(Optional.of(serialisation1));
        Variable var2 = new Variable("?x", RuleTriple.WKT);
        var2.setContent(Optional.of(serialisation2));
        Variable var3 = new Variable("?x", RuleTriple.WKT);
        var3.setContent(Optional.of(serialisation3));
        
        Map<String,Variable> variableMap1 = new HashMap<>();
        variableMap1.put("?x", var1);
        Map<String,Variable> variableMap2 = new HashMap<>();
        variableMap2.put("?x", var2);
        Map<String,Variable> variableMap3 = new HashMap<>();
        variableMap3.put("?x", var3);
        double longitude = 4.0;
        double latitude = 6.0;
        
        WKTParser parser = new WKTParser();
        
        System.out.println("    -- Testing with no CRS");
        Geometry result1 = parser.parseGeometry(variableMap1);
        assertEquals(latitude, result1.getCoordinate().x, 0);
        assertEquals(longitude, result1.getCoordinate().y, 0);
        
        System.out.println("    -- Testing with CRS84");
        Geometry result2 = parser.parseGeometry(variableMap2);
        assertEquals(latitude, result2.getCoordinate().x, 0);
        assertEquals(longitude, result2.getCoordinate().y, 0);
        
        System.out.println("    -- Testing with 4326");
        Geometry result3 = parser.parseGeometry(variableMap3);
        assertEquals(latitude, result3.getCoordinate().x, 0);
        assertEquals(longitude, result3.getCoordinate().y, 0);
    }
}