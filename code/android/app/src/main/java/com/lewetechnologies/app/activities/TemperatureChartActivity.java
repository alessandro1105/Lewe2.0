package com.lewetechnologies.app.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.lewetechnologies.app.R;
import com.lewetechnologies.app.configs.Config;
import com.lewetechnologies.app.database.Database;
import com.lewetechnologies.app.database.DatabaseResult;
import com.lewetechnologies.app.fragments.GSRMainFragment;
import com.lewetechnologies.app.fragments.TemperatureMainFragment;
import com.lewetechnologies.app.logger.Logger;
import com.lewetechnologies.app.services.BluetoothSerialService;
import com.lewetechnologies.app.services.DatabaseService;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Classe dell'activity contente il grafico con tutte le letture ricevute dal sensore di temperatura
 *
 * @author Alessandro Pasqualini - alessandro.pasqualini.1105@gmail.com
 * @version 1.00
 */
public class TemperatureChartActivity extends AppCompatActivity {

    private final static String TAG = TemperatureChartActivity.class.getSimpleName();


    //---COSTANTI---

    //intent action
    private static final String ACTION_DATABASE_RESULT = "com.lewetechnologies.app.activities.TemperatureChartActivity.ACTION_DATABASE_RESULT";

    //chart
    private LineChart chart;

