package com.lewetechnologies.app.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lewetechnologies.app.configs.Config;
import com.lewetechnologies.app.fragments.SettingsBandPreferenceFragment;
import com.lewetechnologies.app.logger.Logger;
import com.lewetechnologies.app.services.BluetoothSerialService;

/**
 * Classe dell'activity contente le impostazioni del bracciale
 *
 * @author Alessandro Pasqualini - alessandro.pasqualini.1105@gmail.com
 * @version 1.00
 */
public class BandSettingsActivity extends AppCompatActivity {

    //fragment
    private SettingsBandPreferenceFragment fragment;

    //riceve gli stati di connessione
    private BroadcastReceiver connectionStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //aggiorno la preferenza "status"
            if (BluetoothSerialService.ACTION_CONNECTION_STATUS.equals(intent.getAction())) {
                fragment.updateStatus(intent.getIntExtra(BluetoothSerialService.EXTRA_CONNECTION_STATUS, BluetoothSerialService.STATE_DISCONNECTED));

            } else if (BluetoothSerialService.ACTION_CONNECTED.equals(intent.getAction())) {
                fragment.updateStatus(BluetoothSerialService.STATE_CONNECTED);

            } else if (BluetoothSerialService.ACTION_DISCONNECTED.equals(intent.getAction())) {
                fragment.updateStatus(BluetoothSerialService.STATE_DISCONNECTED);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //creo il fragment
        fragment = SettingsBandPreferenceFragment.newInstance();

        //Creo il fragment passando lo stato della connessione con il band
        getFragmentManager().beginTransaction().replace(android.R.id.content,
            fragment)
            .commit();

        //intent per controllare lo stato della connessione
        Intent intent = new Intent(BluetoothSerialService.COMMAND_CONNECTION_STATUS);
        sendBroadcast(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectionStatusReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //registro il receiver
        IntentFilter connectionStatusReceiverFilter = new IntentFilter();
        connectionStatusReceiverFilter.addAction(BluetoothSerialService.ACTION_CONNECTION_STATUS);
        connectionStatusReceiverFilter.addAction(BluetoothSerialService.ACTION_CONNECTED);
        connectionStatusReceiverFilter.addAction(BluetoothSerialService.ACTION_DISCONNECTED);

        registerReceiver(connectionStatusReceiver, connectionStatusReceiverFilter);

        //eseguo la richiesta di status della connessione
        Intent intent = new Intent(BluetoothSerialService.COMMAND_CONNECTION_STATUS);
        sendBroadcast(intent);

    }

}
