package gr.athenainnovation.imis.fusion.fetcher.gui;

import com.google.common.base.Optional;
import com.google.common.collect.Ordering;
import gr.athenainnovation.imis.fusion.fetcher.geometry.crs.GeometryCRS;
import gr.athenainnovation.imis.fusion.fetcher.gui.listeners.DatasetListener;
import gr.athenainnovation.imis.fusion.fetcher.gui.listeners.FetcherListener;
import gr.athenainnovation.imis.fusion.fetcher.gui.listeners.MessageListener;
import gr.athenainnovation.imis.fusion.fetcher.gui.listeners.RuleListener;
import gr.athenainnovation.imis.fusion.fetcher.gui.workers.Dataset;
import gr.athenainnovation.imis.fusion.fetcher.gui.workers.FetcherWorker;
import gr.athenainnovation.imis.fusion.fetcher.gui.workers.MatchedRule;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableModel;
import org.apache.log4j.Logger;

/**
 * Fetcher/transformer panel.
 * @author Thomas Maroulis
 */
public class FetcherTransformerPanel extends javax.swing.JPanel implements RuleListener, DatasetListener, FetcherListener {
    private static final Logger LOG = Logger.getLogger(FetcherTransformerPanel.class);
    
    private Map<String, MatchedRule> sourceMatchedRules, targetMatchedRules;
    private Dataset sourceDataset, targetDataset;
    
    private final MessageListener messageListener;
    
    private boolean datasetsReady = false;
    private boolean sourceMatchedRulesReady = false;
    private boolean targetMatchedRulesReady = false;
    
    /**
     * Creates new form FetcherTransformerPanel
     */
    public FetcherTransformerPanel(MessageListener messageListener) {
        this.messageListener = messageListener;
        
        initComponents();
    }
    
    @Override
    public void setSourceMatchedRules(Map<String, MatchedRule> sourceMatchedRules) {
        this.sourceMatchedRules = sourceMatchedRules;
    }

    @Override
    public void setTargetMatchedRules(Map<String, MatchedRule> targetMatchedRules) {
        this.targetMatchedRules = targetMatchedRules;
    }
    
    @Override
    public void setSourceMatchedRulesReady(boolean sourceMatchedRulesReady) {
         this.sourceMatchedRulesReady = sourceMatchedRulesReady;
         if(sourceMatchedRulesReady) {
             sourceDatasetRulesStatusField.setText("Received");
             if(targetMatchedRulesReady) loadRules();
             fetchDatasetsButton.setEnabled(true);
         }
         else {
             sourceDatasetRulesStatusField.setText("Pending...");
             resetRules();
             fetchDatasetsButton.setEnabled(false);
         }
    }
    
    @Override
    public void setTargetMatchedRulesReady(boolean targetMatchedRulesReady) {
         this.targetMatchedRulesReady = targetMatchedRulesReady;
         if(targetMatchedRulesReady) {
             targetDatasetRulesStatusField.setText("Received");
             if(sourceMatchedRulesReady) loadRules();
         }
         else {
             targetDatasetRulesStatusField.setText("Pending...");
             resetRules();
         }
    }
    
    @Override
    public void setSourceDataset(Dataset dataset) {
        this.sourceDataset = dataset;
    }
    
    @Override
    public void setTargetDataset(Dataset dataset) {
        this.targetDataset = dataset;
    }
    
    @Override
    public void setDatasetsReady(boolean datasetsReady) {
        this.datasetsReady = datasetsReady;
        if(!datasetsReady) resetDoneLabels();
    }
    
    @Override
    public void printExceptionMessage(String message) {
        exceptionIndicatorField.setText("Exceptions thrown! Check log.");
    }
    
    private void loadRules() {
        resetRules();
        
        DefaultTableModel model = (DefaultTableModel) rulesTable.getModel();
        
        Ordering<String> rulesComparator = Ordering.natural();
        SortedSet<String> keys = new TreeSet<>(rulesComparator);        
        
        for(String ruleID : sourceMatchedRules.keySet()) {
            if(sourceMatchedRules.get(ruleID).getGeometryProcessor().isPresent()) keys.add(ruleID);
        }
        
        for(String key : keys) {
            Object[] row = new Object[7];
            row[0] = key;
            if(sourceMatchedRules.get(key).getTimesMatched().isPresent()) {
                row[1] = sourceMatchedRules.get(key).getTimesMatched().get();
            }
            else {
                row[1] = "N/A";
            }
            if(targetMatchedRules.get(key).getTimesMatched().isPresent()) {
                row[2] = targetMatchedRules.get(key).getTimesMatched().get();
            }
            else {
                row[2] = "N/A";
            }
            if(sourceMatchedRules.get(key).getGeometryProcessor().get().isCRSSet()) {
                GeometryCRS crs = sourceMatchedRules.get(key).getGeometryProcessor().get().getGeometryCRS();
                row[3] = crs.getAuthority();
                row[4] = crs.getSRID();
                row[5] = crs.isLongitudeFirst();
            }
            else {
                row[3] = "N/A";
                row[4] = "N/A";
                row[5] = false;
            }
            row[6] = sourceMatchedRules.get(key).getRule().getDescription();
            
            model.addRow(row);
        }
        
        rulesTable.setModel(model);
    }
    
