/*
 This is a library for the AM2315 Humidity Pressure & Temp Sensor
 
 We used the AM2315 sensor from Adafruit
 ----> https://www.adafruit.com/products/1293
 
 For detials on hooking up the sensor are here
 ----> http://cactus.io/hookups/sensors/temperature-humidity/am2315/hookup-arduino-to-am2315-temp-humidity-sensor
 
 These displays use I2C to communicate, 2 pins are required to
 interface
 
 This library was derived from the library written by
 by Limor Fried/Ladyada for Adafruit Industries.
 BSD license.

 */

#include "cactus_io_AM2315.h"
#include <util/delay.h>

AM2315::AM2315() {
}


boolean AM2315::begin(void) {
  Wire.begin();
  
   // try to read data, as a test
  return readData();
}

bool AM2315::readSensor() {
    if (!readData()) return false;
    
    return true;
}

float AM2315::getHumidity(void) {
    return humidity;
}

float AM2315::getTemperature_C(void) {
    return temperature_C;
}

float AM2315::getTemperature_F(void) {
    return temperature_F;
}


boolean AM2315::readData(void) {
  uint8_t reply[10];
  
  // Initialise the sensor
  Wire.beginTransmission(AM2315_I2CADDR);
  delay(2);
  Wire.endTransmission();

  // Start the request with the sensor
  Wire.beginTransmission(AM2315_I2CADDR);
  Wire.write(AM2315_READREG);
  Wire.write(0x00);  // start at address 0x0
  Wire.write(4);  // request 4 bytes data
  Wire.endTransmission();
  
  delay(10); // add delay between request and read

  Wire.requestFrom(AM2315_I2CADDR, 8);
  for (uint8_t i=0; i<8; i++) {
    reply[i] = Wire.read();
  }
  
  if (reply[0] != AM2315_READREG) return false;
  if (reply[1] != 4) return false; // not enough data bytes supplied
  
  humidity = reply[2];
  humidity *= 256;
  humidity += reply[3];
  humidity /= 10;
    
  temperature_C = reply[4] & 0x7F;
  temperature_C *= 256;
  temperature_C += reply[5];
  temperature_C /= 10;
    
  // change sign
  if (reply[4] >> 7) temperature_C = -temperature_C;
    
  // convert celcius to Fahrenheit
  temperature_F = temperature_C * 1.8 + 32;

  return true;
}
