package lv.kid.brcontrol.game;

import lv.kid.brcontrol.BRController;

/**
 * Created by IntelliJ IDEA.
 * User: Home
 * Date: 05.02.2010
 * Time: 19:44:00
 * To change this template use File | Settings | File Templates.
 */
public class BrainRingIdleState extends State {
    public final State previousState;

    public BrainRingIdleState(BRController controller, BrainRingQuestionState previousState) {
        super(controller);
        this.previousState = previousState;
    }

    @Override
    public void buttonQueued(int teamNo) {
        super.buttonQueued(teamNo);
        controller.dequeue(teamNo);
    }
}
