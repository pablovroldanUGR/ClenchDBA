/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Component;
import java.awt.FontMetrics;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author PabloHI
 */
public class GUI extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GUI.class.getName());
    private static final Color BACKGROUND = new Color(13, 17, 24);
    private static final Color PANEL = new Color(21, 27, 37);
    private static final Color PANEL_LIGHT = new Color(27, 35, 47);
    private static final Color BORDER = new Color(45, 55, 70);
    private static final Color TEXT = new Color(229, 236, 245);
    private static final Color MUTED = new Color(139, 153, 173);
    private static final Color ACCENT = new Color(73, 133, 255);
    private static final Color GREEN = new Color(31, 160, 110);
    private static final Color RED = new Color(190, 65, 75);

    private JComboBox<Integer> challengeSelector;
    private JTextArea terminalOutput;
    private JLabel runState;

    /**
     * Creates new form GUI
     */
    public GUI() {
        initComponents();
        buildDarkInterface();
    }

    /** Construye la interfaz principal; la lógica de conexión se conecta aparte. */
    private void buildDarkInterface() {
        setTitle("DBA Lab · Grid Control");
        setMinimumSize(new Dimension(980, 640));
        setSize(1360, 900);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(22, 26, 22, 26));
        root.add(createHeader(), BorderLayout.NORTH);

        JSplitPane verticalSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, createMapPanel(), createTerminalPanel());
        verticalSplit.setBorder(null);
        verticalSplit.setDividerSize(7);
        verticalSplit.setResizeWeight(0.67);
        verticalSplit.setDividerLocation(0.67);
        verticalSplit.setBackground(BACKGROUND);
        verticalSplit.setContinuousLayout(true);
        verticalSplit.setOneTouchExpandable(false);

        JPanel workspace = new JPanel(new BorderLayout(18, 0));
        workspace.setOpaque(false);
        workspace.add(createBrandRail(), BorderLayout.WEST);
        workspace.add(verticalSplit, BorderLayout.CENTER);
        workspace.add(createControlPanel(), BorderLayout.EAST);
        root.add(workspace, BorderLayout.CENTER);

        setContentPane(root);
        SwingUtilities.invokeLater(() -> verticalSplit.setDividerLocation(0.67));
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 2, 2, 2));

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 2));
        titles.setOpaque(false);
        JLabel title = new JLabel("GRID  /  CONTROL ROOM");
        title.setForeground(TEXT);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 23));
        JLabel subtitle = new JLabel("DBA LAB     ·     ENTORNO DE SIMULACIÓN");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        titles.add(title);
        titles.add(subtitle);
        header.add(titles, BorderLayout.WEST);

        JPanel challengeBox = new JPanel(new BorderLayout(12, 0));
        challengeBox.setOpaque(false);
        JLabel challengeLabel = new JLabel("CHALLENGE ID");
        challengeLabel.setForeground(MUTED);
        challengeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        challengeSelector = new JComboBox<>();
        for (int id = 1; id <= 19; id++) {
            challengeSelector.addItem(id);
        }
        challengeSelector.setSelectedItem(1);
        challengeSelector.setForeground(TEXT);
        challengeSelector.setBackground(PANEL_LIGHT);
        challengeSelector.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        challengeSelector.setBorder(BorderFactory.createLineBorder(BORDER));
        challengeSelector.setPreferredSize(new Dimension(90, 38));
        challengeSelector.setOpaque(true);
        challengeSelector.setMaximumRowCount(9);
        challengeSelector.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton arrow = new JButton("▾");
                arrow.setForeground(ACCENT);
                arrow.setBackground(PANEL_LIGHT);
                arrow.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
                arrow.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                arrow.setFocusPainted(false);
                return arrow;
            }
        });
        challengeSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel cell = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                cell.setOpaque(true);
                cell.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
                cell.setBorder(new EmptyBorder(8, index < 0 ? 10 : 12, 8, 12));
                cell.setBackground(index < 0 ? PANEL_LIGHT
                        : isSelected ? new Color(39, 76, 126) : PANEL);
                cell.setForeground(index >= 0 && isSelected ? Color.WHITE : TEXT);
                return cell;
            }
        });
        challengeSelector.addActionListener(e -> appendTerminal(
                "ChallengeID seleccionado: " + challengeSelector.getSelectedItem()));
        challengeBox.add(challengeLabel, BorderLayout.WEST);
        challengeBox.add(challengeSelector, BorderLayout.EAST);
        header.add(challengeBox, BorderLayout.EAST);
        return header;
    }

    private JPanel createBrandRail() {
        JPanel rail = new JPanel(new BorderLayout());
        rail.setOpaque(false);
        rail.setPreferredSize(new Dimension(58, 0));
        rail.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));
        rail.add(new NeonBrand(), BorderLayout.CENTER);
        return rail;
    }

    private JPanel createMapPanel() {
        JPanel card = createCard(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(17, 19, 17, 19));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JLabel title = sectionTitle("MAPA DEL GRID");
        JLabel hint = new JLabel("VISTA PREVIA · el tamaño y los datos reales dependen del reto");
        hint.setForeground(MUTED);
        hint.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        heading.add(title, BorderLayout.WEST);
        heading.add(hint, BorderLayout.EAST);
        card.add(heading, BorderLayout.NORTH);

        JPanel mapSurface = new JPanel(new BorderLayout());
        mapSurface.setBackground(new Color(16, 21, 29));
        mapSurface.setBorder(BorderFactory.createLineBorder(BORDER));
        mapSurface.add(new GridPreview(), BorderLayout.CENTER);
        card.add(mapSurface, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTerminalPanel() {
        JPanel card = createCard(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(13, 17, 13, 17));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        heading.add(sectionTitle("TERMINAL"), BorderLayout.WEST);
        JLabel mode = new JLabel("LOCAL OUTPUT");
        mode.setForeground(MUTED);
        mode.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        heading.add(mode, BorderLayout.EAST);
        card.add(heading, BorderLayout.NORTH);

        terminalOutput = new JTextArea();
        terminalOutput.setEditable(false);
        terminalOutput.setLineWrap(true);
        terminalOutput.setWrapStyleWord(true);
        terminalOutput.setBackground(new Color(10, 14, 20));
        terminalOutput.setForeground(new Color(174, 195, 216));
        terminalOutput.setCaretColor(TEXT);
        terminalOutput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        terminalOutput.setBorder(new EmptyBorder(12, 13, 12, 13));
        terminalOutput.setText("[GUI] Interfaz lista. Esperando conexión con DBA Lab...\n");
        JScrollPane scroll = new JScrollPane(terminalOutput);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(terminalOutput.getBackground());
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel createControlPanel() {
        JPanel rail = createCard(new BorderLayout(0, 14));
        rail.setBorder(new EmptyBorder(17, 15, 17, 15));
        rail.setPreferredSize(new Dimension(204, 0));

        JLabel title = sectionTitle("CONTROL");
        rail.add(title, BorderLayout.NORTH);

        JPanel controls = new JPanel(new GridLayout(0, 1, 0, 11));
        controls.setOpaque(false);
        JButton play = styledButton("▶   PLAY", GREEN);
        JButton stop = styledButton("■   DETENER", RED);
        play.addActionListener(e -> appendTerminal("PLAY solicitado para ChallengeID "
                + challengeSelector.getSelectedItem()));
        stop.addActionListener(e -> appendTerminal("DETENER solicitado"));
        controls.add(play);
        controls.add(stop);

        JPanel stateCard = new JPanel(new BorderLayout(0, 8));
        stateCard.setBackground(PANEL_LIGHT);
        stateCard.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel stateCaption = new JLabel("ESTADO DE RUN");
        stateCaption.setForeground(MUTED);
        stateCaption.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        runState = new JLabel("SIN INICIAR");
        runState.setForeground(new Color(238, 190, 91));
        runState.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        stateCard.add(stateCaption, BorderLayout.NORTH);
        stateCard.add(runState, BorderLayout.CENTER);
        controls.add(stateCard);
        rail.add(controls, BorderLayout.CENTER);

        JLabel note = new JLabel("Conexión pendiente");
        note.setForeground(MUTED);
        note.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        rail.add(note, BorderLayout.SOUTH);
        return rail;
    }

    private JPanel createCard(java.awt.LayoutManager layout) {
        JPanel card = new JPanel(layout);
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(1, 1, 1, 1)));
        return card;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        return label;
    }

    private JButton styledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(13, 12, 13, 12));
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        return button;
    }

    private void appendTerminal(String message) {
        if (terminalOutput != null) {
            terminalOutput.append("[GUI] " + message + "\n");
            terminalOutput.setCaretPosition(terminalOutput.getDocument().getLength());
        }
    }

    private static final class GridPreview extends JComponent {
        private static final int ROWS = 12;
        private static final int COLUMNS = 12;

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padding = 26;
            int cell = Math.max(12, Math.min(
                    (getWidth() - padding * 2) / COLUMNS,
                    (getHeight() - padding * 2) / ROWS));
            int width = cell * COLUMNS;
            int height = cell * ROWS;
            int x = (getWidth() - width) / 2;
            int y = (getHeight() - height) / 2;

            for (int row = 0; row < ROWS; row++) {
                for (int column = 0; column < COLUMNS; column++) {
                    g.setColor((row + column) % 2 == 0
                            ? new Color(27, 35, 46) : new Color(24, 31, 41));
                    g.fillRoundRect(x + column * cell + 2, y + row * cell + 2,
                            cell - 4, cell - 4, 5, 5);
                }
            }

            String message = "MAPA EN ESPERA DE DATOS";
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            int messageWidth = g.getFontMetrics().stringWidth(message);
            int boxX = (getWidth() - messageWidth) / 2 - 18;
            int boxY = getHeight() / 2 - 18;
            g.setColor(new Color(13, 17, 24, 225));
            g.fillRoundRect(boxX, boxY, messageWidth + 36, 36, 12, 12);
            g.setColor(MUTED);
            g.drawString(message, (getWidth() - messageWidth) / 2, getHeight() / 2 + 4);
            g.dispose();
        }
    }

    private static final class NeonBrand extends JComponent {
        private static final String BRAND = "CLENCH DBA";
        private static final Color NEON = new Color(83, 234, 255);

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Font brandFont = new Font(Font.SANS_SERIF, Font.BOLD, 19);
            g.setFont(brandFont);
            FontMetrics metrics = g.getFontMetrics();
            g.translate(getWidth() / 2.0, getHeight() / 2.0);
            g.rotate(-Math.PI / 2);

            int textX = -metrics.stringWidth(BRAND) / 2;
            int baseline = metrics.getAscent() / 2;
            g.setColor(new Color(44, 209, 255, 28));
            for (int dx = -5; dx <= 5; dx += 2) {
                for (int dy = -5; dy <= 5; dy += 2) {
                    g.drawString(BRAND, textX + dx, baseline + dy);
                }
            }
            g.setColor(new Color(57, 208, 255, 100));
            g.drawString(BRAND, textX, baseline);
            g.setColor(NEON);
            g.drawString(BRAND, textX, baseline);
            g.dispose();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        botonPlay = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Este es un texto");

        botonPlay.setText("play");
        botonPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPlayActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(148, 148, 148)
                        .addComponent(botonPlay)))
                .addContainerGap(180, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel1)
                .addGap(106, 106, 106)
                .addComponent(botonPlay)
                .addContainerGap(105, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPlayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonPlayActionPerformed

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
    private javax.swing.JButton botonPlay;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
