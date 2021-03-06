package com.ezbus.main;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Classe che salva le preferenze di sistema.
 */

public class SharedPref {

    private SharedPreferences MySharedPref;


    public SharedPref (Context context) {
        MySharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    //Salvataggio Dark Theme
    void setNightModeState(Boolean state) {
        SharedPreferences.Editor editor =  MySharedPref.edit();
        editor.putBoolean("NightMode", state);
        editor.apply();
    }

    //Caricamento Dark Theme
    public Boolean loadNightModeState() {
        return MySharedPref.getBoolean("NightMode", false);
    }

    void setClient(Boolean state) {
        SharedPreferences.Editor editor =  MySharedPref.edit();
        editor.putBoolean("Client", state);
        editor.apply();
    }

    public Boolean isClient() {
        return MySharedPref.getBoolean("Client", false);
    }

    public String getQuery() {
        if (isClient()) return "clients";
        else return "companies";
    }

}