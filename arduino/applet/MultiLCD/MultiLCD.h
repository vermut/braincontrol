#ifndef MultiLCD_h
#define MultiLCD_h

#include <inttypes.h>

#define sbi(sfr, bit) (_SFR_BYTE(sfr) |= _BV(bit))
#define cbi(sfr, bit) (_SFR_BYTE(sfr) &= ~_BV(bit))

#define sbipin(pin) sbi((pin)<8 ? PORTD:PORTB, (pin) - ((pin)<8 ? 0:8))
#define cbipin(pin) cbi((pin)<8 ? PORTD:PORTB, (pin) - ((pin)<8 ? 0:8))

#define mybitWrite(pin, val) { \
  if ((val) == LOW) cbipin(pin); \
  else		  sbipin(pin); \
}

#define shiftOutBit(dataPin, clockPin, val, bit) { \
  mybitWrite(dataPin, ((val) & (1 << (bit))) ? HIGH:LOW); \
  mybitWrite(clockPin, HIGH); \
  mybitWrite(clockPin, LOW); \
}

#define shiftOutByte(dataPin, clockPin, bitOrder, val) { \
  shiftOutBit(dataPin, clockPin, val, (bitOrder) == LSBFIRST ?0:7); \
  shiftOutBit(dataPin, clockPin, val, (bitOrder) == LSBFIRST ?1:6); \
  shiftOutBit(dataPin, clockPin, val, (bitOrder) == LSBFIRST ?2:5); \
  shiftOutBit(dataPin, clockPin, val, (bitOrder) == LSBFIRST ?3:4); \
  shiftOutBit(dataPin, clockPin, val, (bitOrder) == LSBFIRST ?4:3); \
  shiftOutBit(dataPin, clockPin, val, (bitOrder) == LSBFIRST ?5:2); \
  shiftOutBit(dataPin, clockPin, val, (bitOrder) == LSBFIRST ?6:1); \
  shiftOutBit(dataPin, clockPin, val, (bitOrder) == LSBFIRST ?7:0); \
}

class MultiLCD {
public:
  MultiLCD();
  void commandWrite(int value);
  void init();
  void print(int value);
  void printIn(char value[]);
  void setLCD(char lcdNum);
  //non-core---------------
  void clear();
  void line1();
  void line2();
 // void cursorTo(int line_num, int x);
 // void leftScroll(int chars, int delay_time);
  //end of non-core--------



private:
  void _pushByte(int value, bool command);
  void _pushNibble(int nibble, bool command);
  void _pushOut(int value);
  void _lcdSelectorHIGH();
  void _lcdSelectorLOW();};

#endif
