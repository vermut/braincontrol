package lv.kid.brcontrol;

import javax.sound.sampled.*;

public class VolumeMeter implements Runnable {
    /**
     * A reference to the sound source of interest.
     */
    private TargetDataLine line = null;

    /**
     * This is fragile - it assumes a stereo, 16 bit signal,
     * for '4' frames per sample.
     */
    private byte[] b;

    private int channels;
    private int frameSize;

    /**
     * We grab sound data in blocks of 'sampleSize'.  Low values
     * make for a fast display update, but larger values show the
     * signal more clearly, I would recommend values between 100 &
     * 400 here.
     */
    private static int sampleSize;

    private double averageLevel;
    private final Runnable callback;

    /**
     * Construct the plot panel and start the thread.
     * @param runnable
     */
    public VolumeMeter(Runnable runnable) {
        callback = runnable;
        Mixer.Info[] allMixer = AudioSystem.getMixerInfo();
        int ii = 0;

        // iterate the mixers, looking for TDL's.
        // Vector<TargetDataLine> lines = new Vector<TargetDataLine>();
        while (ii < allMixer.length && line == null) {
            Mixer mixer = AudioSystem.getMixer(allMixer[ii]);
            try {
                Line.Info[] allTLineInfos = mixer.getTargetLineInfo();
                for (Line.Info allTLineInfo : allTLineInfos) {
                    try {
                        line = (TargetDataLine) mixer.getLine(allTLineInfo);
                        System.out.println("Available N"  + ii + ": " + line.getLineInfo());
                        break;
                        //lines.add(line);
                    } catch (ClassCastException cce) {
                        // proceed to next Line
                    }
                }
            } catch (LineUnavailableException lue) {
                lue.printStackTrace();
            }
            ii++;
        }

        System.out.println("Using: " + line.getLineInfo());

        try {
            line.open();
            line.start();
            AudioFormat af = line.getFormat();
            channels = af.getChannels();
            frameSize = af.getFrameSize();
            setSamplingSize(2 * 32);

        } catch (LineUnavailableException lue) {
            System.out.println("Unable to open line!");
            lue.printStackTrace();
        }
        start();
    }

    public void start() {
        Thread t = new Thread(this);
        t.start();
    }

    public void setSamplingSize(int sampleSize) {
        b = new byte[sampleSize * frameSize];
        VolumeMeter.sampleSize = sampleSize;
    }

    /**
     * Read data from the TargetDataLine, then paint it
     * to screen.
     */
    public void run() {
        while (line != null) {
            line.read(b, 0, sampleSize * frameSize);
            processSample();
            callback.run();

            try {
                Thread.sleep(1000 / 600);
            } catch (InterruptedException ie) {
                // no problem, wake and continue
            }
        }
    }

    private void processSample() {
        double[] lastSignalSize = null;
        double level = 0;
        int count = 0;
        for (int ii = 0; ii < sampleSize; ii++) {
            byte[] frameSample = new byte[frameSize];
            System.arraycopy(b, (ii * 4), frameSample, 0, frameSize);

            double total = 0;
            for (int jj = 0; jj < channels; jj++) {
                double signalChannelSize = frameToSignedDoubles(frameSample)[jj];
                total += signalChannelSize;
                level += Math.abs(signalChannelSize);
                count++;
            }
            double average = total / channels;
            if (lastSignalSize == null) {
                lastSignalSize = new double[1];
                lastSignalSize[0] = average;
            }
        }

        averageLevel = level / count;
       //  System.out.println("averageLevel = " + averageLevel);
    }
    public double getAverageLevel() {
        return averageLevel;
    }


    /**
     * Converts a single frame of audio bytes to signed doubles
     * ranging from -1 to 1. It will produce one sample value for
     * each channel.
     * Though it will usually be two - stereo, it might (theoretically)
     * range from one - mono, to five - for the 5.1 channel sound
     * supported by some video formats.
     * This method is quite fragile in that it presumes a
     * stereo, 16 bit, little-endian audio signal.  It is
     * expressed in 4 bytes, arranged as follows.
     * <p/>
     * | byte index | Channel | L/S |
     * |____________________________|
     * |     0      |    1    |  S  |
     * |     1      |    1    |  L  |
     * |     2      |    2    |  S  |
     * |     3      |    2    |  L  |
     * <p/>
     * No other configurations available for further testing.
     *
     * @param b bytearray The bytes of a single audio frame
     * @return An array of doubles ranging from -1 to 1, representing
     *         the audio signal strength of a single frame sample.
     */
    double[] frameToSignedDoubles(byte[] b) {
        double[] d = new double[channels];
        for (int cc = 0; cc < channels; cc++) {
            d[cc] = (b[cc * 2 + 1] * 256 + (b[cc *2] & 0xFF))/32678.0;
		}

		return d;
	}
}
