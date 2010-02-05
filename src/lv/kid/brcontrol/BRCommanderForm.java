package lv.kid.brcontrol;

import lv.kid.brcontrol.game.NoState;
import lv.kid.brcontrol.game.State;
import lv.kid.brcontrol.game.TestingState;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.Preferences;

/**
 * Created by IntelliJ IDEA.
 * User: Home
 * Date: 2009.6.10
 * Time: 00:24:49
 * To change this template use File | Settings | File Templates.
 */
public class BRCommanderForm implements ButtonListener {
    JCheckBox teamEnabled1;
    JCheckBox teamEnabled2;
    JCheckBox teamEnabled3;
    JCheckBox teamEnabled4;
    JCheckBox teamEnabled5;
    JCheckBox teamEnabled6;
    JCheckBox teamEnabled7;
    JCheckBox teamEnabled8;
    JTextField teamName1;
    JTextField teamName2;
    JTextField teamName3;
    JTextField teamName4;
    JTextField teamName5;
    JTextField teamName6;
    JTextField teamName7;
    JTextField teamName8;
    private JButton applyButton;
    private JTabbedPane tabbedPane;
    private JToggleButton testModeButton;
    public JLabel BR_timeLabel;
    JPanel GM_queue;
    JButton GM_playButton;
    JButton GM_pauseButton;
    JButton GM_restartButton;
    private JTextField foobar2000LocationTextField;
    private JButton foobarBrowseButton;
    public JTable GM_scoreTable;
    public JTextField GM_teamTextField;
    JButton GM_a1Button;
    JButton GM_a2Button;
    public JLabel GM_Timer;
    public ClockDisplay BR_bigClock;
    JPanel BR_MainPanel;
    JPanel BR_ClockPanel;
    JPanel BR_ScoreTable;
    public JToggleButton BR_newQuestionButton;
    JButton BR_Add1;
    JButton BR_Dec1;
    public JToggleButton BR_startTimeButton;
    JButton BR_resetButton;
    JButton BR_Dec10;
    JButton BR_Add10;

    public BRController controller;
    public State currentState;
    private JCheckBox[] teamEnabled = {teamEnabled1, teamEnabled2, teamEnabled3, teamEnabled4, teamEnabled5, teamEnabled6, teamEnabled7, teamEnabled8};
    private JTextField[] teamName = {teamName1, teamName2, teamName3, teamName4, teamName5, teamName6, teamName7, teamName8};
    public final Preferences prefs;

    // Preference keys for this package
    public static final String FOOBAR2000_LOCATION = "foobar2000";
    public final BrainRing brainRing;
    public final GuessAMelody guessM;

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, IllegalAccessException, InstantiationException {
        UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());

        JFrame frame = new JFrame("BrainRing Controller");
        frame.setContentPane(new BRCommanderForm(args.length == 0 ? null : args[0]).tabbedPane);
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
            e.printStackTrace();
        }

        currentState = new NoState(controller);
        controller.addListener(this);

        prefs = Preferences.userNodeForPackage(getClass());

        brainRing = new BrainRing(this);
        guessM = new GuessAMelody(this);

        foobar2000LocationTextField.setText(prefs.get(FOOBAR2000_LOCATION, "C:\\Program Files\\foobar2000\\foobar2000.exe"));

        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                controller.reset();
                controller.setText((byte) 0xFF, 1, "RESET");
                controller.reset();

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

                guessM.updateTeams(activeTeams);
                brainRing.updateTeams(activeTeams);
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

    }

    public String getTeamName(int teamNo) {
        return teamName[teamNo].getText();
    }


    public void playSound(String filename) {
        // Open an input stream  to the audio file.
        InputStream in;
        
        try {
            // Create an AudioStream object from the input stream.
            in = new FileInputStream(filename);
            AudioStream as = new AudioStream(in);

            // Use the static class member "player" from class AudioPlayer to play clip.
            AudioPlayer.player.start(as);

            // Similarly, to stop the audio.
            // AudioPlayer.player.stop(as);

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
