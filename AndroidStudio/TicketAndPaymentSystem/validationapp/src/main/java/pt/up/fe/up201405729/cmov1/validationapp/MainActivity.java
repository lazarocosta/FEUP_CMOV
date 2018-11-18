package pt.up.fe.up201405729.cmov1.validationapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.up201405729.cmov1.restservices.EncryptionManager;
import pt.up.fe.up201405729.cmov1.restservices.RestServices;

import static pt.up.fe.up201405729.cmov1.sharedlibrary.Shared.qrCodeContentDataDelimiter;
import static pt.up.fe.up201405729.cmov1.sharedlibrary.Shared.qrCodeContentDataTypeDelimiter;

public class MainActivity extends AppCompatActivity {

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
    protected void onResume() {
        super.onResume();
        try {
            Intent i = new Intent("com.google.zxing.client.android.SCAN");
            i.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(i, 0);
        } catch (Exception e) {
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);
            Intent i = getIntent();
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                processQRCode(contents);
            }
        }
    }

    private void processQRCode(String base64Contents) {
        final Context context = this;
        String contents = EncryptionManager.fromBase64(base64Contents);
        String[] dataTypes = contents.split(qrCodeContentDataTypeDelimiter);
        if (dataTypes.length != 2)
            throw new IllegalArgumentException("It was expected 2 data types. Found: " + dataTypes.length);
        String uuid = dataTypes[0];
        String[] ticketsUuids = dataTypes[1].split(qrCodeContentDataDelimiter);
        JSONObject validationData = new JSONObject();
        try {
            JSONObject ticketsIds = new JSONObject();
            for (int i = 0; i < ticketsUuids.length; i++)
                ticketsIds.put("ticketId" + i, ticketsUuids[i]);
            validationData.put("tickets", ticketsIds);
            validationData.put("userId", uuid);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        JSONObject response = RestServices.GET("/validTickets", validationData);
        try {
            response.getString("data");   // If no exception is thrown, the returned value should be "true".
            Intent i = new Intent(context, QRCodeValidatedActivity.class);
            startActivity(i);
            finish();
        } catch (JSONException e) {
            try {
                Toast.makeText(context, response.getString("error"), Toast.LENGTH_LONG).show();
            } catch (JSONException e1) {
                e1.printStackTrace();
                Toast.makeText(context, e1.getMessage(), Toast.LENGTH_LONG).show();
            }
            Intent i = new Intent(context, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}
