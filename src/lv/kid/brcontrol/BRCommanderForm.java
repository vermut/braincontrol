package lv.kid.brcontrol;

import lv.kid.brcontrol.game.State;
import lv.kid.brcontrol.game.NoState;
import lv.kid.brcontrol.game.TestingState;
import lv.kid.brcontrol.game.GuessMelodyState;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.prefs.Preferences;
import java.io.File;
import java.io.IOException;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;

/**
 * Created by IntelliJ IDEA.
 * User: Home
 * Date: 2009.6.10
 * Time: 00:24:49
 * To change this template use File | Settings | File Templates.
 */
public class BRCommanderForm implements ButtonListener {
    private JCheckBox teamEnabled1;
    private JCheckBox teamEnabled2;
    private JCheckBox teamEnabled3;
    private JCheckBox teamEnabled4;
    private JCheckBox teamEnabled5;
    private JCheckBox teamEnabled6;
    private JCheckBox teamEnabled7;
    private JCheckBox teamEnabled8;
    private JTextField teamName1;
    private JTextField teamName2;
    private JTextField teamName3;
    private JTextField teamName4;
    private JTextField teamName5;
    private JTextField teamName6;
    private JTextField teamName7;
    private JTextField teamName8;
    private JButton applyButton;
    private JTabbedPane tabbedPane;
    private JToggleButton testModeButton;
    private JLabel timeLabel;
    private JPanel GM_queue;
    private JButton GM_playButton;
    private JButton GM_pauseButton;
    private JButton GM_restartButton;
    private JTextField foobar2000LocationTextField;
    private JButton foobarBrowseButton;
    public JTable GM_scoreTable;
    public JTextField GM_teamTextField;
    private JButton GM_a1Button;
    private JButton GM_a2Button;
    public JLabel GM_Timer;
    private ClockDisplay bigClock;
    private JTextField txtScore1;
    private JButton BR_dec1;
    private JButton BR_inc1;
    private JPanel BR_MainPanel;
    private JPanel BR_ClockPanel;
    private JToggleButton BR_Status1;
    private JLabel BR_Team1;

    private BRController controller;
    private State currentState;
    private JCheckBox[] teamEnabled = {teamEnabled1, teamEnabled2, teamEnabled3, teamEnabled4, teamEnabled5, teamEnabled6, teamEnabled7, teamEnabled8};
    private JTextField[] teamName = {teamName1, teamName2, teamName3, teamName4, teamName5, teamName6, teamName7, teamName8};
    private Box GM_queueBox;
    public final Preferences prefs;

    // Preference keys for this package
    public static final String FOOBAR2000_LOCATION = "foobar2000";

