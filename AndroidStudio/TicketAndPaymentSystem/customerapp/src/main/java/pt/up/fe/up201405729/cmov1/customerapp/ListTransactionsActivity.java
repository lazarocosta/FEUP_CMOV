package pt.up.fe.up201405729.cmov1.customerapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import pt.up.fe.up201405729.cmov1.restservices.RestServices;

public class ListTransactionsActivity extends AppCompatActivity {
    private ArrayList<Ticket> listTickets;
    private ArrayList<Voucher> listVouchers;
    private ArrayList<Product> listProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_transactions);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle("Transactions");
        }

        SharedPreferences preferences = getSharedPreferences(CustomerApp.sharedPreferencesKeyName, Context.MODE_PRIVATE);
        String uuid = preferences.getString("uuid", null);
        final Context packageContext = this;

        JSONObject transactions = new JSONObject();
        try {
            transactions.put("userId", uuid);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(packageContext, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        JSONObject response = RestServices.PUT("/listTransactionsUser ", transactions);
        listTickets = new ArrayList<>();
        listProducts = new ArrayList<>();
        listVouchers = new ArrayList<>();
        System.out.println(response);
        try {
            JSONObject data = response.getJSONObject("data");
            JSONArray vouchers = data.getJSONArray("vouchers");
            JSONArray tickets = data.getJSONArray("tickets");
            JSONArray products = data.getJSONArray("products");
            System.out.println(products);
            System.out.println(tickets);
            System.out.println(vouchers);
            for (int i = 0; i < tickets.length(); i++) {
                JSONObject jsonObject = tickets.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                MyDate date = new MyDate(jsonObject.getString("date"));
                String roomPlace = jsonObject.getString("place");
                listTickets.add(new Ticket(id, name, date, roomPlace));
            }

            for (int i = 0; i < products.length(); i++) {
                JSONObject jsonObject = products.getJSONObject(i);
                String name = jsonObject.getString("nameProduct");
                int price = jsonObject.getInt("priceProduct");
                int quantity = jsonObject.getInt("quantity");
                listProducts.add(new Product(name, price, quantity));
            }

            for (int i = 0; i < vouchers.length(); i++) {
                JSONObject jsonObject = vouchers.getJSONObject(i);
                String productCode = jsonObject.getString("productCode");
                String id = jsonObject.getString("id");
                listVouchers.add(new Voucher(id,productCode));
            }
        }catch (JSONException e) {
            System.out.println("erro");
            try {
                Toast.makeText(packageContext, response.getString("error"), Toast.LENGTH_LONG).show();
            } catch (JSONException e1) {
                e1.printStackTrace();
                Toast.makeText(packageContext, e1.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        RecyclerView tickets = findViewById(R.id.ticketsRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        tickets.setLayoutManager(gridLayoutManager);
    }
}
