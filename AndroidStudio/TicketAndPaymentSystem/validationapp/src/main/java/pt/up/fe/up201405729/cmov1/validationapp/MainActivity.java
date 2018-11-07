package pt.up.fe.up201405729.cmov1.validationapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.up201405729.cmov1.restservices.RestServices;

import static pt.up.fe.up201405729.cmov1.sharedlibrary.Shared.qrCodeContentDelimiter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    private void processQRCode(String contents) {
        final Context packageContext = this;
        String[] uuids = contents.split(qrCodeContentDelimiter);
        if (uuids.length == 0)
            return;

        JSONObject validationData = new JSONObject();
        try {
            JSONObject ticketsIds = new JSONObject();
            for (int i = 1; i < uuids.length; i++)
                ticketsIds.put("ticketId" + i, uuids[i]);
            validationData.put("tickets", ticketsIds);
            validationData.put("userId", uuids[0]);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(packageContext, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        JSONObject response = RestServices.GET("/validTickets", validationData);
        try {
            response.getString("data");   // If no exception is thrown, the returned value should be "true".
            Intent i = new Intent(packageContext, QRCodeValidatedActivity.class);
            startActivity(i);
            finish();
        } catch (JSONException e) {
            try {
                Toast.makeText(packageContext, response.getString("error"), Toast.LENGTH_LONG).show();
            } catch (JSONException e1) {
                e1.printStackTrace();
                Toast.makeText(packageContext, e1.getMessage(), Toast.LENGTH_LONG).show();
            }
            Intent i = new Intent(packageContext, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}
