package gr.athenainnovation.imis.fusion.fetcher.geometry;

import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GeometryCRS;
import gr.athenainnovation.imis.fusion.fetcher.geometry.parsers.GeoRSSParser;


/**
 * Processor for GeoRSS format geometry.
 */
public class GeoRSSProcessor extends GeometryProcessor {
   
    public GeoRSSProcessor() {       
        super();
        //crsParser = new GeoRSSCRSParser();
        geometryParser = new GeoRSSParser();
        //geometryWriter = new GeoRSSWriter();
        geometryCRSSet = true;
        geometryCRS = new GeometryCRS("EPSG", 4326);
    }
    
    @Override
    public void setGeometryCRS(final GeometryCRS geometryCRS) {
        if(!("EPSG".equals(geometryCRS.getAuthority()) && 4326 == geometryCRS.getSRID() && !geometryCRS.isLongitudeFirst())) {
            throw new IllegalArgumentException("GeoRSS EPSG:4326");
        }
    }
    
}