package com.lewetechnologies.app.jack;

import com.lewetechnologies.app.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Classe contenitore per i messaggi del protocollo Jack
 *
 * @author Alessandro Pasqualini - alessandro.pasqualini.1105@gmail.com
 * @version 1.00
 */
public class JData {

    //root JSON
    private JSONObject root;

    //nested object
    private JSONObject values;

    //indica se è stato
    private boolean nestedObjectExists;


    //---PUBLIC---

    /**
     * Costruttore della classe
     */
    public JData() {

        //creo la root JSON
        root = new JSONObject();

        //imposto che non è ancora stato creato l'oggetto nested
        nestedObjectExists = false;
    }

    /**
     * Costruttore della classe
     *
     * @param message Messaggio Jack nel formato JSON
     * @throws JSONException
     */
    //costruttore passando un messaggio in JSONObject
    public JData(JSONObject message) throws JSONException {
        //salvo la root del messaggio
        root = message;

        //prelevo il payload dei dati
        values = root.getJSONObject(Jack.JK_MESSAGE_PAYLOAD);

    }

    /**
     * Metodo che aggiunge il dato al messaggio
     *
     * @param key Chiave del dato da inserire
     * @param value Valore del dato da inserire
     * @return Valore boleano che indica se l'inserimento è avvenuto correttamente
     * @throws JDataException
     */
    //adder
    public boolean add(String key, boolean value) throws JDataException {

        //se non esiste l'oggetto nested lo creo
        if (!nestedObjectExists) {
            createNestedObject();
        }

        try {
            values.put(key, value);

            return true;

        } catch (JSONException e) {
            Logger.e("JData", "add", e, Logger.SEVERE);

            //lancio l'eccezione
            throw new JDataException();
        }

    }
    /**
     * Metodo che aggiunge il dato al messaggio
     *
     * @param key Chiave del dato da inserire
     * @param value Valore del dato da inserire
     * @return Valore boleano che indica se l'inserimento è avvenuto correttamente
     * @throws JDataException
     */
    public boolean add(String key, float value) throws JSONException {

        //se non esiste l'oggetto nested lo creo
        if (!nestedObjectExists) {
            createNestedObject();
        }

        try {
            values.put(key, value);

            return true;

        } catch (JSONException e) {
            Logger.e("JData", "add", e, Logger.SEVERE);

            //lancio l'eccezione
            throw new JDataException();
        }

    }
    /**
     * Metodo che aggiunge il dato al messaggio
     *
     * @param key Chiave del dato da inserire
     * @param value Valore del dato da inserire
     * @return Valore boleano che indica se l'inserimento è avvenuto correttamente
     * @throws JDataException
     */
    public boolean add(String key, double value) throws JSONException {

        //se non esiste l'oggetto nested lo creo
        if (!nestedObjectExists) {
            createNestedObject();
        }

        try {
            values.put(key, value);

            return true;

        } catch (JSONException e) {
            Logger.e("JData", "add", e, Logger.SEVERE);

            //lancio l'eccezione
            throw new JDataException();
        }

    }
    /**
     * Metodo che aggiunge il dato al messaggio
     *
     * @param key Chiave del dato da inserire
     * @param value Valore del dato da inserire
     * @return Valore boleano che indica se l'inserimento è avvenuto correttamente
     * @throws JDataException
     */
    public boolean add(String key, char value) throws JSONException {

        //se non esiste l'oggetto nested lo creo
        if (!nestedObjectExists) {
            createNestedObject();
        }

        try {
            values.put(key, value);

            return true;

        } catch (JSONException e) {
            Logger.e("JData", "add", e, Logger.SEVERE);

            //lancio l'eccezione
            throw new JDataException();
        }

    }
    /**
     * Metodo che aggiunge il dato al messaggio
     *
     * @param key Chiave del dato da inserire
     * @param value Valore del dato da inserire
     * @return Valore boleano che indica se l'inserimento è avvenuto correttamente
     * @throws JDataException
     */
    public boolean add(String key, long value) throws JSONException {

        //se non esiste l'oggetto nested lo creo
        if (!nestedObjectExists) {
            createNestedObject();
        }

        try {
            values.put(key, value);

            return true;

        } catch (JSONException e) {
            Logger.e("JData", "add", e, Logger.SEVERE);

            //lancio l'eccezione
            throw new JDataException();
        }

    }
    /**
     * Metodo che aggiunge il dato al messaggio
     *
     * @param key Chiave del dato da inserire
     * @param value Valore del dato da inserire
     * @return Valore boleano che indica se l'inserimento è avvenuto correttamente
     * @throws JDataException
     */
    public boolean add(String key, int value) throws JSONException {

        //se non esiste l'oggetto nested lo creo
        if (!nestedObjectExists) {
            createNestedObject();
        }

        try {
            values.put(key, value);

            return true;

        } catch (JSONException e) {
            Logger.e("JData", "add", e, Logger.SEVERE);

            //lancio l'eccezione
            throw new JDataException();
        }

    }
    /**
     * Metodo che aggiunge il dato al messaggio
     *
     * @param key Chiave del dato da inserire
     * @param value Valore del dato da inserire
     * @return Valore boleano che indica se l'inserimento è avvenuto correttamente
     * @throws JDataException
     */
    public boolean add(String key, short value) throws JSONException {

        //se non esiste l'oggetto nested lo creo
        if (!nestedObjectExists) {
            createNestedObject();
        }

        try {
            values.put(key, value);

            return true;

        } catch (JSONException e) {
            Logger.e("JData", "add", e, Logger.SEVERE);

            //lancio l'eccezione
            throw new JDataException();
        }

    }
    /**
     * Metodo che aggiunge il dato al messaggio
     *
     * @param key Chiave del dato da inserire
     * @param value Valore del dato da inserire
     * @return Valore boleano che indica se l'inserimento è avvenuto correttamente
     * @throws JDataException
     */
    public boolean add(String key, String value) throws JSONException {

        //se non esiste l'oggetto nested lo creo
        if (!nestedObjectExists) {
            createNestedObject();
        }

        try {
            values.put(key, value);

            return true;

        } catch (JSONException e) {
            Logger.e("JData", "add", e, Logger.SEVERE);

            //lancio l'eccezione
            throw new JDataException();
        }

    }

