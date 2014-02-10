package gr.athenainnovation.imis.fusion.fetcher.geometry.writers;

import com.google.common.base.Optional;
import com.vividsolutions.jts.geom.Geometry;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.CRSTranformer;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GeometryCRS;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.WKTCRSParser;
import gr.athenainnovation.imis.fusion.fetcher.geometry.exceptions.GeometrySerialisationException;
import gr.athenainnovation.imis.fusion.fetcher.rules.RuleTriple;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;

/**
 * Geometry writer for WKT type geometry.
 * @author Thomas Maroulis
 */
public class WKTWriter implements GeometryWriter {
    private static final Logger LOG = Logger.getLogger(WKTWriter.class);
    
    @Override
    public Map<String, Variable> writeGeometry(final Geometry geometry, final GeometryCRS geometryCRS, final Map<String, Variable> objectVariableMap) throws GeometrySerialisationException{
        final Optional<String> serialisation = getSerialisation(Optional.of(geometry), geometryCRS);
        if(!serialisation.isPresent()) {
            throw new GeometrySerialisationException("Failed to serialise geometry to WKT.");
        }
        //System.out.println("WKT SERIALIZATION:     " + serialisation);
        for(Entry<String, Variable> varEntry : objectVariableMap.entrySet()) {
            if(RuleTriple.WKT.equals(varEntry.getValue().getType())) {
                final Variable variable = varEntry.getValue();
                variable.setContent(serialisation);
                objectVariableMap.put(varEntry.getKey(), variable);
            }
            else {
                LOG.info("Found non WKT type variable. Ignoring. Type found: " + varEntry.getValue().getType());
            }
        }
        
        return objectVariableMap;
    }
    
    private Optional<String> getSerialisation(final Optional<Geometry> geometry, final GeometryCRS geometryCRS) {
        final com.vividsolutions.jts.io.WKTWriter wktWriter = new com.vividsolutions.jts.io.WKTWriter();
        Optional<String> serialisation;
        
        final Optional<Geometry> transformedGeometry = transformToCRS(geometry, geometryCRS);
        
        if(transformedGeometry.isPresent()) {
            String serialisationString = wktWriter.write(transformedGeometry.get());
            
            final Optional<String> crsURI = new WKTCRSParser().getCRSUri(geometryCRS);
            if(crsURI.isPresent() && !"".equals(crsURI.get())) {
                serialisationString = "<" + crsURI.get() +">" + serialisationString;
            }
            
            serialisation = Optional.of(serialisationString);
        }
        else {
            serialisation = Optional.absent();
        }
        
        return serialisation;
    }
    
    private Optional<Geometry> transformToCRS(final Optional<Geometry> geom, final GeometryCRS geometryCRS) {      
        return CRSTranformer.transformToGivenCRS(geom, geometryCRS);
    }
}