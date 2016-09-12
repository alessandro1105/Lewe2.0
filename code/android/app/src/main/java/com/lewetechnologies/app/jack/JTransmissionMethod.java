package com.lewetechnologies.app.jack;

/**
 * Interfaccia contenete i metodi di gestione del mezzo di comunicazione per la classe Jack
 *
 * @author Alessandro Pasqualini - alessandro.pasqualini.1105@gmail.com
 * @version 1.00
 */
public interface JTransmissionMethod {

    /**
     * Ritorna il primo messaggio disponibile nel mezzo di comunicazione
     *
     * @return Il messaggio prelevato
     */
    public String receive(); //deve restituire il messaggio da passare a Jack

    /**
     * Invia il messaggio nel mezzo di comunicazione
     *
     * @param message Il messaggio da inviare
     */
    public void send(String message); //invia il messaggio

    /**
     * Indica se nel mezzo di comunicazione ci sono messaggi in attesa
     *
     * @return Ritorna un valore boleano indicante la presenza o meno di messaggi in attesa
     */
    public boolean available(); //restituisce true se ci sono dati da ricevere nel buffer
}