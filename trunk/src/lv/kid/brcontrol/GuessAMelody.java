package lv.kid.brcontrol;

import lv.kid.brcontrol.game.GuessMelodyState;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Home
 * Date: 05.02.2010
 * Time: 15:31:40
 * To change this template use File | Settings | File Templates.
 */
public class GuessAMelody {
    private final BRCommanderForm form;

    private Box GM_queueBox;

    public GuessAMelody(final BRCommanderForm form) {
        this.form = form;

        GM_queueBox = Box.createVerticalBox();
        form.GM_queue.add(GM_queueBox);
        form.GM_queue.doLayout();

        form.GM_playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                form.currentState.cleanUp();
                form.currentState = new GuessMelodyState(form.controller, form);
            }
        });

        form.GM_restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                form.currentState.resetControllerState();
            }
        });

        form.GM_pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (form.currentState instanceof GuessMelodyState) {
                    GuessMelodyState guessMelodyState = (GuessMelodyState) form.currentState;
                    guessMelodyState.pause();
                }
            }
        });
        form.GM_a1Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (form.currentState instanceof GuessMelodyState) {
                    GuessMelodyState guessMelodyState = (GuessMelodyState) form.currentState;
                    guessMelodyState.addScore(1);
                }
            }
        });
        form.GM_a2Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (form.currentState instanceof GuessMelodyState) {
                    GuessMelodyState guessMelodyState = (GuessMelodyState) form.currentState;
                    guessMelodyState.addScore(2);
                }
            }
        });
    }

    public void updateTeams(String[][] activeTeams) {
        form.GM_scoreTable.removeAll();
        final String[] headers = {"Team", "Score"};
        form.GM_scoreTable.setModel(new DefaultTableModel(activeTeams, headers));
    }

    public void GM_queueAnswer(final int teamNo) {
        final JButton button = new JButton("Hit from " + form.getTeamName(teamNo));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                form.currentState.queuePressed(teamNo);
                button.setEnabled(false);
//                GM_queueBox.remove(button);
//                GM_queue.doLayout();
//                GM_queueBox.doLayout();
            }
        });

        GM_queueBox.add(button);
        form.GM_queue.doLayout();
        GM_queueBox.doLayout();
    }

    public void GM_clearQueue() {
        GM_queueBox.removeAll();
        form.GM_queue.doLayout();
        GM_queueBox.doLayout();
    }
}
