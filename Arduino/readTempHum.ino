#include "DHT.h"         // includo libreria DHT
#define DHTPIN 3         // PIN DHT: 3
#define DHTTYPE DHT11    // definisco componente usato (DHT11)
DHT dht(DHTPIN, DHTTYPE);// creo oggetto DHT


void setup() {
  Serial.begin(9600);    // inizializzazione porta seriale
  delay(3000);           // aspetto 3 secondi per inizializzazione
  //Serial.println("Temperature and Humidity test!"); // test 
  //Serial.println("T(C) \tH(%)");                    // test
  dht.begin();           // inizializzazione comunicazione seriale componente DHT11
}


void loop() {
  float h = dht.readHumidity();    // leggo umidità
  float t = dht.readTemperature(); // leggo temp. in Celsius
  
  // check if any reads failed and exit early (to try again).
  if (isnan(h) || isnan(t)) {    
    Serial.println("Failed to read from DHT sensor!");
    return;
  }
  
  Serial.print(t, 2);    //print the temperature
  Serial.print("\t");
  Serial.println(h, 2);  //print the humidity
  delay(60000);          //aspetto 60 secondi
}
