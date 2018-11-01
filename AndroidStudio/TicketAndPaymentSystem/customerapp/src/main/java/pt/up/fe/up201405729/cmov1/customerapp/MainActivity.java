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

import java.util.ArrayList;
import java.util.Date;

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
            ArrayList<Performance> performances = new ArrayList<>(); // query database
            for (int i = 1; i <= 100; i++)
                performances.add(new Performance("Test performance " + i, new Date(), 1.0));
            performancesRVAdapter = new PerformancesRVAdapter(performances);
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
        if (item.getItemId() == R.id.mainActivityBuyButton) {
            ArrayList<Ticket> tickets = new ArrayList<>();
            // add tickets
            CheckoutData checkoutData = new CheckoutData(tickets);
            Context packageContext = this;
            Intent i = new Intent(packageContext, CheckoutActivity.class);
            i.putExtra(CheckoutActivity.checkoutDataKeyName, checkoutData);
            startActivity(i);
            finish();
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
