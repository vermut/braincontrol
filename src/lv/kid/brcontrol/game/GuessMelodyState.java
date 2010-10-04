package lv.kid.brcontrol.game;

import lv.kid.brcontrol.BRCommanderForm;
import lv.kid.brcontrol.BRController;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Home
 * Date: 2009.8.10
 * Time: 00:54:31
 * To change this template use File | Settings | File Templates.
 */
public class GuessMelodyState extends State {
    private final BRCommanderForm form;

    Set<Integer> history = new HashSet<Integer>();
    int currentTeam = 0;

    public GuessMelodyState(BRController controller, BRCommanderForm form) {
        super(controller);
        this.form = form;

        play();
    }

    private void play() {
        controller.clearQueue();
        getPlayer().play();


        startTimer(60);
    }

    @Override
    public void queuePressed(int teamNo) {
        controller.unblinkLeds((byte) 0xFF);
        controller.blinkLeds(BRController.teamToByte(teamNo));
        history.remove(teamNo);

        form.GM_teamTextField.setText(form.getTeamName(teamNo));
        currentTeam = teamNo;
    }

    @Override
    protected void timeOut() {
        form.GM_Timer.setText("Timeout");
        pause();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        super.actionPerformed(actionEvent);
        form.GM_Timer.setText((timeLeft / 60) + ":" + (timeLeft < 10 ? "0" : "") + (timeLeft - (timeLeft / 60) * 60));
    }

    @Override
    public void buttonQueued(int teamNo) {
        if (timeLeft < 0)
            return;

        if (history.size() == 0) {
            pause();
            pauseTimer();
        }

        history.add(teamNo);

        form.guessM.GM_queueAnswer(teamNo);
        controller.setLeds(BRController.teamToByte(teamNo));
        controller.setText(BRController.teamToByte(teamNo), 2, "Queued " + history.size());
    }

    @Override
    public void cleanUp() {
        super.cleanUp();
        super.resetControllerState();
        form.guessM.GM_clearQueue();
        history.clear();
        pauseTimer();
        currentTeam = 0;
        form.GM_teamTextField.setText("");
    }

    @Override
    public void resetControllerState() {
        cleanUp();

        getPlayer().next();


        play();
    }

    public void pause() {
        controller.unblinkLeds((byte) 0xFF);

        if (timer.isRunning()) {
            pauseTimer();
            getPlayer().pause();
        } else {
            resumeTimer();
            getPlayer().unpause();
        }


    }

    public void addScore(int i) {
        int score;
        try {
            score = (Integer) form.GM_scoreTable.getValueAt(currentTeam, 1);
        } catch (Exception e) {
            score = 0;
        }

        form.GM_scoreTable.setValueAt(score + i, currentTeam, 1);
    }

    Player getPlayer() {
        if (form.playerComboBox.getSelectedIndex() == BRCommanderForm.PLAYER_FOOBAR2000)
            return new Player() {
                @Override
                void play() {
                    try {
                        Runtime.getRuntime().
                                exec(form.prefs.get(BRCommanderForm.FOOBAR2000_LOCATION, "C:\\Program Files\\foobar2000\\foobar2000.exe") + " /play");
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                }

                @Override
                void pause() {
                    try {
                        Process p = Runtime.getRuntime().
                                exec(form.prefs.get(BRCommanderForm.FOOBAR2000_LOCATION, "C:\\Program Files\\foobar2000\\foobar2000.exe") + " /pause");
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

                void unpause() {
                    pause();
                }

                @Override
                void next() {
                    try {
                        Runtime.getRuntime().
                                exec(form.prefs.get(BRCommanderForm.FOOBAR2000_LOCATION, "C:\\Program Files\\foobar2000\\foobar2000.exe") + " /next");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };


        if (form.playerComboBox.getSelectedIndex() == BRCommanderForm.PLAYER_TCMP)
            return new Player() {
                @Override
                void play() {
                    try {
                        Runtime.getRuntime().
                                exec(form.prefs.get(BRCommanderForm.TCMP_LOCATION, "C:\\Program Files\\CoreCodec\\The Core Media Player\\TCMPControl.exe") + " -cmd " + TCMP_CMD_Play);
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

                @Override
                void unpause() {
                    play();
                }

                void pause() {
                    try {
                        Runtime.getRuntime().
                                exec(form.prefs.get(BRCommanderForm.TCMP_LOCATION, "C:\\Program Files\\CoreCodec\\The Core Media Player\\TCMPControl.exe") + " -cmd " + TCMP_CMD_Pause);
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

                @Override
                void next() {
                    try {
                        Runtime.getRuntime().
                                exec(form.prefs.get(BRCommanderForm.TCMP_LOCATION, "C:\\Program Files\\CoreCodec\\The Core Media Player\\TCMPControl.exe") + " -cmd " + TCMP_CMD_Next);
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            };

        return null;
    }

    public abstract class Player {
        public static final int TCMP_CMD_Play = 1;
        public static final int TCMP_CMD_Pause = 2;
        public static final int TCMP_CMD_Stop = 3;
        public static final int TCMP_CMD_Next = 4;


        abstract void play();

        abstract void pause();

        abstract void unpause();

        abstract void next();
    }
}
