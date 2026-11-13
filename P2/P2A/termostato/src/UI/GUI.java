package UI;

import common.RunStatus;
import java.awt.Color;
import entorno.Main;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;
import common.CellType;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author pablo
 */
public class GUI extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GUI.class.getName());
    
    private static final Color MAP_BACKGROUND = new Color(70, 70, 70);
    private static final Color BORDER_MAP_CELL = new Color(45, 55, 70);
    private static final Color UNKNOWN_CELL = new Color(27, 35, 47);
    private static final Color FREE_CELL = new Color(27, 100, 47);
    private static final Color WALL_CELL = new Color(100, 100, 100);
    private static final Color AGENT_CELL = new Color(27, 35, 100);
    private static final Color GOAL_CELL = new Color(255, 255, 47);
    
    private static final Color BACKGROUND = new Color(13, 17, 24);
    private static final Color TEXT = new Color(229, 236, 245);
    private static final Color GREEN = new Color(31, 160, 110);
    private static final Color RED = new Color(190, 65, 75);
    
    private static final Color MUTED = new Color(139, 153, 173);
    private static final Color ACCENT = new Color(73, 133, 255);
    
    private int challengeID = 1;
    private final JPanel[][] mapCells= new JPanel[Main.MAP_ROWS][Main.MAP_COL];
    
    
    private void initComponentsCustom(){      
        startButton.setText("PLAY");
        startButton.setBackground(GREEN);
        startButton.setForeground(TEXT);
        
        stopButton.setText("STOP");
        stopButton.setBackground(RED);
        stopButton.setForeground(TEXT);
        
        terminalText.setText("");
        terminalText.setBackground(BACKGROUND);
        terminalText.setForeground(TEXT);
        terminalText.setCaretColor(TEXT);
        terminalText.setBorder(new EmptyBorder(12, 13, 12, 13));
        terminalText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        infoText.setText("");
        infoText.setBackground(BACKGROUND);
        infoText.setForeground(TEXT);
        infoText.setCaretColor(TEXT);
        infoText.setBorder(new EmptyBorder(12, 13, 12, 13));
        infoText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        mapPanel.setBackground(MAP_BACKGROUND);
    }
    
    public void printText(String text){
        terminalText.append(text);
        terminalText.append("\n");
    }
    
    private void initMap(){
        mapPanel.setLayout(new java.awt.GridLayout(Main.MAP_ROWS, Main.MAP_COL, 2, 2));
        mapPanel.removeAll();
        
        for(int i = 0; i < Main.MAP_ROWS; i++){
            for(int j = 0; j < Main.MAP_COL; j++){
                JPanel cell = new JPanel();
                
                cell.setOpaque(true);
                cell.setBackground(UNKNOWN_CELL);
                cell.setBorder(BorderFactory.createLineBorder(BORDER_MAP_CELL));
                
                mapCells[i][j] = cell;
                mapPanel.add(cell);
            }
        }
        
        mapPanel.revalidate();
        mapPanel.repaint();
    }
    
    public void updateMap(ArrayList<ArrayList<CellType>> data){
        if(data == null || data.size() != Main.MAP_ROWS || data.get(0).size() != Main.MAP_COL){
            Main.printGUI("Error al actualizar el mapa: no coincide la dimensión del mapa a mostrar.");
        }
        else{
            for(int i = 0; i < Main.MAP_ROWS; i++){
                for(int j = 0; j < Main.MAP_COL; j++){
                    switch(data.get(i).get(j)){
                        case UNKNOWN:
                            mapCells[i][j].setBackground(UNKNOWN_CELL);
                        break;
                        
                        case WALL:
                            mapCells[i][j].setBackground(WALL_CELL);
                        break;
                        
                        case FREE:
                            mapCells[i][j].setBackground(FREE_CELL);
                        break;
                        
                        case AGENT:
                            mapCells[i][j].setBackground(AGENT_CELL);
                        break;
                        
                        case GOAL:
                            mapCells[i][j].setBackground(GOAL_CELL);
                        break;
                    }
                }
            }
        }
        
        mapPanel.revalidate();
        mapPanel.repaint();
    }
    
    public void updateInfo(String info){
        infoText.setText(info);
    }
    
    
    /**
     * Creates new form GUI2
     */
    public GUI() {
        initComponents();
        initMap();
        initComponentsCustom();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        terminalText = new javax.swing.JTextArea();
        challengeSelector = new javax.swing.JComboBox<>();
        startButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        mapPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        infoText = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        terminalText.setEditable(false);
        terminalText.setColumns(20);
        terminalText.setRows(5);
        jScrollPane1.setViewportView(terminalText);

        challengeSelector.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19" }));
        challengeSelector.addActionListener(this::challengeSelectorActionPerformed);

        startButton.setText("PLAY");
        startButton.addActionListener(this::startButton1ActionPerformed);

        stopButton.setText("STOP");
        stopButton.addActionListener(this::stopButtonActionPerformed);

        mapPanel.setPreferredSize(new java.awt.Dimension(480, 480));

        javax.swing.GroupLayout mapPanelLayout = new javax.swing.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
        );

        infoText.setEditable(false);
        infoText.setColumns(20);
        infoText.setRows(5);
        jScrollPane2.setViewportView(infoText);

        jLabel1.setText("Challenge:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(mapPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addGap(18, 18, 18)
                            .addComponent(challengeSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(14, 14, 14))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 641, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(27, 27, 27)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(startButton, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                                .addComponent(stopButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(29, 29, 29))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(38, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mapPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(challengeSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(stopButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(19, 19, 19))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void challengeSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_challengeSelectorActionPerformed
        
        Object selected = challengeSelector.getSelectedItem();
        
        if(selected != null){
            this.challengeID = Integer.parseInt(selected.toString());
        }
    }//GEN-LAST:event_challengeSelectorActionPerformed

    private void startButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButton1ActionPerformed
        RunStatus runStatus = Main.getRunStatus();
        
        if(runStatus != RunStatus.ACTIVE){            
            Main.startChallenge(this.challengeID);
        }
        else{
            Main.printGUI("------ YA HAY UNA RUN ACTIVA (no se ha iniciado una nueva run) ------");
        }
    }//GEN-LAST:event_startButton1ActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        RunStatus runStatus = Main.getRunStatus();
                
        if(runStatus == RunStatus.ACTIVE){
            Main.closeRun();
        }
        else{
            Main.printGUI("------ NO SE HA PARADO LA RUN (no hay una run activa) ------");
        }
    }//GEN-LAST:event_stopButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new GUI().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> challengeSelector;
    private javax.swing.JTextArea infoText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel mapPanel;
    private javax.swing.JButton startButton;
    private javax.swing.JButton stopButton;
    private javax.swing.JTextArea terminalText;
    // End of variables declaration//GEN-END:variables
}
