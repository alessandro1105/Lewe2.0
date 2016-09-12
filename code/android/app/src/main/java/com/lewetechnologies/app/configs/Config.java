package com.lewetechnologies.app.configs;

import com.lewetechnologies.app.logger.Logger;

/**
 * Contiene constanti di configurazione del comportamento dell'applicazione
 *
 * @author Alessandro Pasqualini - alessandro.pasqualini.1105@gmail.com
 * @version 1.00
 */
public class Config {

    //---INTENT REQUEST CODE---
    /**
     * Codice di request nell'intent per la chiusura dell'applicazione
     */
    public static final int REQUEST_EXIT_CODE = 0; //richiede l'uscita dall'app (USATO IN SETTINGS)
    /**
     * Codice di request nell'intent per l'attivazione del bluetooth
     */
    public static final int REQUEST_ENABLE_BT = 1; //richiede l'attivazione del bt


    //---INTENT RESULT CODE---
    /**
     * Codice di result restituito dall'intent per la chiusura dell'applicazione
     */
    public static final int RESULT_EXIT_CODE = 1; //ritorna la richiesta di uscita dell'app (USATO IN SETTINGS)


    //---LOGGER ERROR LEVEL---
    /**
     * Indica il livello di errore corrente, il logger stampa solo gli errori con livello maggiore di quello specificato
     */
    public static final int LOGGER_ERROR_LEVEL = Logger.DEBUG;


    //---BT NAME LEWEBAND---
    /**
     * Indica il nome del bluetooth del bracciale
     */
    public static final String LEWEBAND_BT_NAME = "LW2v0";


    //---SHARED PREFERENCE---
    /**
     * Indica il nome univoco delle preferenze condivise
     */
    public static final String SHARED_PREFERENCE_FILE = "com.lewetechnologies.com.SharedPreferences";


    //---DATABASE SETTINGS---
    /**
     * Indica il nome del file contenente il database
     */
    public static final String DATABASE_FILE_NAME = "com.lewetechnologies.com.Database.sql"; //nome del file
    /**
     * Indica la versione del database
     */
    public static final int DATABASE_VERSION = 1; //verisone del database


    //---SHARED PREFERENCE KEY---
    /**
     * Chiave usata nelle preferenze condivise per contenere il MAC Address del bracciale
     */
    public static final String SHARED_PREFERENCE_KEY_DEVICE_MAC = "shared_preference_key_device_mac";
    /**
     * Chiave usata nelle preferenze condivise per indicare se è stata effettuata l'associazione per la prima volta
     */
    public static final String SHARED_PREFERENCE_KEY_FIRST_ASSOCIATION = "shared_preference_key_first_association";
    /**
     * Chiave usata nelle preferenze condivise per contenere il nome bluetooth del bracciale
     */
    public static final String SHARED_PREFERENCE_KEY_BAND_NAME_ASSOCIATED = "shared_preference_key_band_name_associated";


    //---CHIAVI PER EXTRA INTENT CONDIVISI--
    /**
     * Chiave usata negli extra degli intent per indicare la lettura del sensore GSR
     */
    public final static String EXTRA_DATA_GSR = "com.lewetechnologies.app.services.EXTRA_DATA_GSR"; //dato gsr
    /**
     * Chiave usata negli extra degli intent per indicare la lettura del sensore di temperatura
     */
    public final static String EXTRA_DATA_TEMPERATURE = "com.lewetechnologies.app.services.EXTRA_DATA_TEMPERATURE"; //dato temperature
    /**
     * Chiave usata negli extra degli intent per indicare il timestamp di rilevazione dei sensori (data di rilevazione)
     */
    public final static String EXTRA_DATA_TIMESTAMP = "com.lewetechnologies.app.services.EXTRA_DATA_TIMESTAMP"; //dato gsr


    //---CHIAVI PER JACK--
    /**
     * Chiave usata nel protocollo Jack per indicare la lettura del sensore di temperatura
     */
    public final static String JACK_TEMPERATURE = "TME";
    /**
     * Chiave usata nel protocollo Jack per indicare la lettura del sensore GSR
     */
    public final static String JACK_GSR = "GSR";
    /**
     * Chiave usata nel protocollo Jack per indicare il timestamp di rilevazione dei sensori (data di rilevazione)
     */
    public final static String JACK_TIMESTAMP = "TMP";


    //---CHIAVI PER DB---
    /**
     * Nome del sensore di temperatura usato nel database
     */
    public final static String DATABASE_KEY_TEMPERATURE = "TME";
    /**
     * Nome del sensore GSR usato nel database
     */
    public final static String DATABASE_KEY_GSR = "GSR";
}
