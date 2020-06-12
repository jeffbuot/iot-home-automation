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
#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

/* For the oled display. */
#define SCREEN_WIDTH 128 // OLED display width, in pixels
#define SCREEN_HEIGHT 64 // OLED display height, in pixels
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, -1);

/* Set these to your desired credentials. */
const char *ssid = "ZLT P21_00AFD1";  //ENTER YOUR WIFI SETTINGS
const char *password = "6BF062E3";
const char *deviceId = "shsd-01";

//Link to read data from https://jsonplaceholder.typicode.com/comments?postId=7
//Web/Server address to read/write from
//https://chan-78.firebaseio.com/s/shsd-01.json
const char *host = "chan-78.firebaseio.com";// "firestore.googleapis.com"; ////"postman-echo.com";
const int httpsPort = 443;  //HTTPS= 443 and HTTP = 80

//SHA1 finger print of certificate use web browser to view and copy
const char fingerprint[] PROGMEM = "03 D6 42 23 03 D1 0C 06 73 F7 E2 BD 29 47 13 C3 22 71 37 1B";//"56 FA EB AE 23 B9 95 2C 83 18 3A 8F EA 06 6E 26 C5 66 2B 83";//"B6 F5 80 C8 B1 DA 61 C1 07 9D 80 42 D8 A9 1F AF 9F C8 96 7D";//"B5 32 1E 55 8E F7 29 81 22 A4 DA 78 97 EA 8A 27 82 A8 F5 1C";
//=======================================================================
//                    Power on setup
//=======================================================================
int SSID_STAT = 16;
int CH_A = 14;
int CH_B = 12;
int CH_C = 13;
int CH_D = 15;
int CH_E = 0;
int CH_F = 2;
bool a, b, c, d, e, f;
void setup() {
  pinMode(SSID_STAT, OUTPUT);
  pinMode(CH_A, OUTPUT);
  pinMode(CH_B, OUTPUT);
  pinMode(CH_C, OUTPUT);
  pinMode(CH_D, OUTPUT);
  pinMode(CH_E, OUTPUT);
  pinMode(CH_F, OUTPUT);
  if (!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println("SSD1306 allocation failed");
    for (;;);
  }
  initDisplay();
  digitalWrite(SSID_STAT, LOW);
  delay(1000);
  Serial.begin(115200);
  WiFi.mode(WIFI_OFF);        //Prevents reconnection issue (taking too long to connect)
  delay(1000);
  WiFi.mode(WIFI_STA);        //Only Station No AP, This line hides the viewing of ESP as wifi hotspot

  WiFi.begin(ssid, password);     //Connect to your WiFi router
  Serial.println("");

  display.display();
  Serial.print("Connecting to ");
  Serial.print(ssid);
  // Wait for connection
  int i = 0;
  String dots = "";
  while (WiFi.status() != WL_CONNECTED) {
    digitalWrite(SSID_STAT, HIGH);
    delay(250);
    digitalWrite(SSID_STAT, LOW);
    delay(250);
    Serial.print(".");
    i++;
    dots = "";
    for (int x = 0; x < i % 4; x++)dots += ".";
    printDisplay("Connecting to wifi" + dots);
  }

  //If connection successful show IP address in serial monitor
  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());  //IP address assigned to your ESP
  printDisplay("Connected, IP: " + WiFi.localIP());
}

