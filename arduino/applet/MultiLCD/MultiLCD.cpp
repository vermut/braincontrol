#include "MultiLCD.h"
extern "C" {
  #include <string.h> //needed for strlen()
  #include "WConstants.h"  //all things wiring / arduino
}


//command bytes for LCD
#define CMD_CLR   0x01
#define CMD_RIGHT 0x1C
#define CMD_LEFT  0x18
#define CMD_HOME  0x02

// bitmasks for control bits on shift register
//#define SHIFT_EN B00010000
//#define SHIFT_RW B00100000
//#define SHIFT_RS B01000000
#define SHIFT_EN B00001000
#define SHIFT_RW B00000100
#define SHIFT_RS B00000010


// --------- settings -------------------------------------
#define lcd_lines 2		// ne znaju pochemu

#define dat_pin 4+2
#define clk_pin 5+2     // LCD_CLOCK na sheme

#define lcd_latch 7+2
#define lcd_clock 12  // CLOCK na sheme
#define lcd_data 6+2 

int lcd_num = 0xFF;

//stuff the library user might call---------------------------------
//constructor. 
MultiLCD::MultiLCD () {
}

// initiatize lcd after a short pause
//while there are hard-coded details here of lines, cursor and blink settings, you can override these original settings after calling .init()
void MultiLCD::init () {
  // initialize pins
  pinMode(dat_pin,OUTPUT);
  pinMode(clk_pin,OUTPUT);

  pinMode(lcd_latch,OUTPUT);
  pinMode(lcd_clock,OUTPUT);
  pinMode(lcd_data,OUTPUT);
 
  // initiatize lcd after a short pause
  // needed by the LCDs controller
  // just in case init() is the very first thing that happens in the program
  delay(140);
  
  // Software reset
  _pushNibble(B0011, true);
  delay(5);
  _pushNibble(B0011, true);
  delay(1);
  _pushNibble(B0011, true);

  /////////// 4 pin initialization
 // Neponjatno. nikak ne rabotaet, a dolzhno 
  /* if (lcd_lines==1)
    commandWrite(B00100000); // function set: 4-bit interface, 1 display line, 5x7 font
  else
    commandWrite(B00101000); // function set: 4-bit interface, 2 display lines, 5x7 font
  /////////// end of 4 pin initialization 
 */ 
  
  commandWrite(B00000110); // entry mode set:
  // increment automatically, no display shift
  
  commandWrite(CMD_HOME);

  // commandWrite(B00001110); // display control:
  // turn display on, cursor on, no blinking 

  commandWrite(B00001100); // display control:
  // turn display on, cursor off, no blinking

  commandWrite(B00000001); 
  // clear display, set cursor position to zero
}

void MultiLCD::commandWrite(int value) {
  _pushByte(value, true);
  delay(7);
}

//print the given character at the current cursor position. overwrites, doesn't insert.
void inline MultiLCD::print(int value) {
  _pushByte(value, false);
}

//print the given string to the LCD at the current cursor position.  overwrites, doesn't insert.
//While I don't understand why this was named printIn (PRINT IN?) in the original LiquidCrystal library, I've preserved it here to maintain the interchangeability of the two libraries.
void MultiLCD::printIn(char msg[]) {
  unsigned int i;
  for (i=0;i < strlen(msg);i++){
    print(msg[i]);
  }
}

void MultiLCD::_pushByte(int value, bool command) {
  int nibble = 0;

 // digitalWrite(str_pin,LOW); // set the strobe LOW

  nibble = (value >> 4) << 4; //send the first 4 databits (from 8)
  _pushNibble(nibble, command);

  delayMicroseconds(39);

  nibble = (value & 15) << 4; // set HByte to zero 
  _pushNibble(nibble, command);
}

// called twice by each _pushByte
void MultiLCD::_pushNibble(int nibble, bool command) {
  if (!command) {
      nibble |= SHIFT_RS; // set DI HIGH
      nibble &= ~SHIFT_RW; // set RW LOW
  }
  nibble &= ~SHIFT_EN; // set Enable LOW
  _pushOut(nibble);
  nibble |= SHIFT_EN; // Set Enable HIGH
  _pushOut(nibble);
 // delay(1);
  nibble &= ~SHIFT_EN; // set Enable LOW
  _pushOut(nibble); 
}

// push byte to shift register and on to LCD
void MultiLCD::_pushOut(int value) {
  shiftOutByte(dat_pin, clk_pin, MSBFIRST, value);
  _lcdSelectorHIGH();
  delayMicroseconds(10);
  _lcdSelectorLOW();
}

void MultiLCD::setLCD(char _lcdNum)
{
	lcd_num = _lcdNum; 
/*	if (_lcdNum != 0)
		lcd_num = 1 << (_lcdNum-1);
	else 
		lcd_num = 0xFF; */
}

void MultiLCD::_lcdSelectorHIGH()
{
  digitalWrite(lcd_latch, 0);
  shiftOutByte(lcd_data, lcd_clock, MSBFIRST, lcd_num);
  digitalWrite(lcd_latch, 1);
}

void MultiLCD::_lcdSelectorLOW()
{
  digitalWrite(lcd_latch, 0);
  shiftOutByte(lcd_data, lcd_clock, MSBFIRST, 0x00);
  digitalWrite(lcd_latch, 1);
}




//non-core stuff --------------------------------------

//send the clear screen command to the LCD
void MultiLCD::clear(){
  commandWrite(CMD_CLR);
}

void MultiLCD::line1(){
	commandWrite(0x80);
}

void MultiLCD::line2(){
	commandWrite(0xC0);
}

/*
//move the cursor to the given absolute position.  line numbers start at 1.
//if this is not a 2-line MultiLCD instance, will always position on first line.
void MultiLCD::cursorTo(int line_num, int x){
  //first, put cursor home
  commandWrite(CMD_HOME);

  //if we are on a 1-line display, set line_num to 1st line, regardless of given
  if (lcd_lines==1){
    line_num = 1;
  }
  //offset 40 chars in if second line requested
  if (line_num == 2){
    x += 40;
  }
  //advance the cursor to the right according to position. (second line starts at position 40).
  for (int i=0; i<x; i++) {
    commandWrite(0x14);
  }
}

//scroll whole display to left
void MultiLCD::leftScroll(int num_chars, int delay_time){
  for (int i=0; i<num_chars; i++) {
    commandWrite(CMD_LEFT);
    delay(delay_time);
  }
}
*/