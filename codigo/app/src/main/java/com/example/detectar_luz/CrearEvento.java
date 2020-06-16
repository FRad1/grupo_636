package com.example.detectar_luz;

import android.util.Log;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CrearEvento {

    public static HashMap<Integer,Event> offlineEventHashMap = new HashMap<>();
    private static JasonPlaceHolderApi jasonPlaceHolderApi;

    public static void createEvent(String token_pass,String env_pass,String type_events_pass, String state_pass,String descripcion_pass){


        Log.i("myApp", "Pidieron enviar un evento!: " + descripcion_pass);

        if(!Receiver_Internet.hay_internet){
            Log.i("myApp", "Sin internet, guardo el evento");

            Event offlineEvent = new Event(env_pass,type_events_pass,state_pass,descripcion_pass);
            Log.i("myApp", "if(!Receiver_Internet.hay_internet) offlineEventHashMap.size(): " + offlineEventHashMap.size());
            offlineEventHashMap.put(offlineEventHashMap.size(),offlineEvent);


            return;
        }

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

        //Event event = new Event(env_pass,type_events_pass,state_pass,descripcion_pass);

        //Call<Event> call = jasonPlaceHolderApi.createEvent(token_pass,event);
        Call<Event> call = jasonPlaceHolderApi.createEvent(token_pass,env_pass,type_events_pass,state_pass,descripcion_pass);


        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {

                if (!response.isSuccessful()){
                    try {
                        Log.e("myApp", "Error al enviar evento: "+ response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //textViewResult.setText("Code2: "+ response.code() + "\n" + response.errorBody().string());

                    //Toast.makeText(getApplicationContext(), "Falla en el envio de evento Login al servidor", Toast.LENGTH_LONG).show();

                    //openSensores(token_pass); //sacarlo, esta ahora porque no funciona el token en el server

                    return;
                }
                //Toast.makeText(getApplicationContext(), "Envio de evento Login al servidor OK", Toast.LENGTH_LONG).show();
                //Log.w("myApp", "hola4");
                Event eventResponse = response.body();

                String content = "";
                content += "Code: " + response.code() + "\n"; //response code 201 es created
                content += "state: " + eventResponse.getState()+ "\n";
                content += "env: " + eventResponse.getEnv()+ "\n";


                //textViewResult.setText(content);
                Log.i("myApp", "Envio correcto de evento: "+ descripcion_pass+ " content: "+ content);
                //Log.w("myApp", "hola");

                //openSensores(token_pass);

            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.e("myApp", "Fallo en el envio de evento, lo envio al hashmap de offline" + t.getMessage());
                Log.e("myApp", "public void onFailure offlineEventHashMap.size(): " + offlineEventHashMap.size());
                //textViewResult.setText(t.getMessage());
                Event offlineEvent = new Event(env_pass,type_events_pass,state_pass,descripcion_pass);
                offlineEventHashMap.put(offlineEventHashMap.size(),offlineEvent);


                if(t.getMessage().contains("SocketTimeoutException")){
                    Log.e("myApp", "Fallo en el envio de evento, TIMEOUT luego de 5 minutos" + t.getMessage());
                }

            }
        });
    }

    static class EnviarEventosOffline implements Runnable {

        @Override
        public void run() {



            for (int i = offlineEventHashMap.size()-1; i >= 0; i--) {
                System.out.println("key: " + i + " value: " + offlineEventHashMap.get(i).getdescription());
                CrearEvento.createEvent(Receiver_Internet.token,
                        offlineEventHashMap.get(i).getEnv(),
                        offlineEventHashMap.get(i).getType_events(),
                        offlineEventHashMap.get(i).getState(),
                        offlineEventHashMap.get(i).getdescription()
                );
                Log.i("myApp", "-------->Evento offline enviado al create event: key:" + i + " Evento desc: "+offlineEventHashMap.get(i).getdescription());
                offlineEventHashMap.remove(i);
                if(Receiver_Internet.hay_internet) {
                    try {
                        Thread.sleep(5000); //sino envia todos los eventos juntos y no los toma el server de eventos

                        if(!Receiver_Internet.hay_internet) { // <- el problema
                            Log.i("myApp", "keyEventOffline --> offlineEventHashMap.size():" + offlineEventHashMap.size());
                            return;
                        }

                        } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }else{
                    Log.i("myApp", "keyEventOffline --> offlineEventHashMap.size():" + offlineEventHashMap.size());
                    return;
                }

            }


        }



    }
}
