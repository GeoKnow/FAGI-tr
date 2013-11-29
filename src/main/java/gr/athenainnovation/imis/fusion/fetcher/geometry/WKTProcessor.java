package gr.athenainnovation.imis.fusion.fetcher.geometry;

import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.WKTCRSParser;
import gr.athenainnovation.imis.fusion.fetcher.geometry.parsers.WKTParser;
import gr.athenainnovation.imis.fusion.fetcher.geometry.writers.WKTWriter;

/**
 * Processor for WKT format geometry.
 * @author Thomas Maroulis
 */
public class WKTProcessor extends GeometryProcessor {
   
    public WKTProcessor() {
        super();
        
        crsParser = new WKTCRSParser();
        geometryParser = new WKTParser();
        geometryWriter = new WKTWriter();
    }
}