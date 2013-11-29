package gr.athenainnovation.imis.fusion.fetcher.gui;

import gr.athenainnovation.imis.fusion.fetcher.gui.listeners.MessageListener;
import gr.athenainnovation.imis.fusion.fetcher.gui.workers.Dataset;
import gr.athenainnovation.imis.fusion.fetcher.gui.workers.RuleConfig;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.collect.Ordering;
import gr.athenainnovation.imis.fusion.fetcher.geometry.GeometryProcessor;
import gr.athenainnovation.imis.fusion.fetcher.gui.listeners.DatasetListener;
import gr.athenainnovation.imis.fusion.fetcher.gui.listeners.RuleListener;
import gr.athenainnovation.imis.fusion.fetcher.gui.workers.MatchedRule;
import gr.athenainnovation.imis.fusion.fetcher.gui.workers.MatcherWorker;
import gr.athenainnovation.imis.fusion.fetcher.rules.Rule;
import gr.athenainnovation.imis.fusion.fetcher.rules.RuleTriple;
import gr.athenainnovation.imis.fusion.fetcher.rules.RulePattern;
import gr.athenainnovation.imis.fusion.fetcher.rules.parser.RuleConfigParser;
import gr.athenainnovation.imis.fusion.fetcher.rules.parser.RuleQueryUtils;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.log4j.Logger;

/**
 * Matcher panel.
 * @author Thomas Maroulis
 */
public class MatcherPanel extends javax.swing.JPanel implements DatasetListener {
    private static final Logger LOG = Logger.getLogger(MatcherPanel.class);
    
    private final MessageListener messageListener;
    private final List<RuleListener> ruleListeners = new ArrayList<>();
    private final RuleConfig config;
    private Dataset dataset;
    private Map<String, MatchedRule> matchedRulesMap;
    
    private MatchedRule matchedRule;
    private String ruleID;
    
    private int datasetType;
    
    private boolean datasetChanged = false;
    
    /**
     * Creates new form MatcherPanel
     */
    public MatcherPanel(MessageListener messageListener, RuleConfig config, int datasetType) {
        assert(datasetType == RuleConfig.SOURCE_DATASET || datasetType == RuleConfig.TARGET_DATASET);
        
        this.messageListener = messageListener;
        this.config = config;
        this.datasetType = datasetType;
        
        initComponents();
    }
    
    /**
     * Register rule listener.
     * @param ruleListener rule listener
     */
    public void registerRuleListener(final RuleListener ruleListener) {
        ruleListeners.add(ruleListener);
    }
    
    private void setSourceMatchedRules(final Map<String, MatchedRule> sourceMatchedRules) {
        for(RuleListener listener : ruleListeners) {
            listener.setSourceMatchedRules(sourceMatchedRules);
        }
    }
    
     private void setTargetMatchedRules(final Map<String, MatchedRule> targetMatchedRules) {
         for(RuleListener listener : ruleListeners) {
            listener.setTargetMatchedRules(targetMatchedRules);
        }
     }
     
     private void setSourceMatchedRulesReady(final boolean sourceMatchedRulesReady) {
         for(RuleListener listener : ruleListeners) {
            listener.setSourceMatchedRulesReady(sourceMatchedRulesReady);
        }
     }
     
     private void setTargetMatchedRulesReady(final boolean targetMatchedRulesReady) {
         for(RuleListener listener : ruleListeners) {
            listener.setTargetMatchedRulesReady(targetMatchedRulesReady);
        }
     }
    
    @Override
    public void setSourceDataset(Dataset dataset) {
        if(datasetType == RuleConfig.SOURCE_DATASET) {
            if(this.dataset != null && !(this.dataset.getEndpoint().equals(dataset.getEndpoint()) && this.dataset.getGraph().equals(dataset.getGraph())))
                datasetChanged = true;
            this.dataset = dataset;
        }
    }

    @Override
    public void setTargetDataset(Dataset dataset) {
        if(datasetType == RuleConfig.TARGET_DATASET) this.dataset = dataset;
    }
    
    @Override
    public void setDatasetsReady(boolean datasetsReady) {
        matcherBeginButton.setEnabled(datasetsReady);
        if(datasetsReady) {
            if(matchedRulesMap != null && datasetChanged) {
                matcherBeginButton.setText("Click to refresh rule matching");
                matcherBeginButton.setForeground(Color.red);
                updateRuleProductionListenerWithRulesReady(false);
            }
            else {
                matcherBeginButton.setText("Begin rule matching");
                matcherBeginButton.setForeground(Color.BLACK);
            }
        }
        else {
            matcherBeginButton.setText("Dataset not set");
            matcherBeginButton.setForeground(Color.BLACK);
        }
    }
    
