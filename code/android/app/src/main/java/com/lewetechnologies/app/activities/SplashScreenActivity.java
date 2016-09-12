package com.lewetechnologies.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.lewetechnologies.app.R;
import com.lewetechnologies.app.services.MainService;

/**
 * Classe dell'activity contente lo splash screen dell'applicazione
 *
 * @author Alessandro Pasqualini - alessandro.pasqualini.1105@gmail.com
 * @version 1.00
 */
public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplication(), R.color.activity_splashscreen_background_color));

        //avvio i servizi
        startServices();

        //thread per far partire la main activity dopo un 2 secondi
        Thread splashTimer = new Thread(){
            public void run(){
                try{
                    sleep(1500);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent mainActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(mainActivity);
                }
            }
        };
        splashTimer.start();

        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    //avvio dei servizi
    //avvia i servizi
    private void startServices() {

        //avvio il servizio principale
        Intent intent = new Intent(this, MainService.class);
        startService(intent);
    }
}
