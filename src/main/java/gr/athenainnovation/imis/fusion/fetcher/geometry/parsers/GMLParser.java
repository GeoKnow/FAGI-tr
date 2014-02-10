package gr.athenainnovation.imis.fusion.fetcher.geometry.parsers;

import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.CRSTranformer;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GeometryCRS;
import com.google.common.base.Optional;
import com.vividsolutions.jts.geom.Geometry;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GMLCRSParser;
import gr.athenainnovation.imis.fusion.fetcher.geometry.exceptions.GeometryParseException;
import gr.athenainnovation.imis.fusion.fetcher.rules.RuleTriple;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.io.StringReader;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.log4j.Logger;

/**
 * Geometry parser for GML type geometry.
 * @author Thomas Maroulis
 */
public class GMLParser implements GeometryParser {
    private static final Logger LOG = Logger.getLogger(GMLParser.class);
    
    @Override
    public Geometry parseGeometry(final Map<String,Variable> objectVariableMap) throws GeometryParseException {
        Optional<String> gmlSerialisation = Optional.absent();
        
        for(Entry<String, Variable> varEntry: objectVariableMap.entrySet()) {
            if(RuleTriple.GML.equals(varEntry.getValue().getType())) {
                gmlSerialisation = varEntry.getValue().getContent();

            }
            else {
                LOG.info("Found non GML type variable. Ignoring. Type found: " + varEntry.getValue().getType());
            }
        }
        
        if(gmlSerialisation.isPresent()) {
            final GeometryCRS geometryCRS = new GMLCRSParser().parseCRS(objectVariableMap);
            final Optional<Geometry> parsedGeom = parse(gmlSerialisation.get(), geometryCRS);
            if(parsedGeom.isPresent()) {
                return parsedGeom.get();
            }
        }
        throw new GeometryParseException("Failed to parse GML geometry.");
    }
    
    private Optional<Geometry> parse(final String serialisation, final GeometryCRS geometryCRS) {
        String localSerialisation = serialisation.trim();
        
        Optional<Geometry> geometry;
        
        try {            
            final JAXBContext context = JAXBContext.newInstance("org.jvnet.ogc.gml.v_3_1_1.jts");
            localSerialisation = addXMLNS(localSerialisation);
            final Optional<Geometry> parsedGeom = Optional.fromNullable((Geometry) context.createUnmarshaller().unmarshal(new StringReader(localSerialisation)));
            geometry = CRSTranformer.transformToInternalCRS(parsedGeom, geometryCRS);
        }
        catch (JAXBException ex) {
            LOG.warn(ex.getMessage(), ex);
            geometry = Optional.absent();
        }
        if(!geometry.isPresent()) {
            LOG.warn("Failed to parse GML: " + localSerialisation);
        }
        
        return geometry;
    }
    
    // This works, but it's a hack
    private String addXMLNS(final String serialisation) {
        final String xmlns = "xmlns:gml=\"http://www.opengis.net/gml\"";
        
        String localSerialisation = serialisation.trim();
        
        final int indexOfFirstGMLStart = localSerialisation.indexOf("<gml:");
        final int indexOfFirstGMLEnd = localSerialisation.indexOf('>', indexOfFirstGMLStart);
        
        if(!localSerialisation.substring(indexOfFirstGMLStart, indexOfFirstGMLEnd).contains(xmlns)) {
            localSerialisation = localSerialisation.substring(0, indexOfFirstGMLEnd) + " " + xmlns + localSerialisation.substring(indexOfFirstGMLEnd);
        }

        return localSerialisation;
    }
}