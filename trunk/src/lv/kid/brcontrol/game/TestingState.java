package lv.kid.brcontrol.game;

import lv.kid.brcontrol.ButtonListener;
import lv.kid.brcontrol.BRController;

/**
 * Created by IntelliJ IDEA.
 * User: Home
 * Date: 2009.7.10
 * Time: 23:19:31
 * To change this template use File | Settings | File Templates.
 */
public class TestingState extends State {
    public TestingState(BRController pController) {
        super(pController);
    }

    public void buttonPressed(int button) {
        System.out.println("button = " + (1 << button));
        controller.setLeds((byte) (1 << button));
        controller.setText((byte) (1 << button), 2, "Bazinga!");
    }


    public void buttonReleased(int button) {
        controller.unsetLeds((byte) (1 << button));
        //   controller.buttonPressQueue.remove();
        controller.setText((byte) (1 << button), 2, "");
    }
}
