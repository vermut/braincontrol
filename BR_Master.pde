#include <MultiLCD.h>
#include <Messenger.h>

// Instantiate Messenger object with the default separator (the space character)
Messenger message = Messenger('|'); 
MultiLCD lcd = MultiLCD();

#define led_latch 3+2
#define led_data 2+2
#define button_latch 11
#define button_data 10
#define clock 12
#define debugPin 13    // LED connected to digital pin 13

#define interval 5000

long blinkMillis = 0;
#define blink_interval 200
#define unblink_interval 400

// Storage
byte leds = 0;
byte blinkLeds = 0;
byte activePorts = 0; 
byte buttons = 0;
long previousMillis = 0;

// Create the callback function
void messageReady() {
   char command = message.readChar();
   byte port = message.readChar();
   
   char* text = message.readString();
     
   switch (command) {
     case '1':    // ONE
       lcd.setLCD(port);
       lcd.line1();
       lcd.printIn(text);
       break;  
     case '2':
       lcd.setLCD(port);
       lcd.line2();
       lcd.printIn(text);
       break;  
     case 'L':
       leds |= port;
       updateLeds();
       break;
     case 'l':    // Lower L
       leds &= port;
       updateLeds();
       break;
     case 'B':
       blinkLeds |= port;
       blinkMillis = millis();
       break;
     case 'b':
       blinkLeds &= port;
       blinkMillis = millis();
       break;       
     case 'P':
       activePorts = port;
       break;
   }
  
}


void setup()   {      
  // initialize the digital pin as an output:
  pinMode(debugPin, OUTPUT);
  pinMode(led_latch, OUTPUT);
  pinMode(led_data, OUTPUT);
  pinMode(button_latch, OUTPUT);
  pinMode(button_data, INPUT);
  pinMode(clock, OUTPUT);
 
  updateLeds(); 
 
  // Initiate Serial Communication1
  Serial.begin(115200); 
  // Attach the callback function to the Messenger
  message.attach(messageReady);
  
  lcd.init();
  lcd.printIn("Ready");
}
// the loop() method runs over and over again,
// as long as the Arduino has power

void loop()                     
{
  // The following line is the most effective way of using Serial and Messenger's callback
  while ( Serial.available() )  message.process(Serial.read () );

  if (updateButtons())
    Serial.print(buttons);
  
   if (millis() - previousMillis > interval) {
     // save the last time
     previousMillis = millis();   
   
  digitalWrite(debugPin, HIGH);   // set the LED on
  digitalWrite(debugPin, LOW);
   }
   
   if (blinkLeds) {
     long now = millis();
     if (now - blinkMillis > blink_interval) {
       leds = leds ^ blinkLeds;
       updateLeds();
       blinkMillis = now;
     } 
     
/*     if (now - blinkMillis > unblink_interval) {
       leds = 0;
       updateLeds();
       blinkMillis = now;
     } else if (now - blinkMillis > blink_interval) {
       leds |= blinkLeds;
       updateLeds();
     }    */
   } 
    
}

void updateLeds()
{
//  Serial.print(leds);
  shiftOutByte(led_data, clock, MSBFIRST, leds);
  digitalWrite(led_latch, HIGH);
  delayMicroseconds(10);
  digitalWrite(led_latch, LOW);
}

boolean updateButtons()
{
  //set it to 1 to collect parallel data, wait
  // delayMicroseconds(2);
  //set it to 0 to transmit data serially  
  digitalWrite(button_latch, 0);

  byte oldButtons = buttons;

  //while the shift register is in serial mode
  //collect each shift register into a byte
  //the register attached to the chip comes in first 
  buttons = shiftInButtons() & activePorts;

  //Pulse the latch pin:
  //set it to 1 to collect parallel data
  digitalWrite(button_latch,1);
  
  return buttons != oldButtons;
}

////// ----------------------------------------shiftIn function
///// just needs the location of the data pin and the clock pin
///// it returns a byte with each bit in the byte corresponding
///// to a pin on the shift register. leftBit 7 = Pin 7 / Bit 0= Pin 0

inline byte shiftInButtons() { 

//internal function setup
  int i;
  byte myDataIn = 0;

//we will be holding the clock pin high 8 times (0,..,7) at the
//end of each time through the for loop

//at the begining of each loop when we set the clock low, it will
//be doing the necessary low to high drop to cause the shift
//register's DataPin to change state based on the value
//of the next bit in its serial information flow.
//The register transmits the information about the pins from pin 7 to pin 0 
//so that is why our function counts down
  for (i=7; i>=0; i--)
  {
    digitalWrite(clock, 0); 
    // delayMicroseconds(0.1);
   // asm("nop");
    if (digitalRead(button_data)) {
      //set the bit to 0 no matter what
      myDataIn = myDataIn | (1 << i);
    }
    digitalWrite(clock, 1);
  }
  digitalWrite(clock, 0); 
  
  //debuging print statements whitespace 
  return myDataIn;
}

