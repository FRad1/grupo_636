package com.example.detectar_luz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.util.Log;
import java.text.DateFormat;
import java.util.Calendar;

public class Receiver_Internet extends BroadcastReceiver {

    public static boolean hay_internet=true;
    public static String token = "";
    private String currentDate;
    private String DateDesconexion;
    @Override
    public void onReceive(Context context, Intent intent) {

        Calendar calendar = Calendar.getInstance();


        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            boolean noConnectivity = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY,false
            );
            if(noConnectivity){
                //Toast.makeText(context,"Desconectado",Toast.LENGTH_SHORT).show();
                hay_internet=false;
                Log.i("myApp", "desconectado de internet");
                if(Sensores.internet_textview!=null){
                    Sensores.internet_textview.setText("Internet: Desconectado");
                    Sensores.internet_textview.setTextColor(Color.RED);
                }
                if(MainActivity.internet_textview!=null) {
                    MainActivity.internet_textview.setText("Internet: Desconectado");
                    MainActivity.internet_textview.setTextColor(Color.RED);
                }
                if(!token.equals("")) {
                    DateDesconexion = DateFormat.getDateTimeInstance().format(calendar.getTime());
                    CrearEvento.createEvent(token,"DEV","Internet","INACTIVO","Internet desconectado: "+ DateDesconexion);
                }

            }else if (!hay_internet){
                //Toast.makeText(context,"Conectado",Toast.LENGTH_SHORT).show();
                hay_internet=true;
                Log.i("myApp", "conectado a internet");
                if(Sensores.internet_textview!=null) {
                    Sensores.internet_textview.setText("Internet: Conectado");
                    Sensores.internet_textview.setTextColor(Color.parseColor("#03a56a"));
                }
                if(MainActivity.internet_textview!=null) {
                    MainActivity.internet_textview.setText("Internet: Conectado");
                    MainActivity.internet_textview.setTextColor(Color.parseColor("#03a56a"));
                }
                if(!token.equals("")){
                    currentDate = DateFormat.getDateTimeInstance().format(calendar.getTime());

                    CrearEvento.createEvent(token,"DEV","Internet","ACTIVO","Internet conectado: "+ currentDate);


                    CrearEvento.EnviarEventosOffline enviarEventosOffline = new CrearEvento.EnviarEventosOffline();
                    new Thread(enviarEventosOffline).start();


                }
            }

        }
    }
}
