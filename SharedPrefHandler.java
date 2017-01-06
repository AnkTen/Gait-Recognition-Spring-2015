
package com.example.asa.gaitrecog;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class SharedPrefHandler {
    Context context;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    SharedPrefHandler(Context context, String sp_name){
        this.context = context;
        prefs = context.getSharedPreferences(sp_name, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    void put(String key, int value){
        editor.putInt(key, value);
        editor.commit();
    }

    int getInt(String key){
       return prefs.getInt(key, 0);
    }

    void remove(String key){}

}


