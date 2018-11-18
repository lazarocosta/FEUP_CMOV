package pt.up.fe.up201405729.cmov1.customerapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.up.fe.up201405729.cmov1.restservices.RestServices;

public class CheckoutActivity extends NavigableActivity implements Toolbar.OnMenuItemClickListener {
    private CustomerApp app;
    private PerformancesRVAdapter performancesRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Checkout");
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(this);
        ActionMenuItemView actionMenuItemView = findViewById(R.id.toolbar_button);
        actionMenuItemView.setText(R.string.buy_string);
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
    public boolean onMenuItemClick(MenuItem menuItem) {
        final Context context = this;
        switch (menuItem.getItemId()) {
            case R.id.toolbar_button:
                ArrayList<Performance> performances = performancesRVAdapter.getPerformances();
                ArrayList<Integer> ticketsQuantities = performancesRVAdapter.getTicketsQuantities();
                SharedPreferences preferences = getSharedPreferences(CustomerApp.sharedPreferencesKeyName, Context.MODE_PRIVATE);
                String uuid = preferences.getString("uuid", null);

                byte[] signedMessage = new byte[0];
                System.out.println("aqui");
                try {
                    JSONObject buyTicketsData = new JSONObject();
                    JSONArray jsonPerformances = new JSONArray();
                    for (int i = 0; i < performances.size(); i++) {
                        Performance p = performances.get(i);
                        Integer numTickets = ticketsQuantities.get(i);
                        JSONObject jsonPerformance = new JSONObject();
                        jsonPerformance.put("id", p.getId());
                        jsonPerformance.put("numberTickets", numTickets);
                        jsonPerformances.put(jsonPerformance);
                    }
                    buyTicketsData.put("performances", jsonPerformances);
                    buyTicketsData.put("userId", uuid);
                    System.out.println(jsonPerformances);
                    System.out.println(uuid);
                    signedMessage = app.getEncryptionManager().buildSignedMessage(buyTicketsData);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                JSONObject response = RestServices.POST("/buyTickets", signedMessage);
                try {
                    JSONObject jsonData = response.getJSONObject("data");
                    JSONArray jsonVouchers = jsonData.getJSONArray("vouchers");
                    ArrayList<Voucher> vouchers = FileManager.readVouchers(context);
                    for (int i = 0; i < jsonVouchers.length(); i++) {
                        JSONObject jsonVoucher = jsonVouchers.getJSONObject(i);
                        String id = jsonVoucher.getString("id");
                        String productCode = jsonVoucher.getString("productCode");
                        String state = jsonVoucher.getString("state");
                        Voucher v = new Voucher(id, productCode, state);
                        vouchers.add(v);
                    }
                    JSONArray jsonTickets = jsonData.getJSONArray("tickets");
                    ArrayList<Ticket> tickets = FileManager.readTickets(context);
                    for (int i = 0; i < jsonTickets.length(); i++) {
                        JSONObject jsonTicket = jsonTickets.getJSONObject(i);
                        String id = jsonTicket.getString("id");
                        String performanceId = jsonTicket.getString("performanceId");
                        String showName = jsonTicket.getString("name");
                        MyDate date = new MyDate(jsonTicket.getString("date"));
                        String roomPlace = jsonTicket.getString("place");
                        String state = jsonTicket.getString("state");
                        Ticket t = new Ticket(id, performanceId, showName, date, roomPlace, state);
                        tickets.add(t);
                    }
                    FileManager.writeVouchers(context, vouchers);
                    FileManager.writeTickets(context, tickets);
                } catch (JSONException e) {
                    try {
                        Toast.makeText(context, response.getString("error"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        Toast.makeText(context, e1.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                Intent i = new Intent(context, MainActivity.class);
                startActivity(i);
                finish();
                return true;
            default:
                return false;
        }
    }
}
