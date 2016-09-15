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
 * @file SoftwareSerialJack.h
 * @brief Libreria SoftwareSerialJack per Arduino IDE
 * 
 * @author Alessandro Pasqualini (<alessandro.pasqualini.1105@gmail.com>)
 * @version 1.0
 * 
 * @copyright Copyright (c) 2016-2017 Alessandro Pasqualini
 * @copyright Apache License, Version 2.0
 */

#ifndef SOFTWARESERIALJACK_H
#define SOFTWARESERIALJACK_H

#include <Arduino.h>
#include <SoftwareSerial.h>
#include <JTransmissionMethod.h>

//---COSTANTI---
/**
 * @brief Indica il carattere deliminatore di inizio del messaggio
 */
#define SSJ_MESSAGE_START_CHARACTER '<' //carattere di inzio messaggio
/**
 * @brief Indica il carattere deliminatore di fine del messaggio
 */
#define SSJ_MESSAGE_FINISH_CHARACTER '>' //carattere di fine messaggio

/**
 * @brief Dimensione del buffer interno
 */
#define SSJ_BUFFER_SIZE 255


class SoftwareSerialJack : public JTransmissionMethod {

	public:
	
		SoftwareSerialJack(int RX, int TX, long baudRate, size_t bufferSize); //construttore con la scelta della dimensione del buffer
		SoftwareSerialJack(int RX, int TX, long baudRate); //costruttore senza la scelta del buffer
		SoftwareSerialJack(Stream &serial, size_t bufferSize);
		SoftwareSerialJack(Stream &serial); //costruttore
		~SoftwareSerialJack();
		
		size_t receive(char *buffer, size_t size); //metodo che inserisce il messaggio in un buffer e restituisce la dimensione del messaggio
		void send(char *message, size_t length); //invia il messaggio
		
		size_t available(); //restituisce la dimensione del buffer (>0 se ci sono messagi)


	private:

		void bufferInitialize(size_t size); //pulisce il buffer
		void bufferPut(char c); //inserisce il dato nel buffer (0 buffer pieno, 1= successo)
		char bufferGet(); //restituisce il dato alla posizione corrente
		int bufferAvailable(); //restituisce il numero di posizioni libere
		int bufferLength(); //restituisce il numero di elementi memorizzati nel buffer
		void bufferDestroy(); //distrugge il buffer e libera la memoria


		Stream *_serial; //istanza della classe SoftwareSerial
		
		//gestione del buffer
		char *_buffer; //puntatore al buffer
		int _size; //dimensione del buffer
		int _position; //posizione di testa del buffer
		int _length; //quantità di dati memorizzati nel buffer

};


#endif //SOFTWARESERIALJACK_H