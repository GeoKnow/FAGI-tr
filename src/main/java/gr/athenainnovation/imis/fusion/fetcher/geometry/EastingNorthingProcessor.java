package gr.athenainnovation.imis.fusion.fetcher.geometry;

import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GeometryCRS;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.EastingNorthingCRSParser;
import gr.athenainnovation.imis.fusion.fetcher.geometry.parsers.EastingNorthingParser;
import gr.athenainnovation.imis.fusion.fetcher.geometry.writers.EastingNorthingWriter;

/**
 * Processor for Easting/Northing type geometry.
 * @author Thomas Maroulis
 */
public class EastingNorthingProcessor extends GeometryProcessor {
    
    public EastingNorthingProcessor() {
        super();
        
        crsParser = new EastingNorthingCRSParser();
        geometryParser = new EastingNorthingParser();
        geometryWriter = new EastingNorthingWriter();
        
        geometryCRSSet = true;
        geometryCRS = new GeometryCRS("EPSG", 27700);
    }
    
    @Override
    public void setGeometryCRS(final GeometryCRS geometryCRS) {
        if(!("EPSG".equals(geometryCRS.getAuthority()) && 27700 == geometryCRS.getSRID() && !geometryCRS.isLongitudeFirst())) {
            throw new IllegalArgumentException("Easting/Northing writer only supports EPSG:27700");
        }
    }
}