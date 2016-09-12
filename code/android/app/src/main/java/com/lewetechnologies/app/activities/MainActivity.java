package com.lewetechnologies.app.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lewetechnologies.app.R;
import com.lewetechnologies.app.configs.Config;
import com.lewetechnologies.app.database.Database;
import com.lewetechnologies.app.database.DatabaseResult;
import com.lewetechnologies.app.fragments.GSRMainFragment;
import com.lewetechnologies.app.fragments.TemperatureMainFragment;
import com.lewetechnologies.app.logger.Logger;
import com.lewetechnologies.app.services.BluetoothSerialService;
import com.lewetechnologies.app.services.DatabaseService;
import com.lewetechnologies.app.services.MainService;

import android.os.Handler;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe dell'activity principale dell'applicazione
 *
 * @author Alessandro Pasqualini - alessandro.pasqualini.1105@gmail.com
 * @version 1.00
 */
public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();


    //---COSTANTI---

    //intent action
    private static final String ACTION_DATABASE_RESULT_TEMPERATURE = "com.lewetechnologies.app.activities.MainActivity.ACTION_DATABASE_RESULT_TEMPERATURE";
    private static final String ACTION_DATABASE_RESULT_GSR = "com.lewetechnologies.app.activities.MainActivity.ACTION_DATABASE_RESULT_GSR";


    //---VARIABILI---

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    //shared preferences
    private SharedPreferences preferences;

    //new data receiver
    private BroadcastReceiver newDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (BluetoothSerialService.ACTION_NEW_DATA.equals(intent.getAction())) {

                //prelevo i dati dall'intent
                double temperature = intent.getDoubleExtra(Config.EXTRA_DATA_TEMPERATURE, 0);
                long gsr = intent.getLongExtra(Config.EXTRA_DATA_GSR, 0);
                long timestamp = intent.getLongExtra(Config.EXTRA_DATA_TIMESTAMP, 0);

                Logger.e(TAG, "TIMESTAMP NEW DATA" + timestamp);

                //aggiorno i dati
                ((TemperatureMainFragment) mSectionsPagerAdapter.getItem(0)).update(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(timestamp * 1000 - 7200000)).toString(), (float) temperature, true);
                ((GSRMainFragment) mSectionsPagerAdapter.getItem(1)).update(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(timestamp * 1000 - 7200000)).toString(), (float) gsr, true);
            }
        }
    };

    //database data Receiver
    private BroadcastReceiver databaseResultTemperatureReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (ACTION_DATABASE_RESULT_TEMPERATURE.equals(intent.getAction())) {

                Logger.e(TAG, "ACTION_DATABASE_RESULT_TEMPERATURE");

                //prelevo i risultati dal db
                DatabaseResult result = (DatabaseResult) intent.getSerializableExtra(DatabaseService.EXTRA_DATABASE_RESULT);

                //scorro tutti i risultati
                for (int i = result.size() -1; i >= 0; i--) {

                    //prelevo i dati
                    long timestamp = (long) result.getRecordField(i, Database.CULUMN_NAME_TIMESTAMP);
                    double temperature = Double.parseDouble((String) result.getRecordField(i, Database.CULUMN_NAME_SENSOR_VALUE));

                    if (i == 0) {
                        ((TemperatureMainFragment) mSectionsPagerAdapter.getItem(0)).update(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(timestamp * 1000 - 7200000)).toString(), (float) temperature, true);

                    } else {
                        ((TemperatureMainFragment) mSectionsPagerAdapter.getItem(0)).update(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(timestamp * 1000 - 7200000)).toString(), (float) temperature, false);
                    }

                }

                //onetimereceiver
                unregisterReceiver(this);
            }

        }
    };

    //database data Receiver
    private BroadcastReceiver databaseResultGSRReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (ACTION_DATABASE_RESULT_GSR.equals(intent.getAction())) {

                Logger.e(TAG, "ACTION_DATABASE_RESULT_GSR");

                //prelevo i risultati dal db
                DatabaseResult result = (DatabaseResult) intent.getSerializableExtra(DatabaseService.EXTRA_DATABASE_RESULT);

                //scorro tutti i risultati
                for (int i = result.size() -1; i >= 0; i--) {

                    //prelevo i dati
                    long timestamp = (long) result.getRecordField(i, Database.CULUMN_NAME_TIMESTAMP);
                    long gsr = Long.parseLong((String) result.getRecordField(i, Database.CULUMN_NAME_SENSOR_VALUE));

                    if (i == 0) {
                        ((GSRMainFragment) mSectionsPagerAdapter.getItem(1)).update(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(timestamp * 1000 - 7200000)).toString(), (float) gsr, true);

                    } else {
                        ((GSRMainFragment) mSectionsPagerAdapter.getItem(1)).update(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(timestamp * 1000 - 7200000)).toString(), (float) gsr, false);
                    }

                }

                //onetimereceiver
                unregisterReceiver(this);
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                //cambio l'elemento selezionato nella bottombar se cambia la pagina
                @Override
                public void onPageSelected(int position) {
                    if (position == 0) { //selezionata temperatura

                        MainActivity.this.setTemperatureSelected();

                    } else if (position == 1) { //selezionata pagina gsr
                        MainActivity.this.setGSRSelected();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            }

        );


        //on click listener exitButton
        ((ImageButton) findViewById(R.id.exitButton)).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //chiudo i servizi
                stopServices();

                //chiudo l'app
                finish();
            }

        });

        //inizializzo la bottom bar
        setTemperatureSelected();

        //onclick bottom bar
        //temperature
        ((RelativeLayout) findViewById(R.id.bottombar_action_temperature)).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //selezione temperatura nella bottom bar
                setTemperatureSelected();

                //visualizzo il fragment scelto
                mViewPager.setCurrentItem(0, true);
            }

        });

        //gsr
        ((RelativeLayout) findViewById(R.id.bottombar_action_gsr)).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //selezione GSR nella bottom bar
                setGSRSelected();

                //visualizzo il fragment scelto
                mViewPager.setCurrentItem(1, true);
            }

        });
        //settings
        ((RelativeLayout) findViewById(R.id.bottombar_action_settings)).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //avvio l'activity SettingsActivity
                Intent settingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(settingsActivity, Config.REQUEST_EXIT_CODE);
            }

        });


        //registro i receiver
        registerReceiver(newDataReceiver, new IntentFilter(BluetoothSerialService.ACTION_NEW_DATA));
        registerReceiver(databaseResultTemperatureReceiver, new IntentFilter(ACTION_DATABASE_RESULT_TEMPERATURE));
        registerReceiver(databaseResultGSRReceiver, new IntentFilter(ACTION_DATABASE_RESULT_GSR));

        //interrogo il databse
        sendQuery();


        //controllo se è stata fatta la prima associazione dalle preferenze
        //se non è stata fatta la prima associazione avvio la search activity
        if (!preferences.getBoolean(Config.SHARED_PREFERENCE_KEY_FIRST_ASSOCIATION, false)) {
            //avvio l'activity SettingsActivity
            Intent activity = new Intent(this, SearchActivity.class);
            startActivityForResult(activity, Config.REQUEST_EXIT_CODE);

        } else {

            //verifico se il bluetooth è abilitato
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {

                //invio intent richiesta apertura BT
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, Config.REQUEST_ENABLE_BT);

            //il bluetooth è abilitato avvio la ricerca
            } else {

                //invio il comando di connessione
                sendConnectionCommand();

            }

        }

        //SNIPPER PER INSERIMENTO DATI NEI FRAGMENT
        //((TemperatureMainFragment) mSectionsPagerAdapter.getItem(0)).update("22/05/2016 15:07", (float) (Math.random() * 10) + 50f, true);
        //((GSRMainFragment) mSectionsPagerAdapter.getItem(1)).update("22/05/2016 15:07", (float) (Math.random() * 10) + 50f, true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //disregistro i receiver
        unregisterReceiver(newDataReceiver);

        //chiudo l'applicazione
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            //avvio l'activity SettingsActivity
            Intent activity = new Intent(this, SettingsActivity.class);
            startActivityForResult(activity, Config.REQUEST_EXIT_CODE);

            return true;

        } else if (id == R.id.action_credits) {

            //avvio l'activity CreditsActivity
            Intent activity = new Intent(this, CreditsActivity.class);
            startActivity(activity);

            return true;

        } else if (id == R.id.action_about) {

            //avvio l'activity AboutActivity
            Intent activity = new Intent(this, AboutActivity.class);
            startActivity(activity);

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //controllo richiesta di chiusura app
        if (requestCode == Config.REQUEST_EXIT_CODE && resultCode == Config.RESULT_EXIT_CODE) {
            //chiudo i servizi
            stopServices();

            //chiudo l'app
            finish();

        //controllo se è stato abilitato il BT
        } else if (requestCode == Config.REQUEST_ENABLE_BT) {

            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {

                Logger.e(TAG, "connessione");
                sendConnectionCommand();
            }
        }
    }

    //Handler bottom bar
    private void setTemperatureSelected() {
        //setto la nuova icona e il colore del testo della bottombar dell'elemento selezionato
        ((ImageView) findViewById(R.id.bottombar_icon_temperature)).setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_temperature_selected));
        ((TextView) findViewById(R.id.bottombar_text_temperature)).setTextColor(ContextCompat.getColor(getApplication(), R.color.activity_main_bottombar_element_description_text_color_selected));

        //resetto la bottombar
        //setto la nuova icona e il colore del testo della bottombar dell'elemento selezionato
        ((ImageView) findViewById(R.id.bottombar_icon_gsr)).setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_gsr));
        ((TextView) findViewById(R.id.bottombar_text_gsr)).setTextColor(ContextCompat.getColor(getApplication(), R.color.activity_main_bottombar_element_description_text_color));
    }

    private void setGSRSelected() {
        //setto la nuova icona e il colore del testo della bottombar dell'elemento selezionato
        ((ImageView) findViewById(R.id.bottombar_icon_gsr)).setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_gsr_selected));
        ((TextView) findViewById(R.id.bottombar_text_gsr)).setTextColor(ContextCompat.getColor(getApplication(), R.color.activity_main_bottombar_element_description_text_color_selected));

        //resetto la bottombar
        //setto la nuova icona e il colore del testo della bottombar dell'elemento selezionato
        ((ImageView) findViewById(R.id.bottombar_icon_temperature)).setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_temperature));
        ((TextView) findViewById(R.id.bottombar_text_temperature)).setTextColor(ContextCompat.getColor(getApplication(), R.color.activity_main_bottombar_element_description_text_color));

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        //costanti per accedere ai fragment
        public static final int TEMPERATURE_MAIN_FRAGMENT_POSITION = 0;
        public static final int GSR_MAIN_FRAGMENT_POSITION = 1;

        //numero di fragment
        private static final int FRAGMENT_NUMBER = 2;

        //dichiaro i fragment
        private TemperatureMainFragment temperatureFragment;
        private GSRMainFragment gsrFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

            //costruisco i fragment
            temperatureFragment = TemperatureMainFragment.newInstance();
            gsrFragment = GSRMainFragment.newInstance();

        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.

            switch (position) {

                //Temperature fragment
                case TEMPERATURE_MAIN_FRAGMENT_POSITION:
                    return temperatureFragment; //ritorno il fragment della temperatura

                //GSR fragment
                case GSR_MAIN_FRAGMENT_POSITION:
                    return gsrFragment; //ritorno il fragment del GSR
            }

            //ritorno il fragment della temperatura nel caso di errore
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return FRAGMENT_NUMBER; //due fragment
        }

    }


    //---GESTIONE DEI SERVIZI
    //chiude i servizi
    private void stopServices() {
        //chiudo il servizio principale
        Intent intent = new Intent(this, MainService.class);
        stopService(intent);
    }

    //invio comando di connessione
    private void sendConnectionCommand() {

        //prelevo il mac address del device
        final String address = preferences.getString(Config.SHARED_PREFERENCE_KEY_DEVICE_MAC, "");

        //se l'app è associata as un band
        if (!address.equals("")) {

            //connetto il bluetooth service al device
            final Intent intent = new Intent(BluetoothSerialService.COMMAND_CONNECT);
            intent.putExtra(BluetoothSerialService.EXTRA_DEVICE_ADDRESS, address);

            //invio il comando di connessione
            sendBroadcast(intent);

        }

    }

    //invio le query
    private void sendQuery() {
        //eseguo gli intent per ricevere i dati dal DB
        String query = "SELECT * FROM " + Database.TABLE_NAME +
                " WHERE " + Database.CULUMN_NAME_SENSOR_NAME + " = '" + Config.DATABASE_KEY_TEMPERATURE + "'" +
                " ORDER BY " + Database.CULUMN_NAME_TIMESTAMP + " DESC" +
                " LIMIT " + TemperatureMainFragment.MAX_CHART_ELEMENT;

        final Intent querySelectTemperature = new Intent(DatabaseService.COMMAND_EXECUTE_QUERY);
        querySelectTemperature.putExtra(DatabaseService.EXTRA_QUERY, query);
        querySelectTemperature.putExtra(DatabaseService.EXTRA_DESTINATION_ACTION, ACTION_DATABASE_RESULT_TEMPERATURE);

        //eseguo gli intent per ricevere i dati dal DB
        query = "SELECT * FROM " + Database.TABLE_NAME +
                " WHERE " + Database.CULUMN_NAME_SENSOR_NAME + " = '" + Config.DATABASE_KEY_GSR + "'" +
                " ORDER BY " + Database.CULUMN_NAME_TIMESTAMP + " DESC" +
                " LIMIT " + TemperatureMainFragment.MAX_CHART_ELEMENT;

        final Intent querySelectGSR = new Intent(DatabaseService.COMMAND_EXECUTE_QUERY);
        querySelectGSR.putExtra(DatabaseService.EXTRA_QUERY, query);
        querySelectGSR.putExtra(DatabaseService.EXTRA_DESTINATION_ACTION, ACTION_DATABASE_RESULT_GSR);

        //invio le query al db subito
        sendBroadcast(querySelectTemperature);
        sendBroadcast(querySelectGSR);
    }
}
