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
public class EastingNorthingParserTest {
    
    public EastingNorthingParserTest() {
    }

    /**
     * Test of parse method, of class EastingNorthingParser.
     */
    @Test
    public void testParse() {
        System.out.println("EastingNorthingParser.parse");
        
        System.out.println("    -- Testing London");
        double easting = 529613.8;
        double northing = 178392.3;
        double latitude = 51.489632;
        double longitude = -0.134505;
        
        Map<String,Variable> variableMap = new HashMap<>();
        Variable varX = new Variable("?x", RuleTriple.EASTING);
        varX.setContent(Optional.of(easting + ""));
        Variable varY = new Variable("?y", RuleTriple.NORTHING);
        varY.setContent(Optional.of(northing + ""));
        variableMap.put("?x", varX);
        variableMap.put("?y", varY);
        
        double maxDelta = 0.001;
        
        EastingNorthingParser parser = new EastingNorthingParser();
        
        Geometry result = parser.parseGeometry(variableMap);
        assertEquals(latitude, result.getCoordinate().x, maxDelta);
        assertEquals(longitude, result.getCoordinate().y, maxDelta);
    }
}