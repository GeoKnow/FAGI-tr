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
public class GMLParserTest {

    /**
     * Test of parse method, of class GMLParser.
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("GMLParser.parse");
        String serialisation = "<gml:Point srsName=\"EPSG:4326\"><gml:coordinates>6.0,4.0</gml:coordinates></gml:Point>";
        Map<String,Variable> variableMap = new HashMap<>();
        Variable var = new Variable("?x", RuleTriple.GML);
        var.setContent(Optional.of(serialisation));
        variableMap.put("?x", var);
        double longitude = 4.0;
        double latitude =6.0;
        
        GMLParser parser = new GMLParser();
        
        System.out.println("    -- Testing with 4326");
        Geometry result = parser.parseGeometry(variableMap);
        assertEquals(latitude, result.getCoordinate().x, 0);
        assertEquals(longitude, result.getCoordinate().y, 0);
    }
}