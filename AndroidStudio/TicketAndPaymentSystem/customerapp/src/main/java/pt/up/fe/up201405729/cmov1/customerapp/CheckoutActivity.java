package pt.up.fe.up201405729.cmov1.customerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class CheckoutActivity extends AppCompatActivity {
    public static final String checkoutDataKeyName = "pt.up.fe.up201405729.cmov1.customerapp.CheckoutActivity.checkoutData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Intent i = getIntent();
        CheckoutData checkoutData = (CheckoutData) i.getSerializableExtra(checkoutDataKeyName);

        // show tickets and total price

        final Context packageContext = this;
        findViewById(R.id.checkoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update database
                Intent i = new Intent(packageContext, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