    public static void main(String[] args) {
        JFrame frame = new JFrame("BR Commander");
        frame.setContentPane(new BRCommanderForm(args[0]).tabbedPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void buttonPressed(int button) {
        currentState.buttonPressed(button);
    }

    public void buttonReleased(int button) {
        currentState.buttonReleased(button);
    }

    public void buttonQueued(int button) {
        currentState.buttonQueued(button);
    }

    public BRCommanderForm(String myPort) {
        try {
            controller = new BRController(myPort);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        currentState = new NoState(controller);
        controller.addListener(this);

        prefs = Preferences.userNodeForPackage(getClass());

        GM_queueBox = Box.createVerticalBox();
        GM_queue.add(GM_queueBox);
        GM_queue.doLayout();

        foobar2000LocationTextField.setText(prefs.get(FOOBAR2000_LOCATION, "C:\\Program Files\\foobar2000\\foobar2000.exe"));

        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                byte activePorts = 0;
                String[][] activeTeams = new String[8][2];

                for (byte i = 0; i < teamEnabled.length; i++) {
                    JCheckBox jCheckBox = teamEnabled[i];
                    JTextField jTextField = teamName[i];
                    if (jCheckBox.isSelected()) {
                        activePorts |= (1 << i);
                        controller.setText((byte) (1 << i), 1, jTextField.getText());
                        activeTeams[i][0] = jTextField.getText();
                    } else {
                        controller.setText((byte) (1 << i), 1, "Disabled");
                    }
                }

                controller.setActivePorts(activePorts);
                controller.unsetLeds((byte) 0xff);
                controller.buttonPressQueue.clear();

                GM_scoreTable.removeAll();
                final String[] headers = {"Team", "Score"};
                GM_scoreTable.setModel(new DefaultTableModel(activeTeams, headers));
            }
        });

        testModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (testModeButton.isSelected())
                    currentState = new TestingState(controller);
                else
                    currentState = new NoState(controller);
            }
        });

        GM_playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                currentState.cleanUp();
                currentState = new GuessMelodyState(controller, BRCommanderForm.this);
            }
        });


        GM_restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                currentState.nextQuestion();
            }
        });
        foobarBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fc = new JFileChooser("Select foobar2000.exe...");
                int returnVal = fc.showOpenDialog(BRCommanderForm.this.tabbedPane);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        foobar2000LocationTextField.setText(file.getCanonicalPath());
                        prefs.put(FOOBAR2000_LOCATION, file.getCanonicalPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        GM_pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (currentState instanceof GuessMelodyState) {
                    GuessMelodyState guessMelodyState = (GuessMelodyState) currentState;
                    guessMelodyState.pause();
                }
            }
        });
        GM_a1Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (currentState instanceof GuessMelodyState) {
                    GuessMelodyState guessMelodyState = (GuessMelodyState) currentState;
                    guessMelodyState.addScore(1);
                }
            }
        });
        GM_a2Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {

                if (currentState instanceof GuessMelodyState) {
                    GuessMelodyState guessMelodyState = (GuessMelodyState) currentState;
                    guessMelodyState.addScore(2);
                }
            }
        });
    }


    public String getTeamName(int teamNo) {
        return teamName[teamNo].getText();
    }

    public void GM_queueAnswer(final int teamNo) {
        final JButton button = new JButton("Hit from " + getTeamName(teamNo));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                currentState.queuePressed(teamNo);
                button.setEnabled(false);
//                GM_queueBox.remove(button);
//                GM_queue.doLayout();
//                GM_queueBox.doLayout();
            }
        });

        GM_queueBox.add(button);
        GM_queue.doLayout();
        GM_queueBox.doLayout();
    }

    public void GM_clearQueue() {
        GM_queueBox.removeAll();
        GM_queue.doLayout();
        GM_queueBox.doLayout();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setEnabled(true);
        tabbedPane.setFont(new Font(tabbedPane.getFont().getName(), tabbedPane.getFont().getStyle(), tabbedPane.getFont().getSize()));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane.addTab("Brain Ring", panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        BR_ClockPanel = new JPanel();
        BR_ClockPanel.setLayout(new GridBagLayout());
        panel2.add(BR_ClockPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        bigClock = new ClockDisplay();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        BR_ClockPanel.add(bigClock, gbc);
        BR_MainPanel = new JPanel();
        BR_MainPanel.setLayout(new FormLayout("right:52px:noGrow,left:4dlu:noGrow,fill:32px:grow,left:4dlu:noGrow,fill:p:noGrow,fill:p:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        panel2.add(BR_MainPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        BR_Team1 = new JLabel();
        BR_Team1.setText("Label");
        CellConstraints cc = new CellConstraints();
        BR_MainPanel.add(BR_Team1, cc.xy(1, 3));
        txtScore1 = new JTextField();
        BR_MainPanel.add(txtScore1, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        timeLabel = new JLabel();
        timeLabel.setFont(new Font(timeLabel.getFont().getName(), Font.BOLD, 96));
        timeLabel.setHorizontalAlignment(0);
        timeLabel.setHorizontalTextPosition(0);
        timeLabel.setText("0:00");
        BR_MainPanel.add(timeLabel, cc.xyw(1, 1, 8, CellConstraints.CENTER, CellConstraints.DEFAULT));
        BR_dec1 = new JButton();
        BR_dec1.setHorizontalTextPosition(0);
        BR_dec1.setText("-1");
        BR_MainPanel.add(BR_dec1, cc.xy(5, 3));
        BR_inc1 = new JButton();
        BR_inc1.setText("+1");
        BR_MainPanel.add(BR_inc1, cc.xy(6, 3));
        BR_Status1 = new JToggleButton();
        BR_Status1.setText("Status");
        BR_MainPanel.add(BR_Status1, cc.xy(8, 3));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Guess a melody", panel3);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new FormLayout("fill:0dlu:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:317px:noGrow", "center:84px:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow"));
        panel3.add(panel4, BorderLayout.WEST);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:31px:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        panel4.add(panel5, cc.xy(3, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
        GM_playButton = new JButton();
        GM_playButton.setText("Play");
        panel5.add(GM_playButton, cc.xy(1, 1));
        GM_pauseButton = new JButton();
        GM_pauseButton.setText("Pause/Unpause");
        panel5.add(GM_pauseButton, cc.xy(3, 1));
        GM_restartButton = new JButton();
        GM_restartButton.setText("Next song");
        panel5.add(GM_restartButton, cc.xy(5, 1));
        GM_Timer = new JLabel();
        GM_Timer.setFont(new Font(GM_Timer.getFont().getName(), Font.BOLD, 36));
        GM_Timer.setHorizontalAlignment(0);
        GM_Timer.setHorizontalTextPosition(0);
        GM_Timer.setText("0:00");
        panel5.add(GM_Timer, cc.xyw(1, 3, 5, CellConstraints.CENTER, CellConstraints.TOP));
        GM_queue = new JPanel();
        GM_queue.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        GM_queue.setBackground(new Color(-6684775));
        panel4.add(GM_queue, cc.xy(5, 5, CellConstraints.FILL, CellConstraints.FILL));
        GM_queue.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Button Queue"));
        GM_scoreTable = new JTable();
        GM_scoreTable.setAutoCreateRowSorter(false);
        GM_scoreTable.setEnabled(true);
        panel4.add(GM_scoreTable, cc.xy(3, 5, CellConstraints.FILL, CellConstraints.FILL));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel4.add(spacer1, cc.xy(1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,fill:p:noGrow,left:4dlu:noGrow,fill:p:noGrow,left:4dlu:noGrow,fill:p:grow", "center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        panel4.add(panel6, cc.xy(5, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
        panel6.setBorder(BorderFactory.createTitledBorder("Add Score"));
        final JLabel label1 = new JLabel();
        label1.setText("Team:");
        panel6.add(label1, cc.xy(1, 1));
        GM_teamTextField = new JTextField();
        GM_teamTextField.setEditable(false);
        panel6.add(GM_teamTextField, cc.xyw(3, 1, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        GM_a1Button = new JButton();
        GM_a1Button.setText("+1");
        panel6.add(GM_a1Button, cc.xy(3, 3));
        GM_a2Button = new JButton();
        GM_a2Button.setText("+2");
        panel6.add(GM_a2Button, cc.xy(5, 3));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        panel6.add(spacer2, cc.xy(7, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new FormLayout("right:max(d;4px):noGrow,left:4dlu:noGrow,center:57px:noGrow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:d:grow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        tabbedPane.addTab("Teams", panel7);
        panel7.setBorder(BorderFactory.createTitledBorder("Enter team names"));
        final JLabel label2 = new JLabel();
        label2.setFont(new Font(label2.getFont().getName(), Font.BOLD, label2.getFont().getSize()));
        label2.setText("Enabled");
        panel7.add(label2, cc.xy(3, 1));
        final JLabel label3 = new JLabel();
        label3.setFont(new Font(label3.getFont().getName(), Font.BOLD, label3.getFont().getSize()));
        label3.setText("Team name");
        panel7.add(label3, cc.xy(5, 1));
        final JLabel label4 = new JLabel();
        label4.setFont(new Font(label4.getFont().getName(), Font.BOLD, label4.getFont().getSize()));
        label4.setText("No");
        panel7.add(label4, cc.xy(1, 1));
        final JLabel label5 = new JLabel();
        label5.setText("1.");
        panel7.add(label5, cc.xy(1, 3));
        final JLabel label6 = new JLabel();
        label6.setText("2.");
        panel7.add(label6, cc.xy(1, 5));
        final JLabel label7 = new JLabel();
        label7.setText("3.");
        panel7.add(label7, cc.xy(1, 7));
        final JLabel label8 = new JLabel();
        label8.setText("4.");
        panel7.add(label8, cc.xy(1, 9));
        final JLabel label9 = new JLabel();
        label9.setText("5.");
        panel7.add(label9, cc.xy(1, 11));
        final JLabel label10 = new JLabel();
        label10.setText("6.");
        panel7.add(label10, cc.xy(1, 13));
        final JLabel label11 = new JLabel();
        label11.setText("7.");
        panel7.add(label11, cc.xy(1, 15));
        final JLabel label12 = new JLabel();
        label12.setText("8.");
        panel7.add(label12, cc.xy(1, 17));
        teamEnabled1 = new JCheckBox();
        teamEnabled1.setText("");
        panel7.add(teamEnabled1, cc.xy(3, 3));
        teamEnabled2 = new JCheckBox();
        teamEnabled2.setText("");
        panel7.add(teamEnabled2, cc.xy(3, 5));
        teamEnabled3 = new JCheckBox();
        teamEnabled3.setText("");
        panel7.add(teamEnabled3, cc.xy(3, 7));
        teamEnabled4 = new JCheckBox();
        teamEnabled4.setText("");
        panel7.add(teamEnabled4, cc.xy(3, 9));
        teamEnabled5 = new JCheckBox();
        teamEnabled5.setText("");
        panel7.add(teamEnabled5, cc.xy(3, 11));
        teamEnabled6 = new JCheckBox();
        teamEnabled6.setText("");
        panel7.add(teamEnabled6, cc.xy(3, 13));
        teamEnabled7 = new JCheckBox();
        teamEnabled7.setText("");
        panel7.add(teamEnabled7, cc.xy(3, 15));
        teamEnabled8 = new JCheckBox();
        teamEnabled8.setText("");
        panel7.add(teamEnabled8, cc.xy(3, 17));
        teamName1 = new JTextField();
        teamName1.setText("Team 1");
        panel7.add(teamName1, cc.xy(5, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        teamName2 = new JTextField();
        teamName2.setText("Team 2");
        panel7.add(teamName2, cc.xy(5, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        teamName3 = new JTextField();
        teamName3.setText("Team 3");
        panel7.add(teamName3, cc.xy(5, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        teamName4 = new JTextField();
        teamName4.setText("Team 4");
        panel7.add(teamName4, cc.xy(5, 9, CellConstraints.FILL, CellConstraints.DEFAULT));
        teamName5 = new JTextField();
        teamName5.setText("Team 5");
        panel7.add(teamName5, cc.xy(5, 11, CellConstraints.FILL, CellConstraints.DEFAULT));
        teamName6 = new JTextField();
        teamName6.setText("Team 6");
        panel7.add(teamName6, cc.xy(5, 13, CellConstraints.FILL, CellConstraints.DEFAULT));
        teamName7 = new JTextField();
        teamName7.setText("Team 7");
        panel7.add(teamName7, cc.xy(5, 15, CellConstraints.FILL, CellConstraints.DEFAULT));
        teamName8 = new JTextField();
        teamName8.setText("Team 8");
        panel7.add(teamName8, cc.xy(5, 17, CellConstraints.FILL, CellConstraints.DEFAULT));
        applyButton = new JButton();
        applyButton.setText("Apply");
        applyButton.setMnemonic('A');
        applyButton.setDisplayedMnemonicIndex(0);
        panel7.add(applyButton, cc.xyw(1, 21, 3, CellConstraints.CENTER, CellConstraints.DEFAULT));
        testModeButton = new JToggleButton();
        testModeButton.setText("Test Mode");
        panel7.add(testModeButton, cc.xy(5, 21, CellConstraints.LEFT, CellConstraints.DEFAULT));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new FormLayout("fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:400px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:d:noGrow"));
        tabbedPane.addTab("Settings", panel8);
        foobar2000LocationTextField = new JTextField();
        panel8.add(foobar2000LocationTextField, cc.xy(3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label13 = new JLabel();
        label13.setText("Foobar2000 location:");
        panel8.add(label13, cc.xy(1, 1));
        foobarBrowseButton = new JButton();
        foobarBrowseButton.setText("Browse...");
        panel8.add(foobarBrowseButton, cc.xy(5, 1));
        label1.setLabelFor(GM_teamTextField);
        label13.setLabelFor(foobar2000LocationTextField);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return tabbedPane;
    }
}