    //get method

    /**
     * Metodo che ritorna il dato richiesto in boolean
     *
     * @param key Chiave del dato da prelevare
     * @return Ritorna il dato richiesto se presente
     * @throws JDataException
     */
    public boolean getBoolean(String key) {
        try {
            return values.getBoolean(key);

        } catch (JSONException e) {
            //lancio l'eccezione
            throw new JDataException();
        }
    }
    /**
     * Metodo che ritorna il dato richiesto in float
     *
     * @param key Chiave del dato da prelevare
     * @return Ritorna il dato richiesto se presente
     * @throws JDataException
     */
    public double getFloat(String key) {
        try {
            return values.getDouble(key);

        } catch (JSONException e) {
            //lancio l'eccezione
            throw new JDataException();
        }
    }
    /**
     * Metodo che ritorna il dato richiesto in double
     *
     * @param key Chiave del dato da prelevare
     * @return Ritorna il dato richiesto se presente
     * @throws JDataException
     */
    public double getDouble(String key) {
        try {
            return values.getDouble(key);

        } catch (JSONException e) {
            //lancio l'eccezione
            throw new JDataException();
        }
    }
    /**
     * Metodo che ritorna il dato richiesto in char
     *
     * @param key Chiave del dato da prelevare
     * @return Ritorna il dato richiesto se presente
     * @throws JDataException
     */
    public char getChar(String key) {
        try {
            return values.getString(key).charAt(0);

        } catch (JSONException e) {
            //lancio l'eccezione
            throw new JDataException();
        }
    }
    /**
     * Metodo che ritorna il dato richiesto in long
     *
     * @param key Chiave del dato da prelevare
     * @return Ritorna il dato richiesto se presente
     * @throws JDataException
     */
    public long getLong(String key) {
        try {
            return values.getLong(key);

        } catch (JSONException e) {
            //lancio l'eccezione
            throw new JDataException();
        }
    }
    /**
     * Metodo che ritorna il dato richiesto in int
     *
     * @param key Chiave del dato da prelevare
     * @return Ritorna il dato richiesto se presente
     * @throws JDataException
     */
    public int getInt(String key) {
        try {
            return values.getInt(key);

        } catch (JSONException e) {
            //lancio l'eccezione
            throw new JDataException();
        }
    }
    /**
     * Metodo che ritorna il dato richiesto in short
     *
     * @param key Chiave del dato da prelevare
     * @return Ritorna il dato richiesto se presente
     * @throws JDataException
     */
    public int getShort(String key) {
        try {
            return values.getInt(key);

        } catch (JSONException e) {
            //lancio l'eccezione
            throw new JDataException();
        }
    }
    /**
     * Metodo che ritorna il dato richiesto in string
     *
     * @param key Chiave del dato da prelevare
     * @return Ritorna il dato richiesto se presente
     * @throws JDataException
     */
    public String getString(String key) {
        try {
            return values.getString(key);

        } catch (JSONException e) {
            //lancio l'eccezione
            throw new JDataException();
        }
    }

    /**
     * Metodo che ritorna la radice del messaggio Jack (formato JSON)
     *
     * @return ritorna la radice del messaggio Jack
     * @throws JDataException
     */
    //get JsonObject
    public JSONObject getRoot() throws JDataException {

        //se è stato creato l'oggetto nested lo inserisco
        if (nestedObjectExists) {
            try {
                root.put(Jack.JK_MESSAGE_PAYLOAD, values);

            } catch (JSONException e){
                Logger.e("JData", "getRoot", e, Logger.SEVERE);

                //lancio l'eccezione
                throw new JDataException();
            }
        }

        //ritorno la root del messaggio
        return root;
    }

    //---PRIVATE---

    private void createNestedObject() {

        //creo l'oggetto nested
        values = new JSONObject();

        //indico che è stato creato l'oggetto nested
        nestedObjectExists = true;

    }


}
