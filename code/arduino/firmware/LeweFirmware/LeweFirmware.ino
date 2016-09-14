/*
   Copyright 2016 Alessandro Pasqualini

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   @author         Alessandro Pasqualini <alessandro.pasqualini.1105@gmail.com>
   @url            https://github.com/alessandro1105
*/

/**
 * @file LeweFirmware.ino
 * @brief Firmware del bracciale Lewe
 * 
 * @author Alessandro Pasqualini (<alessandro.pasqualini.1105@gmail.com>)
 * @version 1.0
 * 
 * @copyright Copyright (c) 2016-2017 Alessandro Pasqualini
 * @copyright Apache License, Version 2.0
 */
 
//---LIBRERIE---
//RTC
#include <Wire.h>

#include <RTClib.h>

//FlashString
#include <Flash.h>

//Jack
#include <Jack.h>
#include <SoftwareSerialJack.h>
#include <SoftwareSerial.h>


//---COSTANTI--

//VREF ADC
/**
 * @brief Tensione di riferimento per l'ADC
 */
#define VREF 1.1 //tensione di riferimento per il ADC

//LM35
/**
 * @brief Pin di abilitazione del sensore di temperatura (LM35DZ)
 */
#define LM35_ENABLE_PIN 5 //abilitazione
/**
 * @brief Pin di lettura del sensore di temperatura (LM35DZ)
 */
#define LM35_PIN A0 //lettura

//GSR
/**
 * @brief Pin di abilitazione del sensore GSR
 */
#define GSR_ENABLE_PIN 4 //abilitazione
/**
 * @brief Pin di lettura del sensore GSR
 */
#define GSR_PIN A2 //lettura
/**
 * @brief Pin di lettura della DDP applicata al sensore
 */
#define GSR_VCC_PIN A1 //lettura ddp applicata
/**
 * @brief Costante indicante il rumore intrinseco del sensore GSR
 */
#define GSR_NOISE 40 //rimozione del rumore

//HM-10
/**
 * @brief Pin di comunicazione seriale con il modulo bluetooth HM-10 (TX)
 */
#define HM10_TX 8 //TX 
/**
 * @brief Pin di comunicazione seriale con il modulo bluetooth HM-10 (RX)
 */
#define HM10_RX 9 //RX
/**
 * @brief Baudrate di comunicazione seriale con il modulo bluetooth HM-10
 */
#define HM10_BAUDRATE 9600 //baudrate (da verificare)

//DATA COLLECT
/**
 * @brief Tempo di attesa tra letture consecutive dei sensori (millisecondi)
 */
#define INTERVAL_BETWEEN_DATA_COLLECT 300000 //intervallo tra un data collect e un altro (5 min)

//JACK
/**
 * @brief Tempo di attesa tra invii consecutivi dei messaggi da parte della libreria Jack
 */
#define TIMER_SEND_MESSAGE 5000 //intervallo tra l'invio dei messaggi
/**
 * @brief Tempo di attesa tra polling consecutivi del mezzo di comunicazione da parte della della libreria Jack
 */
#define TIMER_POLLING 1000 //intervallo tra un polling e l'altro del mezzo di trasmissione

//CHIAVI PER IL MESSAGGIO
/**
 * @brief Chiave per il messaggio Jack (Timespamp)
 */
#define TIMESTAMP_KEY "TMP" //chiave per timestamp (TiMetamP)
/**
 * @brief Chiave per il messaggio Jack (GSR)
 */
#define GSR_KEY "GSR" //chiave per gsr (GSR)
/**
 * @brief Chiave per il messaggio Jack (Temperatura)
 */
#define TEMPERATURE_KEY "TME" //chiave per temperatura (TeMperaturE)


//costante per il debug su seriale
/**
 * @brief Abilitazine del codice di debug (commentare per disabilitarlo)
 */
#define DEBUG 1


//---VARIABILI---

/**
 * @brief Tipo di dati indicante lo stato dei sensori
 */
//stato dei sensori
typedef enum lwSensorState {
  LW_SENSOR_SLEEP, //sensori addormentati
  LW_SENSOR_WAKE //sensori svegli
};

/**
 * @brief Variabile indicante lo stato dei sensori
 */
lwSensorState sensorState;

//RTC
/**
 * @brief Oggetto della libreria RTC_DS10307 per la gestione del RTC
 */
RTC_DS1307 RTC;

//DATA COLLECT
/**
 * @brief Data ultima lettura dei sensori
 */
