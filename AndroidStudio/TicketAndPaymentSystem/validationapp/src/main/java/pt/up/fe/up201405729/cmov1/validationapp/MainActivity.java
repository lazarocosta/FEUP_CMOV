package pt.up.fe.up201405729.cmov1.validationapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.up201405729.cmov1.sharedlibrary.KeyStoreManager;
import pt.up.fe.up201405729.cmov1.restservices.RestServices;
import pt.up.fe.up201405729.cmov1.sharedlibrary.QRCodeReaderActivity;


public class MainActivity extends QRCodeReaderActivity {
    private static final int numExpectedDataTypes = 2;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle("Validator");
            bar.hide();
        }
    }

    @Override
    protected void processQRCode(String base64Contents) {
        String contents = KeyStoreManager.fromBase64ToString(base64Contents);
        String[] dataTypes = contents.split(qrCodeContentDataTypeDelimiter);
        if (dataTypes.length != numExpectedDataTypes) {
            Toast.makeText(context, "Invalid QR code.", Toast.LENGTH_LONG).show();
            return;
        }
        String uuid = dataTypes[0];
        String[] ticketsUuids = dataTypes[1].split(qrCodeContentDataDelimiter);
        JSONObject validationData = new JSONObject();
        try {
            validationData.put("userId", uuid);
            JSONObject ticketsIds = new JSONObject();
            for (int i = 0; i < ticketsUuids.length; i++)
                ticketsIds.put("ticketId" + i, ticketsUuids[i]);
            validationData.put("tickets", ticketsIds);

            JSONObject response = RestServices.POST("/validTickets", validationData);
            if (response.has("data")) { // The value of "data" should be "true".
                Toast.makeText(context, "Validation successful.", Toast.LENGTH_LONG).show();
                Intent i = new Intent(context, QRCodeValidatedActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(context, response.getString("error"), Toast.LENGTH_LONG).show();
                goToMainActivity();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            goToMainActivity();
        }
    }

    private void goToMainActivity() {
        Intent i = new Intent(context, MainActivity.class);
        startActivity(i);
        finish();
    }
}
