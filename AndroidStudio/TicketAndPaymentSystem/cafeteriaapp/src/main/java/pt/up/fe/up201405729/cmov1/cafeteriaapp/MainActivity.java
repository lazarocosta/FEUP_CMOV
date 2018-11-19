package pt.up.fe.up201405729.cmov1.cafeteriaapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.up201405729.cmov1.restservices.EncryptionManager;
import pt.up.fe.up201405729.cmov1.restservices.RestServices;
import pt.up.fe.up201405729.cmov1.sharedlibrary.QRCodeReaderActivity;

import static pt.up.fe.up201405729.cmov1.sharedlibrary.Shared.qrCodeContentDataDelimiter;
import static pt.up.fe.up201405729.cmov1.sharedlibrary.Shared.qrCodeContentDataTypeDelimiter;

public class MainActivity extends QRCodeReaderActivity {
    //private static final int numExpectedDataTypes = 3;

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
        final Context context = this;
        byte[] signedBytes = EncryptionManager.fromBase64ToByteArray(base64Contents);
        /*String contents = EncryptionManager.fromBase64ToString(base64Contents);
        String[] dataTypes = contents.split(qrCodeContentDataTypeDelimiter);
        if (dataTypes.length != numExpectedDataTypes) {
            Toast.makeText(context, "Invalid QR code.", Toast.LENGTH_LONG).show();
            return;
        }
        String uuid = dataTypes[0];
        String[] productsUuids = dataTypes[1].split(qrCodeContentDataDelimiter);
        String[] vouchersUuids = dataTypes[2].split(qrCodeContentDataDelimiter);
        JSONObject orderData = new JSONObject();*/
        try {
            /*orderData.put("userId", uuid);
            JSONObject productsIds = new JSONObject();
            for (int i = 0; i < productsUuids.length; i++)
                productsIds.put("productId" + i, productsUuids[i]);
            orderData.put("products", productsIds);
            JSONObject vouchersIds = new JSONObject();
            for (int i = 0; i < vouchersUuids.length; i++)
                vouchersIds.put("voucherId" + i, vouchersUuids[i]);
            orderData.put("vouchers", vouchersIds);*/

            JSONObject response = RestServices.POST("/payOrder", signedBytes);
            if (response.has("data")) { // The value of "data" should be "true".
                Toast.makeText(context, "Order accepted.", Toast.LENGTH_LONG).show();
                // Show data on other activity.
            } else
                Toast.makeText(context, response.getString("error"), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        Intent i = new Intent(context, MainActivity.class);
        startActivity(i);
        finish();
    }
}
