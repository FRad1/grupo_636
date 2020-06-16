package com.example.detectar_luz;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;


public class Sensores extends AppCompatActivity implements SensorEventListener {

    public static final String EXTRA_TEXT2 = "Sensores.EXTRA_TEXT2";
    public static final String EXTRA_valor_lux = "Sensores.EXTRA_valor_lux";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    public static final String LUX = "10";

    public String currentDate;

    private SensorManager mSensorManager;
    private TextView luminosidad;
    private TextView acelerometro;
    public TextView elders_scrolls;
    public static TextView internet_textview;

    private Button boton_config;
    public Button boton_borrar_registro;

    public float var_valor_lux;

    private float valor_lux_actual;
    private float valor_acelerometro_actual;

    private boolean pantalla_arriba=true;
    private boolean luz_encendida=true;
    private boolean sensado_pantalla_arriba=true;
    private boolean sensado_luz_encendida=true;

    //para el shared pref

    private String text_sha;
    private Float lux_sha;

    //fin shared

    public String token;

    Receiver_Internet receiver_internet = new Receiver_Internet();
    Receiver_pantalla receiver_pantalla = new Receiver_pantalla();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //receiver_pantalla
        IntentFilter filter2 = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter2.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver_pantalla,filter2);


        setContentView(R.layout.activity_sensores);

        TextView elders_scrolls = (TextView)findViewById(R.id.scroll_text_view);

        elders_scrolls.setText("");

        //shared pref

        loadData();
        updateViews();

        //fin shared pref

        ScrollView the_scroll = (ScrollView)findViewById(R.id.scrollView2);

        //para el scroll down de los registros al cuando abro la app

        the_scroll.post(new Runnable(){
            @Override
            public void run() {
                the_scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        Intent intent = getIntent();


        var_valor_lux = intent.getFloatExtra(Configuracion.EXTRA_valor_lux_config, lux_sha);


        if(intent.hasExtra(MainActivity.EXTRA_TEXT)){
            token = intent.getStringExtra(MainActivity.EXTRA_TEXT);

        }else{
            token = intent.getStringExtra(Configuracion.EXTRA_TEXT3);

        }


        luminosidad   = (TextView) findViewById(R.id.text_luz);
        acelerometro  = (TextView) findViewById(R.id.text_acel);
        internet_textview  = (TextView) findViewById(R.id.internet_textView);
        internet_textview.setText("Internet: Conectado");
        internet_textview.setTextColor(Color.parseColor("#03a56a"));

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mSensorManager.registerListener((SensorEventListener) this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),   SensorManager.SENSOR_DELAY_NORMAL);


        //botones
        boton_config = (Button) findViewById(R.id.b_config);
        boton_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openConfiguracion(token,var_valor_lux);
            }
        });

        boton_borrar_registro = (Button) findViewById(R.id.b_borrar_shared);
        boton_borrar_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteShared();
            }
        });


    }

    public void openConfiguracion(String text,float flo){

        Intent intent = new Intent(this, Configuracion.class);
        intent.putExtra(EXTRA_TEXT2,text);
        intent.putExtra(EXTRA_valor_lux,flo);
        startActivity(intent);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateTimeInstance().format(calendar.getTime());

        elders_scrolls = (TextView)findViewById(R.id.scroll_text_view);
        ScrollView the_scroll = (ScrollView)findViewById(R.id.scrollView2);

        String txt = "";

        synchronized (this) {
            //Log.d("sensor", event.sensor.getName());

            switch (event.sensor.getType()) {


                case Sensor.TYPE_LIGHT:
                    txt += "Luminosidad\n";
                    txt += event.values[0] + " Lux \n";
                    txt += "Lux config: " + var_valor_lux + "\n";
                    if(event.values[0] > var_valor_lux){
                        txt += "Hay luz \n";
                        sensado_luz_encendida = true;
                    }else{
                        txt += "No hay luz \n";
                        sensado_luz_encendida = false;
                    }

                    luminosidad.setText(txt);

                    valor_lux_actual = event.values[0];

                    break;

                case Sensor.TYPE_ACCELEROMETER :
                    txt += "Acelerometro:\n";


                    txt += "z2: " + event.values[2] + " m/seg2 \n";

                    if(event.values[2] > 1){
                        txt += "pantalla hacia arriba \n";
                        sensado_pantalla_arriba = true;
                    }else{
                        txt += "pantalla hacia abajo \n";
                        sensado_pantalla_arriba = false;
                    }

                    acelerometro.setText(txt);

                    valor_acelerometro_actual = event.values[2];

                    break;
            }
        }

        if(pantalla_arriba && luz_encendida){
            if(!sensado_luz_encendida){
                //elders_scrolls.append(currentDate +"\nLuz Apagada\n");
                elders_scrolls.append("\n"+currentDate + " Luz Apagada");
                the_scroll.fullScroll(View.FOCUS_DOWN);
                luz_encendida = false;
                saveData();
                CrearEvento.createEvent(token,"DEV","Luz","INACTIVO","Luz Apagada: "+ currentDate);
                the_scroll.fullScroll(View.FOCUS_DOWN);
            }
        }
        if(pantalla_arriba && !luz_encendida){
            if(sensado_luz_encendida){
                elders_scrolls.append("\n"+currentDate + " Luz Encendida");
                the_scroll.fullScroll(View.FOCUS_DOWN);
                luz_encendida = true;
                saveData();
                CrearEvento.createEvent(token,"DEV","Luz","ACTIVO","Luz Encendida: "+ currentDate);
                the_scroll.fullScroll(View.FOCUS_DOWN);
            }
        }
        if(pantalla_arriba){
            if(!sensado_pantalla_arriba){
                //elders_scrolls.append(currentDate + "\nPantalla Abajo\n");
                elders_scrolls.append("\n"+currentDate + " Pantalla Abajo");
                the_scroll.fullScroll(View.FOCUS_DOWN);
                pantalla_arriba = false;
                saveData();
                CrearEvento.createEvent(token,"DEV","Pantalla","INACTIVO","Pantalla Abajo: "+ currentDate);
                the_scroll.fullScroll(View.FOCUS_DOWN);
            }
        }
        if(!pantalla_arriba){
            if(sensado_pantalla_arriba){
                //elders_scrolls.append(currentDate + "\nPantalla Arriba\n");
                elders_scrolls.append("\n"+currentDate + " Pantalla Arriba");
                the_scroll.fullScroll(View.FOCUS_DOWN);
                pantalla_arriba = true;
                saveData();
                CrearEvento.createEvent(token,"DEV","Pantalla","ACTIVO","Pantalla Arriba: "+ currentDate);
                the_scroll.fullScroll(View.FOCUS_DOWN);
            }
        }

    }

    public void saveData(){

        elders_scrolls = (TextView)findViewById(R.id.scroll_text_view);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT,elders_scrolls.getText().toString());
        editor.putFloat(LUX,var_valor_lux);

        editor.apply();

        Log.w("myApp", "DATA SAVED");
    }

    public void loadData(){

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        text_sha = sharedPreferences.getString(TEXT, "");
        lux_sha = sharedPreferences.getFloat(LUX, 10);


    }

    public void updateViews(){
        elders_scrolls = (TextView)findViewById(R.id.scroll_text_view);
        elders_scrolls.setText(text_sha);
        ScrollView the_scroll = (ScrollView)findViewById(R.id.scrollView2);
        the_scroll.fullScroll(View.FOCUS_DOWN);



    }

    public void deleteShared(){
        elders_scrolls = (TextView)findViewById(R.id.scroll_text_view);
        elders_scrolls.setText("");
        saveData();
        Toast.makeText(getApplicationContext(), "Registro borrado", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver_internet,filter);


        //IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        //registerReceiver(wifiStateReceiver,intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver_internet);
        unregisterReceiver(receiver_pantalla);

        mSensorManager.unregisterListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT));
        mSensorManager.unregisterListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        mSensorManager = null;
        saveData();

        //unregisterReceiver(receiver_pantalla);
        //unregisterReceiver(wifiStateReceiver);
    }

    /*@Override
    protected void onPause() {
        super.onPause();

        // when the screen is about to turn off
        if (Receiver_pantalla.pantallaEstabaEncendida) {
            // this is the case when onPause() is called by the system due to a screen state change
            Log.i("myApp", "pantalla apagada");
        } else {
            // this is when onPause() is called when the screen state has not changed
        }
    }*/

    /*@Override
    protected void onResume() {

        // only when screen turns on
        if (Receiver_pantalla.pantallaEstabaEncendida) {
            // this is when onResume() is called due to a screen state change
            Log.i("myApp", "pantalla encendida");
        } else {
            // this is when onResume() is called when the screen state has not changed
        }
        super.onResume();
    }*/

    /*@Override
    protected void onDestroy() {
        if (receiver_pantalla != null) {
            unregisterReceiver(receiver_pantalla);
            receiver_pantalla = null;
        }
        super.onDestroy();
    }*/

    /*private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);

            switch(wifiStateExtra){
                case WifiManager.WIFI_STATE_ENABLED:
                    Toast.makeText(getApplicationContext(), "Wifi habilitado", Toast.LENGTH_LONG).show();
                    break;

                case WifiManager.WIFI_STATE_DISABLED:
                    Toast.makeText(getApplicationContext(), "Wifi deshabilitado", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };*/
}