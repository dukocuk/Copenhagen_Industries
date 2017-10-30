const int led = 13;
String data = "";          
int ledstate = 0;  


//Potentiometer
int sensorValue = 0;          //Goes from 0 - 1023)



void setup()
{
    Serial.begin(9600);   
    pinMode(led, OUTPUT);  
}
void loop()
{
   if(Serial.available() > 0)      
   {
      data = Serial.readStringUntil('\n');
      
      if(data == "ON") {
         ledstate = 1;        
         digitalWrite(led, HIGH);
         sendData();
      }
      else if(data == "OFF") {
         ledstate = 0;      
         digitalWrite(led, LOW);
         sendData();
      }    
   data = "";
   }
   readAnalogValue();
}


void sendData() {
  if(ledstate == 1) {
    Serial.print("LED:ON");
  }
  else if(ledstate == 0){
     Serial.print("LED:OFF");
  }
 Serial.print("#");
  
  delay(10);
}

//Converts to percent
void readAnalogValue() {
  int oldSensorValue = sensorValue;
  int sensor = analogRead(A0);
  sensorValue = map(sensor,0,1023,0,100);

  if(sensorValue != oldSensorValue) {
    Serial.print("PTM:");
    Serial.print(sensorValue);
    Serial.print("#");
    Serial.println();
    delay(10);

  }
}





