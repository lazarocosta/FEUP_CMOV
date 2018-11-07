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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import pt.up.fe.up201405729.cmov1.restservices.RestServices;

public class MainActivity extends AppCompatActivity {
    public static final String sharedPreferencesKeyName = "pt.up.fe.up201405729.cmov1.customerapp.prefs";
    private static final String performancesRVAdapterKeyName = "pt.up.fe.up201405729.cmov1.customerapp.performancesRVAdapter";
    private PerformancesRVAdapter performancesRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle("Dashboard");
        }

        SharedPreferences preferences = getSharedPreferences(sharedPreferencesKeyName, Context.MODE_PRIVATE);
        String uuid = preferences.getString("uuid", null);
        final Context packageContext = this;
        if (uuid == null) {
            Intent i = new Intent(packageContext, RegistrationActivity.class);
            startActivity(i);
            finish();
        } else {
            RecyclerView performancesRV = findViewById(R.id.performancesRV);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
            performancesRV.setLayoutManager(gridLayoutManager);
            ArrayList<Performance> performances = new ArrayList<>();
            JSONObject response = RestServices.GET("/listTickets", new JSONObject());
            try {
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
            } catch (JSONException e) {
                try {
                    Toast.makeText(packageContext, response.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    Toast.makeText(packageContext, e1.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            ArrayList<Integer> ticketsQuantities = new ArrayList<>(Collections.nCopies(performances.size(), 0));
            performancesRVAdapter = new PerformancesRVAdapter(performances, ticketsQuantities, true);
            performancesRV.setAdapter(performancesRVAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Context packageContext = this;
        if (item.getItemId() == R.id.mainActivityBuyButton) {
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
                Toast.makeText(packageContext, "You should select at least one performance.", Toast.LENGTH_LONG).show();
            else {
                CheckoutData checkoutData = new CheckoutData(desiredPerformances, desiredTicketsQuantities);
                Intent i = new Intent(packageContext, CheckoutActivity.class);
                i.putExtra(CheckoutActivity.checkoutDataKeyName, checkoutData);
                startActivity(i);
                finish();
            }
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(performancesRVAdapterKeyName, performancesRVAdapter);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        performancesRVAdapter = (PerformancesRVAdapter) savedInstanceState.get(performancesRVAdapterKeyName);
        ((RecyclerView) findViewById(R.id.performancesRV)).setAdapter(performancesRVAdapter);
    }
}
