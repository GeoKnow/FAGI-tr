package gr.athenainnovation.imis.fusion.fetcher.geometry.writers;

import gr.athenainnovation.imis.fusion.fetcher.geometry.writers.GMLWriter;
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
public class GMLWriterTest {
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
    public void testGetSerialisation4326() {
        System.out.println("GMLWriter.getSerialisation4326");
        
        GMLWriter instance = new GMLWriter();
        Map<String,Variable> outputVariableMap = new HashMap<>();
        Optional<String> content = Optional.absent();
        Variable var = new Variable("?x", RuleTriple.GML);
        var.setContent(content);
        outputVariableMap.put("?x", var);
        
        outputVariableMap = instance.writeGeometry(point, new GeometryCRS("EPSG", 4326), outputVariableMap);
        String expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<gml:Point srsName=\"urn:ogc:def:crs:EPSG::4326\" xmlns:ns2=\"http://www.w3.org/1999/xlink\" "
                + "xmlns:ns4=\"http://www.w3.org/2001/SMIL20/Language\" xmlns:ns3=\"http://www.w3.org/2001/SMIL20/\" xmlns:gml=\"http://www.opengis.net/gml\">"
                + "<gml:pos>6.0 4.0</gml:pos></gml:Point>";
        
        assertEquals(expectedResult, outputVariableMap.get("?x").getContent().get());
    }
}