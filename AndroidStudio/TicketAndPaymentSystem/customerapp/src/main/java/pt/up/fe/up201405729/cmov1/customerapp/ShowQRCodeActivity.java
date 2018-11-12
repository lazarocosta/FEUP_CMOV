package pt.up.fe.up201405729.cmov1.customerapp;

// Based on https://paginas.fe.up.pt/~apm/CM/docs/QRCodeDemos.zip

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class ShowQRCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_qrcode);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle("QR code");
            bar.hide();
        }

        Intent i = getIntent();
        final String qrCodeContent = i.getStringExtra(CustomerApp.qrCodeContentKeyName);
        final Context context = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Bitmap bitmap = encodeAsBitmap(qrCodeContent);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageView) findViewById(R.id.qrCodeIV)).setImageBitmap(bitmap);
                        }
                    });
                } catch (WriterException e) {
                    e.printStackTrace();
                    final String errorMsg = e.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                            Intent i = new Intent(context, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    });
                }
            }
        }).start();
    }

    private Bitmap encodeAsBitmap(String str) throws WriterException {
        final int DIMENSION = 500;
        BitMatrix result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, DIMENSION, DIMENSION);
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++)
                pixels[offset + x] = result.get(x, y) ? getResources().getColor(R.color.colorPrimary) : getResources().getColor(R.color.colorWhite);
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }
}
