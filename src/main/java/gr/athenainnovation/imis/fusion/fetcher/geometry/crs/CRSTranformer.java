package gr.athenainnovation.imis.fusion.fetcher.geometry.crs;

import com.google.common.base.Optional;
import com.vividsolutions.jts.geom.Geometry;
import org.apache.log4j.Logger;
import org.geotools.geometry.GeneralDirectPosition;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.operation.DefaultCoordinateOperationFactory;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Provides functionality for transforming geometry from one CRS to another.
 * @author Thomas Maroulis
 */
public class CRSTranformer {
    private static final Logger log = Logger.getLogger(CRSTranformer.class);
    
    private static final GeometryCRS INTERNAL_CODE_CRS = new GeometryCRS("EPSG", 4326);
    
    /**
     * Transform to internal CRS.
     * @param geometry geometry
     * @param geometryCRS source geometryCRS
     * @return transformed geometry
     */
    public static Optional<Geometry> transformToInternalCRS(Optional<Geometry> geometry, GeometryCRS geometryCRS) {
        return transform(geometry, geometryCRS, INTERNAL_CODE_CRS);
    }
    
    /**
     * Transform to given CRS
     * @param geometry geometry
     * @param geometryCRS target geometryCRS
     * @return transformed geometry
     */
    public static Optional<Geometry> transformToGivenCRS(Optional<Geometry> geometry, GeometryCRS geometryCRS) {
        return transform(geometry, INTERNAL_CODE_CRS, geometryCRS);
    }
    
    private static Optional<Geometry> transform(Optional<Geometry> geometry, GeometryCRS sourceGeometryCRS, GeometryCRS targetGeometryCRS) {
        if (!geometry.isPresent()) return Optional.absent();
        
        if(geometry.get().getSRID() != sourceGeometryCRS.getSRID()) log.warn("Discrepancy in parsed SRID. Geometry has: " + geometry.get().getSRID() + ", but was expecting: " + sourceGeometryCRS.getSRID());
        
        try {
            CoordinateReferenceSystem sourceCRS = CRS.decode(sourceGeometryCRS.getAuthority() + ":" + sourceGeometryCRS.getSRID(), sourceGeometryCRS.isLongitudeFirst());
            CoordinateReferenceSystem targetCRS = CRS.decode(targetGeometryCRS.getAuthority() + ":" + targetGeometryCRS.getSRID(), targetGeometryCRS.isLongitudeFirst());
            
            MathTransform mathTransform = CRS.findMathTransform(sourceCRS, targetCRS, true);
            
            geometry = Optional.fromNullable(JTS.transform(geometry.get(), mathTransform));
            
            return geometry;
        }
        catch (TransformException | FactoryException ex) {
            log.warn(ex.getMessage(), ex);
        }
        
        return Optional.absent();
    }
    
    /**
     * Transform easing/northing geometry to internal CRS.
     * @param easting easting coordinate
     * @param northing northing coordinate
     * @return transformed geometry
     */
    public static Optional<DirectPosition> transformEastingNorthingToInternalCRS(double easting, double northing) {
        CRSAuthorityFactory authorityFactory = ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG", null);
        
        try {
            CoordinateReferenceSystem wgs84CRS = authorityFactory.createCoordinateReferenceSystem("4326");
            CoordinateReferenceSystem osgbCRS = authorityFactory.createCoordinateReferenceSystem("27700");
            
            CoordinateOperation coordinateOperation = new DefaultCoordinateOperationFactory().createOperation(osgbCRS, wgs84CRS);
            
            DirectPosition eastingNorthing = new GeneralDirectPosition(easting, northing);
            DirectPosition latLong = coordinateOperation.getMathTransform().transform(eastingNorthing, eastingNorthing);
            
            return Optional.fromNullable(latLong);
        }
        catch (FactoryException | TransformException ex) {
            log.warn(ex.getMessage(), ex);
        }
        
        return Optional.absent();
    }
}
