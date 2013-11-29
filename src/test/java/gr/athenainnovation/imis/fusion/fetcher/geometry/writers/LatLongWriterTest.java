package gr.athenainnovation.imis.fusion.fetcher.geometry.writers;

import com.google.common.base.Optional;
import com.vividsolutions.jts.geom.Geometry;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GeometryCRS;
import gr.athenainnovation.imis.fusion.fetcher.geometry.parsers.WKTParser;
import gr.athenainnovation.imis.fusion.fetcher.rules.RuleTriple;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Thomas Maroulis
 */
public class LatLongWriterTest {
    Geometry point;    
    String pointSerialisation = "POINT(4 6)";
    Map<String,Variable> variableMap = new HashMap<>();
    
    String expectedLat;
    String expectedLong;
    String expectedLatLong;
    
    @Before
    public void setUp() {
        Variable var = new Variable("?x" , RuleTriple.WKT);
        var.setContent(Optional.of(pointSerialisation));
        variableMap.put("?x", var);
        point = new WKTParser().parseGeometry(variableMap);
        
        expectedLat = "6.0";
        expectedLong = "4.0";
        expectedLatLong = "6.0 4.0";
    }

    @Test
    public void testGetLatLong() {
        System.out.println("LatLongWriter.getLatLong");
        LatLongWriter instance = new LatLongWriter();
        
        Map<String,Variable> outputVariableMap = new HashMap<>();
        outputVariableMap.put("?lat", new Variable("?lat", RuleTriple.LAT));
        outputVariableMap.put("?long", new Variable("?long", RuleTriple.LONG));
        outputVariableMap.put("?latlong", new Variable("?latlong", RuleTriple.LAT_LONG));
        
        outputVariableMap = instance.writeGeometry(point, new GeometryCRS("EPSG", 4326), outputVariableMap);
        
       assertEquals(expectedLat, outputVariableMap.get("?lat").getContent().get());
       assertEquals(expectedLong, outputVariableMap.get("?long").getContent().get());
       assertEquals(expectedLatLong, outputVariableMap.get("?latlong").getContent().get());
    }
}