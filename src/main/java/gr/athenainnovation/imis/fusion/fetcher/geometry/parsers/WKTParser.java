package gr.athenainnovation.imis.fusion.fetcher.geometry.parsers;

import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.CRSTranformer;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GeometryCRS;
import com.google.common.base.Optional;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.WKTCRSParser;
import gr.athenainnovation.imis.fusion.fetcher.geometry.exceptions.GeometryParseException;
import gr.athenainnovation.imis.fusion.fetcher.rules.RuleTriple;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;

/**
 * Geometry parser for WKT type geometry.
 * @author Thomas Maroulis
 */
public class WKTParser implements GeometryParser{
    private static final Logger LOG = Logger.getLogger(WKTParser.class);
    
    @Override
    public Geometry parseGeometry(final Map<String,Variable> objectVariableMap) throws GeometryParseException {
        Optional<String> wktSerialisation = Optional.absent();
        
        for(Entry<String, Variable> varEntry : objectVariableMap.entrySet()) {
            if(RuleTriple.WKT.equals(varEntry.getValue().getType())) {
                wktSerialisation = varEntry.getValue().getContent();
            }
            else {
                LOG.info("Found non WKT type variable. Ignoring. Type found: " + varEntry.getValue().getType());
            }
        }
        
        if(wktSerialisation.isPresent()) {
            final GeometryCRS geometryCRS = new WKTCRSParser().parseCRS(objectVariableMap);
            final Optional<Geometry> parsedGeom = parse(wktSerialisation.get(), geometryCRS);
            if(parsedGeom.isPresent()) {
                return parsedGeom.get();
            }
        }
        throw new GeometryParseException("Failed to parse WKT geometry.");
    }
    
    private Optional<Geometry> parse(final String serialisation, final GeometryCRS geometryCRS) {
        final String localSerialisation = serialisation.trim();
        String wktString;
        
        if(localSerialisation.charAt(0) == '<' && localSerialisation.indexOf('>') != -1) {
            wktString = localSerialisation.substring(localSerialisation.indexOf('>') + 1);
        }
        else {
            wktString = localSerialisation;
        }
        
        final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), geometryCRS.getSRID());
        final WKTReader reader = new WKTReader(geometryFactory);
        
        Optional<Geometry> geometry;
        
        try {
            final Optional<Geometry> parsedGeom = Optional.fromNullable(reader.read(wktString));
            geometry = CRSTranformer.transformToInternalCRS(parsedGeom, geometryCRS);
        }
        catch (ParseException ex) {
            LOG.warn(ex.getMessage(), ex);
            geometry = Optional.absent();
        }
        
        if(!geometry.isPresent()) {
            LOG.warn("Failed to parse WKT: " + wktString);
        }
        
        return geometry;
    }
}