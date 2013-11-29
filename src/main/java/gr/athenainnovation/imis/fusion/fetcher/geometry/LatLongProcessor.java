package gr.athenainnovation.imis.fusion.fetcher.geometry;

import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GeometryCRS;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.LatLongCRSParser;
import gr.athenainnovation.imis.fusion.fetcher.geometry.parsers.LatLongParser;
import gr.athenainnovation.imis.fusion.fetcher.geometry.writers.LatLongWriter;

/**
 * Processor for Lat/Long format geometry.
 * @author Thomas Maroulis
 */
public class LatLongProcessor extends GeometryProcessor {
    
    public LatLongProcessor() {
        super();
        
        crsParser = new LatLongCRSParser();
        geometryParser = new LatLongParser();
        geometryWriter = new LatLongWriter();
        
        geometryCRSSet = true;
        geometryCRS = new GeometryCRS("EPSG", 4326);
    }
    
    @Override
    public void setGeometryCRS(final GeometryCRS geometryCRS) {
        if(!("EPSG".equals(geometryCRS.getAuthority()) && 4326 == geometryCRS.getSRID() && !geometryCRS.isLongitudeFirst())) {
            throw new IllegalArgumentException("Lat/Long writer only supports EPSG:4326");
        }
    }
}
