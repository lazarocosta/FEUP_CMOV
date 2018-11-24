package pt.up.fe.up201405729.cmov1.customerapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import pt.up.fe.up201405729.cmov1.restservices.RestServices;
import pt.up.fe.up201405729.cmov1.sharedlibrary.KeyStoreManager;
import pt.up.fe.up201405729.cmov1.sharedlibrary.StringFormat;

public class RegistrationActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    private final Context context = this;
    private CustomerApp app;
    private Calendar calendar;
    private DatePickerDialog datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("New user");
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(this);
        ActionMenuItemView actionMenuItemView = findViewById(R.id.toolbar_button);
        actionMenuItemView.setText(R.string.register_string);
        this.app = (CustomerApp) getApplicationContext();

        final EditText creditCardValidity = findViewById(R.id.registrationCreditCardValidityET);
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                creditCardValidity.setText(StringFormat.formatAsDate(year, month, dayOfMonth));
            }
        };
        calendar = Calendar.getInstance();
        datePicker = new DatePickerDialog(context, onDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
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
    protected void onResume() {
        super.onResume();
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.toolbar_button:
                performRegistration();
                goToMainActivity();
                return true;
            default:
                return false;
        }
    }

    private void performRegistration() {
        JSONObject registrationData = new JSONObject();
        try {
            JSONObject rsaPublicKeyJSONObject = new JSONObject();
            KeyStoreManager keyStoreManager = app.getKeyStoreManager();
            rsaPublicKeyJSONObject.put("modulus", keyStoreManager.getPublicKeyModulus());
            rsaPublicKeyJSONObject.put("publicExponent", keyStoreManager.getPublicKeyExponent());
            registrationData.put("publicKey", rsaPublicKeyJSONObject);
            registrationData.put("name", ((EditText) findViewById(R.id.registrationNameET)).getText().toString());
            registrationData.put("nif", ((EditText) findViewById(R.id.registrationNifET)).getText().toString());
            registrationData.put("creditCardType", ((EditText) findViewById(R.id.registrationCreditCardTypeET)).getText().toString());
            registrationData.put("creditCardNumber", ((EditText) findViewById(R.id.registrationCreditCardNumberET)).getText().toString());
            registrationData.put("creditCardValidity", ((EditText) findViewById(R.id.registrationCreditCardValidityET)).getText().toString());

            final JSONObject response = RestServices.PUT("/register", registrationData);
            SharedPreferences sharedPreferences = getSharedPreferences(CustomerApp.sharedPreferencesKeyName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (response.has("data"))
                editor.putString("uuid", response.getString("data"));
            else
                Toast.makeText(context, response.getString("error"), Toast.LENGTH_LONG).show();
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void goToMainActivity() {
        Intent i = new Intent(context, MainActivity.class);
        startActivity(i);
        finish();
    }
}
