# Java application and firmware for BrainRing Multitool #
### made by Pavel "Vermut" Veretennikov ###

1) Download USB Virtual COM port drivers from [here](http://www.ftdichip.com/Drivers/VCP.htm) and install.

2) Plug BR module to USB, wait for installation.

3) Check and note USB Serial Port number in "Device Manager" (i.e. COM7)

4) Download software from "Downloads" and unpack to C:\BR

5) (Optional) In case of 64-bit system replace rxtxSerial.dll with 64-bit version from [here](http://jlog.org/rxtx-win.html).

6) Install Java

7) Open command prompt (Start -> Run -> cmd) and type:
```
cd C:\BR
java -jar BR_Control.jar COM7
```
Replace COM7 with the correct number from p3.


See http://tpavlov.wiki.zoho.com/BrainControl.html for details.