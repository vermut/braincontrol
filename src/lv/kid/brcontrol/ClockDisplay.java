package lv.kid.brcontrol;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Home
 * Date: 2009.7.10
 * Time: 13:32:29
 * To change this template use File | Settings | File Templates.
 */
public class ClockDisplay extends JPanel {
    public int hours, minutes, seconds;
    int centerX, centerY;
    int rClock, rNumbers, rHourHand, rMinHand, rSecHand;
    double angle;
    int width1, width2, height;
    Font f;

    public void init() {
        Graphics g = getGraphics();
        f = new Font("Monospaced", Font.BOLD, 36);
        g.setFont(f);
        width1 = g.getFontMetrics().stringWidth("0");
        width2 = g.getFontMetrics().stringWidth("00");
        height = g.getFontMetrics().getAscent();

//      Calendar cal = Calendar.getInstance();
//      hours = cal.get(Calendar.HOUR);
//      minutes =  cal.get(Calendar.MINUTE);
//      seconds = cal.get(Calendar.SECOND);
//      repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int margin = 20;
        rClock = Math.min(getWidth(), getHeight()) / 2 - margin;  // clock rad
        rNumbers = (int) Math.round(80 * rClock / 100.0);  // number radius
        rHourHand = (int) (6.0 * rClock / 10);             // hours hand length
        rMinHand = (int) Math.round(7.5 * rClock / 10);    // minutes hand length
        rSecHand = (int) Math.round(87 * rClock / 100.0);  // seconds hand length
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;

        // drawing the clock
        g.setColor(Color.black);
        g.drawOval(getWidth() / 2 - rClock, getHeight() / 2 - rClock, 2 * rClock, 2 * rClock);

        // draw the numbers
        g.setFont(f);
        g.setColor(Color.black);
        for (int i = 0; i < 12; i++) {
            int j = (i + 3) % 12;
            if (j == 0) j = 12;
            int width = width1;
            if (j > 9) width = width2;
            angle = i * 30 * Math.PI / 180;
            g.drawString(j * 5 + "", centerX + (int) (rNumbers * Math.cos(angle) - width / 2.0),
                    centerY + (int) (rNumbers * Math.sin(angle) + height / 2.0) - 2);
        }

        //draw the hours hand
        g.setColor(Color.magenta);
        angle = (90 - (hours + minutes / 60.0) * 30.0) * Math.PI / 180;
//      g.drawLine(centerX, centerY, (int)(centerX + rHourHand*Math.cos(angle)),
//                           (int)(centerY - rHourHand*Math.sin(angle)));
        // drawArm(g, 30, rHourHand);

        //draw the minutes hand
        g.setColor(Color.blue);
        angle = (90 - minutes * 6.0) * Math.PI / 180;
//      g.drawLine(centerX, centerY, (int)(centerX +
//      rMinHand*Math.cos(angle)),
//                           (int)(centerY - rMinHand*Math.sin(angle)));
        drawArm(g, 20, rMinHand);

        // draw the seconds hand
        g.setColor(Color.red);
        angle = (90 - seconds * 6.0) * Math.PI / 180;
//      g.drawLine(centerX, centerY, (int)(centerX +
//      rSecHand*Math.cos(angle)),
//                           (int)(centerY - rSecHand*Math.sin(angle)));
        drawArm(g, 30, rSecHand);


        g.setColor(Color.black);
        g.fillOval(centerX - 5, centerY - 5, 10, 10);

        // draw the minutes ticks
        for (int i = 0; i < 60; i++) {
            angle = i * 6 * Math.PI / 180;
            if (i % 5 != 0)
                g.drawLine(centerX + (int) (93 * rClock * Math.cos(angle) / 100),
                        centerY - (int) (93 * rClock * Math.sin(angle) / 100),
                        centerX + (int) (100 * rClock * Math.cos(angle) / 100),
                        centerY - (int) (100 * rClock * Math.sin(angle) / 100));
        }

        // draw the hours ticks
        for (int i = 0; i < 12; i++) {
            angle = i * 30 * Math.PI / 180;
            g.drawLine(centerX + (int) (87 * rClock * Math.cos(angle) / 100),
                    centerY - (int) (87 * rClock * Math.sin(angle) / 100),
                    centerX + (int) (100 * rClock * Math.cos(angle) / 100),
                    centerY - (int) (100 * rClock * Math.sin(angle) / 100));
        }

    }

    void drawArm(Graphics g, int r, int R) {
        int[] x = new int[3];
        int[] y = new int[3];
        x[0] = (int) (centerX + r * Math.cos(angle + 3 * Math.PI / 4));
        x[1] = (int) (centerX + r * Math.cos(angle - 3 * Math.PI / 4));
        x[2] = (int) (centerX + R * Math.cos(angle));
        y[0] = (int) (centerY - r * Math.sin(angle + 3 * Math.PI / 4));
        y[1] = (int) (centerY - r * Math.sin(angle - 3 * Math.PI / 4));
        y[2] = (int) (centerY - R * Math.sin(angle));
        g.fillPolygon(x, y, 3);
    }
}