    private void resetRules() {
        DefaultTableModel model = (DefaultTableModel) rulesTable.getModel();
        model.setRowCount(0);
        rulesTable.setModel(model);
    }
    
    private void resetDoneLabels() {
        sourceDatasetDoneField.setText("Source dataset not done...");
        targetDatasetDoneField.setText("Target dataset not done...");
        exceptionIndicatorField.setText("No exceptions thrown...");
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        rulesTable = new javax.swing.JTable();
        fetchDatasetsButton = new javax.swing.JButton();
        sourceDatasetDoneField = new javax.swing.JLabel();
        targetDatasetDoneField = new javax.swing.JLabel();
        sourceDatasetProgressBar = new javax.swing.JProgressBar();
        targetDatasetProgressBar = new javax.swing.JProgressBar();
        exceptionIndicatorField = new javax.swing.JLabel();
        transformationToggle = new javax.swing.JToggleButton();
        jLabel3 = new javax.swing.JLabel();
        crsField = new javax.swing.JTextField();
        overrideCRSCheckbox = new javax.swing.JCheckBox();
        longitudeFirstCheckbox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        sourceDatasetRulesStatusField = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        targetDatasetRulesStatusField = new javax.swing.JLabel();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Select rule to transform to"));

        rulesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "RuleID", "# matched in source", "# matched in target", "CRS Authority", "CRS SRID", "CRS Long first", "Description"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(rulesTable);

        fetchDatasetsButton.setText("Fetch and transform datasets");
        fetchDatasetsButton.setEnabled(false);
        fetchDatasetsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fetchDatasetsButtonActionPerformed(evt);
            }
        });

        sourceDatasetDoneField.setText("Source dataset not done...");

        targetDatasetDoneField.setText("Target dataset not done...");

        exceptionIndicatorField.setText("No exceptions thrown...");

        transformationToggle.setText("Transformation enabled");
        transformationToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transformationToggleActionPerformed(evt);
            }
        });

        jLabel3.setText("CRS:");

        crsField.setText("EPSG:4326");
        crsField.setEnabled(false);

        overrideCRSCheckbox.setText("Override CRS");
        overrideCRSCheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                overrideCRSCheckboxItemStateChanged(evt);
            }
        });

        longitudeFirstCheckbox.setText("Longitude first?");
        longitudeFirstCheckbox.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 846, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(fetchDatasetsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(exceptionIndicatorField))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sourceDatasetDoneField)
                            .addComponent(targetDatasetDoneField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(sourceDatasetProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addComponent(targetDatasetProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(crsField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(longitudeFirstCheckbox)
                        .addGap(18, 18, 18)
                        .addComponent(overrideCRSCheckbox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(transformationToggle, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(crsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(transformationToggle)
                    .addComponent(overrideCRSCheckbox)
                    .addComponent(longitudeFirstCheckbox))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fetchDatasetsButton)
                    .addComponent(exceptionIndicatorField))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sourceDatasetDoneField)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(sourceDatasetProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(targetDatasetDoneField)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(targetDatasetProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setText("Source dataset:");

        sourceDatasetRulesStatusField.setText("Pending...");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel2.setText("Target dataset:");

        targetDatasetRulesStatusField.setText("Pending...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sourceDatasetRulesStatusField)
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(targetDatasetRulesStatusField)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(sourceDatasetRulesStatusField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jSeparator1))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(targetDatasetRulesStatusField)))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void fetchDatasetsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fetchDatasetsButtonActionPerformed
        
        Optional<MatchedRule> targetRule;
        if(transformationToggle.isSelected()) {
            targetRule = Optional.absent();
        }
        else if(rulesTable.getSelectedRow() != -1) {
            String targetRuleID = (String) rulesTable.getValueAt(rulesTable.getSelectedRow(), 0);
            targetRule = Optional.of(sourceMatchedRules.get(targetRuleID));

            if(overrideCRSCheckbox.isSelected()) {
                String[] crsTokens = crsField.getText().split(":", 2);
                try {
                    GeometryCRS geometryCRS = new GeometryCRS(crsTokens[0], Integer.parseInt(crsTokens[1]), longitudeFirstCheckbox.isSelected());
                    targetRule.get().getGeometryProcessor().get().setGeometryCRS(geometryCRS);
                }
                catch (NumberFormatException ex) {
                    messageListener.printMessageDialogue("Error", "Failed to parse: " + crsField.getText() + " with " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            else if(!targetRule.get().getGeometryProcessor().get().isCRSSet()) {
                GeometryCRS geometryCRS = new GeometryCRS("EPSG", 4326, true);
                targetRule.get().getGeometryProcessor().get().setGeometryCRS(geometryCRS);
            }
        }
        else {
            messageListener.printMessageDialogue("Error", "Must select target transformation rule or disable transformation.", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        FetcherWorker sourceFetcherWorker = new FetcherWorker(this, sourceDataset, sourceMatchedRules, targetRule) {
            @Override
            protected void done() {
                // Call get despite return type being Void to prevent SwingWorker from swallowing exceptions
                try {
                    get();
                    
                    if(!sourceDataset.isRemote()) {
                        sourceDatasetDoneField.setText("Source dataset transformed.");
                    }
                    else {
                        sourceDatasetDoneField.setText("Source dataset transformed and fetched locally.");
                    }
                }
                catch (InterruptedException | RuntimeException ex) {
                    LOG.warn(ex.getMessage());
                    messageListener.printMessageDialogue("Error", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
                    sourceDatasetDoneField.setText("Worker terminated abnormally.");
                }
                catch (ExecutionException ex) {
                    LOG.warn(ex.getCause().getMessage());
                    messageListener.printMessageDialogue("Error", ex.getCause().getMessage(), JOptionPane.ERROR_MESSAGE);
                    sourceDatasetDoneField.setText("Worker terminated abnormally.");
                }
                
                LOG.info("Source fetcher worker has terminated.");
            }
        };
        
        sourceFetcherWorker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override public void propertyChange(PropertyChangeEvent evt) {
                if("progress".equals(evt.getPropertyName())) {
                    sourceDatasetProgressBar.setValue((Integer) evt.getNewValue());
                }
            }
        });
        
        FetcherWorker targetFetcherWorker = new FetcherWorker(this, targetDataset, targetMatchedRules, targetRule) {
            @Override
            protected void done() {
                // Call get despite return type being Void to prevent SwingWorker from swallowing exceptions
                try {
                    get();
                    
                    if(!targetDataset.isRemote()) {
                        targetDatasetDoneField.setText("Target dataset transformed.");
                    }
                    else {
                        targetDatasetDoneField.setText("Target dataset transformed and fetched locally.");
                    }
                }
                catch (InterruptedException | RuntimeException ex) {
                    LOG.warn(ex.getMessage());
                    messageListener.printMessageDialogue("Error", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
                    targetDatasetDoneField.setText("Worker terminated abnormally.");
                }
                catch (ExecutionException ex) {
                    LOG.warn(ex.getCause().getMessage());
                    messageListener.printMessageDialogue("Error", ex.getCause().getMessage(), JOptionPane.ERROR_MESSAGE);
                    targetDatasetDoneField.setText("Worker terminated abnormally.");
                }
                
                LOG.info("Target fetcher worker has terminated.");
            }
        };
        
        targetFetcherWorker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override public void propertyChange(PropertyChangeEvent evt) {
                if("progress".equals(evt.getPropertyName())) {
                    targetDatasetProgressBar.setValue((Integer) evt.getNewValue());
                }
            }
        });
        
        exceptionIndicatorField.setText("No exceptions thrown...");
        sourceDatasetDoneField.setText("Source dataset not done...");
        targetDatasetDoneField.setText("Target dataset not done...");
        
        sourceDatasetProgressBar.setValue(0);
        targetDatasetProgressBar.setValue(0);
        
        sourceFetcherWorker.execute();
        targetFetcherWorker.execute();
    }//GEN-LAST:event_fetchDatasetsButtonActionPerformed

    private void transformationToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transformationToggleActionPerformed
        JToggleButton toggle = (JToggleButton) evt.getSource();
        rulesTable.setEnabled(!toggle.isSelected());
        if(toggle.isSelected()) {
            toggle.setText("Transformation disabled");
        }
        else {
            toggle.setText("Transformation enabled");
        }
    }//GEN-LAST:event_transformationToggleActionPerformed

    private void overrideCRSCheckboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_overrideCRSCheckboxItemStateChanged
        JCheckBox checkbox = (JCheckBox) evt.getSource();
        
        crsField.setEnabled(checkbox.isSelected());
        longitudeFirstCheckbox.setEnabled(checkbox.isSelected());
    }//GEN-LAST:event_overrideCRSCheckboxItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField crsField;
    private javax.swing.JLabel exceptionIndicatorField;
    private javax.swing.JButton fetchDatasetsButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JCheckBox longitudeFirstCheckbox;
    private javax.swing.JCheckBox overrideCRSCheckbox;
    private javax.swing.JTable rulesTable;
    private javax.swing.JLabel sourceDatasetDoneField;
    private javax.swing.JProgressBar sourceDatasetProgressBar;
    private javax.swing.JLabel sourceDatasetRulesStatusField;
    private javax.swing.JLabel targetDatasetDoneField;
    private javax.swing.JProgressBar targetDatasetProgressBar;
    private javax.swing.JLabel targetDatasetRulesStatusField;
    private javax.swing.JToggleButton transformationToggle;
    // End of variables declaration//GEN-END:variables

}
