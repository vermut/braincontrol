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
    private final State previousState;

    public BrainRingAnswerIdleState(BRCommanderForm form, State previousState) {
        super(form.controller);
        this.form = form;
        this.brainRing = form.brainRing;
        this.previousState = previousState;

        form.BR_startTimeButton.setSelected(true);
        form.playSound(BrainRing.SOUND_ARM);
    }

    @Override
    public void buttonQueued(int teamNo) {
        super.buttonQueued(teamNo);
        controller.dequeue(teamNo);
    }

    public void resumeState() {
        boolean allTeamsOut = true;
        for (BrainRing.Team team : brainRing.teams) {
            team.highlight(false);
            if (team.isActive() && !team.isOut())
                allTeamsOut = false;
        }

        if (allTeamsOut) {
            // Go to timeout State
            timeOut();
        } else {
            // Continue game
            form.setCurrentState(previousState);
            form.playSound(BrainRing.SOUND_START);
            previousState.timeLeft = 20;
            previousState.displayTime();
            previousState.resumeTimer();
        }
    }
}