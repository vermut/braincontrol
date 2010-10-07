package lv.kid.brcontrol;

import sun.awt.VerticalBagLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Home
 * Date: 2009.8.10
 * Time: 00:22:57
 * To change this template use File | Settings | File Templates.
 */
class TeamButtonQueuePanel extends JPanel {
    private BRCommanderForm commanderForm;

    public TeamButtonQueuePanel() {
        super(new VerticalBagLayout());
    }

    public void addTeam(int teamNo) {
        add(new JLabel("Hit from " + commanderForm.getTeamName(teamNo)));
    }

    public void setCommanderForm(BRCommanderForm commanderForm) {
        this.commanderForm = commanderForm;
    }
}
