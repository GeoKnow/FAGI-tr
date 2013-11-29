package gr.athenainnovation.imis.fusion.fetcher.geometry.crs;

import com.google.common.base.Optional;
import static gr.athenainnovation.imis.fusion.fetcher.geometry.crs.AbstractCRSParser.CRS84;
import gr.athenainnovation.imis.fusion.fetcher.geometry.exceptions.CRSParseException;
import gr.athenainnovation.imis.fusion.fetcher.rules.RuleTriple;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * CRS parser for WKT type geometry
 * @author Thomas Maroulis
 */
public class WKTCRSParser extends AbstractCRSParser {
    private static final Logger LOG = Logger.getLogger(WKTCRSParser.class);
    
    @Override
    public GeometryCRS parseCRS(final Map<String, Variable> objectVariableMap) {
        final String crsString = extractCRSString(objectVariableMap);
        return parseCRSString(crsString);
    }
    
    private String extractCRSString(final Map<String,Variable> objectVariableMap) throws CRSParseException {
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
            String crsString;
            if(wktSerialisation.get().startsWith("<") && wktSerialisation.get().indexOf(">") != -1) {
                crsString = wktSerialisation.get().substring(1, wktSerialisation.get().indexOf(">"));
            }
            else {
                crsString = AbstractCRSParser.CRS84;
            }
            
            return crsString;
        }
        throw new CRSParseException("Failed to parse CRS from WKT geometry.");
    }
    
    private GeometryCRS parseCRSString(final String crsString) {
        final String localCRSString = crsString.trim();
        
        GeometryCRS geometryCRS;
        
        // CRS84
        if(CRS84.equals(localCRSString)) {
            geometryCRS = new GeometryCRS("EPSG", 4326, true);
        }
        else {
            // EPSG family
            final String epsgPrefix = "http://www.opengis.net/def/crs/epsg/0/";
            if(localCRSString.startsWith(epsgPrefix) && StringUtils.isNumeric(localCRSString.substring(epsgPrefix.length()))) {
                geometryCRS = new GeometryCRS("EPSG", Integer.parseInt(localCRSString.substring(epsgPrefix.length())));
            }
            else {
                // Fallback
                geometryCRS = new GeometryCRS("EPSG", 4326, true);
            }
        }
        
        return geometryCRS;
    }
}
