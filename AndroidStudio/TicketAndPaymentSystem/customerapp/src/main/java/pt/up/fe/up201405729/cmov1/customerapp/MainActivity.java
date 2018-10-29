package pt.up.fe.up201405729.cmov1.customerapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static final String sharedPreferencesKeyName = "pt.up.fe.up201405729.cmov1.customerapp.prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       SharedPreferences preferences = getSharedPreferences(sharedPreferencesKeyName, Context.MODE_PRIVATE);
       String uuid = preferences.getString("uuid", null);
       final Context packageContext = this;
       if (uuid == null){
           Intent i = new Intent(packageContext, RegistrationActivity.class);
           startActivity(i);
           finish();
       }
       else {
           RecyclerView performancesRV = findViewById(R.id.performancesRV);
           LinearLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
           performancesRV.setLayoutManager(gridLayoutManager);
           ArrayList<Performance> performances = new ArrayList<>(); // query database
           for (int i = 1; i <= 100; i++)
               performances.add(new Performance("Test performance " + i, new ArrayList<Date>(), 1));
           PerformancesRVAdapter performancesRVAdapter = new PerformancesRVAdapter(performances);
           performancesRV.setAdapter(performancesRVAdapter);
           performancesRV.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {    // not being called
                   ArrayList<Ticket> tickets = new ArrayList<>();
                   // add tickets
                   CheckoutData checkoutData = new CheckoutData(tickets);
                   Intent i = new Intent(packageContext, CheckoutActivity.class);
                   i.putExtra(CheckoutActivity.checkoutDataKeyName, checkoutData);
                   startActivity(i);
                   finish();
               }
           });
       }
    }
}
