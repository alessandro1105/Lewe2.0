package com.lewetechnologies.app.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lewetechnologies.app.configs.Config;

/**
 * Classe di gestione del database
 *
 * @author Alessandro Pasqualini - alessandro.pasqualini.1105@gmail.com
 * @version 1.00
 */
public class Database {

    //---DATABASE SETTINGS---
    /**
     * Costante contenente il nome del file del database (preleva il suo valore direttamente dalla classe Config)
     */
    public static final String DATABASE_FILE_NAME = Config.DATABASE_FILE_NAME; //nome del file
    /**
     * Costante contenente la versione del database (preleva il suo valore direttamente dalla classe Config)
     */
    public static final int DATABASE_VERSION = Config.DATABASE_VERSION; //verisone del database

    //tabella
    /**
     * Nome della tabella contenuta nel databse
     */
    public static final String TABLE_NAME = "sensors";

    //field del database
    /**
     * Nome della colonna ID contenuta nella tabella
     */
    public static final String CULUMN_NAME_ID = "id"; //id
    /**
     * Nome della colonna Sensor Name contenuta nella tabella
     */
    public static final String CULUMN_NAME_SENSOR_NAME = "sensor_name"; //sensor name
    /**
     * Nome della colonna Sensor Value contenuta nella tabella
     */
    public static final String CULUMN_NAME_SENSOR_VALUE = "sensor_value"; //sensor value
    /**
     * Nome della colonna Timestamp contenuta nella tabella
     */
    public static final String CULUMN_NAME_TIMESTAMP = "timestamp"; //timestamp

    //statement di creazione
    private static final String DATABASE_CREATE_STATEMENT =
            "CREATE TABLE " + TABLE_NAME + " (" +
            CULUMN_NAME_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CULUMN_NAME_SENSOR_NAME + " TEXT NOT NULL, " +
            CULUMN_NAME_SENSOR_VALUE + " TEXT NOT NULL, " +
            CULUMN_NAME_TIMESTAMP + " INTEGER NOT NULL" +
            ");";


    //---VARIABILI---
    private Context context; //context dell'applicazione (per aprire connessione con db)
    private SQLiteDatabase database; //database
    private DatabaseHelper databaseHelper; //helper per la connessione al db


    /**
     * Costruttore della classe
     *
     * @param context Contesto dell'applicazione
     */
    //costruttore
    public Database(Context context) {

        //salvo il context
        this.context = context;

        //inizializzo le varibili
        database = null;
        databaseHelper = null;

    }


    /**
     * Metodo che apre la connesione con il database
     */
    //apertura database
    public void open() { //apre la connessione con il db

        //apro il database
        databaseHelper = new DatabaseHelper(context, DATABASE_FILE_NAME, DATABASE_VERSION, DATABASE_CREATE_STATEMENT);

        //ottengo il database
        database = databaseHelper.getWritableDatabase();

    }


    /**
     * Metodo che chiude la connessione con il database
     */
    //chiusura database
    public void close() { //chiude la connessione con il db

        //se il database è stato aperto
        if (databaseHelper != null)
            databaseHelper.close(); //chiudo il database

        //elimino il riferimento al database
        database = null;

    }

    /**
     * Metodo che esegue la query passata come argomento
     *
     * @param querySQL Query da eseguire nel database
     * @return Ritorna il cursore per scorrere i record selezionati
     * @throws DatabaseException
     */
    //execute
    public Cursor executeQuery(String querySQL) {

        //se il database è stato aperto
        if (database != null) {

            //se è una query di select
            if (querySQL.startsWith("SELECT")) {

                //eseguo la query e ritorno il risultato
                return database.rawQuery(querySQL, null);

            //non è una query che necessita risultato
            } else {

                //eseguo la query
                database.execSQL(querySQL);

                //ritorno null
                return null;
            }

        //il database non è stato aperto
        } else {
            throw new DatabaseException();
        }
    }

}
