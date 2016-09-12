package com.lewetechnologies.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.lewetechnologies.app.R;
import com.lewetechnologies.app.configs.Config;

/**
 * Classe del fragment contenente la pagina principale delle impostazioni
 *
 * @author Alessandro Pasqualini - alessandro.pasqualini.1105@gmail.com
 * @version 1.00
 */
public class SettingsPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings_main);


        //on click listener su "Exit" (richiede di chiudere l'app)
        Preference exitPreference = (Preference) findPreference("exit"); //search by key
        exitPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                //chiudo l'app
                sendExitRequest();

                return true;
            }
        });

    }

    private void sendExitRequest() {
        //ritorno la richiesta di chiusura dell'app
        Intent exitIntent = new Intent();
        getActivity().setResult(Config.RESULT_EXIT_CODE, exitIntent);
        getActivity().finish();
    }
}
