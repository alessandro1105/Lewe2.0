package com.lewetechnologies.app.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.lewetechnologies.app.configs.Config;
import com.lewetechnologies.app.database.Database;
import com.lewetechnologies.app.jack.JData;
import com.lewetechnologies.app.jack.JTransmissionMethod;
import com.lewetechnologies.app.jack.Jack;
import com.lewetechnologies.app.logger.Logger;

import java.util.UUID;

/**
 * Servizio il cui compito è quello di gestire la connessione bluetooth seriale
 * Implementa l'interfaccia JTransmissionMethod del protocollo di comunicazione Jack
 *
 *
 * @author Alessandro Pasqualini - alessandro.pasqualini.1105@gmail.com
 * @version 1.00
 */
public class BluetoothSerialService extends Service implements JTransmissionMethod {
    private final static String TAG = BluetoothSerialService.class.getSimpleName();

    //---COSTANTI--

    //AZIONI PER INTENT IN ENTRATA (COMANDI)
    /**
     * Codice di request nell'intent per richiedere la connessione al dispositivo bluetooth
     */
    public final static String COMMAND_CONNECT = "com.lewetechnologies.app.services.BluetoothSerialService.COMMAND_CONNECT"; //connetti
    /**
     * Codice di request nell'intent per richiedere la disconnessione dal dispositivo bluetooth
     */
    public final static String COMMAND_DISCONNECT = "com.lewetechnologies.app.services.BluetoothSerialService.COMMAND_DISCONNECT"; //disconnetti
    /**
     * Codice di request nell'intent per richiedere lo stato della connessione bluetooth
     */
    public final static String COMMAND_CONNECTION_STATUS = "com.lewetechnologies.app.services.BluetoothSerialService.COMMAND_CONNECTION_STATUS"; //status


    //extra per intent in entrata
    /**
     * Chiave usata negli extra degli intent per indicare il MAC Address del dispositivo bluetooth
     */
    public final static String EXTRA_DEVICE_ADDRESS = "com.lewetechnologies.app.services.BluetoothSerialService.EXTRA_DEVICE_ADDRESS"; //devic// e bt address

    //AZIONI PER INTENT IN USCITA
    /**
     * Azione degli intent inviati in broadcast dal servizio indicante che è stata stabilita la connessione al dispositivo bluetooth
     */
    public final static String ACTION_CONNECTED = "com.lewetechnologies.app.services.BluetoothSerialService.ACTION_BAND_CONNECTED"; //band connesso
    /**
     * Azione degli intent inviati in broadcast dal servizio indicante che è il dispositivo bluetooth è stato disconnesso
     */
    public final static String ACTION_DISCONNECTED = "com.lewetechnologies.app.services.BluetoothSerialService.ACTION_BAND_DISCONNECTED"; //band disconnesso
    /**
     * Azione degli intent inviati in broadcast dal servizio indicante la presenza di nuovi dati ricevuti dal dispositivo bluetooth
     */
    public final static String ACTION_NEW_DATA = "com.lewetechnologies.app.services.BluetoothSerialService.ACTION_NEW_DATA"; //nuovi dati
    /**
     * Azione degli intent inviati in broadcast dal servizio indicante la presenza di nuovi dati ricevuti dal dispositivo bluetooth
     */
    public final static String ACTION_CONNECTION_STATUS = "com.lewetechnologies.app.services.BluetoothSerialService.ACTION_CONNECTION_STATUS"; //band connesso

    //extra per intent in uscita
    /**
     * Chiave usata negli extra degli intent per indicare lo stato della connessione bluetooth
     */
    public final static  String EXTRA_CONNECTION_STATUS = "com.lewetechnologies.app.services.BluetoothSerialService.EXTRA_CONNECTION_STATUS"; //devic// e bt address

    //uuid gatt
    private static final String SERIAL_SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb"; //servizio seriale
    private static final String SERIAL_CHARACTERISTC_TX = "0000ffe1-0000-1000-8000-00805f9b34fb"; //caratteristica di TX
    private static final String SERIAL_CHARACTERISTC_RX = "0000ffe1-0000-1000-8000-00805f9b34fb"; //caratteristica di RX

    //connection states
    /**
     * Valore restituito per indicare che il dispositivo bluetooth è disconnesso
     */
    public static final int STATE_DISCONNECTED = 0; //disconnesso
    /**
     * Valore restituito per indicare che il dispositivo bluetooth è in connessione
     */
    public static final int STATE_CONNECTING = 1; //in attesa di connessione
    /**
     * Valore restituito per indicare che il dispositivo bluetooth è connesso
     */
    public static final int STATE_CONNECTED = 2; //connesso

