#include <Wire.h>
#include "cactus_io_AM2315.h"

// Define sensor pins
AM2315 am2315;
#define SOUND_GATE_IN 3
#define SOUND_ANALOG_IN A3
#define DIST_TRIG_OUT 2
#define DIST_ECHO_IN 4

// Calibrated values from analog input
const float dBQuiet = 10;
const float dBModerate = 12;
const float dBdBLoud = 17;

// Set initial past distance
int olddist = 0;

void setup() {
  Serial.begin(115200);
  
  // Configure AM2315 temperature/humidity sensor
  if (!am2315.begin()) {
    Serial.println("Sensor not found, check wiring");
    while (1);
  }

  // Set pinMode of distance/sound detection sensors
  pinMode(DIST_TRIG_OUT, OUTPUT);
  pinMode(DIST_ECHO_IN, INPUT);
  pinMode(SOUND_ANALOG_IN, INPUT);
  pinMode(SOUND_GATE_IN, INPUT);
}

void loop() {
  // Temperature/humidity sensor data
  am2315.readSensor();
  Serial.print("RH: ");Serial.print(am2315.getHumidity()); Serial.print("\n");
  Serial.print("TMP: ");Serial.print(am2315.getTemperature_C()); Serial.print("\n");

  // Sound Detector sensor data
  int value;
  float decibelsQuiet = 20;
  float decibelsModerate = 40;
  float decibelsLoud = 65;
  value = analogRead(SOUND_ANALOG_IN);
  if (value < 13)
  {
    decibelsQuiet += 20 * log10(value/dBQuiet);
    Serial.print("DB: "); Serial.print(decibelsQuiet); Serial.print("\n");
    }
  else if ((value >= 13) && ( value <= 23) )
  {
    decibelsModerate += log10(value/dBModerate);
    Serial.print("DB: "); Serial.print(decibelsModerate); Serial.print("\n");
  }
  else if(value > 23)
  {
    decibelsLoud += log10(value/dBdBLoud);
    Serial.print("DB: "); Serial.print(decibelsLoud); Serial.print("\n");
  }

  //Ultrasonic distance sensor data
  long duration, distance, olddist;
  digitalWrite(DIST_TRIG_OUT, LOW);
  delayMicroseconds(2);
  digitalWrite(DIST_TRIG_OUT, HIGH);
  delayMicroseconds(10);
  digitalWrite(DIST_TRIG_OUT, LOW);
  duration = pulseIn(DIST_ECHO_IN, HIGH);
  distance = (duration/2) / 29.1;

  // If distance is out of range, output previous recorded distance value in range
  if(distance >= 100) {
    Serial.print("DIST: "); Serial.print(olddist); Serial.print("\n");
  }
  else if(distance < 100) {
    olddist = distance;
    Serial.print("DIST: "); Serial.print(distance); Serial.print("\n");
  }
  else {
    Serial.print("DIST: "); Serial.print(olddist); Serial.print("\n");
  }
  Serial.println("\n"); 
  
  // Add a 5 second delay
  delay(5000);
}
