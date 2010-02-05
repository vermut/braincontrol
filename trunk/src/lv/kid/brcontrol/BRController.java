package lv.kid.brcontrol;

import gnu.io.*;

import java.io.*;
import java.util.*;

import processing.app.RunnerException;

public class BRController {
    public static int MAX_MESSAGE = 8;

    private InputStream input;
    private OutputStream output;

    public LinkedList<Integer> buttonPressQueue = new LinkedList<Integer>();
    private Vector<ButtonListener> buttonListeners = new Vector<ButtonListener>();
    private byte lastState = 0;


    public void addListener(ButtonListener buttonListener) {
        buttonListeners.add(buttonListener);
    }

    public BRController(String myPort) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException, RunnerException {
        final int baud = 115200;
        if (myPort == null)
            myPort = new ComPortLocator().getComPort();
        System.out.println("Using port: " + myPort);

        System.out.println("Baud:" + baud);
        CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(myPort);

        SerialPort port = (SerialPort) portId.open("serial talk", 4000);
        input = port.getInputStream();
        output = port.getOutputStream();
        port.setSerialPortParams(baud,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);

        Runnable buttonQueueThread = new Runnable() {
            public void run() {
                while (true) {
                    if (hasData()) {
                        try {
                            byte c = (byte) (input.read());
                            // a vot tyt grjaznij hack
                            // ciferki na korobochke okazalis' naoborot
                            c = bitsViceVersa(c);

                            System.out.println("c = " + Integer.toBinaryString(c));

                            for (int i = 0; i < 8; i++) {
                                if ((c & (1 << i)) > 0) {
                                    if ((lastState & (1 << i)) == 0)
                                        for (ButtonListener listener : buttonListeners) {
                                            listener.buttonPressed(i);
                                        }

                                    if (!buttonPressQueue.contains(i)) {
                                        buttonPressQueue.add(i);
                                        for (ButtonListener listener : buttonListeners) {
                                            listener.buttonQueued(i);
                                        }
                                    }
                                } else {
                                    if ((lastState & (1 << i)) > 0)
                                        for (ButtonListener listener : buttonListeners) {
                                            listener.buttonReleased(i);
                                        }
                                }
                            }

                            lastState = c;
                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        };

        Thread t = new Thread(buttonQueueThread);
        t.start();
    }

    public void close() {
        try {
            input.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public boolean hasData() {
        try {
            return input.available() > 0;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return false;
    }

    public void setText(byte port, int lineNo, String text) {
        final String outText = text.substring(0, Math.min(MAX_MESSAGE, text.length()));
        message(String.valueOf(lineNo).charAt(0), port, padRight(outText, 8));
    }

    public void setLeds(byte leds) {
        message('L', leds, "");
    }

    public void unsetLeds(byte leds) {
        message('l', (byte) (leds ^ 0xFF), "");
    }

    public void blinkLeds(byte leds) {
        message('B', leds, "");
    }

    public void unblinkLeds(byte leds) {
        message('b', (byte) (leds ^ 0xFF), "");
    }

    public void setActivePorts(byte ports) {
        message('P', ports, "");
    }

    public void reset() {
        message('R', (byte) 0, "");
    }


    private synchronized void message(char command, byte port, String message) {
        // a vot tyt grjaznij hack
        // ciferki na korobochke okazalis' naoborot
        port = bitsViceVersa(port);

        try {
            output.write(command);
            output.write('|');
            output.write(port);
            output.write('|');
            output.write(message.getBytes());
            output.write(0x0D);

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private byte bitsViceVersa(byte port) {
        byte ret = 0;
        for (int i = 0; i < 8; i++) {
            if ((port & (1 << i)) > 0) {
                ret += (1 << (7 - i));
            }
        }

        return ret;
    }

    public static byte buildByte(int bit1, int bit2, int bit3, int bit4, int bit5, int bit6, int bit7, int bit8) {
        return (byte) (bit1 + (bit2 << 1) + (bit3 << 2) + (bit4 << 3) + (bit5 << 4) + (bit6 << 5) + (bit7 << 6) + (bit8 << 7));
    }

    public void clearQueue() {
        buttonPressQueue.clear();
    }

    public void dequeue(int teamNo) {
        buttonPressQueue.remove(new Integer(teamNo));
    }

    public static byte teamToByte(int button) {
        return (byte) (1 << button);
    }

    public int getQueueSize() {
        return buttonPressQueue.size();
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }
}
