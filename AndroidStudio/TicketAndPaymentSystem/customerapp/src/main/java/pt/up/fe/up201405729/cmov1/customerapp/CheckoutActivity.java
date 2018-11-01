package pt.up.fe.up201405729.cmov1.customerapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.up.fe.up201405729.cmov1.restservices.RestServices;

public class CheckoutActivity extends AppCompatActivity {
    public static final String checkoutDataKeyName = "pt.up.fe.up201405729.cmov1.customerapp.CheckoutActivity.checkoutData";
    private PerformancesRVAdapter performancesRVAdapter;

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

        String totalPrice = StringFormat.formatAsPrice(checkoutData.getTotalPrice());
        ((TextView) findViewById(R.id.checkoutTotalPriceValueTV)).setText(totalPrice);

        RecyclerView performancesRV = findViewById(R.id.checkoutRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        performancesRV.setLayoutManager(gridLayoutManager);
        performancesRVAdapter = new PerformancesRVAdapter(checkoutData.getPerformances(), checkoutData.getTicketsQuantities(), false);
        performancesRV.setAdapter(performancesRVAdapter);
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
            ArrayList<Performance> performances = performancesRVAdapter.getPerformances();
            ArrayList<Integer> ticketsQuantities = performancesRVAdapter.getTicketsQuantities();
            SharedPreferences preferences = getSharedPreferences(MainActivity.sharedPreferencesKeyName, Context.MODE_PRIVATE);
            String uuid = preferences.getString("uuid", null);

            for (int i = 0; i < performances.size(); i++) {
                Performance p = performances.get(i);
                Integer numTickets = ticketsQuantities.get(i);
                JSONObject ticketData = new JSONObject();
                try {
                    ticketData.put("date", p.getDate());
                    ticketData.put("numbersTickets", numTickets);
                    ticketData.put("userId", uuid);
                    ticketData.put("priceTicket", p.getPrice());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(packageContext, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                JSONObject response = RestServices.PUT("/buyTicket ", ticketData);  // not working
                try {
                    response.getJSONObject("data");
                } catch (JSONException e) {
                    try {
                        Toast.makeText(packageContext, response.getString("error"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        Toast.makeText(packageContext, e1.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            Intent i = new Intent(packageContext, MainActivity.class);
            startActivity(i);
            finish();
        }
        return (super.onOptionsItemSelected(item));
    }
}
