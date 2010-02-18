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
        try {
            Runtime.getRuntime().
                    exec(form.prefs.get(BRCommanderForm.FOOBAR2000_LOCATION, "C:\\Program Files\\foobar2000\\foobar2000.exe") + " /play");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

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

        try {
            Runtime.getRuntime().
                    exec(form.prefs.get(BRCommanderForm.FOOBAR2000_LOCATION, "C:\\Program Files\\foobar2000\\foobar2000.exe") + " /next");
        } catch (IOException e) {
            e.printStackTrace();
        }
        play();
    }

    public void pause() {
        controller.unblinkLeds((byte) 0xFF);

        if (timer.isRunning())
            pauseTimer();
        else
            resumeTimer();

        try {
            Process p = Runtime.getRuntime().
                    exec(form.prefs.get(BRCommanderForm.FOOBAR2000_LOCATION, "C:\\Program Files\\foobar2000\\foobar2000.exe") + " /pause");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
}
