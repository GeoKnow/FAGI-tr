package gr.athenainnovation.imis.fusion.fetcher.gui.workers;

import com.google.common.base.Optional;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import gr.athenainnovation.imis.fusion.fetcher.core.MetadataFetcher;
import gr.athenainnovation.imis.fusion.fetcher.core.GeometryFetcher;
import gr.athenainnovation.imis.fusion.fetcher.gui.listeners.FetcherListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.SwingWorker;
import org.apache.jena.riot.RDFDataMgr;

/**
 * This worker handles fetching metadata and geometry triples from specified datasets and transforming geometries if requested.
 * @author Thomas Maroulis
 */
public class FetcherWorker extends SwingWorker<Void, Void> {  
    private final FetcherListener listener;
    
    private final Dataset dataset;
    private final Map<String, MatchedRule> matchedRulesMap;
    private final Optional<MatchedRule> targetRule;
    
    private int fetcherProgress = 0;
    private int transformerProgress = 0;
    
    /**
     * 
     * @param listener listener for exception message printing
     * @param dataset dataset
     * @param matchedRulesMap matched rules map
     * @param targetRule target rule for transforming geometries to
     */
    public FetcherWorker(final FetcherListener listener, final Dataset dataset, final Map<String, MatchedRule> matchedRulesMap, final Optional<MatchedRule> targetRule) {
        super();
        
        this.listener = listener;
        this.dataset = dataset;
        this.matchedRulesMap = matchedRulesMap;
        this.targetRule = targetRule; 
    }

    @Override
    protected Void doInBackground() {
        Optional<List<String>> links = Optional.absent();
        if(dataset.doLinksExist()) {
            links = Optional.of(parseLinksFile(dataset.getLinksFIle(), dataset.isThisSetFirstInLinks()));
        }
        
        //the following two commands replace the if-else statement    
        final MetadataFetcher metadataFetcher = new MetadataFetcher(dataset, this, targetRule.isPresent(), links);
        metadataFetcher.fetchMetadata(matchedRulesMap);
        
        final int totalTransformerCount = matchedRulesMap.size();
        int transformerCount = 1;
        
        final GeometryFetcher geometryFetcher = new GeometryFetcher(dataset, targetRule, this, links);
                               
        for(MatchedRule matchedRule : matchedRulesMap.values()) {
            if(matchedRule.isToBeRetained()) {
                geometryFetcher.fetchGeometries(matchedRule);
            }
            
            publishTransformerProgress((int) (0.5 + (100 * (double) transformerCount++ / (double) totalTransformerCount)));
        }
        
        return null;
    }
    
    /**
     * Publish progress.
     * @param progress 
     */
    public void publishFetcherProgress(final int progress) {
        fetcherProgress = progress;
        publishProgress();
    }
    
    private void publishTransformerProgress(final int progress) {
        transformerProgress = progress;
        publishProgress();
    }
    
    private void publishProgress() {
        int globalProgress = (int) ((fetcherProgress + transformerProgress) / 2);

        // We manually restrict values to a [0,100] range to prevent an exception being thrown in case of erroneous progress value.
        if(globalProgress < 0) {
            globalProgress = 0;
        }
        if(globalProgress > 100) {
            globalProgress = 100;
        }
        
        setProgress(globalProgress);
    }
    
    /**
     * Notify listener of exception message.
     * @param message
     */
    public void printExceptionMessage(final String message) {
        listener.printExceptionMessage(message);
    }
    
    private List<String> parseLinksFile(final String linksFile, final boolean thisSetFirst) {
        List<String> output = new ArrayList<>();
        
        Model model = RDFDataMgr.loadModel(linksFile);
        if(thisSetFirst){
            ResIterator resIterator = model.listResourcesWithProperty(OWL.sameAs);
            while(resIterator.hasNext()) {
                output.add(resIterator.nextResource().getURI());
            }
            return output;
        }
        else {
            NodeIterator nodeIterator = model.listObjectsOfProperty(OWL.sameAs);
            while(nodeIterator.hasNext()) {
                output.add(nodeIterator.nextNode().asResource().getURI());
            }
            return output;
        }
    }
}