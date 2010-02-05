package lv.kid.brcontrol;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Calendar;

public class BigClock extends JFrame implements ActionListener
{
  private ClockDisplay clock;

  public static void main(String[] args)
  {
    BigClock application = new BigClock();
    application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  public BigClock()
  {
    super("Analog Clock");
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize = toolkit.getScreenSize();
    int screenWidth = screenSize.width;
    int screenHeight = screenSize.height;
    if (screenWidth > screenHeight)
    {
      setSize(screenHeight, screenHeight);
      setLocation((screenWidth-screenHeight)/2, 0);
    }
    else
    {
      setSize(screenWidth, screenWidth);
      setLocation(0, (screenHeight-screenWidth)/2);
    }

    Container pane = getContentPane();
    clock = new ClockDisplay();
    clock.setBackground(new Color(0xffffe0));
    pane.add(clock);
    setVisible(true);

    clock.init();
    Timer timer = new Timer(1000, this);
    timer.start();
  }

  public void actionPerformed(ActionEvent e)
  {
    clock.seconds++;
    if (clock.seconds == 60)
    {
      clock.seconds = 0;
      clock.minutes++;
      if (clock.minutes == 60)
      {
        clock.minutes = 0;
        clock.hours++;
        if (clock.hours == 24)
          clock.hours = 0;
      }
    }
    clock.repaint();
  }
}

