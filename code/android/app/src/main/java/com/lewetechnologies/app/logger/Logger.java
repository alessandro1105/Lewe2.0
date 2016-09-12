package com.lewetechnologies.app.logger;

import android.util.Log;
import com.lewetechnologies.app.configs.Config;

/**
 * Classe usata per la gesione dei log (usata per il debugging dell'applicazione)
 *
 * @author Alessandro Pasqualini - alessandro.pasqualini.1105@gmail.com
 * @version 1.00
 */

//LOGGER class used to handle better log info
public class Logger {

    //---LOGGER ERROR LEVEL---
    /**
     * Livello di errore: nessun log (usato in produzione)
     */
    public static final int NO_LOG = -1; //severe level
    /**
     * Livello di errore: informazioni di debug
     */
    public static final int DEBUG = 0; //debug level
    /**
     * Livello di errore: informazioni generiche
     */
    public static final int INFO = 1; //info level
    /**
     * Livello di errore: informazioni di warning
     */
    public static final int WARNING = 2; //warning level
    /**
     * Livello di errore: informazioni gravi (massimo livello di gravità)
     */
    public static final int SEVERE = 3; //severe level

    //---METHOD FOR LOGGING USING DEBUG ERROR LEVEL---
    /**
     * Metodo che stampa log informativi (usa il livello DEBUG)
     *
     * @param tag Tag del log
     * @param msg Messaggio del log
     */
    public static void i(String tag, String msg) { i(tag, msg, DEBUG); }
    /**
     * Metodo che stampa log di debug (usa il livello DEBUG)
     *
     * @param tag Tag del log
     * @param msg Messaggio del log
     */
    public static void d(String tag, String msg) { d(tag, msg, DEBUG); }
    /**
     * Metodo che stampa log di errore (usa il livello DEBUG)
     *
     * @param tag Tag del log
     * @param msg Messaggio del log
     */
    public static void e(String tag, String msg) { e(tag, msg, DEBUG); }
    /**
     * Metodo che stampa log di errore con annessa eccezione (usa il livello DEBUG)
     *
     * @param tag Tag del log
     * @param msg Messaggio del log
     * @param e Eccezione da loggare
     */
    public static void e(String tag, String msg, Exception e) { e(tag, msg, DEBUG); }



    //---METHOD FOR LOGGING USING USER SPECIFIED ERROR LEVEL--
    /**
     * Metodo che stampa log informativi
     *
     * @param tag Tag del log
     * @param msg Messaggio del log
     * @param errorLevel livello di errore
     */
    public static void i(String tag, String msg, int errorLevel) {
        if (errorLevel >= Config.LOGGER_ERROR_LEVEL) {
            Log.i(tag, msg);
        }
    }
    /**
     * Metodo che stampa log di debug
     *
     * @param tag Tag del log
     * @param msg Messaggio del log
     * @param errorLevel livello di errore
     */
    public static void d(String tag, String msg, int errorLevel) {
        if (errorLevel >= Config.LOGGER_ERROR_LEVEL) {
            Log.d(tag, msg);
        }
    }
    /**
     * Metodo che stampa log di errore
     *
     * @param tag Tag del log
     * @param msg Messaggio del log
     * @param errorLevel livello di errore
     */
    public static void e(String tag, String msg, int errorLevel) {
        if (errorLevel >= Config.LOGGER_ERROR_LEVEL) {
            Log.e(tag, msg);
        }
    }
    /**
     * Metodo che stampa log di errore con annessa eccezione
     *
     * @param tag Tag del log
     * @param msg Messaggio del log
     * @param e Eccezione da loggare
     * @param errorLevel livello di errore
     */
    public static void e(String tag, String msg, Exception e, int errorLevel) {
        if (errorLevel >= Config.LOGGER_ERROR_LEVEL) {
            Log.e(tag, msg, e);
        }
    }

}
