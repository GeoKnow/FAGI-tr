package gr.athenainnovation.imis.fusion.fetcher.gui.workers;

import com.sun.istack.NotNull;
import java.io.File;

/**
 * An object that contains information about a given dataset.
 * @author Thomas Maroulis
 */
public class Dataset {
    private final String endpoint, graph;
    private final  DBConnectionParameters dbConnectionParameters;
    private final String rootNodeNS;
    
    //private boolean remote;
    private String unmodifiedLocalGraph;
    private String transformedLocalGraph;
    
    private boolean linksExist = false;
    private String linkFile;
    private Boolean setFirstInLinks;    
    
    /**
     * Construct new remote dataset with specified parameters. This constructor is meant for remote datasets only and parameter remote must be set to true.
     * No parameters may be null.
     * @param endpoint sparql endpoint uri
     * @param graph graph uri
     * param remote must be set to true
     * @param rootNodeNS regex for matching the URI of root nodes in dataset
     * @param unmodifiedLocalGraph local graph uri where the dataset is to be copied to with geometries intact
     * @param transformedLocalGraph local graph uri where the dataset is to be copied to with geometries transformed
     * @param dbConnectionParameters connection parameters for the db
     * @throws IllegalArgumentException  if any parameters are null, if remote==false or if endpoint.isEmpty()==true
     */
    public Dataset(final String endpoint, final String graph, final String rootNodeNS, final String unmodifiedLocalGraph, final String transformedLocalGraph, 
            final DBConnectionParameters dbConnectionParameters)
            throws IllegalArgumentException {
        this(endpoint, graph, rootNodeNS, dbConnectionParameters);
        
        this.unmodifiedLocalGraph = unmodifiedLocalGraph;
        this.transformedLocalGraph = transformedLocalGraph;
    }
    
    private Dataset(final String endpoint, final String graph, final String rootNodeNS, final DBConnectionParameters dbConnectionParameters) {
        if(endpoint == null || endpoint.isEmpty()) {
            throw new IllegalArgumentException("Endpoint cannot be null or empty.");
        }
        if(graph == null) {
            throw new IllegalArgumentException("Graph cannot be null.");
        }
        if(rootNodeNS == null) {
            throw new IllegalArgumentException("RootNodeNS cannot be null.");
        }
        if(dbConnectionParameters == null) {
            throw new IllegalArgumentException("DB connection parameters cannot be null.");
        }
        
        this.endpoint = endpoint;
        this.graph = graph;
        this.rootNodeNS = rootNodeNS;
        this.dbConnectionParameters = dbConnectionParameters;
    }
    
    /**
     *
     * @return sparql endpoint uri
     */
    @NotNull
    public String getEndpoint() {
        return endpoint;
    }
    
    /**
     *
     * @return graph uri
     */
    @NotNull
    public String getGraph() {
        return graph;
    }
    
    
    /**
     * 
     * @return regex for matching the URI of root nodes in dataset
     */
    public String getRootNodeNS() {
        return rootNodeNS;
    }
    
    /**
     *
     * @return local graph uri where the dataset is to be copied to with geometries intact
     * @throws IllegalStateException if dataset is not remote
     */
    @NotNull
    public String getUnmodifiedLocalGraph() throws IllegalStateException {       
        return unmodifiedLocalGraph;
    }
    
    /**
     *
     * @return local graph uri where the dataset is to be copied to with geometries transformed
     * @throws IllegalStateException if dataset is not remote
     */
    @NotNull
    public String getTransformedLocalGraph() throws IllegalStateException {

        return transformedLocalGraph;
    }
    
    /**
     * Set link file to be used for this dataset.
     * @param linkFile path of link file or empty if no link file is to be used
     * @param setFirstInLinks true if this set is first in link file, false otherwise. This value is ignored if linkFile.isEmpty()==true
     * @throws IllegalArgumentException if linkFile is null or if linkFile on disk does not exist
     */
    public void setLinks(final String linkFile, final boolean setFirstInLinks) throws IllegalArgumentException {
        if(linkFile == null) {
            throw new IllegalArgumentException("Link file cannot be null.");
        }
        
        if(!linkFile.isEmpty()) {
            final File file = new File(linkFile);
            if(!file.exists()) {
                throw new IllegalArgumentException("Failed to open link file: " + linkFile);
            }
            
            this.linkFile = linkFile;
            this.setFirstInLinks = setFirstInLinks;
            linksExist = true;
        }        
    }
    
    /**
     *
     * @return true if link file has been specified for this dataset, false otherwise
     */
    public boolean doLinksExist() {
        return linksExist;
    }
    
    /**
     *
     * @return path of link file
     * @throws IllegalStateException  if a link file has not been specified for this dataset
     */
    public String getLinksFIle() throws IllegalStateException {
        if(!linksExist) {
            throw new IllegalStateException("Links requested but they do not exist in dataset.");
        }
        return linkFile;
    }
    
    /**
     *
     * @return true if this dataset comes first in link file, false otherwise
     * @throws IllegalStateException  if a link file has not been specified for this dataset
     */
    public Boolean isThisSetFirstInLinks() throws IllegalStateException {
        if(!linksExist) {
            throw new IllegalStateException("Links' flag requested but links do not exist in dataset.");
        }
        return setFirstInLinks;
    }
    
    /**
     *
     * @return connection parameters for the db
     */
    public DBConnectionParameters getDBConnectionParameters() {
        return dbConnectionParameters;
    }
}