    private void updateRuleProductionListener(Map<String, MatchedRule> matchedRulesMap) {
        if(datasetType == RuleConfig.SOURCE_DATASET) {
            setSourceMatchedRules(matchedRulesMap);
        }
        else if(datasetType == RuleConfig.TARGET_DATASET) {
            setTargetMatchedRules(matchedRulesMap);
        }
    }
    
    private void updateRuleProductionListenerWithRulesReady(boolean status) {
        if(datasetType == RuleConfig.SOURCE_DATASET) {
            setSourceMatchedRulesReady(status);
        }
        else if (datasetType == RuleConfig.TARGET_DATASET) {
            setTargetMatchedRulesReady(status);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        ruleList = new javax.swing.JList();
        ruleNumOfTimesMatchedLabel = new javax.swing.JLabel();
        ruleNumOfTimesMatchedField = new javax.swing.JLabel();
        ruleStatusField = new javax.swing.JLabel();
        numOfRulesExecutedLabel = new javax.swing.JLabel();
        numOfRulesExecutedField = new javax.swing.JLabel();
        numOfRulesMatchedLabel = new javax.swing.JLabel();
        numOfRulesMatchedField = new javax.swing.JLabel();
        numOfRulesReturnedErrorLabel = new javax.swing.JLabel();
        numOfRulesReturnedErrorField = new javax.swing.JLabel();
        sourceSRIDLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        sridField = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        ruleDescriptionField = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        ruleMatchedTriplesField = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        ruleTriplesField = new javax.swing.JTable();
        toBeRetainedCheckbox = new javax.swing.JCheckBox();
        numOfRulesLabel = new javax.swing.JLabel();
        numOfRulesField = new javax.swing.JLabel();
        matcherBeginButton = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Source dataset rule matching"));

        ruleList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        ruleList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                ruleListValueChanged(evt);
            }
        });
        jScrollPane6.setViewportView(ruleList);

        ruleNumOfTimesMatchedLabel.setText("# times matched:");

        ruleNumOfTimesMatchedField.setText("N/A");

        ruleStatusField.setText("No rule selected...");

        numOfRulesExecutedLabel.setText("# rules executed:");

        numOfRulesExecutedField.setText("0");

        numOfRulesMatchedLabel.setText("# rules matched:");

        numOfRulesMatchedField.setText("0");

        numOfRulesReturnedErrorLabel.setText("# rules returned error:");

        numOfRulesReturnedErrorField.setText("0");

        sourceSRIDLabel.setText("SRID:");

        sridField.setText("N/A");

        jLabel2.setText("Description:");

        ruleDescriptionField.setColumns(20);
        ruleDescriptionField.setLineWrap(true);
        ruleDescriptionField.setRows(5);
        jScrollPane3.setViewportView(ruleDescriptionField);

        jLabel3.setText("Rule triples:");

        ruleMatchedTriplesField.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Subject", "Predicate", "Object"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane10.setViewportView(ruleMatchedTriplesField);

        jLabel4.setText("Sample matched triples:");

        ruleTriplesField.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Subject", "Predicate", "Object", "Datatype", "Type"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane11.setViewportView(ruleTriplesField);

        toBeRetainedCheckbox.setText("Retain?");
        toBeRetainedCheckbox.setEnabled(false);
        toBeRetainedCheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toBeRetainedCheckboxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(ruleNumOfTimesMatchedLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ruleNumOfTimesMatchedField, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sourceSRIDLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sridField, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                .addGap(26, 26, 26)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ruleStatusField))
                            .addComponent(jScrollPane3)
                            .addComponent(jScrollPane10)
                            .addComponent(jScrollPane11)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(toBeRetainedCheckbox))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(numOfRulesExecutedLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numOfRulesExecutedField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numOfRulesMatchedLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numOfRulesMatchedField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numOfRulesReturnedErrorLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numOfRulesReturnedErrorField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numOfRulesExecutedLabel)
                    .addComponent(numOfRulesExecutedField)
                    .addComponent(numOfRulesMatchedLabel)
                    .addComponent(numOfRulesMatchedField)
                    .addComponent(numOfRulesReturnedErrorLabel)
                    .addComponent(numOfRulesReturnedErrorField))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ruleNumOfTimesMatchedLabel)
                            .addComponent(ruleNumOfTimesMatchedField)
                            .addComponent(ruleStatusField)
                            .addComponent(sourceSRIDLabel)
                            .addComponent(jLabel1)
                            .addComponent(sridField))
                        .addGap(21, 21, 21)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(toBeRetainedCheckbox))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))
                    .addComponent(jScrollPane6))
                .addContainerGap())
        );

        numOfRulesLabel.setText("# rules:");

        numOfRulesField.setText("0");

        matcherBeginButton.setText("Dataset not set");
        matcherBeginButton.setEnabled(false);
        matcherBeginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matcherBeginButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(numOfRulesLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numOfRulesField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(matcherBeginButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numOfRulesLabel)
                    .addComponent(numOfRulesField)
                    .addComponent(matcherBeginButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents
  
    private void matcherBeginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matcherBeginButtonActionPerformed
        reset();
        
        //Get rules from config files
        Map<String, Rule> rules;
        try {
            RuleConfigParser ruleParser = new RuleConfigParser();
            ruleParser.readAllRules(config.getClassRulesFile(), config.getPropertyRulesFile(), config.getObjectRulesFile(), config.getTripleDefaultRulesFile(), config.getTripleUserRulesFile());
            rules = ruleParser.getTripleRules();
        }
        catch (RuntimeException ex) {
            messageListener.printMessageDialogue("Error", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            return;
        }

        numOfRulesField.setText(rules.size() + "");

        MatcherWorker matcherWorker = new MatcherWorker(rules, dataset.getEndpoint(), dataset.getGraph()) {
            @Override protected void done() {
                try {
                    Map<String, MatchedRule> matchedRulesMap = get();

                    DefaultListModel ruleListModel = new DefaultListModel();

                    int rulesMatched = 0;
                    int rulesReturnedError = 0;
                    
                    Ordering<String> rulesComparator = 
                            Ordering.from(MatchedRule.TIMES_MATCHED_COMPARATOR).onResultOf(Functions.forMap(matchedRulesMap)).
                            compound(Ordering.natural());
                    SortedSet<String> keys = new TreeSet<>(rulesComparator);
                    keys.addAll(matchedRulesMap.keySet());
                    
                    for(String ruleID : keys) {
                        MatchedRule matchedRule = matchedRulesMap.get(ruleID);

                        if(matchedRule.getExceptionMessageThrown().isPresent())
                        rulesReturnedError++;
                        else if(matchedRule.getTimesMatched().isPresent() && matchedRule.getTimesMatched().get() > 0)
                        rulesMatched++;

                        ruleListModel.addElement(ruleID);
                    }
                    numOfRulesMatchedField.setText(rulesMatched + "");
                    numOfRulesReturnedErrorField.setText(rulesReturnedError + "");
                    ruleList.setModel(ruleListModel);
                    MatcherPanel.this.matchedRulesMap = matchedRulesMap;
                    
                    updateRuleProductionListener(matchedRulesMap);
                    updateRuleProductionListenerWithRulesReady(true);
                }
                catch (InterruptedException | RuntimeException ex) {
                    LOG.warn(ex.getMessage(), ex);
                    messageListener.printMessageDialogue("Error", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
                }
                catch (ExecutionException ex) {
                    LOG.warn(ex.getCause().getMessage());
                    messageListener.printMessageDialogue("Error", ex.getCause().getMessage(), JOptionPane.ERROR_MESSAGE);
                }
                
                LOG.info("Matcher worker has terminated.");
            }
        };

        PropertyChangeListener listener = new PropertyChangeListener(){
            @Override public void propertyChange(PropertyChangeEvent event) {
                if ("progress".equals(event.getPropertyName())) {
                    numOfRulesExecutedField.setText((Integer) event.getNewValue() + "");
                }
            }
        };
        matcherWorker.addPropertyChangeListener(listener);

        matcherWorker.execute();
        
        datasetChanged = false;
    }//GEN-LAST:event_matcherBeginButtonActionPerformed
    
    private void ruleListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_ruleListValueChanged
        JList list = (JList) evt.getSource();

        if (!list.isSelectionEmpty()) {
            resetRuleValues();

            int index = list.getMinSelectionIndex();
            ruleID = (String) list.getModel().getElementAt(index);
            matchedRule = matchedRulesMap.get(ruleID);

            setTimesMatchedField(matchedRule.getTimesMatched());
            setSRIDField(matchedRule.getGeometryProcessor());
            setExceptionMessageThrownField(matchedRule.getExceptionMessageThrown());
            setRuleDescriptionField(matchedRule.getRule());
            setRuleTriplesField(matchedRule.getRule());

            if(matchedRule.getTimesMatched().isPresent() && matchedRule.getTimesMatched().get() > 0 && matchedRule.getRulePattern().isPresent()) {
                setRuleMatchedTriplesField(matchedRule.getRulePattern().get());
                
                toBeRetainedCheckbox.setEnabled(true);
                toBeRetainedCheckbox.setSelected(matchedRule.isToBeRetained());
            }
        }
    }//GEN-LAST:event_ruleListValueChanged

    private void toBeRetainedCheckboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_toBeRetainedCheckboxItemStateChanged
        if(matchedRule == null) return;
        
        JCheckBox checkbox = (JCheckBox) evt.getSource();
        boolean state = checkbox.isSelected();
        matchedRule.setToBeRetained(state);
        matchedRulesMap.put(ruleID, matchedRule);
        updateRuleProductionListener(matchedRulesMap);
    }//GEN-LAST:event_toBeRetainedCheckboxItemStateChanged
    
    private void reset() {
        numOfRulesField.setText("0");
        numOfRulesExecutedField.setText("0");
        
        DefaultListModel model = new DefaultListModel();
        ruleList.setModel(model);
        
        resetRuleValues();
    }
    
    private void resetRuleValues() {
        matchedRule = null;
        
        ruleNumOfTimesMatchedField.setText("N/A");
        
        sridField.setText("N/A");
        
        ruleStatusField.setText("No rule selected...");
        ruleStatusField.setForeground(Color.BLACK);
        ruleStatusField.setToolTipText(null);
        
        ruleDescriptionField.setText("");
        
        DefaultTableModel tripleTableModel = (DefaultTableModel) ruleTriplesField.getModel();
        tripleTableModel.setRowCount(0);
        ruleTriplesField.setModel(tripleTableModel);
        
        DefaultTableModel matchedTripleTableModel = (DefaultTableModel) ruleMatchedTriplesField.getModel();
        matchedTripleTableModel.setRowCount(0);
        ruleMatchedTriplesField.setModel(matchedTripleTableModel);
        
        toBeRetainedCheckbox.setSelected(false);
        toBeRetainedCheckbox.setEnabled(false);
    }
    
    private void setTimesMatchedField(Optional<Integer> timesMatched) {
        if(timesMatched.isPresent()) ruleNumOfTimesMatchedField.setText(timesMatched.get() + "");
        else ruleNumOfTimesMatchedField.setText("N/A");
    }
    
    private void setSRIDField(Optional<GeometryProcessor> geometryProcessor) {
        if(geometryProcessor.isPresent()) {
            String sridText = geometryProcessor.get().getGeometryCRS().getAuthority() + ":" + geometryProcessor.get().getGeometryCRS().getSRID();
            if(geometryProcessor.get().getGeometryCRS().isLongitudeFirst()) sridText = sridText.concat(" (Long/Lat)");
            sridField.setText(sridText);
        }
        else sridField.setText("N/A");
    }
    
    private void setExceptionMessageThrownField(Optional<String> exceptionMessage) {
        if(exceptionMessage.isPresent()) {
            ruleStatusField.setText("Exception occurred!");
            ruleStatusField.setForeground(Color.red);
            ruleStatusField.setToolTipText(exceptionMessage.get());
        }
        else {
            ruleStatusField.setText("Execution successful!");
            ruleStatusField.setForeground(Color.BLACK);
            ruleStatusField.setToolTipText(null);
        }
    }
    
    private void setRuleDescriptionField(Rule rule) {
        ruleDescriptionField.setText(rule.getDescription());
    }
    
    private void setRuleTriplesField(Rule rule) {
        DefaultTableModel tableModel = (DefaultTableModel) ruleTriplesField.getModel();
        
        for(RuleTriple triple : rule.getTriples()) {
            String[] row = {triple.getSubject(), triple.getPredicate(), triple.getObject(), triple.getDatatype(), triple.getType()};
            tableModel.addRow(row);
        }
        
        ruleTriplesField.setModel(tableModel);      
    }
    
    private void setRuleMatchedTriplesField(RulePattern rulePattern) {
        List<String> triples = RuleQueryUtils.substituteVariableContentIntoPattern(rulePattern);
        
        DefaultTableModel tableModel = (DefaultTableModel) ruleMatchedTriplesField.getModel();
        
        for(String triple : triples) {
            String[] row = triple.split(" ", 3);
            tableModel.addRow(row);
        }
        
        ruleMatchedTriplesField.setModel(tableModel);
    }
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JButton matcherBeginButton;
    private javax.swing.JLabel numOfRulesExecutedField;
    private javax.swing.JLabel numOfRulesExecutedLabel;
    private javax.swing.JLabel numOfRulesField;
    private javax.swing.JLabel numOfRulesLabel;
    private javax.swing.JLabel numOfRulesMatchedField;
    private javax.swing.JLabel numOfRulesMatchedLabel;
    private javax.swing.JLabel numOfRulesReturnedErrorField;
    private javax.swing.JLabel numOfRulesReturnedErrorLabel;
    private javax.swing.JTextArea ruleDescriptionField;
    private javax.swing.JList ruleList;
    private javax.swing.JTable ruleMatchedTriplesField;
    private javax.swing.JLabel ruleNumOfTimesMatchedField;
    private javax.swing.JLabel ruleNumOfTimesMatchedLabel;
    private javax.swing.JLabel ruleStatusField;
    private javax.swing.JTable ruleTriplesField;
    private javax.swing.JLabel sourceSRIDLabel;
    private javax.swing.JLabel sridField;
    private javax.swing.JCheckBox toBeRetainedCheckbox;
    // End of variables declaration//GEN-END:variables
}
