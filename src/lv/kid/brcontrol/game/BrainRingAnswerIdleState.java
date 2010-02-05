package lv.kid.brcontrol.game;

import lv.kid.brcontrol.BRCommanderForm;
import lv.kid.brcontrol.BrainRing;

/**
 * Created by IntelliJ IDEA.
 * User: Home
 * Date: 05.02.2010
 * Time: 19:44:00
 * To change this template use File | Settings | File Templates.
 */
public class BrainRingAnswerIdleState extends BrainRingImpl {
    public final State previousState;

    public BrainRingAnswerIdleState(BRCommanderForm form, State previousState) {
        super(form.controller);
        this.form = form;
        this.brainRing = form.brainRing;
        this.previousState = previousState;

        form.BR_startTimeButton.setSelected(true);
    }

    @Override
    public void buttonQueued(int teamNo) {
        super.buttonQueued(teamNo);
        controller.dequeue(teamNo);
    }

    public void resumeState() {
        for (BrainRing.Team team : brainRing.teams) {
            team.highlight(false);
        }

        form.currentState = previousState;
        previousState.resumeTimer();
    }
}