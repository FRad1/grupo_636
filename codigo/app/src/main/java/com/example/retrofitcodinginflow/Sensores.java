package com.example.retrofitcodinginflow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
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

    public String currentDate;

    private SensorManager mSensorManager;
    private TextView luminosidad;
    private TextView acelerometro;
    public TextView elders_scrolls;

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

    //fin shared




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensores);




        TextView elders_scrolls = (TextView)findViewById(R.id.scroll_text_view);

        elders_scrolls.setText("");

        //shared pref

        loadData();
        updateViews();

        //fin shared pref

        ScrollView the_scroll = (ScrollView)findViewById(R.id.scrollView2);

        //para el scroll down al cuando abro la app

        the_scroll.post(new Runnable(){
            @Override
            public void run() {
                the_scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        Intent intent = getIntent();


        var_valor_lux = intent.getFloatExtra(Configuracion.EXTRA_valor_lux_config, 10);


        String token;

        if(intent.hasExtra(MainActivity.EXTRA_TEXT)){
            token = intent.getStringExtra(MainActivity.EXTRA_TEXT);

        }else{
            token = intent.getStringExtra(Configuracion.EXTRA_TEXT3);

        }

        TextView textView1 = (TextView)findViewById(R.id.text_sensores_token);

        textView1.setText(token);



        luminosidad   = (TextView) findViewById(R.id.text_luz);
        acelerometro  = (TextView) findViewById(R.id.text_acel);

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
            Log.d("sensor", event.sensor.getName());

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
                elders_scrolls.append(currentDate +"\nLuz Apagada\n");
                the_scroll.fullScroll(View.FOCUS_DOWN);
                luz_encendida = false;
                saveData();
            }
        }
        if(pantalla_arriba && !luz_encendida){
            if(sensado_luz_encendida){
                elders_scrolls.append(currentDate + "\nLuz Encendida\n");
                the_scroll.fullScroll(View.FOCUS_DOWN);
                luz_encendida = true;
                saveData();
            }
        }
        if(pantalla_arriba){
            if(!sensado_pantalla_arriba){
                elders_scrolls.append(currentDate + "\nPantalla Abajo\n");
                the_scroll.fullScroll(View.FOCUS_DOWN);
                pantalla_arriba = false;
                saveData();
            }
        }
        if(!pantalla_arriba){
            if(sensado_pantalla_arriba){
                elders_scrolls.append(currentDate + "\nPantalla Arriba\n");
                the_scroll.fullScroll(View.FOCUS_DOWN);
                pantalla_arriba = true;
                saveData();
            }
        }

    }

    public void saveData(){

        elders_scrolls = (TextView)findViewById(R.id.scroll_text_view);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT,elders_scrolls.getText().toString());

        editor.apply();

        Log.w("myApp", "DATA SAVED");
    }

    public void loadData(){

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        text_sha = sharedPreferences.getString(TEXT, "");


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
}