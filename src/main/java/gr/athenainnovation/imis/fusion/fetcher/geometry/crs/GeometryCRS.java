package gr.athenainnovation.imis.fusion.fetcher.geometry.crs;

/**
 * Representation for a geometry CRS.
 * @author Thomas Maroulis
 */
public class GeometryCRS {
    private final String authority;
    private final int srid;
    private final boolean longitudeFirst;

    /**
     * Constructor for a CRS with coordinates in lat/long order.
     * @param authority CRS authority
     * @param srid CRS SRID
     */
    public GeometryCRS(final String authority, final int srid) {
        this(authority, srid, false);
    }
    
    /**
     * Constructor for a CRS with coordinates in either lat/long or long/lat order.
     * @param authority CRS authority
     * @param srid CRS SRID
     * @param longitudeFirst true if coordinates are in long/lat order, false otherwise
     */
    public GeometryCRS(final String authority, final int srid, final boolean longitudeFirst) {
        this.authority = authority;
        this.srid = srid;
        this.longitudeFirst = longitudeFirst;
    }

    /**
     * 
     * @return CRS authority
     */
    public String getAuthority() {
        return authority;
    }

    /**
     *
     * @return CRS SRID
     */
    public int getSRID() {
        return srid;
    }
    
    /**
     *
     * @return true if coordinates are in long/lat order, false otherwise
     */
    public boolean isLongitudeFirst() {
        return longitudeFirst;
    }
}
