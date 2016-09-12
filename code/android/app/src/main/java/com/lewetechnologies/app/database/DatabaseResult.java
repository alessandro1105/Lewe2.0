package com.lewetechnologies.app.database;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Classe contenitore per i risultati prelevati dal database
 *
 * @author Alessandro Pasqualini - alessandro.pasqualini.1105@gmail.com
 * @version 1.00
 */
public class DatabaseResult implements Serializable {

    //contenitore dei dati
    private HashMap<Integer, HashMap<String, Object>> result;

    //indica il numero di record
    private int size;

    /**
     * Costruttore del contenitore
     */
    //costruttore
    public DatabaseResult() {

        //creo il contenitore dei dati
        result = new HashMap<Integer, HashMap<String, Object>>();
    }

    /**
     * Metodo che aggiunge un record vuoto nel contenitore
     * @return Ritorna l'indice del record appena aggiunto
     */
    //aggiunta record di risultati
    public int addRecord() {

        //nuovo record
        HashMap<String, Object> record = new HashMap<String, Object>();

        //inserisco il record
        result.put(size, record);

        //incremento size
        size++;

        //ritorno l'indice del record
        return size -1;

    }

    /**
     * Metodo che aggiunge un campo a un record immagazzinato nel contenitore
     *
     * @param index Indice del record
     * @param key Chiave del nuovo campo
     * @param value Valore del nuovo campo
     */
    //aggiunta di un field nel record
    public void addRecordField(int index, String key, Object value) {
        result.get(index).put(key, value);
    }

    /**
     * Metodo che restituisce il valore del campo del record specificati
     *
     * @param index Indice del record
     * @param key Chiave del campo del record
     * @return Ritorna il valore del campo
     */
    //get field dal record
    public Object getRecordField(int index, String key) {
        return result.get(index).get(key);
    }

    /**
     * Ritorna il numero di record immagazzinati nel contenitore
     *
     * @return Ritorna il numero di record immagazzinati nel contenitore
     */
    //ritorna il numero di record contenuti
    public int size() {
        return result.size();
    }

}
