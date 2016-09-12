package com.lewetechnologies.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.lewetechnologies.app.R;
import com.lewetechnologies.app.activities.GSRChartActivity;

import java.text.DecimalFormat;

/**
 * Classe del main fragment per la visualizzazione dei dati del sensore GSR
 *
 * @author Alessandro Pasqualini - alessandro.pasqualini.1105@gmail.com
 * @version 1.00
 */
//---FRAGMENT CLASS GSR---
public class GSRMainFragment extends Fragment {

    //numero massimo di elementi nel grafico
    private static final int MAX_CHART_ELEMENT = 5;

    //rootView
    private View rootView;

    //componenti del fragment da modificare dinamicamente
    private TextView data; //dato in "grande"
    private TextView timestamp; //timestamp ricezione dato in "grande"

    private LineChart chart; //grafico


    /**
     * Costruttore vuoto del fragment
     */
    //costruttore
    public GSRMainFragment() {
        //richiede un costruttore vuoto
    }

    /**
     * Factory Method per il fragment
     *
     * @return Ritorna un'istanza del fragment
     */
    //factory method per la costruzuione del fragment
    public static GSRMainFragment newInstance() {

        //istanzio il fragment
        GSRMainFragment fragment = new GSRMainFragment();

        //ritorno il fragment
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //creo la vista
        rootView = inflater.inflate(R.layout.fragment_main_gsr, container, false); //TEMPERATURE

        //accedo ai dati da cambiare
        data = (TextView) rootView.findViewById(R.id.data); //dato in "grande"
        timestamp = (TextView) rootView.findViewById(R.id.timestamp); //timestamp dato in "grande"
        chart = (LineChart) rootView.findViewById(R.id.chart); //grafico


        //cambio il font del valore mostrato
        data.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/DINCond-Bold.ttf"));

        //onclick chart
        chart.setTouchEnabled(true); //abilito i gesti touch

        //listener
        chart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                //avvio l'activity GSRChart
                Intent activity = new Intent(GSRMainFragment.this.getActivity(), GSRChartActivity.class);
                startActivity(activity);
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });

        //inizializzo il grafico
        initializeChart();

        //inserisco un oggetto data vuoto con dataset nel grafico
        LineData dataChart = new LineData();
        dataChart.addDataSet(createDataSet());
        chart.setData(dataChart);

        //invalido i valori del grafico
        chart.invalidate();


        //ritorno la vista
        return rootView;
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
        chart.setExtraRightOffset(45f);
        chart.setExtraBottomOffset(20f);
        chart.setExtraTopOffset(28f);

        //settings asse x
        chart.getXAxis().setDrawGridLines(false); //disabilita la grid dell'asse x
        chart.getXAxis().setDrawLabels(true); //abilita le label dell'asse x
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); //posizione asse x
        chart.getXAxis().setTextColor(Color.parseColor("#666666"));
        chart.getXAxis().setAxisLineWidth(2f);
        chart.setScaleXEnabled(false); //disabilita lo zoom dell'asse x

        //settings asse y
        chart.getAxisLeft().setDrawGridLines(true);//abilito la griglia
        chart.getAxisLeft().setGridColor(Color.parseColor("#BBBBBB")); //colore della griglia
        chart.getAxisRight().setEnabled(false); //disabilita l'asse di destra (right y)
        chart.setScaleYEnabled(false); //disabilita lo zoom dell'asse y
        chart.getAxisLeft().setTextColor(Color.parseColor("#666666")); //colore delle label
        chart.getAxisLeft().setAxisLineWidth(2f);

        //formatter
        chart.getAxisLeft().setValueFormatter(new GSRYAxisValueFormatter());

        //maker view
        chart.setMarkerView(new GSRTooltipMarkerView(getContext(), R.layout.chart_tooltip));
    }

    private LineDataSet createDataSet() {

        //creo il dataset
        LineDataSet set = new LineDataSet(null, "GSR");

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


    //chart YAxis formatter
    private static class GSRYAxisValueFormatter implements YAxisValueFormatter {

        public GSRYAxisValueFormatter () {
            //costruttore vuoto
        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            // write your logic here
            // access the YAxis object to get more information
            return new DecimalFormat("##").format(value) + "%"; // e.g. append a dollar-sign
        }
    }

    //chart tooltip formatter
    private static class GSRTooltipMarkerView extends MarkerView {

        private TextView tvContent;

        public GSRTooltipMarkerView (Context context, int layoutResource) {
            super(context, layoutResource);
            // this markerview only displays a textview
            tvContent = (TextView) findViewById(R.id.tvContent);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {

            //creo il testo per il valore grande
            String value = new DecimalFormat("##").format(e.getVal()) + "%";

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

    /**
     * Metodo usato per aggiornare i dati contenuti nel fragment
     *
     * @param xKey Chiave del nuovo dato
     * @param yVal Valore del nuovo dato
     * @param updateData Indica se il dato inserito è l'ultimo
     */
    public void update(String xKey, double yVal, boolean updateData) {

        //se è richiesto l'update del dato "grande"
        if (updateData) {

            //creo il testo per il valore grande
            String dataText = new DecimalFormat("##").format(yVal) + "%";

            //aggiorno i valori
            data.setText(dataText); //data
            timestamp.setText(xKey); //date (timestamp)
        }


        //ottengo l'oggetto data dal grafico
        LineData dataChart = chart.getData();

        //aggiungo xKey
        dataChart.addXValue(xKey);

        //aggiungo l'entry del nuovo dato
        dataChart.addEntry(new Entry((float) yVal, dataChart.getDataSetByIndex(0).getEntryCount()), 0);

        if (dataChart.getDataSetByIndex(0).getEntryCount() > MAX_CHART_ELEMENT) {
            //rimuovo l'entry più vecchia
            dataChart.getDataSetByIndex(0).removeFirst();

            //rimuovo la xKey più vecchia
            dataChart.removeXValue(0);

            //modifico gli indici
            for (int i = 0; i < dataChart.getDataSetByIndex(0).getEntryCount(); i++) {
                Entry entry = dataChart.getDataSetByIndex(0).getEntryForXIndex(i+1);
                entry.setXIndex(i);
            }

        }

        //notifico che il data set è cambiato
        chart.notifyDataSetChanged();

        //invalido il grafico per il refresh
        chart.invalidate();

        //indico di mostrare solo gli indici da 0 a MAX_CHART_ELEMENT-1, ovvero MAX_CHART_ELEMENT elementi
        chart.setVisibleXRangeMaximum(MAX_CHART_ELEMENT-1);


    }

}