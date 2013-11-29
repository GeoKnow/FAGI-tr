package gr.athenainnovation.imis.fusion.fetcher.geometry;

import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GMLCRSParser;
import gr.athenainnovation.imis.fusion.fetcher.geometry.parsers.GMLParser;
import gr.athenainnovation.imis.fusion.fetcher.geometry.writers.GMLWriter;

/**
 * Processor for Geometry Markup Language (GML) format geometry.
 * @author Thomas Maroulis
 */
public class GMLProcessor extends GeometryProcessor {

    public GMLProcessor() {
        super();
        
        crsParser = new GMLCRSParser();
        geometryParser = new GMLParser();
        geometryWriter = new GMLWriter();
    }
}