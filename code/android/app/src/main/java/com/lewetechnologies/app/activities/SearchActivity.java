package com.lewetechnologies.app.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lewetechnologies.app.R;
import com.lewetechnologies.app.configs.Config;
import com.lewetechnologies.app.services.BluetoothSerialService;

/**
 * Classe dell'activity che effettua la ricerca del dispositivo bluetooth
 *
 * @author Alessandro Pasqualini - alessandro.pasqualini.1105@gmail.com
 * @version 1.00
 */
public class SearchActivity extends AppCompatActivity {
    private final static String TAG = SearchActivity.class.getSimpleName();

    //---COSTANTI---
    private static final long SCAN_PERIOD = 10000;


    //---VARIABILI---
    private Handler scanHandler;

    //bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter;

    //ble scanner
    private BluetoothLeScanner mBluetoothLeScanner;

    //indica se è stato registrato il receiver
    private boolean onBTDeviceFoundReceiverRegistered;

    //shared preferences
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //creo shared preferences
        preferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);

        //inserisco la vista
        setContentView(R.layout.activity_search);

        //imposto la toolbar personalizzata
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //se è già stata fatta la prima associazione
        if (preferences.getBoolean(Config.SHARED_PREFERENCE_KEY_FIRST_ASSOCIATION, false)) {
            //imposto il tasto indietro
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //inizializzo le variabili
        onBTDeviceFoundReceiverRegistered = false; //non ancora registrato il receiver
        scanHandler = new Handler(); //inizializzo l'handler

        //creao bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //se il bt non è abilitato chiedo l'attivazione
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Config.REQUEST_ENABLE_BT);

        //il bluetooth è abilitato avvio la ricerca
        } else {
            startDiscovery();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //se il ricevitore è stato registrato lo disregistro
        if (onBTDeviceFoundReceiverRegistered) {
            stopDiscovery(); //fermo la ricerca
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //se la richiesta è quella di abilitare il bluetooth
        if (requestCode == Config.REQUEST_ENABLE_BT) {

            //se il bt è stato abilitato
            if (mBluetoothAdapter.isEnabled()) {
                startDiscovery();

            //il bt non è stato abilitato
            } else {
                setupErrorView(); //blocco la progress bar e metto l'immagine di errore

                //mostro toast con l'errore
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.activity_search_bt_not_enabled), Toast.LENGTH_LONG).show();
            }
        }
    }

    //metodo che avvia la ricerca tramite BT
    private void startDiscovery() {

        //prelevo lo scanner BLE
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        setupStartView();

        scanHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopDiscovery();

                //se non è stato trovato il band
                setupRetryView();
            }
        }, SCAN_PERIOD);

        //inizio la ricerca
        mBluetoothLeScanner.startScan(new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                //super.onScanResult(callbackType, result);

                //prelevo il device trovato
                BluetoothDevice device = result.getDevice();

                //chiamo la funzione di gestione
                onBTDeviceFound(device);
            }
        });

    }

    //metodo che avvia la ricerca tramite BT
    private void stopDiscovery() {

        //stoppo la ricerca
        mBluetoothLeScanner.stopScan(new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                //super.onScanResult(callbackType, result);

                //prelevo il device trovato
                BluetoothDevice device = result.getDevice();

                //chiamo la funzione di gestione
                onBTDeviceFound(device);
            }
        });

    }

    //metodo chiamato quando viene trovato un dispositivo BT
    private void onBTDeviceFound(BluetoothDevice device) {

        //se il nome del device trovato è quello di un leweband e non è lo stesso già
        if (Config.LEWEBAND_BT_NAME.equals(device.getName())
                && !preferences.getString(Config.SHARED_PREFERENCE_KEY_DEVICE_MAC, "").equals(device.getAddress())) {

            //leweband trovato

            //fermo al progress bar e metto l'icona di successo
            setupSuccessView();

            //stoppo la ricerca
            stopDiscovery();

            //salvo nelle preferenze il device trovato
            SharedPreferences.Editor editor = preferences.edit();

            //salvo i valori nelle preferenze
            editor.putString(Config.SHARED_PREFERENCE_KEY_DEVICE_MAC, device.getAddress()); //salvo il mac del device
            editor.putBoolean(Config.SHARED_PREFERENCE_KEY_FIRST_ASSOCIATION, true); //indico che è stato effettuata la prima associazione
            editor.putString(Config.SHARED_PREFERENCE_KEY_BAND_NAME_ASSOCIATED, device.getName()); //indico che il band è stato associato

            //salvo i cambiamenti
            editor.commit();

            //connetto il bluetooth service al device
            final Intent intent = new Intent(BluetoothSerialService.COMMAND_CONNECT);
            intent.putExtra(BluetoothSerialService.EXTRA_DEVICE_ADDRESS, device.getAddress());

            //invio l'intent di connessione
            sendBroadcast(intent);

        }
    }


    //---GESTIONE GRAFICA---
    private void setupErrorView() {
        //progressbar
        ProgressBar progressBatRotating = (ProgressBar) findViewById(R.id.progress_rotating);
        progressBatRotating.setVisibility(View.GONE);

        ProgressBar progressBarNotRotating = (ProgressBar) findViewById(R.id.progress_not_rotating);
        progressBarNotRotating.setIndeterminateDrawable(null); //elimino il drawable indeterminate
        progressBarNotRotating.setVisibility(View.VISIBLE);

        //change image icon
        ImageView image = (ImageView) findViewById(R.id.icon);
        image.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_error));

        //button
        TextView button = (TextView) findViewById(R.id.button);
        button.setText(getResources().getString(R.string.activity_search_button_text_exit));
        button.setPaintFlags(button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        button.setVisibility(View.VISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupSuccessView() {
        //progressbar
        ProgressBar progressBatRotating = (ProgressBar) findViewById(R.id.progress_rotating);
        progressBatRotating.setVisibility(View.GONE);

        ProgressBar progressBarNotRotating = (ProgressBar) findViewById(R.id.progress_not_rotating);
        progressBarNotRotating.setIndeterminateDrawable(null); //elimino il drawable indeterminate
        progressBarNotRotating.setVisibility(View.VISIBLE);

        //change image icon
        ImageView image = (ImageView) findViewById(R.id.icon);
        image.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_success));

        //button
        TextView button = (TextView) findViewById(R.id.button);
        button.setText(getResources().getString(R.string.activity_search_button_text_continue));
        button.setPaintFlags(button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        button.setVisibility(View.VISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupRetryView() {
        //progressbar
        ProgressBar progressBatRotating = (ProgressBar) findViewById(R.id.progress_rotating);
        progressBatRotating.setVisibility(View.GONE);

        ProgressBar progressBarNotRotating = (ProgressBar) findViewById(R.id.progress_not_rotating);
        progressBarNotRotating.setIndeterminateDrawable(null); //elimino il drawable indeterminate
        progressBarNotRotating.setVisibility(View.VISIBLE);

        //change image icon
        ImageView image = (ImageView) findViewById(R.id.icon);
        image.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_error));

        //button
        TextView button = (TextView) findViewById(R.id.button);
        button.setText(getResources().getString(R.string.activity_search_button_text_retry));
        button.setPaintFlags(button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        button.setVisibility(View.VISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscovery();
            }
        });
    }

    private void setupStartView() {
        //progressbar
        ProgressBar progressBatRotating = (ProgressBar) findViewById(R.id.progress_rotating);
        progressBatRotating.setVisibility(View.VISIBLE);

        ProgressBar progressBarNotRotating = (ProgressBar) findViewById(R.id.progress_not_rotating);
        progressBarNotRotating.setIndeterminateDrawable(null); //elimino il drawable indeterminate
        progressBarNotRotating.setVisibility(View.GONE);

        //change image icon
        ImageView image = (ImageView) findViewById(R.id.icon);
        image.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_search));

        //button
        TextView button = (TextView) findViewById(R.id.button);
        button.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {

        //se è già stata fatta la prima associazione
        if (!preferences.getBoolean(Config.SHARED_PREFERENCE_KEY_FIRST_ASSOCIATION, false)) {

            //richiedo la chiusura dell'app
            Intent exitIntent = new Intent();
            setResult(Config.RESULT_EXIT_CODE, exitIntent);
            finish();

        } else {
            finish();
        }
    }
}
