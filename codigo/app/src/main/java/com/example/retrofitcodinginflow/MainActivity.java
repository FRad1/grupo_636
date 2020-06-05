package com.example.retrofitcodinginflow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_TEXT = "main.EXTRA_TEXT";

    private TextView textViewResult;
    private JasonPlaceHolderApi jasonPlaceHolderApi;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        textViewResult = findViewById(R.id.text_view_result);

        //listener de botones

        if(!haveNetworkConnection()){
            Toast.makeText(getApplicationContext(), "Sin conexion a Internet", Toast.LENGTH_LONG).show();
        }

        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!haveNetworkConnection()){
                    Toast.makeText(getApplicationContext(), "Sin conexion a Internet", Toast.LENGTH_LONG).show();
                }else{
                    createPostLogin();
                }

            }
        });

        b_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!haveNetworkConnection()){
                    Toast.makeText(getApplicationContext(), "Sin conexion a Internet", Toast.LENGTH_LONG).show();
                }else {
                    createPostRegister();
                }
            }
        });

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel((HttpLoggingInterceptor.Level.BODY));

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://so-unlam.net.ar/api/api/") //siempre poner el / al final del url
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        jasonPlaceHolderApi = retrofit.create(JasonPlaceHolderApi.class);



        //createPost();
    }


    private void createPostLogin(){

        //Post post = new Post("TEST","nombre1","apellido2",36066665,"emaildo@hotmaildo.com","123456789",25, 30); //hardcodeado para testeo

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

        Call<Post> call = jasonPlaceHolderApi.createPostLogin(post);



        call.enqueue(new Callback<Post>() {

            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if (!response.isSuccessful()){
                    try {
                        textViewResult.setText("Code2: "+ response.code() + "\n" + response.errorBody().string());
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

                textViewResult.setText(content);

                createEvent(postResponse.getToken().replace("/","\\/"),"DEV","Login","ACTIVO","Login");


            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

                textViewResult.setText(t.getMessage());
            }
        });
    }

    private void createPostRegister(){

        //Post post = new Post("TEST","nombre1","apellido2",36066665,"emaildo@hotmaildo.com","123456789",25, 30); //hardcodeado para testeo

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

        Call<Post> call = jasonPlaceHolderApi.createPostRegister(post);



        call.enqueue(new Callback<Post>() {

            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if (!response.isSuccessful()){
                    try {
                        textViewResult.setText("Code2: "+ response.code() + "\n" + response.errorBody().string());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    return;
                }



                Post postResponse = response.body();

                String content = "";
                content += "Code: " + response.code() + "\n"; //response code 201 es created
                content += "state: " + postResponse.getState()+ "\n";;
                content += "env: " + postResponse.getEnv()+ "\n";;
                content += "msg: " + postResponse.getMsg()+ "\n";;
                content += "token: " + postResponse.getToken()+ "\n";


                textViewResult.setText(content);


            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

                textViewResult.setText(t.getMessage());
            }
        });
    }

    public void createEvent(String token_pass,String env_pass,String type_events_pass, String state_pass,String descripcion_pass ){


        Event event = new Event(env_pass,type_events_pass,state_pass,descripcion_pass);

        Call<Event> call = jasonPlaceHolderApi.createEvent("\""+token_pass+"\"",event);

        Log.w("myApp", "hola1");
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {

                if (!response.isSuccessful()){
                    try {
                        Log.w("myApp", "hola2");
                        textViewResult.setText("Code2: "+ response.code() + "\n" + response.errorBody().string());

                        Toast.makeText(getApplicationContext(), "Falla en el envio de evento Login al servidor", Toast.LENGTH_LONG).show();

                        openSensores(token_pass); //sacarlo, esta ahora porque no funciona el token en el server

                    } catch (IOException e) {
                        Log.w("myApp", "hola3");
                        e.printStackTrace();
                    }

                    return;
                }

                Log.w("myApp", "hola4");
                Event eventResponse = response.body();

                String content = "";
                content += "Code: " + response.code() + "\n"; //response code 201 es created
                content += "state: " + eventResponse.getState()+ "\n";
                content += "env: " + eventResponse.getEnv()+ "\n";


                textViewResult.setText(content);
                Log.w("myApp", content);
                Log.w("myApp", "hola");

                openSensores(token_pass);

            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.w("myApp", "hola5");
                textViewResult.setText(t.getMessage());
            }
        });
    }


    public void openSensores(String text){
        Intent intent = new Intent(this, Sensores.class);
        intent.putExtra(EXTRA_TEXT,text);
        startActivity(intent);
    }


    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean haveNetworkConnection() {
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
    }


}