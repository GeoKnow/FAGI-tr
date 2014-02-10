package gr.athenainnovation.imis.fusion.fetcher.gui;

import gr.athenainnovation.imis.fusion.fetcher.gui.listeners.MessageListener;
import gr.athenainnovation.imis.fusion.fetcher.gui.workers.RuleConfig;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import org.apache.log4j.Logger;


/**
 * Application entrypoint
 * @author Thomas Maroulis
 */
public class FetcherGUI extends javax.swing.JFrame implements MessageListener {
    private RuleConfig config;
    
    private DatasetPanel datasetPanel;
    private MatcherPanel sourceMatcherPanel, targetMatcherPanel;
    private FetcherTransformerPanel fetcherTransformerPanel;
    
    private static final int SCROLL_INCREMENT = 20;    

    public FetcherGUI() {
        config = new RuleConfig();
        initComponents();     
    }

    private javax.swing.JTabbedPane JTabbedPane;
    
    private void initComponents() {
        JTabbedPane = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(JTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(JTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                .addContainerGap())
        );

        datasetPanel = new DatasetPanel(this);
        JScrollPane datasetPanelScrollPane = new JScrollPane(datasetPanel);
        datasetPanelScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        datasetPanelScrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_INCREMENT);
        JTabbedPane.addTab("Datasets", datasetPanelScrollPane);
        
        sourceMatcherPanel = new MatcherPanel(this, config, RuleConfig.SOURCE_DATASET);
        JScrollPane sourceMatcherPanelScrollPane = new JScrollPane(sourceMatcherPanel);
        sourceMatcherPanelScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        sourceMatcherPanelScrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_INCREMENT);
        JTabbedPane.addTab("Source dataset matcher", sourceMatcherPanelScrollPane);
        datasetPanel.registerDatasetListener(sourceMatcherPanel);
        
        targetMatcherPanel = new MatcherPanel(this, config, RuleConfig.TARGET_DATASET);
        JScrollPane targetMatcherPanelScrollPane = new JScrollPane(targetMatcherPanel);
        targetMatcherPanelScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        targetMatcherPanelScrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_INCREMENT);
        JTabbedPane.addTab("Target dataset matcher", targetMatcherPanelScrollPane);
        datasetPanel.registerDatasetListener(targetMatcherPanel);
        
        fetcherTransformerPanel = new FetcherTransformerPanel(this);
        JScrollPane fetcherTransformerPanelScrollPane = new JScrollPane(fetcherTransformerPanel);
        fetcherTransformerPanelScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        fetcherTransformerPanelScrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_INCREMENT);
        JTabbedPane.addTab("Fetcher & Transformer", fetcherTransformerPanelScrollPane);
        datasetPanel.registerDatasetListener(fetcherTransformerPanel);
        sourceMatcherPanel.registerRuleListener(fetcherTransformerPanel);
        targetMatcherPanel.registerRuleListener(fetcherTransformerPanel);
        
        setPreferredSize(new Dimension(1024, 960));
        pack();
    }
    
    @Override
    public void printMessageDialogue(String title, String message, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {  
                    
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            Logger log = Logger.getLogger(FetcherGUI.class);
            log.fatal(ex.getMessage(), ex);
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FetcherGUI().setVisible(true);
            }
        });
    }           
}