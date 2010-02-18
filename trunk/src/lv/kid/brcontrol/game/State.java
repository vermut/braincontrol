package lv.kid.brcontrol.game;

import lv.kid.brcontrol.BRController;
import lv.kid.brcontrol.ButtonListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Home
 * Date: 2009.7.10
 * Time: 23:17:41
 * To change this template use File | Settings | File Templates.
 */
public abstract class State implements ButtonListener, ActionListener {
    protected BRController controller;
    protected Timer timer = new Timer(1000, this);
    public int timeLeft;

    public State(BRController controller) {
        this.controller = controller;
        timer.setRepeats(true);
    }

    public void buttonPressed(int teamNo) {
    }

    public void buttonReleased(int teamNo) {
    }

    // Called when a team queues it's answer
    public void buttonQueued(int teamNo) {
    }

    // Call to give right to answer to a team
    public void queuePressed(int teamNo) {

    }

    public void resetControllerState() {
        controller.clearQueue();
        controller.unblinkLeds((byte) 0xFF);
        controller.unsetLeds((byte) 0xFF);
    }

    public void cleanUp() {

    }

    @Override
    // Timer ticks
    public void actionPerformed(ActionEvent actionEvent) {
        timeLeft--;
        displayTime();

        if (timeLeft <= 0) {
            controller.setText((byte) 0xFF, 2, "Time out!");
//            pauseTimer();
            timeOut();
        }
    }

    public void displayTime() {
        controller.setText((byte) 0xFF, 2, "  " + (timeLeft / 60) + ":" + ((timeLeft % 60) < 10 ? "0" : "") + (timeLeft % 60));
    }

    protected void timeOut() {

    }

    protected void startTimer(int pTimeLeft) {
        timeLeft = pTimeLeft;
        timer.start();
    }

    protected void pauseTimer() {
        timer.stop();
    }

    public void resumeTimer() {
        timer.start();
    }
}
