package pt.up.fe.up201405729.cmov1.customerapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import pt.up.fe.up201405729.cmov1.restservices.RestServices;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration); // TODO: landscape layout

        final Context packageContext = this;
        findViewById(R.id.registrationSubmitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: generate RSA key pair, send public key to database and store private key
                // TODO: check input data?
                ArrayList<String> registrationData = new ArrayList<String>() {
                    {
                        ((EditText) findViewById(R.id.registrationNameET)).getText();
                        ((EditText) findViewById(R.id.registrationNifET)).getText();
                        ((EditText) findViewById(R.id.registrationCreditCardTypeET)).getText();
                        ((EditText) findViewById(R.id.registrationCreditCardNumberET)).getText();
                        ((EditText) findViewById(R.id.registrationCreditCardValidityET)).getText();
                    }
                };
                String uuid = RestServices.PUT("/register", registrationData);
                SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedPreferencesKeyName, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("uuid", uuid);
                editor.apply();

                Intent i = new Intent(packageContext, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
