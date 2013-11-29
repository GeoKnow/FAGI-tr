package gr.athenainnovation.imis.fusion.fetcher.rules.parser;

import gr.athenainnovation.imis.fusion.fetcher.rules.Rule;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 *
 * @author Thomas Maroulis
 */
public class RuleParserTest {
    
    private static RuleConfigParser instance;
    
    @BeforeClass
    public static void setUp() {
        String file_class ="src/test/resources/class";
        String file_property = "src/test/resources/property";
        String file_object = "src/test/resources/object";
        String file_default = "src/test/resources/triple_default";
        String file_user = "src/test/resources/triple_user";
        instance = new RuleConfigParser();
        instance.readAllRules(file_class, file_property, file_object, file_default, file_user);
    }
    
    @Test
    @Ignore
    public void testReadClassRules() {
        System.out.println("RuleParser.readClassRules");
        
        Map<String,String> map = instance.getClassRules();
        
        String id = "c_geosparql_geometry";
        String regex = "{http://www.opengis.net/ont/geosparql#Geometry}";
        
        assertEquals(regex, map.get(id));
    }

    @Test
    @Ignore
    public void testReadPropertyRules() {
        System.out.println("RuleParser.readPropertyRules");
        
        Map<String,String> map = instance.getPropertyRules();
        
        String id = "p_geo";
        String regex = "\"^.*geo[^/]*$\"";
        
        assertEquals(regex, map.get(id));
    }

    @Test
    @Ignore
    public void testReadObjectRules() {
        System.out.println("RuleParser.readObjectRules");
        
        Map<String,String[]> map = instance.getObjectRules();
        
        String id = "o_wkt";
        String regex = "\".*(point|linestring|polygon|polyhedralsurface|triangle|tin|multipoint|multilinestring|multipolygon|geometrycollection)(\\\\szm|\\\\sz|\\\\sm|)\\\\s*\\\\(.*\\\\)\"";
        String datatype = "none";
        String type = "&wkt";
        
        assertNotNull(map.get(id));
        assertEquals(regex, map.get(id)[0]);
        assertEquals(datatype, map.get(id)[1]);
        assertEquals(type, map.get(id)[2]);
    }

    @Test
    @Ignore
    public void testReadCombinedRules() {
        String test1_id = "k_w3c_lat";
        String test1_subject = "?x";
        String test1_predicate = "{http://www.w3.org/2003/01/geo/wgs84_pos#lat}";
        String test1_object = "\"([0-9]|\\\\.)+\"";
        String test1_datatype = "none";
        String test1_type = "&lat";
        
        Rule rule1 = instance.getTripleRules().get(test1_id);
        
        assertEquals(test1_subject, rule1.getTriples()[0].getSubject());
        assertEquals(test1_predicate, rule1.getTriples()[0].getPredicate());
        assertEquals(test1_object, rule1.getTriples()[0].getObject());
        assertEquals(test1_datatype, rule1.getTriples()[0].getDatatype());
        assertEquals(test1_type, rule1.getTriples()[0].getType());
        
        String test2_id = "k_geosparql_wkt";
        String test2_subject = "?x";
        String test2_predicate = "\"(^http://www.opengis.net/ont/geosparql#asWKT$|^http://www.opengis.net/ont/geosparql#hasSerialization$)\"";
        String test2_object = "\".*(point|linestring|polygon|polyhedralsurface|triangle|tin|multipoint|multilinestring|multipolygon|geometrycollection)(\\\\szm|\\\\sz|\\\\sm|)\\\\s*\\\\(.*\\\\)\"";
        String test2_datatype = "none";
        String test2_type = "&wkt";
        
        Rule rule2 = instance.getTripleRules().get(test2_id);
        
        assertEquals(test2_subject, rule2.getTriples()[0].getSubject());
        assertEquals(test2_predicate, rule2.getTriples()[0].getPredicate());
        assertEquals(test2_object,rule2.getTriples()[0].getObject());
        assertEquals(test2_datatype, rule2.getTriples()[0].getDatatype());
        assertEquals(test2_type, rule2.getTriples()[0].getType());
        
        String test3_id = "k_w3c_loc1";
        
        String test3_subject[] = {"?x", "_:a", "_:a", "_:a"};
        String test3_predicate[] = {"{http://www.w3.org/2003/01/geo/wgs84_pos#location}", "{http://www.w3.org/2003/01/geo/wgs84_pos#lat}",
            "{http://www.w3.org/2003/01/geo/wgs84_pos#long}", "{http://www.w3.org/2003/01/geo/wgs84_pos#alt}"};
        String test3_object[] = {"_:a", "\"([0-9]|\\\\.)+\"", "\"([0-9]|\\\\.)+\"", "\"([0-9]|\\\\.)+\""};
        String test3_datatype[] ={"none", "none", "none", "none"};
        String test3_type[] = {"", "&lat", "&long", "&alt"};
        
        Rule rule3 = instance.getTripleRules().get(test3_id);
        
        for(int i = 0; i < 4; i++) {
            assertEquals(test3_subject[i], rule3.getTriples()[i].getSubject());
            assertEquals(test3_predicate[i], rule3.getTriples()[i].getPredicate());
            assertEquals(test3_object[i], rule3.getTriples()[i].getObject());
            assertEquals(test3_datatype[i], rule3.getTriples()[i].getDatatype());
            assertEquals(test3_type[i], rule3.getTriples()[i].getType());
        }
        
        String test4_id = "k_w3c_ns";
        String test4_subject = "?x";
        String test4_predicate = "\"^http://www.w3.org/2003/01/geo/wgs84_pos#[a-z]+$\"";
        String test4_object = "\".*\"";
        String test4_postProc = "none";
        
        Rule rule4 = instance.getTripleRules().get(test4_id);
        
        assertEquals(test4_subject, rule4.getTriples()[0].getSubject());
        assertEquals(test4_predicate, rule4.getTriples()[0].getPredicate());
        assertEquals(test4_object, rule4.getTriples()[0].getObject());
        assertEquals(test4_postProc, rule4.getTriples()[0].getDatatype());
    }
}