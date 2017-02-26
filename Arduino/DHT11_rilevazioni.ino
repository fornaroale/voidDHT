#include "dht.h"		// includo libreria DHT
#define DHT11_PIN 8		// PIN DHT: 8
dht DHT;			// definisco componente usato (DHT11)


void setup(){
	Serial.begin(9600);			// inizializzazione seriale
	Serial.println("SENSORE DHT ");		// messaggio
	Serial.print("VERSIONE LIBRERIA: ");	// messaggio
	Serial.println(DHT_LIB_VERSION);	// messaggio
	Serial.println();			// messaggio
	Serial.println("Type,\tstatus,\tHumidity (%),\tTemperature (C)");	// messaggio
}


void loop(){
	Serial.print("DHT11, \t");		// messaggio
	int chk = DHT.read11(DHT11_PIN);	// leggo temp. in Celsius e umidità
  
	switch (chk){	//switch di controllo (in caso di errori)
		case DHTLIB_OK:  
		Serial.print("OK,\t"); 
		break;
		case DHTLIB_ERROR_CHECKSUM: 
		Serial.print("Errore nel Checksum,\t"); 
		break;
		case DHTLIB_ERROR_TIMEOUT: 
		Serial.print("Errore di time-out.,\t"); 
		break;
		default: 
		Serial.print("Errore sconosciuto,\t"); 
		break;
	}
  
	Serial.print(DHT.humidity,1);		// stampo umidità
	Serial.print(",\t");			// vado a capo
	Serial.println(DHT.temperature,1);	// stampo temperatura

	delay(1000);	// aspetto 1 sec. per la prossima rilevazione
}
