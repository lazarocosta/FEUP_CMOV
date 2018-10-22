package pt.up.fe.up201405729.cmov1.customerapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity {
    public static  final String sharedPreferencesKeyName = "pt.up.fe.up201405729.cmov1.customerapp.prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       SharedPreferences preferences = getSharedPreferences(sharedPreferencesKeyName, Context.MODE_PRIVATE);
       String uuid = preferences.getString("uuid", null);
       if (uuid == null){
           Intent i = new Intent(this, RegistrationActivity.class);
           startActivity(i);
       }
    }
}