long timeLastDataCollect;

//JACK
/**
 * @brief Istanza seriale software per comunicare con il modulo bluetooth HM-10
 */
SoftwareSerial bluetooth(HM10_TX, HM10_RX); //seriale per il modulo HM-10
/**
 * @brief Istanza della libreria SoftwareSerialJack. Usata per pilotare il mezzo di comunicazione (bluetooth)
 */
SoftwareSerialJack mmJTM(bluetooth); //Mezzo di trasmissione per Jack
/**
 * @brief Istanza della libreria Jack
 */
Jack jack(mmJTM, &onReceive, &onReceiveAck, &getTimestamp, TIMER_SEND_MESSAGE, TIMER_POLLING); //Jack



//---HANDLER JACK---
/**
 * @brief Handler dell'evento di ricezione di un nuovo messaggio (da passare alla libreria Jack)
 * 
 * @param message Messaggio ricevuto
 * @param id ID del messaggio ricevuto
 */
void onReceive(JData &message, long id) {} //handler per messaggi dati in entrata
/**
 * @brief Handler dell'evento di ricezione della conferma di un messaggio (da passare alla libreria Jack)
 * 
 * @param id ID del messaggio confermato
 */
void onReceiveAck(long id) {} //handler per ricezione ack


//---GET DATA FROM SENSORS FUNCTIONS---

//legge e coverte in percentuale la lettura del sensore GSR
/**
 * @brief Funzione che restituisce la lettura del sensore GSR (privato del rumore intrinseco)
 * 
 * @return Lettura del sensore GSR
 */
uint8_t getGSR() {

  int gsr = analogRead(GSR_PIN); //prelevo la lettura dal sensore

  //rimozione del rumore
  if (gsr > GSR_NOISE) {
    gsr -= GSR_NOISE;
  } else {
    gsr = 0;
  }

  int vcc = analogRead(GSR_VCC_PIN); //leggo la vcc applicata al sensore

#ifdef DEBUG
  Serial.print(F("\nGSR READ: "));
  Serial.println(gsr);
  Serial.print(F("\nVCC READ: "));
  Serial.println(gsr);
#endif

  return (uint8_t) (100.0 * gsr / vcc); //ritorno la lettura in percentuale

}

//legge e converte in gradi C la lettura del sensore LM35
/**
 * @brief Funzione che restituisce la lettura del sensore di temperatura (LM35DZ)
 * 
 * @return Lettura del sensore di temperatura (in gradi Celsius)
 */
double getTemperature() {

  double temp = analogRead(LM35_PIN); //prelevo la lettura dal sensore
  temp = (temp * VREF / 1023.0) * 100.0; //converto la temperatura letta in gradi

  double decimalPart = temp - floor(temp); //ricavo la parte decimale
  temp = floor(temp) + (floor(decimalPart * 10) / 10); //sommo la parte intera e una cifra dopo la virgola

#ifdef DEBUG
  Serial.print(F("\nTEMPERATURE READ: "));
  Serial.println(temp);
#endif

  return temp; //restituisco la temperatura con un decimale

}

//ottiene il timestamp da RTC
/**
 * @brief Funzione che legge il RTC e ne resisuisce il timestamp
 * 
 * @return Timestamp dell'ora corrente
 */
long getTimestamp() {

  long timestamp = RTC.now().unixtime();

#ifdef DEBUG
  Serial.print(F("\nTIMESTAMP: "));
  Serial.println(timestamp);
#endif

  return timestamp; //get unix timestamp;

}


//---SETUP/SLEEP/WAKEUP SENSORS FUNCTIONS---

//setup sensor
/**
 * @brief Funzione che configura i sensori
 * 
 */
void setupSensor() {

  analogReference(INTERNAL); //imposto VREF a 1.1V

  //preparo i pin di abilitazione
  pinMode(LM35_ENABLE_PIN, OUTPUT);
  pinMode(GSR_ENABLE_PIN, OUTPUT);

  //spengo i sensori
  sleepSensor();

  //setup RTC
  Wire.begin(); //wire
  RTC.begin(); //rtc

  //se RTC non è partito imposto l'ora
  if (!RTC.isrunning()) {

#ifdef DEBUG
    Serial.println(F("\nRTC is NOT running!\n"));
#endif

    RTC.adjust(DateTime(__DATE__, __TIME__)); //setto RTC con il data e ora di compilazione dello sketch
  }

#ifdef DEBUG
  Serial.print(F("\nSENSORI SETTATI\n"));
#endif

}

