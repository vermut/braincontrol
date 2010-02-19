package lv.kid.brcontrol.game;

import lv.kid.brcontrol.BRCommanderForm;
import lv.kid.brcontrol.BRController;
import lv.kid.brcontrol.BrainRing;

/**
 * Created by IntelliJ IDEA.
 * User: Home
 * Date: 05.02.2010
 * Time: 19:22:23
 * To change this template use File | Settings | File Templates.
 */
public class BrainRingAnswerState extends BrainRingImpl {
    public final long startMillis;

    public BrainRingAnswerState(BRController controller, BRCommanderForm form) {
        super(controller);
        startMillis = System.currentTimeMillis();
        form.BR_millis.setText("");

        this.form = form;
        this.brainRing = form.brainRing;
        form.playSound(BrainRing.SOUND_START);

        resumeTimer();
    }

    @Override
    public void buttonQueued(int teamNo) {
        form.BR_millis.setText(String.valueOf(System.currentTimeMillis() - startMillis) + " ms");

        pauseTimer();

        for (BrainRing.Team team : brainRing.teams) {
            team.setText(brainRing.teams[teamNo].getTeamName().getText());
        }
        brainRing.teams[teamNo].answered();

        // Ignore others
        form.setCurrentState(new BrainRingAnswerIdleState(form, this));
    }
}