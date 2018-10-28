package pt.up.fe.up201405729.cmov1.customerapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.up201405729.cmov1.restservices.RestServices;

/*
https://stackoverflow.com/questions/12471999/rsa-encryption-decryption-in-android
https://developer.android.com/reference/java/security/KeyStore
https://developer.android.com/training/articles/keystore
https://www.androidauthority.com/use-android-keystore-store-passwords-sensitive-information-623779/
https://paginas.fe.up.pt/~apm/CM/docs/04Fragments.pdf
 */
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
                JSONObject registrationData = new JSONObject();
                try {
                    registrationData.put("publicKey", "");
                    registrationData.put("name", ((EditText) findViewById(R.id.registrationNameET)).getText().toString());
                    registrationData.put("nif", ((EditText) findViewById(R.id.registrationNifET)).getText().toString());
                    registrationData.put("creditCardType", ((EditText) findViewById(R.id.registrationCreditCardTypeET)).getText().toString());
                    registrationData.put("creditCardNumber", ((EditText) findViewById(R.id.registrationCreditCardNumberET)).getText().toString());
                    registrationData.put("creditCardValidity", ((EditText) findViewById(R.id.registrationCreditCardValidityET)).getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject response = RestServices.PUT("/register", registrationData);
                SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedPreferencesKeyName, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                try {
                    editor.putString("uuid", (String) response.get("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editor.apply();

                Intent i = new Intent(packageContext, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
