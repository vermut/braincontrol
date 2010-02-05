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
    public BrainRingAnswerState(BRController controller, BRCommanderForm form) {
        super(controller);
        this.form = form;
        this.brainRing = form.brainRing;

        resumeTimer();
    }

    @Override
    public void buttonQueued(int teamNo) {
        pauseTimer();

        brainRing.teams[teamNo].answered();

        // Ignore others
        form.currentState = new BrainRingAnswerIdleState(form, this);
    }

    @Override
    public void cleanUp() {
        super.cleanUp();
        super.nextQuestion();
        pauseTimer();
    }
}