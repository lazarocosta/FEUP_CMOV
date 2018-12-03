package pt.up.fe.up201405729.cmov1.customerapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.up.fe.up201405729.cmov1.restservices.RestServices;
import pt.up.fe.up201405729.cmov1.sharedlibrary.MyDate;
import pt.up.fe.up201405729.cmov1.sharedlibrary.Product;
import pt.up.fe.up201405729.cmov1.sharedlibrary.Voucher;

public class ListTransactionsActivity extends NavigableActivity {
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_transactions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.list_transactions_activity_title);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        ActionMenuItemView actionMenuItemView = findViewById(R.id.toolbar_button);
        actionMenuItemView.setText("");
        actionMenuItemView.setEnabled(false);

        SharedPreferences preferences = getSharedPreferences(CustomerApp.sharedPreferencesKeyName, Context.MODE_PRIVATE);
        String uuid = preferences.getString("uuid", null);

        try {
            JSONObject transactions = new JSONObject();
            transactions.put("userId", uuid);

            JSONObject response = RestServices.POST("/listTransactionsUser", transactions);
            ArrayList<Ticket> listTickets = new ArrayList<>();
            ArrayList<Product> listProducts = new ArrayList<>();
            ArrayList<Voucher> listVouchers = new ArrayList<>();
            if (response.has("data")) {
                JSONObject data = response.getJSONObject("data");
                JSONArray vouchers = data.getJSONArray("vouchers");
                JSONArray tickets = data.getJSONArray("tickets");
                JSONArray products = data.getJSONArray("products");

                for (int i = 0; i < tickets.length(); i++) {
                    JSONObject jsonObject = tickets.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String performanceId = jsonObject.getString("performanceId");
                    String name = jsonObject.getString("name");
                    MyDate date = new MyDate(jsonObject.getString("date"));
                    String roomPlace = jsonObject.getString("place");
                    String state = jsonObject.getString("state");
                    listTickets.add(new Ticket(id, performanceId, name, date, roomPlace, state));
                }

                for (int i = 0; i < products.length(); i++) {
                    JSONObject jsonObject = products.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String name = jsonObject.getString("nameProduct");
                    int price = jsonObject.getInt("priceProduct");
                    int quantity = jsonObject.getInt("quantity");
                    listProducts.add(new Product(id, name, price, quantity));
                }

                for (int i = 0; i < vouchers.length(); i++) {
                    JSONObject jsonObject = vouchers.getJSONObject(i);
                    String productCode = jsonObject.getString("productCode");
                    String id = jsonObject.getString("id");
                    String state = jsonObject.getString("state");
                    listVouchers.add(new Voucher(id, productCode, state));
                }

                FileManager.writeTickets(context, listTickets);
                FileManager.writeVouchers(context, listVouchers);
            } else
                Toast.makeText(context, response.getString("error"), Toast.LENGTH_LONG).show();

            RecyclerView ticketsRV = findViewById(R.id.ticketsRecyclerView);
            GridLayoutManager gridLayoutManagerTickets = new GridLayoutManager(context, 1);
            TicketsRVAdapter ticketsRVAdapter = new TicketsRVAdapter(listTickets);
            ticketsRV.setLayoutManager(gridLayoutManagerTickets);
            ticketsRV.setAdapter(ticketsRVAdapter);

            RecyclerView productRV = findViewById(R.id.productsRecyclerView);
            GridLayoutManager gridLayoutManagerProduct = new GridLayoutManager(context, 1);
            ProductsRVAdapter productsRVAdapter = new ProductsRVAdapter(listProducts);
            productRV.setLayoutManager(gridLayoutManagerProduct);
            productRV.setAdapter(productsRVAdapter);

            RecyclerView voucherRV = findViewById(R.id.vouchersRecyclerView);
            GridLayoutManager gridLayoutManagerVouchers = new GridLayoutManager(context, 1);
            VouchersRVAdapter vouchersRVAdapter = new VouchersRVAdapter(listVouchers);
            voucherRV.setLayoutManager(gridLayoutManagerVouchers);
            voucherRV.setAdapter(vouchersRVAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