    //caratteri per JTM
    private static final String JTM_START_SEQUENCE = "<";
    private static final String JTM_FINISH_SEQUENCE = ">";

    //comandi
    private static final long WAIT_PERIOD = 500;

    //jack
    Jack jack;


    //---VARIABILI---
    private String buffer; //message buffer

    private BluetoothManager mBluetoothManager; //bluetooth manager
    private BluetoothAdapter mBluetoothAdapter; //bluetooth adapter
    private String mBluetoothDeviceAddress; //indirizzo device bluetooth
    private BluetoothGatt mBluetoothGatt; //blueooth GATT
    private int mConnectionState = STATE_DISCONNECTED; //stato del servizio (DISCONNECTED, CONNECTING, CONNECTED)
    private BluetoothGattCharacteristic characteristicTX; //caratteristica TX

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {

                mConnectionState = STATE_CONNECTED; //cambio lo stato della connessione
                broadcastUpdate(ACTION_CONNECTED); //annuncio la connessione
                mBluetoothGatt.discoverServices(); //start discovery services

                Logger.i(TAG, "Device connected.");

                //avvio jack
                jack.start();
                Logger.i(TAG, "JACK start.");

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED; //cambio lo stato della connessione
                broadcastUpdate(ACTION_DISCONNECTED); //annuncio la connessione

                //stoppo JACK
                jack.stop();
                Logger.i(TAG, "JACK stop.");

                //verifo dalle preferenze se è possibile riconnettere (se non è cambiato il mac address)
                SharedPreferences preferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);

