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
 * @file JData.cpp
 * @brief Contenitore dei messaggi da inviare con Jack
 * 
 * @author Alessandro Pasqualini (<alessandro.pasqualini.1105@gmail.com>)
 * @version 1.0
 * 
 * @copyright Copyright (c) 2016-2017 Alessandro Pasqualini
 * @copyright Apache License, Version 2.0
 */

#include <Arduino.h>
#include "JData.h"


//---JDATA---

/**
 * @brief Costruttore del contenitore
 */
JData::JData() { //costruttore

   //costruisco la root
   _root = &_buffer.createObject();

   //indico che l'oggetto nested non è ancora stato creato
   _nestedObjectExists = 0;

}


/**
 * @brief Distruttore della classe
 */
JData::~JData() { //distruttore

   //elimino la root
   //delete _root;
}

//---ADD FUNCTION---
/**
 * @brief Metodo usato per inserire un dato nel contenitore
 * 
 * @param key Chiave dato
 * @param value Valore del dato
 */
void JData::add(const char *key, bool value) {
   //verifico se è stato costruito l'oggetto nested per memorizzare i dati
   if (!_nestedObjectExists) {
      createNestedObject();
   }

   (*_values)[key] = value;

}

/**
 * @brief Metodo usato per inserire un dato nel contenitore
 * 
 * @param key Chiave dato
 * @param value Valore del dato
 */
void JData::add(const char *key, float value) {
   //verifico se è stato costruito l'oggetto nested per memorizzare i dati
   if (!_nestedObjectExists) {
      createNestedObject();
   }

   (*_values)[key] = value;
}

/**
 * @brief Metodo usato per inserire un dato nel contenitore
 * 
 * @param key Chiave dato
 * @param value Valore del dato
 */
void JData::add(const char *key, double value) {
   //verifico se è stato costruito l'oggetto nested per memorizzare i dati
   if (!_nestedObjectExists) {
      createNestedObject();
   }

   (*_values)[key] = value;
}

/**
 * @brief Metodo usato per inserire un dato nel contenitore
 * 
 * @param key Chiave dato
 * @param value Valore del dato
 */
void JData::add(const char *key, signed char value) {
   //verifico se è stato costruito l'oggetto nested per memorizzare i dati
   if (!_nestedObjectExists) {
      createNestedObject();
   }

   (*_values)[key] = value;
}

/**
 * @brief Metodo usato per inserire un dato nel contenitore
 * 
 * @param key Chiave dato
 * @param value Valore del dato
 */
void JData::add(const char *key, signed long value) {
   //verifico se è stato costruito l'oggetto nested per memorizzare i dati
   if (!_nestedObjectExists) {
      createNestedObject();
   }

   (*_values)[key] = value;
}

/**
 * @brief Metodo usato per inserire un dato nel contenitore
 * 
 * @param key Chiave dato
 * @param value Valore del dato
 */
void JData::add(const char *key, signed int value) {
   //verifico se è stato costruito l'oggetto nested per memorizzare i dati
   if (!_nestedObjectExists) {
      createNestedObject();
   }

   (*_values)[key] = value;
}

/**
 * @brief Metodo usato per inserire un dato nel contenitore
 * 
 * @param key Chiave dato
 * @param value Valore del dato
 */
void JData::add(const char *key, signed short value) {
   //verifico se è stato costruito l'oggetto nested per memorizzare i dati
   if (!_nestedObjectExists) {
      createNestedObject();
   }

   (*_values)[key] = value;
}

/**
 * @brief Metodo usato per inserire un dato nel contenitore
 * 
 * @param key Chiave dato
 * @param value Valore del dato
 */
void JData::add(const char *key, unsigned char value) {
   //verifico se è stato costruito l'oggetto nested per memorizzare i dati
   if (!_nestedObjectExists) {
      createNestedObject();
   }

   (*_values)[key] = value;
}

/**
 * @brief Metodo usato per inserire un dato nel contenitore
 * 
 * @param key Chiave dato
 * @param value Valore del dato
 */
void JData::add(const char *key, unsigned long value) {
   //verifico se è stato costruito l'oggetto nested per memorizzare i dati
   if (!_nestedObjectExists) {
      createNestedObject();
   }

   (*_values)[key] = value;
}

/**
 * @brief Metodo usato per inserire un dato nel contenitore
 * 
 * @param key Chiave dato
 * @param value Valore del dato
 */
void JData::add(const char *key, unsigned int value) {
   //verifico se è stato costruito l'oggetto nested per memorizzare i dati
   if (!_nestedObjectExists) {
      createNestedObject();
   }

   (*_values)[key] = value;
}

/**
 * @brief Metodo usato per inserire un dato nel contenitore
 * 
 * @param key Chiave dato
 * @param value Valore del dato
 */
void JData::add(const char *key, unsigned short value) {
   //verifico se è stato costruito l'oggetto nested per memorizzare i dati
   if (!_nestedObjectExists) {
      createNestedObject();
   }

   (*_values)[key] = value;
}

/**
 * @brief Metodo usato per inserire un dato nel contenitore
 * 
 * @param key Chiave dato
 * @param value Valore del dato
 */
void JData::add(const char *key, const char *value) {
   //verifico se è stato costruito l'oggetto nested per memorizzare i dati
   if (!_nestedObjectExists) {
      createNestedObject();
   }

   (*_values)[key] = value;
}

/**
 * @brief Metodo usato per inserire un dato nel contenitore
 * 
 * @param key Chiave dato
 * @param value Valore del dato
 */
void JData::add(const char *key, const String &value) {
   //verifico se è stato costruito l'oggetto nested per memorizzare i dati
   if (!_nestedObjectExists) {
      createNestedObject();
   }

   (*_values)[key] = value;
}

//---GET FUNCTION---
/**
 * @brief Metodo che restituice il valore del dato indicato
 * 
 * @param key Chiave del dato
 * @return Istanza dell'oggetto JsonVariant contenente il valore del dato (autocasting in base al target)
 */
JsonVariant JData::get(const char *key) {

   //ritorno il dato richiesto
   return (*_values)[key];
}


//---PRIVATE---

//funzione che crea l'oggetto nested
void JData::createNestedObject() {

   //creo l'oggetto nested
   _values = &_root->createNestedObject(JK_MESSAGE_PAYLOAD);

   //indico che è stato creato
   _nestedObjectExists = 1;
}


//---PROTECTED---

//costruttore privato che permette di costruire JData a partire da JsonObject
JData::JData(JsonObject &root) { //costruttore

   //salvo la root
   _root = &root;

   //imposto l'oggetto nested
   _values = &_buffer.parseObject((*_root)[JK_MESSAGE_PAYLOAD].asString());

   //indico che l'oggetto nested è stato creato
   _nestedObjectExists = 1;
}

//restituisce la root
JsonObject *JData::getRoot() {
   return _root;
}


