package pt.up.fe.up201405729.cmov1.cafeteriaapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.up201405729.cmov1.restservices.RestServices;
import pt.up.fe.up201405729.cmov1.sharedlibrary.KeyStoreManager;
import pt.up.fe.up201405729.cmov1.sharedlibrary.QRCodeReaderActivity;

public class MainActivity extends QRCodeReaderActivity {
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle("Cafeteria");
            bar.hide();
        }
    }

    @Override
    protected void processQRCode(String base64Contents) {
        byte[] signedBytes = KeyStoreManager.fromBase64ToByteArray(base64Contents);
        try {
            JSONObject response = RestServices.POST("/payOrder", signedBytes);
            if (response.has("data")) { // The value of "data" should be "true".
                Toast.makeText(context, "Order accepted.", Toast.LENGTH_LONG).show();
                Intent i = new Intent(context, ResponseActivity.class);
                i.putExtra("Order", response.toString());
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
