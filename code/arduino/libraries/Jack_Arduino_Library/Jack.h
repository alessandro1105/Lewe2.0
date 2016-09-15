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
 * @file Jack.h
 * @brief Classe principale della libreria Jack per Arduino IDE
 * 
 * @author Alessandro Pasqualini (<alessandro.pasqualini.1105@gmail.com>)
 * @version 1.0
 * 
 * @copyright Copyright (c) 2016-2017 Alessandro Pasqualini
 * @copyright Apache License, Version 2.0
 */


#ifndef JACK_H
#define JACK_H


#include <Arduino.h>
#include <HashMap.h>
#include "JData.h"
#include "JTransmissionMethod.h"
#include <ArduinoJson.h>


//---CONSTANTI---

//Jack
/**
 * @brief Chiave della tipologia del messaggio
 */
#define JK_MESSAGE_TYPE "type" //key tipo messaggio
/**
 * @brief Tipologia del messaggio ACK
 */
#define JK_MESSAGE_TYPE_ACK "ack" //tipo ack
/**
 * @brief Tipologia del messaggio DATA
 */ 
#define JK_MESSAGE_TYPE_DATA "data" //tipo dati

/**
 * @brief Chiave dell'ID del messaggio
 */		
#define JK_MESSAGE_ID "id" //id messaggio
/**
 * @brief Chiave del payload del messaggio
 */
#define JK_MESSAGE_PAYLOAD "val" //payload messaggio

/**
 * @brief Timer che controlla l'invio dei messaggi (millisecondi)
 */
#define JK_TIMER_RESEND_MESSAGE 1000//tempo (ms) da attendere prima di reinviare i messaggi non confermati
/**
 * @brief Timer che controlla il polling del mezzo di comunicazione (millisecondi)
 */
#define JK_TIMER_POLLING 500 //tempo (ms) da attendere tra un polling e un altro del mezzo di strasmissione

//---DEBUG---
/**
 * @brief Costante usata per abilitare il codice di debug
 */
#define DEBUG 1

//indico l'esistenza di JData
class JData;

//---JACK---
//classe Jack per il protocollo
class Jack {
		
	public:
	
		//construttori
		Jack(JTransmissionMethod &mmJTM, void (*onReceive)(JData &, long), void (*onReceiveAck)(long), long (*getMessageID)()); //costruttore con mmJTM e funzione onRceive e OnReceiveAck
		Jack(JTransmissionMethod &mmJTM, void (*onReceive)(JData &, long), void (*onReceiveAck)(long), long (*getMessageID)(), long timerSendMessage, long timerPolling); //tempo per il reinvio
		
		//distruttore
		~Jack(); //distruttore
		
		//funzioni per attivare il polling del mezzo di trasmissione
		void start(); //avvia il polling
		void stop(); //stoppa il polling
		
		//controlla il buffer di invio
		void flushBufferSend(); //cancella i buffer contenente i messaggi da inviare
		
		//invio messaggi
		long send(JData &message); //invia il messaggio
		
		//loop
		void loop(); //luppa per simulare il thread ed esegue le funzioni di polling su mmJTM


	private:		

		//funzione che elabora i messaggi ricevuti
		void execute(char *messageJSON); //funzione che gestisce il protocollo		

		//gestione ACK
		void sendAck(long id); //invia l'ack di conferma
		void checkAck(long id); //controlla l'ack

		//timer
		long _timerSendMessage; //tempo (ms) da attendere prima di reinviare i messaggi non confermati
		long _timerPolling; //tempo (ms) da attendere tra un polling e un altro del mezzo di strasmissione

		//tempi
		long _timeLastPolling;
		long _timeLastSend;

		//mezzo di trasmissione
		JTransmissionMethod *_mmJTM; //contiene il metodo di trasmissione da usare

		//contenitori dei dati
		HashMap<long, char *> *_messageBuffer; //buffer per i messaggi da inviare
		
		//indica se il polling è fermo o meno
		uint8_t _pollingEnabled; //indica se il polling è fermo (non si ricevono messaggi)

		//puntatori a funzioni esterne
		void (*_onReceive)(JData &, long); //puntatore a funzione OnReceive
		void (*_onReceiveAck)(long); //puntatore a funzione OnReceive
		long (*_getMessageID)(); //puntatore a funzione per ottenere il timestamp in long
		
};


#endif //JACK_H