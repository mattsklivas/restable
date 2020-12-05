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

#if (ARDUINO >= 100)
 #include "Arduino.h"
#else
 #include "WProgram.h"
#endif
#include "Wire.h"

#define AM2315_I2CADDR       0x5C
#define AM2315_READREG       0x03

class AM2315 {
    public:
        AM2315();
        boolean begin(void);
        bool readSensor();
        float getHumidity(void);
        float getTemperature_C(void);
        float getTemperature_F(void);
  
 private:
    boolean readData(void);
    float humidity;
    float temperature_C;
    float temperature_F;
};
