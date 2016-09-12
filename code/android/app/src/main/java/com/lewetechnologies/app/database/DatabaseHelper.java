package com.lewetechnologies.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lewetechnologies.app.logger.Logger;

/**
 * Classe Helper del database
 *
 * @author Alessandro Pasqualini - alessandro.pasqualini.1105@gmail.com
 * @version 1.00
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //query di creazione del db se non esiste (passata nel costruttore)
    private String databaseCreateStatement;

    /**
     * Costruttore della classe helper del database
     *
     * @param context Costesto dell'applicazione
     * @param databaseName Nome del databse
     * @param databaseVersion Versione del database
     * @param databaseCreateStatement Statement di creazione del database
     */
    //costruttore
    public DatabaseHelper(Context context, String databaseName, int databaseVersion, String databaseCreateStatement) {
        super(context, databaseName, null, databaseVersion);

        //salvo la query di costruzione
        this.databaseCreateStatement = databaseCreateStatement;
    }

    //creo il database se non esiste
    @Override
    public void onCreate(SQLiteDatabase database) {

        //creo il database
        database.execSQL(databaseCreateStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        //non deve effettuare upgrade
    }

}