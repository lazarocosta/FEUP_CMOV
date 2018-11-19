package pt.up.fe.up201405729.cmov1.sharedlibrary;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

public abstract class QRCodeReaderActivity extends AppCompatActivity {
    protected static final String qrCodeContentDataDelimiter = ">";
    protected static final String qrCodeContentDataTypeDelimiter = "\n";
    private static final int requestCode = 0;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Intent i = new Intent("com.google.zxing.client.android.SCAN");
            i.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(i, requestCode);
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

    protected abstract void processQRCode(String contents);
}
