/*
   HTTPS Secured Client Switch Control
   Copyright (c) 2019, DOSCST Banaybanay
   Connects to WiFi HotSpot.
   #bumblebee
*/

#include <ESP8266WiFi.h>
#include <WiFiClientSecure.h>
#include <ESP8266WebServer.h>
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>

/* Set these to your desired credentials. */
const char *ssid = "Service";  //ENTER YOUR WIFI SETTINGS
const char *password = "JesusMoreThanEverything";

//Link to read data from https://jsonplaceholder.typicode.com/comments?postId=7
//Web/Server address to read/write from
const char *host = "chan-78.firebaseio.com";//"postman-echo.com";
const int httpsPort = 443;  //HTTPS= 443 and HTTP = 80

//SHA1 finger print of certificate use web browser to view and copy
const char fingerprint[] PROGMEM = "B6 F5 80 C8 B1 DA 61 C1 07 9D 80 42 D8 A9 1F AF 9F C8 96 7D";//"B5 32 1E 55 8E F7 29 81 22 A4 DA 78 97 EA 8A 27 82 A8 F5 1C";
//=======================================================================
//                    Power on setup
//=======================================================================
int CH_A = 5;
int CH_B = 4;
int CH_C = 0;
int CH_D = 2;
int CH_E = 14;
int CH_F = 12;

void setup() {
  pinMode(CH_A, OUTPUT);
  pinMode(CH_B, OUTPUT);
  pinMode(CH_C, OUTPUT);
  pinMode(CH_D, OUTPUT);
  pinMode(CH_E, OUTPUT);
  pinMode(CH_F, OUTPUT);

  delay(1000);
  Serial.begin(115200);
  WiFi.mode(WIFI_OFF);        //Prevents reconnection issue (taking too long to connect)
  delay(1000);
  WiFi.mode(WIFI_STA);        //Only Station No AP, This line hides the viewing of ESP as wifi hotspot

  WiFi.begin(ssid, password);     //Connect to your WiFi router
  Serial.println("");

  Serial.print("Connecting");
  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  //If connection successful show IP address in serial monitor
  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());  //IP address assigned to your ESP
}

//=======================================================================
//                    Main Program Loop
//=======================================================================
void loop() {
  WiFiClientSecure httpsClient;    //Declare object of class WiFiClient

  Serial.println(host);

  Serial.printf("Using fingerprint '%s'\n", fingerprint);
  httpsClient.setFingerprint(fingerprint);
  httpsClient.setTimeout(1000); // 10 Seconds
  delay(500);

  Serial.print("HTTPS Connecting");
  int r = 0; //retry counter
  while ((!httpsClient.connect(host, httpsPort)) && (r < 30)) {
    delay(100);
    Serial.print(".");
    r++;
  }
  Serial.println("");
  if (r == 30) {
    Serial.println("Connection failed");
  }
  else {
    Serial.println("Connected to web");
  }

  String ADCData, getData, Link;
  int adcvalue = analogRead(A0); //Read Analog value of LDR
  ADCData = String(adcvalue);   //String to interger conversion

  //GET Data
  Link = "/s.json";//"/get?foo1=bar1&foo2=bar2";//"v1beta1/projects/chan-78/databases/(default)/documents/s";

  Serial.print("requesting URL: ");
  Serial.println(host + Link);

  httpsClient.print(String("GET ") + Link + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "Connection: close\r\n\r\n");

  Serial.println("request sent");

  while (httpsClient.connected()) {
    String line = httpsClient.readStringUntil('\n');
    if (line == "\r") {
      Serial.println("headers received");
      break;
    }
  }

  Serial.println("reply was:");
  Serial.println("==========");
  String line = "";
  while (httpsClient.available()) {
    line += httpsClient.readStringUntil('\n');  //Read Line by Line
  }
  Serial.println(line); //Print response
  Serial.println("==========");
  Serial.println("closing connection");

  if (line != "") {
    const size_t capacity = JSON_OBJECT_SIZE(6) + 20 ;
    DynamicJsonDocument doc(capacity);

    deserializeJson(doc, line);

    bool a = doc["a"]; // true
    bool b = doc["b"]; // false
    bool c = doc["c"]; // false
    bool d = doc["d"]; // false
    bool e = doc["e"]; // false
    bool f = doc["f"]; // false

    digitalWrite(CH_A, ( a ? HIGH : LOW));
    digitalWrite(CH_B, ( b ? HIGH : LOW));
    digitalWrite(CH_C, ( c ? HIGH : LOW));
    digitalWrite(CH_D, ( d ? HIGH : LOW));
    digitalWrite(CH_E, ( e ? HIGH : LOW));
    digitalWrite(CH_F, ( f ? HIGH : LOW));

    Serial.println("Reply was: " + httpsClient.readString());
  }
  //delay(500);  //GET Data at every 2 seconds
}