    //new data receiver
    private BroadcastReceiver newDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (BluetoothSerialService.ACTION_NEW_DATA.equals(intent.getAction())) {

                //prelevo i dati dall'intent
                double temperature = intent.getDoubleExtra(Config.EXTRA_DATA_TEMPERATURE, 0);
                long gsr = intent.getLongExtra(Config.EXTRA_DATA_GSR, 0);
                long timestamp = intent.getLongExtra(Config.EXTRA_DATA_TIMESTAMP, 0);

                //aggiorno i dati
                update(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(timestamp * 1000 - 7200000)).toString(), (float) temperature);

            }
        }
    };

    //database data Receiver
    private BroadcastReceiver databaseResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (ACTION_DATABASE_RESULT.equals(intent.getAction())) {

                Logger.e(TAG, "ACTION_DATABASE_RESULT_TEMPERATURE");

                //prelevo i risultati dal db
                DatabaseResult result = (DatabaseResult) intent.getSerializableExtra(DatabaseService.EXTRA_DATABASE_RESULT);

                //scorro tutti i risultati
                for (int i = 0; i < result.size(); i++) {

                    //prelevo i dati
                    long timestamp = (long) result.getRecordField(i, Database.CULUMN_NAME_TIMESTAMP);
                    double temperature = Double.parseDouble((String) result.getRecordField(i, Database.CULUMN_NAME_SENSOR_VALUE));

                    update(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(timestamp * 1000 - 7200000)).toString(), (float) temperature);

                }

                //onetimereceiver
                unregisterReceiver(this);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_chart);

        //recupero il riferimento al grafico
        chart = (LineChart) findViewById(R.id.chart);

        //inizializzo il grafico
        initializeChart();

        //inserisco un oggetto data vuoto con dataset nel grafico
        LineData dataChart = new LineData();
        dataChart.addDataSet(createDataSet());
        chart.setData(dataChart);

        //invalido i valori del grafico
        chart.invalidate();

        //registro i receiver
        registerReceiver(newDataReceiver, new IntentFilter(BluetoothSerialService.ACTION_NEW_DATA));
        registerReceiver(databaseResultReceiver, new IntentFilter(ACTION_DATABASE_RESULT));

        //invio l'intent per ricevere i dati
        //eseguo gli intent per ricevere i dati dal DB
        String query = "SELECT * FROM " + Database.TABLE_NAME +
                " WHERE " + Database.CULUMN_NAME_SENSOR_NAME + " = '" + Config.DATABASE_KEY_TEMPERATURE + "'" +
                " ORDER BY " + Database.CULUMN_NAME_TIMESTAMP + " ASC";

        final Intent intent = new Intent(DatabaseService.COMMAND_EXECUTE_QUERY);
        intent.putExtra(DatabaseService.EXTRA_QUERY, query);
        intent.putExtra(DatabaseService.EXTRA_DESTINATION_ACTION, ACTION_DATABASE_RESULT);

        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //unregistro i receiver
        unregisterReceiver(newDataReceiver);
    }

    private void initializeChart() {
        //description
        chart.setDescription("");
        chart.setNoDataText("No data available");
        chart.setNoDataTextDescription("Connect your LeweBand");

        //background
        chart.setBackgroundColor(Color.TRANSPARENT); //background color

        //legends
        chart.getLegend().setEnabled(false); //disabilita la leggenda

        //bordi
        chart.setDrawBorders(false); //elimina i bordi

        //animazione
        //chart.animateXY(1500, 1500); //animazione di riempimento

        //chart offset
        chart.setExtraLeftOffset(20f);
        chart.setExtraRightOffset(33f);
        chart.setExtraBottomOffset(20f);
        chart.setExtraTopOffset(28f);

        //settings asse x
        chart.getXAxis().setDrawGridLines(false); //disabilita la grid dell'asse x
        chart.getXAxis().setDrawLabels(true); //abilita le label dell'asse x
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); //posizione asse x
        chart.getXAxis().setTextColor(Color.parseColor("#666666"));
        chart.getXAxis().setAxisLineWidth(2f);

        //settings asse y
        chart.getAxisLeft().setDrawGridLines(true);//abilito la griglia
        chart.getAxisLeft().setGridColor(Color.parseColor("#BBBBBB")); //colore della griglia
        chart.getAxisRight().setEnabled(false); //disabilita l'asse di destra (right y)
        chart.setScaleYEnabled(false); //disabilita lo zoom dell'asse y
        chart.getAxisLeft().setTextColor(Color.parseColor("#666666")); //colore delle label
        chart.getAxisLeft().setAxisLineWidth(2f);
        chart.getAxisLeft().setLabelCount(6, true);

        //formatter
        chart.getAxisLeft().setValueFormatter(new TemperatureYAxisValueFormatter());

        //maker view
        chart.setMarkerView(new TemperatureTooltipMarkerView(this, R.layout.chart_tooltip));
    }

    private LineDataSet createDataSet() {

        //creo il dataset
        LineDataSet set = new LineDataSet(null, "Temperature");

        //line settings
        set.setLineWidth(2f); //line width

        //color
        set.setColor(Color.parseColor("#4EBCF4")); //colore linea
        set.setCircleColor(Color.parseColor("#3F51B5")); //colore cerchietti
        set.setDrawCircleHole(false); //cerchietti senza buco

        set.setDrawValues(false); //disabilito la visualizzazione dei valori

        set.setCircleRadius(5f); //dimensione del cerchietto
        set.setDrawCircleHole(false); //niente buco


        //dataset settings
        set.setDrawHighlightIndicators(false); //elimina le linee di highlight
        set.setDrawFilled(false); //elimina la colorazione dell'area sottostante al grafico

        //ritorno il dataset
        return set;
    }


    private static class TemperatureTooltipMarkerView extends MarkerView {

        private TextView tvContent;

        public TemperatureTooltipMarkerView (Context context, int layoutResource) {
            super(context, layoutResource);
            // this markerview only displays a textview
            tvContent = (TextView) findViewById(R.id.tvContent);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            //text formatter
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator('.');

            //creo il testo per il valore grande
            String value = new DecimalFormat("##0.0", symbols).format(e.getVal()) + "°C";

            tvContent.setText(value); // set the entry-value as the display text
        }

        @Override
        public int getXOffset(float xpos) {
            // this will center the marker-view horizontally
            return -(getWidth() / 2);
        }

        @Override
        public int getYOffset(float ypos) {
            // this will cause the marker-view to be above the selected value
            return -getHeight();
        }
    }

    //chart YAxis formatter
    private static class TemperatureYAxisValueFormatter implements YAxisValueFormatter {

        public TemperatureYAxisValueFormatter () {
            //costruttore vuoto
        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {

            //text formatter
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator('.');

            //creo il testo per il valore grande
            return new DecimalFormat("##0.0", symbols).format(value) + "°C";
        }
    }

    /**
     * Metodo usato per aggiornare i dati contenuti nel grafico
     *
     * @param xKey Chiave del nuovo dato
     * @param yVal Valore del nuovo dato
     */
    //update dati grafico
    public void update(String xKey, double yVal) {

        //ottengo l'oggetto data dal grafico
        LineData dataChart = chart.getData();

        //aggiungo xKey
        dataChart.addXValue(xKey);

        //aggiungo l'entry del nuovo dato
        dataChart.addEntry(new Entry((float) yVal, dataChart.getDataSetByIndex(0).getEntryCount()), 0);

        //notifico che il data set è cambiato
        chart.notifyDataSetChanged();

        //invalido il grafico per il refresh
        chart.invalidate();

    }
}
