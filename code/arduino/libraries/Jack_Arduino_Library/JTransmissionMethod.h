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
 * @file JTransmissionMethod.h
 * @brief Classe astratta (interfaccia) contenente i metodi da implementare per poter utilizzare il mezzo di comunicazione
 * 
 * @author Alessandro Pasqualini (<alessandro.pasqualini.1105@gmail.com>)
 * @version 1.0
 * 
 * @copyright Copyright (c) 2016-2017 Alessandro Pasqualini
 * @copyright Apache License, Version 2.0
 */

#ifndef JTRANSMISSIONMETHOD_H
#define JTRANSMISSIONMETHOD_H


//---JTRANSMISSION METHOD---
class JTransmissionMethod {

	public:
		
      /**
       * @brief Metodo usato per prelevare il primo messaggio disponibile
       * 
       * @param buffer Buffer in cui salvare il messaggio
       * @param size Dimensione del buffer
       * 
       * @return Lunghezza del messaggio
       */
		virtual size_t receive(char *buffer, size_t size); //deve restituire il messaggio da passare a Jack
      /**
       * @brief Metodo usato per inviare un messaggio nel mezzo di comunicazione
       * 
       * @param message Il messaggio da inviare
       * @param length La lunghezza del messaggio
       */
		virtual void send(char *message, size_t length); //invia il messaggio

      /**
       * @brief Metodo che ritorna il numero di caratteri disponibili e pronti per essere prelevati
       * @return Il numero di caratteri disponibili
       */
		virtual size_t available(); //restituisce true se ci sono dati da ricevere nel buffer

};

#endif //JTRANSMISSIONMETHOD_H