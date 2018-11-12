package pt.up.fe.up201405729.cmov1.customerapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import pt.up.fe.up201405729.cmov1.restservices.RestServices;

public class RegistrationActivity extends AppCompatActivity {
    private CustomerApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration); // TODO: landscape layout

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle("New user");
        }
        this.app = (CustomerApp) getApplicationContext();

        final EditText creditCardValidity = findViewById(R.id.registrationCreditCardValidityET);
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                creditCardValidity.setText(StringFormat.formatAsDate(year, month, dayOfMonth));
            }
        };
        Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePicker = new DatePickerDialog(this, onDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        creditCardValidity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    datePicker.show();
                else
                    datePicker.hide();
            }
        });
        creditCardValidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_registration_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Context context = this;
        if (item.getItemId() == R.id.registrationActivityRegisterButton) {
            // TODO: check input data?
            JSONObject registrationData = new JSONObject();
            try {
                registrationData.put("publicKey", app.getEncryptionManager().getPublicKey());
                registrationData.put("name", ((EditText) findViewById(R.id.registrationNameET)).getText().toString());
                registrationData.put("nif", ((EditText) findViewById(R.id.registrationNifET)).getText().toString());
                registrationData.put("creditCardType", ((EditText) findViewById(R.id.registrationCreditCardTypeET)).getText().toString());
                registrationData.put("creditCardNumber", ((EditText) findViewById(R.id.registrationCreditCardNumberET)).getText().toString());
                registrationData.put("creditCardValidity", ((EditText) findViewById(R.id.registrationCreditCardValidityET)).getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            JSONObject response = RestServices.PUT("/register", registrationData);
            SharedPreferences sharedPreferences = getSharedPreferences(CustomerApp.sharedPreferencesKeyName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            try {
                editor.putString("uuid", response.getString("data"));
            } catch (JSONException e) {
                try {
                    Toast.makeText(context, response.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    Toast.makeText(context, e1.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            editor.apply();

            Intent i = new Intent(context, MainActivity.class);
            startActivity(i);
            finish();
        }
        return (super.onOptionsItemSelected(item));
    }
}
