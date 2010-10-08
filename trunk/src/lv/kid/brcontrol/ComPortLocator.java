package lv.kid.brcontrol;

import gnu.io.CommPortIdentifier;
import processing.app.Base;
import processing.app.RunnerException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: Home
 * Date: 05.02.2010
 * Time: 14:17:44
 * To change this template use File | Settings | File Templates.
 */
public class ComPortLocator {

    //return the most likely com port, maybe
    public String getComPort() throws RunnerException {
        String retstr = null;
        String userdir = System.getProperty("java.io.tmpdir") + File.separator;
        if (Base.isMacOS()) { // do a mac thing
        } else if (Base.isLinux()) { // do a linux thing
        } else { // do a windows thing
            String commandSize[] = new String[]{"regedit", "/e",
                    userdir + "tmp.reg",
                    "HKEY_LOCAL_MACHINE\\SYSTEM\\ControlSet001\\Enum\\FTDIBUS"};
            String tag = "\"PortName\"=";
            HashSet<String> r1 = new HashSet<String>();
            try {
                Process process = Runtime.getRuntime().exec(commandSize);
                process.waitFor();
                BufferedReader r = new BufferedReader(new FileReader(userdir
                        + "tmp.reg"));
                String s = trimNulls(r.readLine());
                while (null != s) {
                    int p = s.indexOf(tag);
                    if (p != -1)
                        r1.add(s.substring(p + tag.length() + 1,
                                s.length() - 1));
                    s = trimNulls(r.readLine());
                }
                r.close();

            } catch (Exception e) {
                System.out.println("e = " + e);
                return null;
            }

            HashSet<String> r2 = new HashSet<String>();

            Enumeration portList = CommPortIdentifier.getPortIdentifiers();
            while (portList.hasMoreElements()) {
                CommPortIdentifier portId = (CommPortIdentifier) portList
                        .nextElement();
                if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    r2.add(portId.getName());
                }
            }

            r1.retainAll(r2);// intersection of live ports and ftdi known ports

            String[] ports = r1.toArray(new String[0]);
            if (ports.length == 0) {
                throw new RunnerException("No Com port found");
            }
            if (ports.length > 1) {
                String c = "";
                for (String port : ports)
                    c = c + " " + port;
                throw new RunnerException("Too many Com ports found: " + c);
            }
            retstr = ports[0];
        }
        return retstr;
    }

    public String trimNulls(String s) {//weird null thing going on with regedit
        if (s == null)
            return null;
        String r = "";
        char[] t = s.toCharArray();
        for (char aT : t) {
            if (aT != 0)
                r = r + aT;
        }
        return r;
    }

    public static void main(String[] args) throws Exception { //just for testing
        System.out.println(new ComPortLocator().getComPort());
    }
}
