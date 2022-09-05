/*
  ESP32 Chuck Norris Jokes
  by R. Pelayo
  Tutorial: https://www.teachmemicro.com/esp32-restful-api
  
  V1.0 - 10/27/2020
*/

#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

/* For the oled display. */
#define SCREEN_WIDTH 128  // OLED display width, in pixels
#define SCREEN_HEIGHT 64  // OLED display height, in pixels
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, -1);

//Provide your own WiFi credentials
const char* ssid = "Love";
const char* password = "JesusIsWayTruthLife!7";
const char* deviceId = "shsd-01";
//String for storing server response
String response = "";
//JSON document
DynamicJsonDocument doc(2048);

int SSID_STAT = 2;
int CH_A = 19;
int CH_B = 18;
int CH_C = 5;
int CH_D = 17;
int CH_E = 16;
int CH_F = 4;
// For the display circles
bool a, b, c, d, e, f;


void setup(void) {
  //Register pinouts
  pinMode(SSID_STAT, OUTPUT);
  pinMode(CH_A, OUTPUT);
  pinMode(CH_B, OUTPUT);
  pinMode(CH_C, OUTPUT);
  pinMode(CH_D, OUTPUT);
  pinMode(CH_E, OUTPUT);
  pinMode(CH_F, OUTPUT);

  //Initiate olded display
  if (!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println("SSD1306 allocation failed");
    for (;;)
      ;
  }
  initDisplay();

  //For displaying the joke on Serial Monitor
  Serial.begin(9600);
  
  display.display();
  //Initiate WiFi connection
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
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
  Serial.print("WiFi connected with IP: ");
  Serial.println(WiFi.localIP());
}

void loop(void) {
  digitalWrite(SSID_STAT, ((WiFi.status() == WL_CONNECTED ? HIGH : LOW)));
  //Initiate HTTP client
  HTTPClient http;
  //The API URL
  String request = "https://chan-78.firebaseio.com/shsd-01.json";
  Serial.println("Requesting data...");
  printDisplay("Requesting data...");
  //Start the request
  http.begin(request);
  //Use HTTP GET request
  http.GET();
  //Response from server
  response = http.getString();

  Serial.println(response);
  //Parse JSON, read error if any
  DeserializationError error = deserializeJson(doc, response);
  if (error) {
    Serial.print(F("deserializeJson() failed: "));
    //Serial.println(error.f_str());
    return;
  }
  //Set the bools for display circles
  a = doc["a"].as<bool>();
  b = doc["b"].as<bool>();
  c = doc["c"].as<bool>();
  d = doc["d"].as<bool>();
  e = doc["e"].as<bool>();
  f = doc["f"].as<bool>();

  //Print parsed value on Serial Monitor
  digitalWrite(CH_A, (a ? HIGH : LOW));
  digitalWrite(CH_B, (b ? HIGH : LOW));
  digitalWrite(CH_C, (c ? HIGH : LOW));
  digitalWrite(CH_D, (d ? HIGH : LOW));
  digitalWrite(CH_E, (e ? HIGH : LOW));
  digitalWrite(CH_F, (f ? HIGH : LOW));

    printDisplay("Data fetched.");
  //Close connection
  http.end();
}

void initDisplay() {
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(0, 0);
  display.display();
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
  display.display();  // actually display all of the above
}