package com.lewetechnologies.app.utils;

import com.lewetechnologies.app.logger.Logger;

/**
 * Classe astratta usata per realizzare thread che eseguono continuamente il codice allo scadere di un timer
 *
 * @author Alessandro Pasqualini - alessandro.pasqualini.1105@gmail.com
 * @version 1.00
 */

//classe che gestisce un thread ciclico
public abstract class CyclicalThread extends Thread {

    private long timerPolling; //indica ogni quanto eseguire il metodo execute
    private boolean threadEnabled; //variabile che se true fa terminare naturalmente il thread

    /**
     * Costruttore della classe
     *
     * @param timer indica il valore del timer espresso in millisecondi
     */
    //costruttore
    public CyclicalThread(long timer) {
        super();

        //imposto il timer di polling
        timerPolling = timer;

        //indico che il thread non è abilitato
        threadEnabled = false;
    }

    /**
     * Funzione che avvia il thread ciclico
     */
    //stop thread
    public void startThread() { //avvia il thread

        //dico che il thread è abilitato
        threadEnabled = true;

        //faccio iniziare il thread
        super.start();
    }

    /**
     * Funzione che ferma il thread ciclico (il thread viene messo in pausa)
     */
    public void stopThread() { //stoppa il thread

        //fermo il thread
        threadEnabled = false;
    }

    /**
     * Implementazione del metodo run della superclasse Thread, garantendo la ciclicità
     */
    //metodo run
    public final void run() {

        new Runnable() {

            @Override
            public void run() {
                while (threadEnabled) {

                    //richiamo il metodo user specified
                    execute();

                    //provo a mettere in pausa il thread
                    try {
                        Thread.sleep(timerPolling); //metto in  pausa il thread

                    } catch (InterruptedException e) {
                        Logger.e("CyclicalThread", "InterruptedException", e, Logger.SEVERE);
                    }
                }

            }

        }.run(); //avvio il runnable

    }

    /**
     * Metodo da implementare con il codice dell'utente da ripetere ciclicamente
     */
    //metodo che contiene il corpo del thread da eseguire ripetutamente
    abstract public void execute();

}
