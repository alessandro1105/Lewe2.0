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
 * @file JData.h
 * @brief Contenitore dei messaggi da inviare con Jack
 * 
 * @author Alessandro Pasqualini (<alessandro.pasqualini.1105@gmail.com>)
 * @version 1.0
 * 
 * @copyright Copyright (c) 2016-2017 Alessandro Pasqualini
 * @copyright Apache License, Version 2.0
 */

#ifndef JDATA_H
#define JDATA_H

#include <Arduino.h>
#include <ArduinoJson.h>
#include <Jack.h>


//---JDATA---
//classe usata come contenitore per i messaggi
class JData {

   //la classe Jack può accedere ai membri privati di JData
   friend class Jack;

   public:
      
      JData();
      ~JData(); //distruttore

      //add function
      void add(const char *key, bool value);
      void add(const char *key, float value);
      void add(const char *key, double value);
      void add(const char *key, signed char value);
      void add(const char *key, signed long value);
      void add(const char *key, signed int value);
      void add(const char *key, signed short value);
      void add(const char *key, unsigned char value);
      void add(const char *key, unsigned long value);
      void add(const char *key, unsigned int value);
      void add(const char *key, unsigned short value);
      void add(const char *key, const char *value);
      void add(const char *key, const String &value);
      
      
      //get
      JsonVariant get(const char *key);


   private:

      //costruisce l'oggetto nested per memorizzare i dati degli utenti
      void createNestedObject();

      //indica se è stato costruito l'oggetto nested
      uint8_t _nestedObjectExists;

      //buffer Json
      StaticJsonBuffer<200> _buffer;

      //json object
      JsonObject *_root;

      //json nested object
      JsonObject *_values;

   protected:

      //costruttore privato che permette di costruire JData a partire da JsonObject
      JData(JsonObject &root);

      //restituisce la root
      JsonObject *getRoot(); 

};


#endif //JDATA