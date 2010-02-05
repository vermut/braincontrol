package lv.kid.brcontrol.game;

import lv.kid.brcontrol.BRController;
import lv.kid.brcontrol.ButtonListener;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
    protected int timeLeft;

    public State(BRController controller) {
        this.controller = controller;
        timer.setRepeats(true);
    }

    public void buttonPressed(int teamNo) {
    }

    public void buttonReleased(int teamNo) {
    }

    public void buttonQueued(int teamNo) {
    }

    public void queuePressed(int teamNo) {

    }

    public void nextQuestion() {
        controller.clearQueue();
        controller.unblinkLeds((byte) 0xFF);
        controller.unsetLeds((byte) 0xFF);
    }

    public void cleanUp() {

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        timeLeft--;
        controller.setText((byte) 0xFF, 2, "  " + (timeLeft / 60) + ":" + (timeLeft < 10 ? "0" : "") + (timeLeft - (timeLeft / 60) * 60));

        if (timeLeft < 0) {
            controller.setText((byte) 0xFF, 2, "Time out!");
//            pauseTimer();
            timeOut();
        }
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

    protected void resumeTimer() {
        timer.start();
    }
}
