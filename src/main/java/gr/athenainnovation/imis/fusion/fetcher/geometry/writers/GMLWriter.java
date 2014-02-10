package gr.athenainnovation.imis.fusion.fetcher.geometry.writers;

import com.google.common.base.Optional;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import com.vividsolutions.jts.geom.Geometry;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.CRSTranformer;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GeometryCRS;
import gr.athenainnovation.imis.fusion.fetcher.geometry.exceptions.GeometrySerialisationException;
import gr.athenainnovation.imis.fusion.fetcher.rules.RuleTriple;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.log4j.Logger;


/**
 * Geometry writer for GML type geometry.
 * @author Thomas Maroulis
 */
public class GMLWriter implements GeometryWriter {
    private static final Logger LOG = Logger.getLogger(GMLWriter.class);
    
    @Override
    public Map<String, Variable> writeGeometry(final Geometry geometry, final GeometryCRS geometryCRS, final Map<String, Variable> objectVariableMap) 
    throws GeometrySerialisationException {
        final Optional<String> serialisation = getSerialisation(Optional.of(geometry), geometryCRS);
        if(!serialisation.isPresent()) {
            throw new GeometrySerialisationException("Failed to serialise geometry to GML.");
        }
        
        for(Entry<String, Variable> varEntry : objectVariableMap.entrySet()) {
            if(RuleTriple.GML.equals(varEntry.getValue().getType())) {

                final Variable variable = varEntry.getValue();
                variable.setContent(serialisation);
                objectVariableMap.put(varEntry.getKey(), variable);
            }
            else {
                LOG.info("Found non GML type variable. Ignoring. Type found: " + varEntry.getValue().getType());
            }
        }
        
        return objectVariableMap;
    }
    
    private Optional<String> getSerialisation(final Optional<Geometry> geometry, final GeometryCRS geometryCRS) {
        Optional<String> serialisation;
        
        try {
            final StringWriter writer = new StringWriter();
            final JAXBContext context = JAXBContext.newInstance("org.jvnet.ogc.gml.v_3_1_1.jts");
            
            final Optional<Geometry> transformedGeometry = transformToCRS(geometry, geometryCRS);
            
            if(transformedGeometry.isPresent()) {
                final Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new GMLNamespacePrefixMapper());
                
                marshaller.marshal(transformedGeometry.get(), writer);
                serialisation = Optional.fromNullable(writer.toString());
            }
            else {
                serialisation = Optional.absent();
            }
        }
        catch(JAXBException ex) {
            LOG.warn(ex.getMessage(), ex);
            serialisation = Optional.absent();
        }
        
        return serialisation;
    }
    
    private Optional<Geometry> transformToCRS(final Optional<Geometry> geom, final GeometryCRS geometryCRS) {      
        return CRSTranformer.transformToGivenCRS(geom, geometryCRS);
    }
}
class GMLNamespacePrefixMapper extends NamespacePrefixMapper {
    
    private static final String GML_PREFIX = "gml";
    private static final String GML_URI = "http://www.opengis.net/gml";

    @Override
    public String getPreferredPrefix(final String namespaceURI, final String suggestion, final boolean requirePrefix) {
        String prefix;
        
        if(GML_URI.equals(namespaceURI)) {
            prefix = GML_PREFIX;
        }
        else {
            prefix = suggestion;
        }
        
        return prefix;
    }
    
    @Override
    public String[] getPreDeclaredNamespaceUris() {
        return new String[] { GML_URI, GML_URI };
    }
}