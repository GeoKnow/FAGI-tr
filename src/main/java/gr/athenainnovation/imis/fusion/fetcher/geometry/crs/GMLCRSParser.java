package gr.athenainnovation.imis.fusion.fetcher.geometry.crs;

import com.google.common.base.Optional;
import gr.athenainnovation.imis.fusion.fetcher.geometry.exceptions.CRSParseException;
import gr.athenainnovation.imis.fusion.fetcher.rules.RuleTriple;
import gr.athenainnovation.imis.fusion.fetcher.rules.Variable;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * CRS parser for GML type geometry.
 * @author Thomas Maroulis
 */
public class GMLCRSParser extends AbstractCRSParser {
    private static final Logger LOG = Logger.getLogger(GMLCRSParser.class);
    
    @Override
    public GeometryCRS parseCRS(final Map<String,Variable> objectVariableMap) {
        final String crsString = extractCRSString(objectVariableMap);
        return parseCRSString(crsString);
    }
    
    private String extractCRSString(final Map<String,Variable> objectVariableMap) throws CRSParseException {
        Optional<String> gmlSerialisation = Optional.absent();
        
        for(Entry<String, Variable> varEntry : objectVariableMap.entrySet()) {
            if(RuleTriple.GML.equals(varEntry.getValue().getType())) {
                gmlSerialisation = varEntry.getValue().getContent();
            }
            else {
                LOG.info("Found non GML type variable. Ignoring. Type found: " + varEntry.getValue().getType());
            }
        }
        
        if(gmlSerialisation.isPresent()) {
            final String crsString = getSRSName(gmlSerialisation.get());
            
            return crsString;
        }
        throw new CRSParseException("Failed to parse CRS from GML geometry.");
    }
    
    private GeometryCRS parseCRSString(final String crsString) {
        final String localCRSString = crsString.trim();
        GeometryCRS geometryCRS = null;
        
        // EPSG family
        final String epsgPrefix[] = {"EPSG:", "urn:ogc:def:crs:EPSG::", "urn:ogc:def:crs:EPSG:1:", "urn:x-ogc:def:crs:EPSG::", "urn:x-ogc:def:crs:EPSG:1:",
            "http://www.opengis.net/gml/srs/epsg.xml#\\", "http://www.opengis.net/def/crs/epsg/0/"};
        
        for(String epsgAbbrev : epsgPrefix ) {
            if(localCRSString.startsWith(epsgAbbrev) && StringUtils.isNumeric(localCRSString.substring(epsgAbbrev.length()))) {
                geometryCRS = new GeometryCRS("EPSG", Integer.parseInt(localCRSString.substring(epsgAbbrev.length())));
            }
        }
        
        //Fallback
        if(geometryCRS == null) {
            geometryCRS = new GeometryCRS("EPSG", 4326, true);
        }
        
       return geometryCRS;
    }
    
    private String getSRSName(final String serialisation) {
        return StringUtils.substringBetween(serialisation, "srsName=\"", "\"");
    }    
}
