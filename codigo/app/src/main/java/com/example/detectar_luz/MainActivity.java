package com.example.detectar_luz;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_TEXT = "main.EXTRA_TEXT";

    //private TextView textViewResult;
    private JasonPlaceHolderApi jasonPlaceHolderApi;
    public static TextView internet_textview;

    //los inputs y botones
    private EditText e_nombre;
    private EditText e_apellido;
    private EditText e_dni;
    private EditText e_email;
    private EditText e_password;
    private EditText e_comision;
    private EditText e_grupo;
    private Button b_login;
    private Button b_registro;
    private Switch s_guardar_datos;

    Receiver_Internet receiver_internet = new Receiver_Internet();
    //Receiver_pantalla receiver_pantalla = new Receiver_pantalla();

    //sharedpref
    public static final String SHARED_PREF_LOGIN ="sharedpref_login";
    public static final String SP_NOMBRE = "sp_nombre";
    public static final String SP_APELLIDO = "sp_apellido";
    public static final String SP_DNI = "sp_dni";
    public static final String SP_EMAIL = "sp_email";
    public static final String SP_PASSWORD = "sp_password";
    public static final String SP_COMISION = "sp_comision";
    public static final String SP_GRUPO = "sp_grupo";
    public static final String SWITCH1 = "switch1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        internet_textview = (TextView) findViewById(R.id.internet_textview_main);
        internet_textview.setText("Internet: Conectado");
        internet_textview.setTextColor(Color.parseColor("#03a56a"));
        //asigno los input y botones

        e_nombre = (EditText)findViewById(R.id.etName);
        e_apellido = (EditText)findViewById(R.id.etApellido);
        e_dni = (EditText)findViewById(R.id.etDNI);
        e_email = (EditText)findViewById(R.id.etEmail);
        e_password = (EditText)findViewById(R.id.etPassword);
        e_comision = (EditText)findViewById(R.id.etComision);
        e_grupo = (EditText)findViewById(R.id.etGrupo);
        b_login = (Button)findViewById(R.id.button_login);
        b_registro = (Button)findViewById(R.id.button_registrar);
        b_registro = (Button)findViewById(R.id.button_registrar);
        s_guardar_datos = (Switch) findViewById(R.id.switch_guardar_datos);


        //receiver_pantalla
        IntentFilter filter2 = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter2.addAction(Intent.ACTION_SCREEN_OFF);
        //registerReceiver(receiver_pantalla,filter2);

        //sharedpref
        CargarDatos();

        //listener de botones

        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(s_guardar_datos.isChecked()){
                    GuardarDatos();
                }else{
                    BorrarDatos();
                }

                if(!Receiver_Internet.hay_internet){
                    Toast.makeText(getApplicationContext(), "Sin conexion a Internet", Toast.LENGTH_LONG).show();
                }else{
                    createPost(1);
                }

            }
        });

        b_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(s_guardar_datos.isChecked()){
                    GuardarDatos();
                }else{
                    BorrarDatos();
                }

                if(!Receiver_Internet.hay_internet){
                    Toast.makeText(getApplicationContext(), "Sin conexion a Internet", Toast.LENGTH_LONG).show();
                }else {
                    createPost(2);
                }
            }
        });

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel((HttpLoggingInterceptor.Level.BODY));

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(5, TimeUnit.MINUTES) // connect timeout
                .writeTimeout(5, TimeUnit.MINUTES) // write timeout
                .readTimeout(5, TimeUnit.MINUTES) // read timeout
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://so-unlam.net.ar/api/api/") //siempre poner el / al final del url
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        jasonPlaceHolderApi = retrofit.create(JasonPlaceHolderApi.class);


    }


    private void createPost(int tipo){
        //tipo 1:login 2:registro

        //Validaciones

        String ver_nombre = e_nombre.getText().toString();

        if(ver_nombre.length()==0){
            Toast.makeText(getApplicationContext(), "Nombre vacio", Toast.LENGTH_LONG).show();
            return;
        }
        String ver_apellido = e_apellido.getText().toString();

        if(ver_apellido.length()==0){
            Toast.makeText(getApplicationContext(), "Apellido vacio", Toast.LENGTH_LONG).show();
            return;
        }

        Integer ver_dni =  !e_dni.getText().toString().equals("")?Integer.parseInt(e_dni.getText().toString()) : 0;

        if(ver_dni==0){
            Toast.makeText(getApplicationContext(), "DNI vacio", Toast.LENGTH_LONG).show();
            return;
        }

        String ver_email = e_email.getText().toString();

        if(!isValidEmail(ver_email)){
            Toast.makeText(getApplicationContext(), "Email invalido", Toast.LENGTH_LONG).show();
            return;
        }

        String ver_pw = e_password.getText().toString();
        int pw_size = ver_pw.length();

        if(pw_size < 8){
            Toast.makeText(getApplicationContext(), "Password debe tener al menos 8 caracteres", Toast.LENGTH_LONG).show();
            return;
        }

        Integer ver_comision =  !e_comision.getText().toString().equals("")?Integer.parseInt(e_comision.getText().toString()) : 0;

        if(ver_comision==0){
            Toast.makeText(getApplicationContext(), "Comision vacio", Toast.LENGTH_LONG).show();
            return;
        }

        Integer ver_grupo =  !e_grupo.getText().toString().equals("")?Integer.parseInt(e_grupo.getText().toString()) : 0;

        if(ver_grupo==0){
            Toast.makeText(getApplicationContext(), "Grupo vacio", Toast.LENGTH_LONG).show();
            return;
        }

        Post post = new Post("DEV",ver_nombre,ver_apellido,ver_dni,ver_email,ver_pw,ver_comision, ver_grupo);

        Call<Post> call = null;

        if(tipo==1){
            call = jasonPlaceHolderApi.createPostLogin(post);
        }else if(tipo==2){
            call = jasonPlaceHolderApi.createPostRegister(post);
        }

        Log.i("myApp", "Envio de login/registro al servidor");

        call.enqueue(new Callback<Post>() {

            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if (!response.isSuccessful()){
                    try {
                        //textViewResult.setText("Code2: "+ response.code() + "\n" + response.errorBody().string());
                        Log.e("myApp", "Respusta no correcta al enviar login/registro: "+ response.errorBody().string());

                        Toast.makeText(getApplicationContext(), "Error al registrar (Ya registrado?)", Toast.LENGTH_LONG).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }



                Post postResponse = response.body();

                String content = "";
                content += "Code: " + response.code() + "\n"; //response code 201 es created
                content += "state: " + postResponse.getState()+ "\n";
                content += "env: " + postResponse.getEnv()+ "\n";
                content += "msg: " + postResponse.getMsg()+ "\n";
                content += "token: " + postResponse.getToken()+ "\n";

                //textViewResult.setText(content);
                Log.i("myApp", "Login/registro correcto (server respondio ok)");

                if(tipo==1){
                    String currentDate;
                    Calendar calendar = Calendar.getInstance();
                    currentDate = DateFormat.getDateTimeInstance().format(calendar.getTime());

                    CrearEvento.createEvent(postResponse.getToken()/*.replace("/","\\/")*/,"DEV","Login","ACTIVO","Login: "+currentDate);
                    Receiver_pantalla.token = postResponse.getToken();
                    Receiver_Internet.token = postResponse.getToken();
                    openSensores(postResponse.getToken());
                }

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

                //textViewResult.setText(t.getMessage());
                Log.e("myApp", "Fallo al enviar login/registro: "+t.getMessage());
                Toast.makeText(getApplicationContext(), "Fallo al enviar login/registro", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void openSensores(String text){
        Intent intent = new Intent(this, Sensores.class);
        intent.putExtra(EXTRA_TEXT,text);
        //unregisterReceiver(receiver_pantalla);
        startActivity(intent);
    }


    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver_internet,filter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver_internet);
        //unregisterReceiver(receiver_pantalla);
        //unregisterReceiver(wifiStateReceiver);
    }

    /*@Override
    protected void onDestroy() {
        if (receiver_pantalla != null) {
            unregisterReceiver(receiver_pantalla);
            receiver_pantalla = null;
        }
        super.onDestroy();
    }*/

    public void GuardarDatos(){

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_LOGIN,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(SP_NOMBRE,e_nombre.getText().toString());
        editor.putString(SP_APELLIDO,e_apellido.getText().toString());
        editor.putString(SP_DNI,e_dni.getText().toString());
        editor.putString(SP_EMAIL,e_email.getText().toString());
        editor.putString(SP_PASSWORD,e_password.getText().toString());
        editor.putString(SP_COMISION,e_comision.getText().toString());
        editor.putString(SP_GRUPO,e_grupo.getText().toString());

        editor.putBoolean(SWITCH1, s_guardar_datos.isChecked());

        editor.apply();

        Log.i("myApp", "Datos login guardados en sharedpref");
    }

    public void BorrarDatos(){

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_LOGIN,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(SP_NOMBRE,"");
        editor.putString(SP_APELLIDO,"");
        editor.putString(SP_DNI,"");
        editor.putString(SP_EMAIL,"");
        editor.putString(SP_PASSWORD,"");
        editor.putString(SP_COMISION,"");
        editor.putString(SP_GRUPO,"");

        editor.putBoolean(SWITCH1, false);

        editor.apply();

        Log.i("myApp", "Datos login BORRADOS del sharedpref");
    }

    public void CargarDatos(){

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_LOGIN,MODE_PRIVATE);

        e_nombre.setText(sharedPreferences.getString(SP_NOMBRE,""));
        e_apellido.setText(sharedPreferences.getString(SP_APELLIDO,""));
        e_dni.setText(sharedPreferences.getString(SP_DNI,""));
        e_email.setText(sharedPreferences.getString(SP_EMAIL,""));
        e_password.setText(sharedPreferences.getString(SP_PASSWORD,""));
        e_comision.setText(sharedPreferences.getString(SP_COMISION,""));
        e_grupo.setText(sharedPreferences.getString(SP_GRUPO,""));

        s_guardar_datos.setChecked(sharedPreferences.getBoolean(SWITCH1,false));

    }

    /*private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }*/


}