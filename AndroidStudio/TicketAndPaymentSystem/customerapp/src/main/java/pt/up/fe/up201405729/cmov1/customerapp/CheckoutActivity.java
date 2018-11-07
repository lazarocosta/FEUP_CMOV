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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import pt.up.fe.up201405729.cmov1.restservices.RestServices;
import pt.up.fe.up201405729.cmov1.sharedlibrary.SHA256;

public class CheckoutActivity extends AppCompatActivity {
    private CustomerApp app;
    private PerformancesRVAdapter performancesRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle("Checkout");
        }
        this.app = (CustomerApp) getApplicationContext();

        Intent i = getIntent();
        CheckoutData checkoutData = (CheckoutData) i.getSerializableExtra(CustomerApp.checkoutDataKeyName);

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
            SharedPreferences preferences = getSharedPreferences(CustomerApp.sharedPreferencesKeyName, Context.MODE_PRIVATE);
            String uuid = preferences.getString("uuid", null);

            JSONObject buyTicketsData = new JSONObject();
            try {
                JSONArray jsonPerformances = new JSONArray();
                for (int i = 0; i < performances.size(); i++) {
                    Performance p = performances.get(i);
                    Integer numTickets = ticketsQuantities.get(i);
                    JSONObject jsonPerformance = new JSONObject();
                    jsonPerformance.put("id", p.getId());
                    jsonPerformance.put("numberTickets", numTickets);
                    jsonPerformances.put(jsonPerformance);
                }
                buyTicketsData.put("tickets", jsonPerformances);
                buyTicketsData.put("userId", uuid);
                String hash = SHA256.SHA256(buyTicketsData.toString());
                buyTicketsData.put("hash", hash);
                buyTicketsData.put("encryptedHash", app.getEncryptionManager().encryptString(hash));
            } catch (JSONException | NoSuchAlgorithmException e) {
                e.printStackTrace();
                Toast.makeText(packageContext, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            JSONObject response = RestServices.PUT("/buyTickets", buyTicketsData);
            try {
                JSONObject jsonData = response.getJSONObject("data");
                JSONArray jsonVouchers = jsonData.getJSONArray("vouchers");
                ArrayList<Voucher> vouchers = FileManager.readVouchers(packageContext);
                for (int i = 0; i < jsonVouchers.length(); i++) {
                    JSONObject jsonVoucher = jsonVouchers.getJSONObject(i);
                    Voucher v = new Voucher(jsonVoucher.getString("id"), jsonVoucher.getString("productCode"));
                    vouchers.add(v);
                }
                JSONArray jsonTickets = jsonData.getJSONArray("tickets");
                ArrayList<Ticket> tickets = FileManager.readTickets(packageContext);
                for (int i = 0; i < jsonTickets.length(); i++) {
                    JSONObject jsonTicket = jsonTickets.getJSONObject(i);
                    String id = jsonTicket.getString("id");
                    String performanceId = jsonTicket.getString("performanceId");
                    String showName = jsonTicket.getString("name");
                    MyDate date = new MyDate(jsonTicket.getString("date"));
                    String roomPlace = jsonTicket.getString("place");
                    Ticket t = new Ticket(id, performanceId, showName, date, roomPlace);
                    tickets.add(t);
                }
                FileManager.writeVouchers(packageContext, vouchers);
                FileManager.writeTickets(packageContext, tickets);
            } catch (JSONException e) {
                try {
                    Toast.makeText(packageContext, response.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    Toast.makeText(packageContext, e1.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            Intent i = new Intent(packageContext, MainActivity.class);
            startActivity(i);
            finish();
        }
        return (super.onOptionsItemSelected(item));
    }
}
