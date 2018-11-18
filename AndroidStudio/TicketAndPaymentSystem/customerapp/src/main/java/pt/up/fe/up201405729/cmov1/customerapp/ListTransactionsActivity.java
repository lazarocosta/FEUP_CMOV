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

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences(CustomerApp.sharedPreferencesKeyName, Context.MODE_PRIVATE);
                String uuid = preferences.getString("uuid", null);

                try {
                    JSONObject transactions = new JSONObject();
                    transactions.put("userId", uuid);

                    JSONObject response = RestServices.PUT("/listTransactionsUser", transactions);
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
                    } else {
                        final String error = response.getString("error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    final RecyclerView ticketsRV = findViewById(R.id.ticketsRecyclerView);
                    final GridLayoutManager gridLayoutManagerTickets = new GridLayoutManager(context, 1);
                    final TicketsRVAdapter ticketsRVAdapter = new TicketsRVAdapter(listTickets);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ticketsRV.setLayoutManager(gridLayoutManagerTickets);
                            ticketsRV.setAdapter(ticketsRVAdapter);
                        }
                    });

                    final RecyclerView productRV = findViewById(R.id.productsRecyclerView);
                    final GridLayoutManager gridLayoutManagerProduct = new GridLayoutManager(context, 1);
                    final ProductsRVAdapter productsRVAdapter = new ProductsRVAdapter(listProducts);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            productRV.setLayoutManager(gridLayoutManagerProduct);
                            productRV.setAdapter(productsRVAdapter);
                        }
                    });

                    final RecyclerView voucherRV = findViewById(R.id.vouchersRecyclerView);
                    final GridLayoutManager gridLayoutManagerVouchers = new GridLayoutManager(context, 1);
                    final VouchersRVAdapter vouchersRVAdapter = new VouchersRVAdapter(listVouchers);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            voucherRV.setLayoutManager(gridLayoutManagerVouchers);
                            voucherRV.setAdapter(vouchersRVAdapter);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    final String exceptionMessage = e.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, exceptionMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }
}
