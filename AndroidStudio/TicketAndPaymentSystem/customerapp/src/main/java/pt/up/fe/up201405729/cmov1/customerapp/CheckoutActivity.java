package pt.up.fe.up201405729.cmov1.customerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CheckoutActivity extends AppCompatActivity {
    public static final String checkoutDataKeyName = "pt.up.fe.up201405729.cmov1.customerapp.CheckoutActivity.checkoutData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle("Checkout");
        }

        Intent i = getIntent();
        CheckoutData checkoutData = (CheckoutData) i.getSerializableExtra(checkoutDataKeyName);
        // show tickets and total price
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_checkout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Context packageContext = this;
        if (item.getItemId() == R.id.checkoutActivityBuyButton) {
            // update database
            Intent i = new Intent(packageContext, MainActivity.class);
            startActivity(i);
            finish();
        }
        return (super.onOptionsItemSelected(item));
    }
}