//=======================================================================
//                    Main Program Loop
//=======================================================================
void loop() {
  digitalWrite(SSID_STAT, ((WiFi.status() == WL_CONNECTED ? HIGH : LOW)));
  WiFiClientSecure httpsClient;    //Declare object of class WiFiClient

  Serial.println(host);

  Serial.printf("Using fingerprint '%s'\n", fingerprint);
  httpsClient.setFingerprint(fingerprint);
  httpsClient.setTimeout(1000); // 10 Seconds
  printDisplay("Connecting to host..");
  delay(500);
  Serial.print("HTTPS Connecting");
  int r = 0; //retry counter
  while ((!httpsClient.connect(host, httpsPort)) && (r < 30)) {
    delay(50);
    Serial.print(".");
    r++;
  }
  Serial.println("");
  if (r == 30) {
    Serial.println("Connection failed");
  }  else {
    Serial.println("Connected to web");
  }

  //  String ADCData, getData, Link;
  //  int adcvalue = analogRead(A0); //Read Analog value of LDR
  //  ADCData = String(adcvalue);   //String to interger conversion

  //GET Data
  String link = "/";//"/";
  link += deviceId;
  link += ".json";

  printDisplay("Requesting data..");
  Serial.print("requesting URL: ");
  Serial.println(host + link);

  httpsClient.print(String("GET ") + link + " HTTP/1.1\r\n" +
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
    line = httpsClient.readStringUntil('\n');  //Read Line by Line
  }
  Serial.println(line); //Print response
  Serial.println("==========");
  Serial.println("closing connection");

  if (line != "") {
    const size_t capacity = 6 * JSON_OBJECT_SIZE(1) + JSON_OBJECT_SIZE(4) + JSON_OBJECT_SIZE(6) + 270;
    DynamicJsonDocument doc(capacity);
    //    const size_t capacity = JSON_OBJECT_SIZE(6) + 20 ;
    //    DynamicJsonDocument doc(capacity);

    deserializeJson(doc, line);

//    JsonObject fields = doc["fields"];
//    f = fields["f"]["booleanValue"]; // true
//    c = fields["c"]["booleanValue"]; // true
//    d = fields["d"]["booleanValue"]; // true
//    e = fields["e"]["booleanValue"]; // true
//    a = fields["a"]["booleanValue"]; // true
//    b = fields["b"]["booleanValue"]; // true
        a = doc["a"];
        b = doc["b"]; 
        c = doc["c"]; 
        d = doc["d"]; 
        e = doc["e"]; 
        f = doc["f"]; 

    digitalWrite(CH_A, ( a ? HIGH : LOW));
    digitalWrite(CH_B, ( b ? HIGH : LOW));
    digitalWrite(CH_C, ( c ? HIGH : LOW));
    digitalWrite(CH_D, ( d ? HIGH : LOW));
    digitalWrite(CH_E, ( e ? HIGH : LOW));
    digitalWrite(CH_F, ( f ? HIGH : LOW));

    Serial.println("Reply was: " + line);

    printDisplay("Data fetched.");
  }
}
void printDisplay(String inf) {
  display.clearDisplay();
  display.setCursor(0, 0);
  display.print(inf);
  display.print("\r\n");
  display.print("WiFi: ");
  display.print(ssid);
  display.print((WiFi.status() == WL_CONNECTED ? "*\r\n" : "\r\n"));
  display.print("Pw: ");
  display.print(password);
  display.print("\r\n");
  display.print("Device: ");
  display.print(deviceId);
  if (f) {
    display.fillCircle(123, 60, 2, WHITE);
  } else {
    display.drawCircle(123, 60, 2, WHITE);
  }
  if (e) {
    display.fillCircle(123, 54, 2, WHITE);
  } else {
    display.drawCircle(123, 54, 2, WHITE);
  }
  if (d) {
    display.fillCircle(123, 48, 2, WHITE);
  } else {
    display.drawCircle(123, 48, 2, WHITE);
  }
  if (c) {
    display.fillCircle(123, 42, 2, WHITE);
  } else {
    display.drawCircle(123, 42, 2, WHITE);
  }
  if (b) {
    display.fillCircle(123, 36, 2, WHITE);
  } else {
    display.drawCircle(123, 36, 2, WHITE);
  }
  if (a) {
    display.fillCircle(123, 30, 2, WHITE);
  } else {
    display.drawCircle(123, 30, 2, WHITE);
  }
  display.display(); // actually display all of the above
}
void initDisplay() {
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(0, 0);
  display.display();
}
