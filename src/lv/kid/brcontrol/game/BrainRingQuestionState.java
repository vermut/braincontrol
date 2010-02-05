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
public class BrainRingQuestionState extends BrainRingImpl {

    public BrainRingQuestionState(BRController controller, BRCommanderForm form) {
        super(controller);
        this.form = form;
        this.brainRing = form.brainRing;

        for (BrainRing.Team team : brainRing.teams) {
            team.arm();
        }

        timeLeft = 60;
        displayTime();
        
        form.BR_startTimeButton.setSelected(false);
        form.BR_startTimeButton.setEnabled(true);
        form.BR_bigClock.init();
    }

    @Override
    public void buttonQueued(int teamNo) {
        // Falstart
        timeLeft = 20;
        displayTime();

        brainRing.teams[teamNo].falstart();

        // Ignore others
        form.currentState = new BrainRingQuestionIdleState(form, this);
    }

}
