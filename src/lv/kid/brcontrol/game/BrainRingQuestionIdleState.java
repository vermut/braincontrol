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
public class BrainRingQuestionIdleState extends BrainRingImpl {
    public final State previousState;

    public BrainRingQuestionIdleState(BRCommanderForm form, State previousState) {
        super(form.controller);
        this.form = form;
        this.brainRing = form.brainRing;
        this.previousState = previousState;

        form.BR_newQuestionButton.setSelected(true);
        form.BR_startTimeButton.setEnabled(false);
        form.playSound(BrainRing.SOUND_FALSTART);
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

        form.BR_startTimeButton.setEnabled(true);
        form.setCurrentState(previousState);
        form.playSound(BrainRing.SOUND_ARM);

    }
}
