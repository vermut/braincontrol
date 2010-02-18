package lv.kid.brcontrol;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import lv.kid.brcontrol.game.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Home
 * Date: 05.02.2010
 * Time: 15:29:55
 * To change this template use File | Settings | File Templates.
 */
public class BrainRing {
    private final BRCommanderForm form;

    public Team[] teams = new Team[8];
    public static final String SOUND_ARM = "sound/arm.wav";
    public static final String SOUND_START = "sound/start.wav";
    public static final String SOUND_FALSTART = "sound/falstart.wav";
    public static final String SOUND_5SEC = "sound/5sec.wav";
    public static final String SOUND_TIMEOUT = "sound/timeout.wav";

    public BrainRing(final BRCommanderForm form) {
        this.form = form;

        form.BR_ScoreTable.setLayout(new FormLayout("right:52px:noGrow,left:4dlu:noGrow,fill:32px:grow,left:4dlu:noGrow,fill:p:noGrow,fill:p:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow",
                "center:max(d;4px):noGrow,top:4dlu:noGrow," +
                        "center:max(d;4px):noGrow,top:4dlu:noGrow," +
                        "center:max(d;4px):noGrow,top:4dlu:noGrow," +
                        "center:max(d;4px):noGrow,top:4dlu:noGrow," +
                        "center:max(d;4px):noGrow,top:4dlu:noGrow," +
                        "center:max(d;4px):noGrow,top:4dlu:noGrow," +
                        "center:max(d;4px):noGrow,top:4dlu:noGrow," +
                        "center:max(d;4px):noGrow,top:4dlu:noGrow"));

        for (int i = 0; i < teams.length; i++) {
            teams[i] = new Team(form.controller, i);
            createTeamUI(teams[i], (i * 2) + 1);
            teams[i].show(false);
        }

        form.BR_newQuestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (form.BR_newQuestionButton.isSelected()) {
                    form.currentState.cleanUp();
                    form.currentState = new BrainRingQuestionState(form.controller, form);
                    form.BR_newQuestionButton.setSelected(false);
                } else {
                    if (form.currentState instanceof BrainRingQuestionIdleState) {
                        BrainRingQuestionIdleState brainRingQuestionIdleState = (BrainRingQuestionIdleState) form.currentState;
                        brainRingQuestionIdleState.resumeState();
                    }
                }
            }
        });
        form.BR_startTimeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (form.BR_startTimeButton.isSelected()) {
                    form.currentState.cleanUp();

                    // Save time from NewQuestionState
                    int timeLeft = form.currentState.timeLeft;
                    form.currentState = new BrainRingAnswerState(form.controller, form);
                    form.currentState.timeLeft = timeLeft;
                    form.BR_startTimeButton.setSelected(false);
                } else {
                    if (form.currentState instanceof BrainRingAnswerIdleState) {
                        BrainRingAnswerIdleState brainRingAnswerIdleState = (BrainRingAnswerIdleState) form.currentState;
                        brainRingAnswerIdleState.resumeState();
                    }
                }
            }
        });
        form.BR_resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                form.BR_newQuestionButton.setEnabled(true);
                form.BR_newQuestionButton.setSelected(false);

                form.BR_startTimeButton.setEnabled(false);
                form.BR_startTimeButton.setSelected(false);

                for (Team team : teams) {
                    team.reset();
                    form.currentState.cleanUp();
                    form.currentState = new NoState(form.controller);
                }
            }
        });

        form.BR_Add1.addActionListener(new TimeActionListener(1));
        form.BR_Dec1.addActionListener(new TimeActionListener(-1));
        form.BR_Add10.addActionListener(new TimeActionListener(10));
        form.BR_Dec10.addActionListener(new TimeActionListener(-10));

    }

    public void updateTeams(String[][] activeTeams) {
        for (int i = 0; i < activeTeams.length; i++) {
            String activeTeam = activeTeams[i][0];

            if (activeTeam == null)
                teams[i].show(false);
            else {
                teams[i].getTeamName().setText(activeTeam);
                teams[i].getStatus().setText(Team.IDLE);
                teams[i].show(true);
            }
        }
    }

    private void createTeamUI(final Team team, int row) {
        team.setTeamName(new JLabel());
        CellConstraints cc = new CellConstraints();
        form.BR_ScoreTable.add(team.getTeamName(), cc.xy(1, row));

        team.setScore(new JTextField("0"));
        form.BR_ScoreTable.add(team.getScore(), cc.xy(3, row, CellConstraints.FILL, CellConstraints.DEFAULT));

        team.setDec1(new JButton("-1"));
        team.getDec1().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                team.addScore(-1);
            }
        });
        form.BR_ScoreTable.add(team.getDec1(), cc.xy(5, row));

        team.setInc1(new JButton("+1"));
        team.getInc1().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                team.addScore(1);
            }
        });
        form.BR_ScoreTable.add(team.getInc1(), cc.xy(6, row));

        team.setStatus(new JToggleButton(Team.IDLE));
        team.getStatus().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (actionEvent.getSource() instanceof JToggleButton) {
                    JToggleButton jToggleButton = (JToggleButton) actionEvent.getSource();
                    if (!jToggleButton.isSelected()) {
                        // Unban
                        form.controller.dequeue(team.teamNo);
                        team.arm();
                    } else {
                        // ban
                        form.controller.forcequeue(team.teamNo);
                        team.falstart();
                    }
                }

            }
        });
        form.BR_ScoreTable.add(team.getStatus(), cc.xy(8, row));
    }

    public static class Team {
        private JLabel teamName;
        private JTextField score;
        private JButton dec1;
        private JButton inc1;
        private JToggleButton status;
        private static final String ARMED = "Armed";
        private final BRController controller;
        private final int teamNo;
        private static final String FALSTART = "Falstart";
        private static final String ANSWER = "Answer!";
        private static final String IDLE = "Idle";

        public Team(BRController controller, int i) {
            this.controller = controller;
            teamNo = i;
        }

        public void show(boolean show) {
            teamName.setVisible(show);
            score.setVisible(show);
            dec1.setVisible(show);
            inc1.setVisible(show);
            status.setVisible(show);
        }

        public JLabel getTeamName() {
            return teamName;
        }

        public JTextField getScore() {
            return score;
        }

        public JButton getDec1() {
            return dec1;
        }

        public JButton getInc1() {
            return inc1;
        }

        public JToggleButton getStatus() {
            return status;
        }

        public void setTeamName(JLabel teamName) {
            this.teamName = teamName;
        }

        public void setScore(JTextField score) {
            this.score = score;
        }

        public void setDec1(JButton dec1) {
            this.dec1 = dec1;
        }

        public void setInc1(JButton inc1) {
            this.inc1 = inc1;
        }

        public void setStatus(JToggleButton status) {
            this.status = status;
        }

        public void addScore(int i) {
            try {
                score.setText((Integer.parseInt(score.getText()) + i) + "");
            } catch (NumberFormatException e) {
                score.setText(String.valueOf(i));
            }
        }

        public void reset() {
            score.setText(0 + "");
            status.setText(IDLE);
            status.setSelected(false);
        }

        public void arm() {
            if (!teamName.isVisible())
                return;

            highlight(false);
            status.setText(ARMED);
            controller.setText(BRController.teamToByte(teamNo), 2, ARMED);
            status.setSelected(false);
        }

        public void falstart() {
            status.setText(FALSTART);
            status.setSelected(true);
            controller.setText(BRController.teamToByte(teamNo), 2, FALSTART);
            highlight(true);
        }

        public void answered() {
            status.setText(ANSWER);
            status.setSelected(true);
            controller.setText(BRController.teamToByte(teamNo), 2, ANSWER);
            highlight(true);
        }

        public void highlight(boolean b) {
            teamName.setForeground(b ? Color.red : Color.black);
        }

        public boolean isOut() {
            return status.isSelected();
        }
    }

    private class TimeActionListener implements ActionListener {
        private final int timeShift;

        private TimeActionListener(int timeShift) {
            this.timeShift = timeShift;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            form.currentState.timeLeft += timeShift;
            form.currentState.displayTime();
        }
    }
}
