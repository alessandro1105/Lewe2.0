package com.lewetechnologies.app.jack;

import com.lewetechnologies.app.logger.Logger;
import com.lewetechnologies.app.utils.CyclicalThread;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Classe principale dell'implementazione del protocollo Jack.
 * Deve essere estesa per implementare gli handler agli eventi di ricezione di un messaggio e di ricezione di una conferma
 * e per il metodo che fornisce un ID univoco per i messaggi inviati
 *
 *
 * @author Alessandro Pasqualini - alessandro.pasqualini.1105@gmail.com
 * @version 1.00
 */
public abstract class Jack {

    //---COSTANTI---
    private static final String JK_MESSAGE_TYPE = "type"; //key tipo messaggio
    private static final String JK_MESSAGE_TYPE_ACK = "ack"; //tipo ack
    private static final String JK_MESSAGE_TYPE_DATA = "data"; //tipo dati

    private static final String JK_MESSAGE_ID = "id"; //id messaggio
    /**
     * Chiave del messaggio Jack che contiene il payload del messaggio
     */
    public static final String JK_MESSAGE_PAYLOAD = "val"; //payload messaggio

    private static final long JK_TIMER_RESEND_MESSAGE = 1000;//tempo (ms) da attendere prima di reinviare i messaggi non confermati
    private static final long JK_TIMER_POLLING = 500; //tempo (ms) da attendere tra un polling e un altro del mezzo di strasmissione


    //---VARIABILI---
    //mmJTM
    private JTransmissionMethod mmJTM; //mmJTM

    //timer
    private long timerSendMessage; //tempo (ms) da attendere prima di reinviare i messaggi non confermati

    //thread che gestisce il polling
    private PollingThread pollingThread;

    //contenitori dei messaggi
    private HashMap<Long, String> messageBuffer; //buffer per i messaggi da inviare


    //---HANDLER USER SPECIFIED---

    /**
     * Handler dell'evento di ricezione di un messaggio (da implementare)
     *
     * @param message Contenuto del messaggio ricevuto
     * @param id ID del messaggio ricevuto
     */
    public abstract void onReceive(JData message, long id); //handler onReceive

    /**
     * Handler dell'evento di ricezione di una conferma ad un messaggio inviato (da implementare)
     *
     * @param id ID del messaggio confermato
     */
    public abstract void onReceiveAck(long id); //handler onReceiveAck

    /**
     * Metodo che deve restituire un ID univoco per i messaggi da inviare
     *
     * @return Ritorna un numero long univoco
     */
    public abstract long getMessageID(); //deve ritornare un id valido per il messaggio


    //---PUBLIC---

    /**
     * Costruttore della classe
     *
     * @param mmJTM Mezzo di trasmissione (oggetto di una classe che implementa JTransmissionMethod)
     * @param timerSendMessage Timer in millisecondi tra un intervallo di invio e un'altro
     * @param timerPolling Timer in millisecondi di attesa tra polling successivi del mezzo di trasmissione
     */
    //costruttore con tutti i parametri (mmJTM, timerResendMessage, timerPolling)
    public Jack(JTransmissionMethod mmJTM, long timerSendMessage, long timerPolling) {

        //salvo mmJTM
        this.mmJTM = mmJTM;

        //salvo i valori dei timer
        this.timerSendMessage = timerSendMessage;

        //creo il contenitore dei messaggi
        this.messageBuffer = new HashMap<Long, String>();

        //creo il thread di polling
        this.pollingThread = new PollingThread(timerPolling);

    }

    /**
     * Costruttore della classe
     *
     * @param mmJTM Mezzo di trasmissione (oggetto di una classe che implementa JTransmissionMethod)
     */
    //costruttore con solo mmJTM
    public Jack(JTransmissionMethod mmJTM) {
        this(mmJTM, JK_TIMER_RESEND_MESSAGE, JK_TIMER_POLLING);
    }

    /**
     * Metodo che avvia il polling del mezzo di comunicazione
     */
    //funzione di avvio
    public void start() {
        Logger.i("Jack", "Start Jack protocol");

        //avvio il thread del polling
        this.pollingThread.startThread();

    }

    /**
     * Metodo che ferma il polling del mezzo di comunicazione
     */
    //funzione di stop
    public void stop() {
        Logger.i("Jack", "Stop Jack protocol");

        //stoppo il thread del polling
        this.pollingThread.stopThread();

    }

    /**
     * Metodo che svuota il buffer dei messaggi da inviare
     */
    //funzione per svuotare il buffer di invio
    public void flushBufferSend() {

        //creo un nuovo contenitore dei dati (Garbage collector elimina il vecchio)
        this.messageBuffer = new HashMap<Long, String>();

    }