                //se il mac non è cambiato riconnetti, altrimenti non eseguo la riconnessione
                if (preferences.getString(Config.SHARED_PREFERENCE_KEY_BAND_NAME_ASSOCIATED, "").equals(mBluetoothDeviceAddress)) {
                    //provo a riconnettere
                    connect(mBluetoothDeviceAddress);
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                //scorro tutti i servizi trovati
                for (BluetoothGattService gattService : gatt.getServices()) {

                    String uuid = gattService.getUuid().toString();

                    if (uuid.equals(SERIAL_SERVICE_UUID)) {
                        // get characteristic when UUID matches RX/TX UUID
                        characteristicTX = gattService.getCharacteristic(UUID.fromString(SERIAL_CHARACTERISTC_TX));

                        //abilita le notifiche sulla caratteristica di RX
                        mBluetoothGatt.setCharacteristicNotification(gattService.getCharacteristic(UUID.fromString(SERIAL_CHARACTERISTC_TX)), true);

                        //è stato trovato il servizio seriale
                        break;

                    }
                }

            } else {
                Logger.e(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS && characteristic.getUuid().toString().equals(SERIAL_CHARACTERISTC_RX)) {
                //leggo i dati
                final byte[] data = characteristic.getValue();

                //se ci sono dati
                if (data != null && data.length > 0) {
                    //aggiungo la stringa ricevuta al buffer
                    buffer += new String(data);
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (SERIAL_CHARACTERISTC_RX.equals(characteristic.getUuid().toString())) {
                //leggo i dati
                final byte[] data = characteristic.getValue();

                //se ci sono dati
                if (data != null && data.length > 0) {
                    //aggiungo la stringa ricevuta al buffer
                    buffer += new String(data);
                }

                Logger.e(TAG, "Buffer: " + buffer);
            }
        }
    };

    //broadcast receiver per i command
    BroadcastReceiver commandReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (COMMAND_CONNECT.equals(intent.getAction()) && BluetoothAdapter.getDefaultAdapter().isEnabled()) {

                //prelevo l'indirizzo del device bt
                String address = intent.getStringExtra(EXTRA_DEVICE_ADDRESS);

                //connessione
                connect(address);

            } else if (COMMAND_DISCONNECT.equals(intent.getAction()) && BluetoothAdapter.getDefaultAdapter().isEnabled()) {

                //disconnessione
                disconnect();

            } else if (COMMAND_CONNECTION_STATUS.equals(intent.getAction())) {
                broadcastUpdate(ACTION_CONNECTION_STATUS);
            }

        }
    };

    //invia un intent broadcast solo con action
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);

        if (ACTION_CONNECTION_STATUS.equals(action)) {

            //se mi sto connettendo il device è sconnesso
            if (mConnectionState == STATE_CONNECTING) {
                intent.putExtra(EXTRA_CONNECTION_STATUS, STATE_DISCONNECTED);

            } else {
                intent.putExtra(EXTRA_CONNECTION_STATUS, mConnectionState);
            }

        }

        sendBroadcast(intent);
    }


    //connect al device bt
    private boolean connect(final String address) {
        //se è stato inizializzato
        if (mBluetoothAdapter == null || address == null) {
            Logger.e(TAG, "BluetoothAdapter not initialized or unspecified address.", Logger.SEVERE);
            return false;
        }

        //connessione ad un devivice precedentemente connesso
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            Logger.i(TAG, "Trying to use an existing mBluetoothGatt for connection.");

            //connetto GATT
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING; //imposto lo stato in "connessione in corso"
                return true;
            } else {
                return false;
            }
        }

        //connessione ad un nuovo device
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Logger.e(TAG, "Device not found.  Unable to connect.", Logger.SEVERE);
            return false;
        }

        // We want to directly connect to the device, so we are setting the autoConnect parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);

        Logger.d(TAG, "Trying to create a new connection.");

        mBluetoothDeviceAddress = address; //salvo l'indirizzo
        mConnectionState = STATE_CONNECTING; //imposto che lo stato in "connessione in corso"
        return true;
    }

    //disconnette dal device bt
    private void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Logger.e(TAG, "BluetoothAdapter not initialized or unspecified address.", Logger.SEVERE);
            return;
        }

        //disconnetto GATT
        mBluetoothGatt.disconnect();
    }

    //chiude il servizio
    private void close() {

        //se non è mai stato instanziato gatt
        if (mBluetoothGatt == null) {
            return;
        }

        //chiude GATT
        mBluetoothGatt.close();
        mBluetoothGatt = null; //garbage collector

        //disregistro il commandReceiver
        unregisterReceiver(commandReceiver);
    }

    //inizializza il servizio

    /**
     * Metodo che inzializza il servizio. Da chiamarsi dopo la creazione del servizio stesso
     *
     * @return Ritorna un valore booleano indicante se l'inizializzazione del servizio è avvenuta correttamente
     */
    public boolean initialize() {

        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Logger.e(TAG, "Unable to initialize BluetoothManager.", Logger.SEVERE);
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Logger.e(TAG, "Unable to obtain a BluetoothAdapter.", Logger.SEVERE);
            return false;
        }

        //inizializzo il buffer
        buffer = "";

        //inizializzo Jack (valori di default per i tempi di polling)
        jack = new Jack(this) {

            //al ricevimento di un messaggio valido
            @Override
            public void onReceive(JData message, long id) {

                //inserisco il dato del DB

                //---TEMPERATURE---
                String query = "INSERT INTO " + Database.TABLE_NAME + " (" +
                        Database.CULUMN_NAME_SENSOR_NAME + ", " +
                        Database.CULUMN_NAME_SENSOR_VALUE + ", " +
                        Database.CULUMN_NAME_TIMESTAMP +
                        ") VALUES (" +
                        "'" + Config.DATABASE_KEY_TEMPERATURE + "', " +
                        "'" + message.getDouble(Config.JACK_TEMPERATURE) + "', " +
                        "" + message.getLong(Config.JACK_TIMESTAMP) +
                        ");";

                Intent insertTemperatureQuery = new Intent(DatabaseService.COMMAND_EXECUTE_QUERY);
                insertTemperatureQuery.putExtra(DatabaseService.EXTRA_QUERY, query);

                Logger.i(TAG, query);

                sendBroadcast(insertTemperatureQuery);

                //---GSR---
                query = "INSERT INTO " + Database.TABLE_NAME + " (" +
                        Database.CULUMN_NAME_SENSOR_NAME + ", " +
                        Database.CULUMN_NAME_SENSOR_VALUE + ", " +
                        Database.CULUMN_NAME_TIMESTAMP +
                        ") VALUES (" +
                        "'" + Config.DATABASE_KEY_GSR + "', " +
                        "'" + message.getLong(Config.JACK_GSR) + "', " +
                        "" + message.getLong(Config.JACK_TIMESTAMP) +
                        ");";

                Intent insertGSRQuery = new Intent(DatabaseService.COMMAND_EXECUTE_QUERY);
                insertGSRQuery.putExtra(DatabaseService.EXTRA_QUERY, query);

                Logger.i(TAG, query);

                sendBroadcast(insertGSRQuery);


                //---AVVISO DEL RICEVIMENTO DI NUOVI DATI---
                //preparo l'intent per avvisare l'app dei nuovi dati
                final Intent intent = new Intent(ACTION_NEW_DATA);

                intent.putExtra(Config.EXTRA_DATA_TEMPERATURE, message.getDouble(Config.JACK_TEMPERATURE));
                intent.putExtra(Config.EXTRA_DATA_GSR, message.getLong(Config.JACK_GSR));
                intent.putExtra(Config.EXTRA_DATA_TIMESTAMP, message.getLong(Config.JACK_TIMESTAMP));


                Logger.e(TAG, "Timestamp: " + message.getLong(Config.JACK_TIMESTAMP));

                sendBroadcast(intent);

                Logger.e(TAG, "onReceive()");

            }

            //al ricevimento di un ACK
            @Override
            public void onReceiveAck(long id) {
                //non deve fare niente
            }

            @Override
            public long getMessageID() {
                //timestamp per avere id unici
                return System.currentTimeMillis();
            }
        };

        //creo intent filter per il command receiver
        IntentFilter commandReceiverFilter = new IntentFilter();
        commandReceiverFilter.addAction(COMMAND_CONNECT);
        commandReceiverFilter.addAction(COMMAND_DISCONNECT);
        commandReceiverFilter.addAction(COMMAND_CONNECTION_STATUS);

        //registro il commandReceiver
        registerReceiver(commandReceiver, commandReceiverFilter);

        return true;
    }

    //---FUNZIONE DI JTM--

    /**
     * Implementazione del metodo dell'interfaccia JTransmissionMethod.
     *
     * @return restituisce il primo messaggio disponibile nel buffer di ricezione dei messaggi
     */
    @Override
    public String receive() {

        Logger.e(TAG, "Buffer receive(): " + buffer);

        //se il buffer è vuoto
        if (buffer.equals("")) {
            return null; //non è stato prelevato nessun messaggio
        }

        //se nel buffer non è presente la sequenza di inizio
        if (!buffer.contains(JTM_START_SEQUENCE)) {
            buffer = ""; //azzero il buffer
            return null; //non è stato prelevato nessun messaggio
        }

        //elimino i caratteri errati prima della sequenza di inizio
        buffer = buffer.substring(buffer.indexOf(JTM_START_SEQUENCE) + JTM_FINISH_SEQUENCE.length());

        //se il buffer non contiene la sequenza finale
        if (!buffer.contains(JTM_FINISH_SEQUENCE)) {
            buffer = ""; //azzero il buffer
            return null; //non è stato prelevato nessun messaggio
        }

        //prelevo il messaggio
        String message = buffer.substring(0, buffer.indexOf(JTM_FINISH_SEQUENCE));

        //elimino il messaggio dal buffer
        buffer = buffer.substring(buffer.indexOf(JTM_FINISH_SEQUENCE) + JTM_FINISH_SEQUENCE.length());

        Logger.e(TAG, "Message received: " + message);

        //ritorno il messaggio prelevato
        return message;

    }

    /**
     * Implementazione del metodo dell'interfaccia JTransmissionMethod.
     *
     * @param message Messaggio da inviare al dispositivo bluetooth connesso
     */
    @Override
    public void send(String message) {

        Logger.e(TAG, "send(): " + message);

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Logger.e(TAG, "BluetoothAdapter not initialized", Logger.SEVERE);
            return;
        }

        if (mConnectionState == STATE_CONNECTED) {

            final String messageToSend = JTM_START_SEQUENCE + message + JTM_FINISH_SEQUENCE;

            //creo un thread che gestisca l'invio del messaggio
            new Thread(new Thread() {
                @Override
                public void run() {
                    //MAX 20 byte per messaggio
                    for (int i = 0; i < (int) (Math.ceil(messageToSend.length() / 20.0)); i++) {

                        //parte del messaggio
                        String part = messageToSend.substring(i*20, ((i+1)*20 < messageToSend.length()) ? ((i+1)*20) : messageToSend.length());

                        Logger.d(TAG, "Sending: " + part);

                        //converto in byte il messaggio
                        final byte[] tx = part.getBytes();

                        //scrivo la caratteristica TX
                        characteristicTX.setValue(tx);

                        //speed-up BLE transfert
                        characteristicTX.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

                        //attendo che il pezzo di messaggio precedentemente inviato sia stato ricevuto
                        try {
                            sleep(i*20);

                        } catch (InterruptedException e) {
                            return;
                        }

                        //scrivo la caratteristica
                        mBluetoothGatt.writeCharacteristic(characteristicTX);

                    }
                }

            }).start();

        }

    }

    /**
     * Implementazione del metodo dell'interfaccia JTransmissionMethod.
     *
     * @return Ritorna un valore booleano indicante se il buffer è vuoto o contiene almeno un carattere
     */
    @Override
    public boolean available() {
        return !buffer.equals("");
    }

    //---FUNZIONI PER IL SERIZIO---
    @Override
    public void onCreate() {
        super.onCreate();

        //inizializzo il servizio
        initialize();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //chiudo il servizio
        close();
    }

    //---FUNZIONI PER IL BINDER---
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
