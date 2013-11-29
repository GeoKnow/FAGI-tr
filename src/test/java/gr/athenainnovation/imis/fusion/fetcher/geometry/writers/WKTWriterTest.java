package gr.athenainnovation.imis.fusion.fetcher.geometry.writers;

import com.google.common.base.Optional;
import com.vividsolutions.jts.geom.Geometry;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GeometryCRS;
import gr.athenainnovation.imis.fusion.fetcher.geometry.parsers.WKTParser;
import gr.athenainnovation.imis.fusion.fetcher.rules.RuleTriple;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Thomas Maroulis
 */
public class WKTWriterTest {
    Geometry point;    
    String pointSerialisation = "POINT(4 6)";
    Map<String,Variable> variableMap = new HashMap<>();
        
    @Before
    public void setUp() {
        Variable var = new Variable("?x", RuleTriple.WKT);
        var.setContent(Optional.of(pointSerialisation));
        variableMap.put("?x", var);
        point = new WKTParser().parseGeometry(variableMap);
    }

    @Test
    public void testGetSerialisationPointCRS84() {
        System.out.println("WKTWriter.getSerialisationPointCRS84");
        
        String expectedResult = "POINT (4 6)";
        
        Map<String,Variable> outputVariableMap = new HashMap<>();
        outputVariableMap.put("?x", new Variable("?x", RuleTriple.WKT));
        
        WKTWriter instance = new WKTWriter();
        outputVariableMap = instance.writeGeometry(point, new GeometryCRS("EPSG", 4326, true), outputVariableMap);
        
        assertEquals(expectedResult, outputVariableMap.get("?x").getContent().get());
    }
    
    @Test
    public void testGetSerialisationPoint4326() {
        System.out.println("WKTWriter.getSerialisationPoint4326");
        
        String expectedResult = "<http://www.opengis.net/def/crs/epsg/0/4326>POINT (6 4)";
        
        Map<String,Variable> outputVariableMap = new HashMap<>();
        outputVariableMap.put("?x", new Variable("?x", RuleTriple.WKT));
        
        WKTWriter instance = new WKTWriter();
        outputVariableMap = instance.writeGeometry(point, new GeometryCRS("EPSG", 4326), outputVariableMap);
        
        assertEquals(expectedResult, outputVariableMap.get("?x").getContent().get());
    }
}