    /**
     * Metodo che inserisce nel buffer dei messaggi da inviare il messaggio passato come argomento
     *
     * @param message Messaggio da inviare
     * @return ID del messaggio inviato
     * @throws JackException
     */
    //funzione per inviare un messaggio
    public long send(JData message) throws JackException {

        try {

            //prelevo la root da messaggio JData
            JSONObject root = message.getRoot();

            //ottengo l'id del messaggio dalla funzione specificata dell'utente
            long id = getMessageID();

            //aggiungo id e la tipologia del messaggio
            root.put(JK_MESSAGE_ID, id);//id del messaggio da confermare
            root.put(JK_MESSAGE_TYPE, JK_MESSAGE_TYPE_DATA); //il messaggio è un ACK

            //inserisco il messaggio nel buffer di invio
            messageBuffer.put(id, root.toString());

            //ritorno l'id del messaggio inserito nel buffer
            return id;


        } catch (JSONException e) {
            Logger.e("Jack", "onSendAck", e, Logger.SEVERE);

            //lancio l'eccezione
            throw new JackException();
        }
    }


    //---PRIVATE---

    //funzione che elabora il messaggio
    private void execute(String messageJSON) {

        try {

            //parso il messaggio
            JSONObject root = new JSONObject(messageJSON);

            //verifico se possiede la chiave per il tipo di messaggio
            if (!root.has(JK_MESSAGE_TYPE)) {
                return; //non ha la chiave quindi il messaggio non è valido
            }

            //prelevo la stringa del tipo di messaggio
            String type = root.getString(JK_MESSAGE_TYPE);

            //se è un messaggio dati
            if (type.equals(JK_MESSAGE_TYPE_DATA)) {

                //verifico se possiede la chiave per l'id del messaggio
                if (!root.has(JK_MESSAGE_ID)) {
                    return; //non ha la chiave quindi il messaggio non è valido
                }

                //ottengo l'id del messaggio
                long id = root.getLong(JK_MESSAGE_ID);


                //costruisco il messaggio JData
                JData message = new JData(root);

                //confermo il messaggio
                sendAck(id);

                //chiamo la funzione di gestione definita dall'utente
                onReceive(message, id);

            //se è un messaggio di ACK
            } else if (type.equals(JK_MESSAGE_TYPE_ACK)) {

                //verifico se possiede la chiave per l'id del messaggio
                if (!root.has(JK_MESSAGE_ID)) {
                    return; //non ha la chiave quindi il messaggio non è valido
                }

                //ottengo l'id del messaggio
                long id = root.getLong(JK_MESSAGE_ID);

                //chiamo la funzione di gestione degli ack
                checkAck(id);

            }

        } catch (JSONException e) {

            //JSON non valido
            return; //messaggio non valido

        }

    }

    //funzione che invia il messaggio ACK
    private void sendAck(long id) {

        try {
            //creo la root del messaggio JSON
            JSONObject root = new JSONObject();

            //inserisco i dati
            root.put(JK_MESSAGE_ID, id);//id del messaggio da confermare
            root.put(JK_MESSAGE_TYPE, JK_MESSAGE_TYPE_ACK); //il messaggio è un ACK

            //invio il messaggio
            this.mmJTM.send(root.toString());

        } catch (JSONException e) {
            Logger.e("Jack", "onSendAck", e, Logger.SEVERE);
        }

    }

    //funzione che controlla un messaggio ACK
    private void checkAck(long id) {

        //se l'id corrisponde ad un messaggio nel buffer
        if (this.messageBuffer.containsKey(id)) {

            //elimino il messaggio
            this.messageBuffer.remove(id);

            //richiamo l'handler dell'utente
            onReceiveAck(id);
        }

    }

    //thread per controllare periodicamente il mezzo di trasmissione
    private class PollingThread extends CyclicalThread {

        private long timeLastSend;


        public PollingThread(long timer) {
            super(timer);

            //inizializzo le varibili
            timeLastSend = 0; //0 significa non ancora inviato
        }

        @Override
        public void execute() {

            //controllo se ci sono messaggi da ricevere
            if (Jack.this.mmJTM.available()) {

                String message = Jack.this.mmJTM.receive();

                if (message != null) {
                    Jack.this.execute(message);
                }

            }

            //controllo se posso inviare i messaggi e se ce ne sono da inviare
            if (System.currentTimeMillis() - timeLastSend >= Jack.this.timerSendMessage && Jack.this.messageBuffer.size() > 0) {

                //salvo l'ultimo invio dei messaggi
                timeLastSend = System.currentTimeMillis();

                //creo iteratore del contenitore dei messaggi
                Iterator<Long> iter = Jack.this.messageBuffer.keySet().iterator();

                //finchè ci sono messaggi
                while(iter.hasNext()) {

                    //prelevo la chiave
                    long key = iter.next();

                    //invio il messaggio
                    Jack.this.mmJTM.send("" + Jack.this.messageBuffer.get(key));
                }

            }

        }
    }

}
