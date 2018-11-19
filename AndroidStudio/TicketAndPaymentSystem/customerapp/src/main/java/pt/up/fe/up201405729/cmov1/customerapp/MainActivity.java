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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import pt.up.fe.up201405729.cmov1.restservices.RestServices;
import pt.up.fe.up201405729.cmov1.sharedlibrary.MyDate;

public class MainActivity extends NavigableActivity implements Toolbar.OnMenuItemClickListener {
    private final Context context = this;
    private PerformancesRVAdapter performancesRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.main_activity_title);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(this);
        ActionMenuItemView actionMenuItemView = findViewById(R.id.toolbar_button);
        actionMenuItemView.setText(R.string.buy_string);

        RecyclerView performancesRV = findViewById(R.id.performancesRV);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        performancesRV.setLayoutManager(gridLayoutManager);

        SharedPreferences preferences = getSharedPreferences(CustomerApp.sharedPreferencesKeyName, Context.MODE_PRIVATE);
        String uuid = preferences.getString("uuid", null);
        if (uuid == null) {
            Intent i = new Intent(context, RegistrationActivity.class);
            startActivity(i);
            finish();
        } else {
            ArrayList<Performance> performances = new ArrayList<>();
            try {
                JSONObject response = RestServices.GET("/listTickets", new JSONObject());
                if (response.has("data")) {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        MyDate date = new MyDate(jsonObject.getString("date"));
                        Double price = Double.valueOf(jsonObject.getString("price"));
                        performances.add(new Performance(id, name, date, price));
                    }
                    Collections.sort(performances);
                } else
                    Toast.makeText(context, response.getString("error"), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            ArrayList<Integer> ticketsQuantities = new ArrayList<>(Collections.nCopies(performances.size(), 0));
            performancesRVAdapter = new PerformancesRVAdapter(performances, ticketsQuantities, true);
            performancesRV.setAdapter(performancesRVAdapter);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.toolbar_button:
                ArrayList<Performance> allPerformances = performancesRVAdapter.getPerformances();
                ArrayList<Integer> ticketsQuantities = performancesRVAdapter.getTicketsQuantities();
                ArrayList<Performance> desiredPerformances = new ArrayList<>();
                ArrayList<Integer> desiredTicketsQuantities = new ArrayList<>();
                for (int i = 0; i < allPerformances.size(); i++) {
                    Integer quantity = ticketsQuantities.get(i);
                    if (quantity > 0) {
                        desiredPerformances.add(allPerformances.get(i));
                        desiredTicketsQuantities.add(quantity);
                    }
                }
                if (desiredPerformances.isEmpty())
                    Toast.makeText(context, "You should select at least one performance.", Toast.LENGTH_LONG).show();
                else {
                    CheckoutData checkoutData = new CheckoutData(desiredPerformances, desiredTicketsQuantities);
                    Intent i = new Intent(context, CheckoutActivity.class);
                    i.putExtra(CustomerApp.checkoutDataKeyName, checkoutData);
                    startActivity(i);
                    finish();
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CustomerApp.performancesRVAdapterKeyName, performancesRVAdapter);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        performancesRVAdapter = (PerformancesRVAdapter) savedInstanceState.get(CustomerApp.performancesRVAdapterKeyName);
        ((RecyclerView) findViewById(R.id.performancesRV)).setAdapter(performancesRVAdapter);
    }
}
