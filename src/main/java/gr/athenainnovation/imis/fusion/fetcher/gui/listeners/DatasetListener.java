package gr.athenainnovation.imis.fusion.fetcher.gui.listeners;

import gr.athenainnovation.imis.fusion.fetcher.gui.workers.Dataset;

/**
 * Listener for dataset information.
 * @author Thomas Maroulis
 */
public interface DatasetListener {
    void setSourceDataset(Dataset sourceDataset);
    void setTargetDataset(Dataset targetDataset);
    void setDatasetsReady(boolean datasetsReady);
}
