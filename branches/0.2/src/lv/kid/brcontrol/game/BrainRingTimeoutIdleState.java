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
public class BrainRingTimeoutIdleState extends BrainRingImpl {

    public BrainRingTimeoutIdleState(BRCommanderForm form) {
        super(form.controller);
        this.form = form;
        this.brainRing = form.brainRing;

        form.playSound(BrainRing.SOUND_TIMEOUT);
    }

    @Override
    public void buttonQueued(int teamNo) {
        super.buttonQueued(teamNo);
        controller.dequeue(teamNo);
    }
}