//sveglia i sensori
/**
 * @brief Funzione che sveglia i sensori
 */
void wakeupSensor() {

  //sveglio i sensori
  digitalWrite(LM35_ENABLE_PIN, HIGH);
  digitalWrite(GSR_ENABLE_PIN, HIGH);

  //imposto lo stato dei sensori
  sensorState = LW_SENSOR_WAKE;

#ifdef DEBUG
  Serial.print(F("\nSENSORI SVEGLIATI\n"));
#endif

}

//addormenta i sensori per risparmiare energia
/**
 * @brief Funzione che addormenta i sensori per risparmio energetico
 */
void sleepSensor() {

  //addormento i sensori
  digitalWrite(LM35_ENABLE_PIN, LOW);
  digitalWrite(GSR_ENABLE_PIN, LOW);

  //imposto lo stato dei sensori
  sensorState = LW_SENSOR_SLEEP;

#ifdef DEBUG
  Serial.print(F("\nSENSORI ADDORMENTATI\n"));
#endif

}

//ritorna lo stato corrente dei sensori (svegli, addormentati)
/**
 * @brief Funzione che ritorna lo stato dei sensori (addormentati/svegli)
 * 
 * @return Stato dei sensori
 */
lwSensorState getSensorState() {
  return sensorState;
}


//---SETUP BLUETOOTH---
/**
 * @brief Funzione che configura il modulo bluetooth HM-10
 */
void setupBluetooth() {
  //avvio la seriale
  bluetooth.begin(HM10_BAUDRATE);

}


//---DATA COLLECT FUNCTION---

//preleva i dati dai sensori e li invia
/**
 * @brief Funzione che preleva i dati dai sensori, li incapsula e li passa alla libreria Jack
 */
void collectData() {

  //verifico che i sensori non siano addormentati (ovvero siano svegli)
  if (getSensorState() == LW_SENSOR_SLEEP) {

#ifdef DEBUG
    Serial.println(F("\n\nIMPOSSIBILE PRELEVARE I DATI SEI SENSORI PERCHE' SONO ADDORMENTATI\n\n"));
#endif

    //i sensori sono addormentati non posso prelevare i dati
    return;
  }

  //prelevo i dati dai sensori insieme al timestamp
  long timestamp = getTimestamp();
  uint8_t gsr = getGSR();
  double temperature = getTemperature();

#ifdef DEBUG
  Serial.println(F("\n\n---------LETTURE DEI SENSORI (collectData())---------\n"));
  Serial.print(F("TIMESTAMP: "));
  Serial.print(timestamp);
  Serial.print(F("\nTEMPERATURA: "));
  Serial.print(temperature);
  Serial.print(F("\nGSR: "));
  Serial.print(gsr);
  Serial.println(F("\n------------------\n\n"));
#endif

  //creo il contenitore del messaggio
  JData message;

  //aggiungo i dati
  message.add(TIMESTAMP_KEY, timestamp);
  message.add(GSR_KEY, gsr);
  message.add(TEMPERATURE_KEY, temperature);

  //invio il messaggio
  jack.send(message);

}


//---SETUP FUNCTION---
/**
 * @brief Funzione predisposta dall'IDE di Arduino che ha il compito di configurare il firmware
 */
void setup() {

#ifdef DEBUG
  Serial.begin(9600);
#endif

  //inizializzo i sensori
  setupSensor();

  //inizializzo il bluetooth
  setupBluetooth();

  //avvio jack
  jack.start();

}


//---LOOP FUNCTION---
/**
 * @brief Funzione predisposta dall'IDE di Arduino che ha viene iterata fino al reset del MCU
 */
void loop() {

  //loop jack
  jack.loop();

  //prelevo il tempo passato dall'inizio dell'esecuzione
  long now = millis();

  //se l'intervallo di data collect è stato raggiunto
  if (now - timeLastDataCollect >= INTERVAL_BETWEEN_DATA_COLLECT) {

    //prelevo i dati dai sensori
    collectData();

    //addormento i sensori
    sleepSensor();

    //salvo il tempo passato dall'inizio dell'esecuzione all'ultimo data collect
    timeLastDataCollect = now;

    //se è stato raggiunta la metà dell'intervallo di invio
  } else if (getSensorState() == LW_SENSOR_SLEEP && now - timeLastDataCollect >= (int) (INTERVAL_BETWEEN_DATA_COLLECT / 2)) {

    //sveglio i sensori
    wakeupSensor();

  }

}
