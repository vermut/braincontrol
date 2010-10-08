package lv.kid.brcontrol;

import lv.kid.brcontrol.game.GuessMelodyState;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: nadia
 * Date: 2010.9.10
 * Time: 00:40:36
 * To change this template use File | Settings | File Templates.
 */
public class RunningLights implements ActionListener {
    private final BRCommanderForm form;
    private Vector<Byte> teams = new Vector<Byte>();
    private int currentTeam = 0;
    private final Timer timer;

    public RunningLights(BRCommanderForm form) {
        this.form = form;
        timer = new Timer(250, this);
        timer.setRepeats(true);
        timer.start();
    }

    public void updateTeams(String[][] activeTeams) {
        teams = new Vector<Byte>();
        for (int i = 0; i < activeTeams.length; i++) {
            String activeTeam = activeTeams[i][0];

            if (activeTeam != null)
                teams.add(BRController.teamToByte(i));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (teams.size() == 0)
            return;

        if (!form.chkRunningLights.isSelected()) {
            if (!form.chkMicActivated.isSelected() && form.currentState instanceof GuessMelodyState) {
                GuessMelodyState guessMelodyState = (GuessMelodyState) form.currentState;
                guessMelodyState.setActiveTeams((byte) 0xFF);

            }
            return;
        }

        form.controller.unsetLeds(teams.get(currentTeam));

        currentTeam++;
        if (currentTeam == teams.size())
            currentTeam = 0;

        form.controller.setLeds(teams.get(currentTeam));
        if (form.currentState instanceof GuessMelodyState) {
            GuessMelodyState guessMelodyState = (GuessMelodyState) form.currentState;
            guessMelodyState.setActiveTeams(teams.get(currentTeam));

        }
    }
}
