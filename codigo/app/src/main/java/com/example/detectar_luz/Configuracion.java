package com.example.detectar_luz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Configuracion extends AppCompatActivity {

    public static final String EXTRA_TEXT3 = "Configuracion.EXTRA_TEXT3";
    public static final String EXTRA_valor_lux_config = "Configuracion.EXTRA_valor_lux_config";

    private Button boton_guardar;

    private EditText e_lux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        Intent intent = getIntent();
        String token = intent.getStringExtra(Sensores.EXTRA_TEXT2);
        TextView textView1 = (TextView)findViewById(R.id.t_token);
        textView1.setText(token);

        float valor_lux  = intent.getFloatExtra(Sensores.EXTRA_valor_lux,10);


        TextView t_input_lux = (TextView)findViewById(R.id.i_numero_lux);
        t_input_lux.setText(String.valueOf(valor_lux));



        boton_guardar = (Button) findViewById(R.id.b_Guardar);
        boton_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                e_lux = (EditText)findViewById(R.id.i_numero_lux);

                float valor_lux_new = Float.parseFloat(e_lux.getText().toString());
                openSensores(token,valor_lux_new);
            }
        });
    }



    public void openSensores(String text,float valor_lux){

        Intent intent = new Intent(this, Sensores.class);
        intent.putExtra(EXTRA_TEXT3,text);
        intent.putExtra(EXTRA_valor_lux_config,valor_lux);
        startActivity(intent);
    }
}