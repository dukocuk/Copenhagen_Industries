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
static String gunName        = "Mads Mikkelsen 1";

//shooting information
int armedMode = 0;     
int armed = 0;                       
int fireMode = 0;                                       //Which mode the gun fires in. Can be semi (0), burst(1) or full automatic(2).
int rateOfFire = 1;                                     //How many times the gun shoots in a second. (delay)


//Gas
int oxygenLVL = 0;                                      //The level of oxygen in the tank in percent
int propaneLVL = 0;                                     //The level of propane in the tank in percent.
int batteryLVL = 0;                                     //The battery level in percent.

//Hardware
int LED = 11;                                           //LED that functions as the gunshot. On represents a shot.
int analogInput = A0;


void setup()
{

  Serial.begin(9600);
  pinMode(LED, OUTPUT);

}






void readAnalogInput() {
  int sensorValue = analogRead(analogInput);
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
  char cmdcpy[strlen(cmd)];
  strncpy(cmdcpy, cmd, strlen(cmd));
  cmdcpy[3] = '\0';
  
 
  size_t sizeCMD = strlen(cmd);
  char value[sizeCMD - 4];
  strncpy(value, &cmd[4], sizeCMD - 4);
  value[sizeCMD - 4] = '\0';                              //Add end character. 

  if (strncmp(cmd, "GST", 3) == 0) {      //Get status
    Serial.print("<GST;");
    Serial.print("IO:");
    Serial.print(oxygenLVL);
    Serial.print(",IP:");
    Serial.print(propaneLVL);
    Serial.print(",IB:");
    Serial.print(batteryLVL);
    Serial.println(">");
        
  }

  else if (strncmp(cmd, "GSS", 3) == 0)   //Get shooting status
  {
    Serial.print("<GSS;");
    Serial.print("IArm:");
    Serial.print(armedMode);
    Serial.print(",IFM:");
    delay(100);
    Serial.print(fireMode);
    Serial.print(",IRoF:");
    Serial.print(rateOfFire);
    Serial.println(">");
    delay(100);
   
    
  }
  else if (strncmp(cmd, "GTS", 3) == 0)   //Get total status
  {

    Serial.print("<GTS;");
    Serial.print("IN:");
    Serial.print(gunName);
    delay(10);
    Serial.print(",ISN:");
    Serial.print(serialNumber);
    delay(10);
    Serial.print(",IGT:");
    Serial.print(gunType);
    delay(10);
    Serial.print("IArm:");
    Serial.print(armedMode);
    Serial.print(" armed: "); 
    Serial.print(armed);
    delay(10);
    Serial.print(",IFM:");
    Serial.print(fireMode);
    delay(10);
    Serial.print(",IRoF:");
    Serial.print(rateOfFire);
    delay(10);
    Serial.print("IO:");
    Serial.print(oxygenLVL);
    delay(10);
    Serial.print(",IP:");
    Serial.print(propaneLVL);
    delay(10);
    Serial.print(",IB:");
    Serial.print(batteryLVL);
    delay(10);
    Serial.println(">");
    delay(100);
    
  }
  else if (strncmp(cmd, "SAS", 3) == 0) {
    if(strncmp(value,"1",1) == 0) 
    {
      armed = 1;
    }
    else if(strncmp(value,"0",1) == 0) 
    {
      armed = 0;
    }  
  }
  else if (strncmp(cmd, "SFM", 3) == 0) {
    if(strncmp(value, "0", 1) == 0) {
      fireMode = 0;
    }
    else if(strncmp(value, "1", 1) == 0) {
      fireMode = 1;
    }
    else if(strncmp(value, "2", 1) == 0) {
      fireMode = 2;
    }
  }
  else if (strncmp(cmd, "SRF", 3) == 0) {
    rateOfFire = atoi(value);
  }
  else if (strncmp(cmd, "SGN", 3) == 0) {
    gunName = value;
  }
  else if (strncmp(cmd, "KCA", 3) == 0) {
    //Nothing happens here. 
  }
  else {
    //Serial.println("No input matching"); 
  }


}



void splitData() {
  if (newData == true) {
 
    char receivedData[numChars];                                      //Create a new char array with the length of the data received.
    strcpy(receivedData, data);
    if (receivedData[0] == '\0') {
      //Serial.println("No Data received");
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
      //free(cmd);
    }
    //free(receivedData);
    //free(token);
  }
  newData = false;
}



void timerHandler() {
  if (armedMode) {
    if (tlc >= millis()) 
    {
      
      //armed = 0;
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


