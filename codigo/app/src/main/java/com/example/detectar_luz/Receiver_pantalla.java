package com.example.detectar_luz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;


public class Receiver_pantalla extends BroadcastReceiver {

    public static boolean pantallaEstabaEncendida = true;
    public static String token = "";
    private String currentDate;

    @Override
    public void onReceive(Context context, Intent intent) {

        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateTimeInstance().format(calendar.getTime());

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

            //PANTALLA APAGADA
            Log.i("myApp", "pantallaEstabaEncendida = false");
            pantallaEstabaEncendida = false;
            if(!token.equals("")){
                CrearEvento.createEvent(token,"DEV","Pantalla","INACTIVO","Pantalla Apagada: "+ currentDate);
            }


        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.i("myApp", "pantallaEstabaEncendida = true");
            pantallaEstabaEncendida = true;

            if(!token.equals("")) {
                CrearEvento.createEvent(token, "DEV", "Pantalla", "ACTIVO", "Pantalla Encendida: "+currentDate);
            }
            //PANTALLA ENCENDIDA
        }

        }
    }

