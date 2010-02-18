package lv.kid.brcontrol.game;

import lv.kid.brcontrol.BRCommanderForm;
import lv.kid.brcontrol.BRController;
import lv.kid.brcontrol.BrainRing;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Home
 * Date: 05.02.2010
 * Time: 20:19:43
 * To change this template use File | Settings | File Templates.
 */
public abstract class BrainRingImpl extends State {
    protected BrainRing brainRing;
    protected BRCommanderForm form;

    public BrainRingImpl(BRController controller) {
        super(controller);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        super.actionPerformed(actionEvent);
        if (timeLeft == 5)
            form.playSound(BrainRing.SOUND_5SEC);
    }

    @Override
    protected void timeOut() {
        pauseTimer();
        form.currentState = new BrainRingTimeoutIdleState(form);
    }

    @Override
    public void displayTime() {
        super.displayTime();
        form.BR_timeLabel.setText((timeLeft / 60) + ":" + ((timeLeft % 60) < 10 ? "0" : "") + (timeLeft % 60));
        form.BR_bigClock.minutes = timeLeft / 60;
        form.BR_bigClock.seconds = 60 - timeLeft % 60;
        form.BR_bigClock.repaint();
    }

    @Override
    public void cleanUp() {
        super.cleanUp();
        resetControllerState();
        pauseTimer();
    }
}
