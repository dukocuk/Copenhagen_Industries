#include <string.h>


//BluetoothConnection
const byte numChars = 32;                                     //How many chars a received command can contain
char data[numChars];                                    //String that contains the received information from the Android app.
char endChar = '~';;                                    //Character used to tell when the transmision is over.
int ttl = 30000;                                        //Time to live. Decides how long the weapon can be armed without receiving dta. Measured in milliseconds.
unsigned long tlc = 0;                                  //Time of last communication. If this exceeds ttl, the weapon will disarm.
boolean newData = false;


//Weapon information
String serialNumber   = "CI1234";
String gunType        = "Ak-47";
String gunName        = "Mads Mikkelsen 1";

//shooting information
int armed = 0;
int fireMode = 0;                                       //Which mode the gun fires in. Can be semi (0), burst(1) or full automatic(2).
int rateOfFire = 1;                                     //How many times the gun shoots in a second.

//Gas
int oxygenLVL = 0;                                      //The level of oxygen in the tank in percent
int propaneLVL = 0;                                     //The level of propane in the tank in percent.
int batteryLVL = 0;                                     //The battery level in percent.

//Hardware
int LED = 11;                                           //LED that functions as the gunshot. On represents a shot.


void setup()
{

  Serial.begin(9600);
  pinMode(LED, OUTPUT);

}








//This function receives data via a bluetooth connection and resets the tlc
void receiveData() {
  static boolean recvInProgress = false;
  static byte ndx = 0;
  char startMarker = '<';
  char endMarker = '>';
  char rc;
  while (Serial.available() > 0 && newData == false) {
    rc = Serial.read();
    if (recvInProgress == true) {
      if (rc != endMarker) {
        data[ndx] = rc;
        ndx++;
        if (ndx >= numChars) {
          ndx = numChars - 1;
        }
      }
      else {
        data[ndx] = '\0';                                 //Terminate the string.
        recvInProgress = false;
        ndx = 0;
        newData = true;
      }
    }
    else if (rc == startMarker) {
      recvInProgress = true;
    }
    tlc = millis();
  }




}

void handleCommand(char *cmd) {
  size_t sizeCMD = strlen(cmd);
  char value[sizeCMD - 4];
  strncpy(value, &cmd[4], sizeCMD - 4);
  value[sizeCMD - 4] = '\0';
  if (strncmp(cmd, "LED", 3) == 0) {
    Serial.println("Reached LED");
    Serial.println(value);
  }
  else if (strncmp(cmd, "AAA", 3) == 0)
  {
    Serial.println("Reached AAA");
    Serial.println(value);
  }
  else if (strncmp(cmd, "DDD", 3) == 0)
  {
    Serial.println("Reached DDD");
    Serial.println(value);
  }
  else
  {
    Serial.println("Input doesn't match any known commands.");
    delay(40);
  }
  delay(20);
  free(value);


}



void splitData() {
  if (newData == true) {
    Serial.println(data);
    delay(100);
    char receivedData[numChars];                                      //Create a new char array with the length of the data received.
    strcpy(receivedData, data);
    if (receivedData[0] == '\0') {
      Serial.println("No Data received");
      newData = false;
      return;
    }
    char *token;                                                        //Token. String we get after splitting it at ','.
    const char *delimiter = ",";                                        //Delimiter
    token = strtok(receivedData, delimiter);                            //get the first token
    while (token != NULL) {                                             //walk through other tokens
      char cmd[strlen(token) + 1];                                       //Copy the token to prevent destroying the original input.
      strcpy(cmd, token);
      handleCommand(cmd);                                               //Handle to command.
      token = strtok(NULL, delimiter);                                  //Get the second token.
      free(cmd);
    }
    free(receivedData);
    free(token);
  }
  newData = false;
}



void timerHandler() {
  if (armed) {
    if (tlc >= millis()) {
      armed = 0;
    }
  }
}

//Function to simulate the gas and battery level
void readAnalogValue() {
  int sensor = analogRead(A0);
  oxygenLVL = map(sensor, 0, 1023, 0, 100);
  propaneLVL = oxygenLVL + 5;
  batteryLVL = oxygenLVL - 5;
}




void loop() {
  timerHandler();
  receiveData();
  splitData();

}
