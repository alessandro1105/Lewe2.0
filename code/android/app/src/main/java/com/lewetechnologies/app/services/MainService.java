package com.lewetechnologies.app.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.lewetechnologies.app.R;
import com.lewetechnologies.app.activities.MainActivity;

/**
 * Servizio principale. Ha il compito di avviare e gestire i servizi secondari (DatabaseService e BluetoothService)
 *
 * @author Alessandro Pasqualini - alessandro.pasqualini.1105@gmail.com
 * @version 1.00
 */
public class MainService extends Service {

    //---COSTANTI---
    private static final int ONGOING_NOTIFICATION_ID = 1105;

    //riceve gli stati di connessione
    private BroadcastReceiver connectionStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //aggiorno la preferenza "status"
            if (BluetoothSerialService.ACTION_CONNECTION_STATUS.equals(intent.getAction())) {
                setNotification(intent.getIntExtra(BluetoothSerialService.EXTRA_CONNECTION_STATUS, BluetoothSerialService.STATE_DISCONNECTED));

            } else if (BluetoothSerialService.ACTION_CONNECTED.equals(intent.getAction())) {
                setNotification(BluetoothSerialService.STATE_CONNECTED);

            } else if (BluetoothSerialService.ACTION_DISCONNECTED.equals(intent.getAction())) {
                setNotification(BluetoothSerialService.STATE_DISCONNECTED);
            }
        }
    };


    //---FUNZIONI DI GESTIONE PER I SERVIZI---
    //chiude i servizi
    private void stopServices() {
        //chiudo il servizio di bluetooth
        Intent bluetoothSerialService = new Intent(this, BluetoothSerialService.class);
        stopService(bluetoothSerialService);

        //chiudo il servizio di database
        Intent databaseService = new Intent(this, DatabaseService.class);
        stopService(databaseService);
    }

    //avvia i servizi
    private void startServices() {

        //avvio il servizio bluetooth
        Intent bluetoothSerialService = new Intent(this, BluetoothSerialService.class);
        startService(bluetoothSerialService);

        //avvio il servizio di database
        Intent databaseService = new Intent(this, DatabaseService.class);
        startService(databaseService);
    }


    //---NOTIFICATION FUNCTION---
    private void setNotification(int state) {

        Notification.Builder builder = new Notification.Builder(this); //creo builder

        builder.setSmallIcon(R.drawable.notification_icon);

        //builder.setSmallIcon(R.mipmap.ic_launcher); //imposto icona
        builder.setContentTitle(getString(R.string.service_main_notification_title_text)); //titolo notifica
        builder.setPriority(Notification.PRIORITY_LOW);


        //impostazione descrizione
        switch (state) {

            case BluetoothSerialService.STATE_CONNECTED:
                builder.setContentText(getString(R.string.service_main_notification_description_band_connected_text)); //sottotitolo
                break;

            case BluetoothSerialService.STATE_DISCONNECTED:
                builder.setContentText(getString(R.string.service_main_notification_description_band_disconnected_text)); //sottotitolo
                break;

        }


        //intent apertura main
        Intent intent = new Intent(this, MainActivity.class); //creo intent apertura main
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0); //pending intent

        builder.setContentIntent(pendingIntent);  //imposto l'intent

        //avvio il servizio come foregroud
        startForeground(ONGOING_NOTIFICATION_ID, builder.build());

    }


    //---FUNZIONI PER IL SERVIZIO---
    @Override
    public void onCreate() {
        super.onCreate();

        //imposto la notifica e il servizio come foreground
        setNotification(BluetoothSerialService.STATE_DISCONNECTED);

        //avvio i servizi
        startServices();

        //registro il receiver
        IntentFilter connectionStatusReceiverFilter = new IntentFilter();
        connectionStatusReceiverFilter.addAction(BluetoothSerialService.ACTION_CONNECTION_STATUS);
        connectionStatusReceiverFilter.addAction(BluetoothSerialService.ACTION_CONNECTED);
        connectionStatusReceiverFilter.addAction(BluetoothSerialService.ACTION_DISCONNECTED);

        registerReceiver(connectionStatusReceiver, connectionStatusReceiverFilter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //chiudo i servizi
        stopServices();

        unregisterReceiver(connectionStatusReceiver);
    }

    //---FUNZIONI PER IL BINDER---
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
