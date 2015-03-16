# Java application and firmware for BrainRing Multitool
#### made by Pavel "Vermut" Veretennikov

1. Download USB Virtual COM port drivers [from here](http://www.ftdichip.com/Drivers/VCP.htm) and install. 
 * Seems like you need [unsupported 32-bit drivers](http://www.ftdichip.com/Drivers/CDM/CDM%20v2.10.00%20WHQL%20Certified.zip) or at least it should be consistent with RXTX lib from p5

2. Plug BR module to USB, wait for installation. 
3. Check and note USB Serial Port number in "Device Manager" (i.e. COM7) 
4. Download software from "dist" and unpack to C:\BR 
5. (Optional) In case of 64-bit system replace rxtxSerial.dll with 64-bit version [from here](http://jlog.org/rxtx-win.html). 
6. [Install Java](https://ninite.com/java)
7. Open command prompt as Administrator and type: 
cd C:\BR
java -jar BR_Control.jar COM7

Replace COM7 with the correct number from p3. 

See [tpavlov.wiki](http://tpavlov.wiki.zoho.com/BrainControl.html) for details.
