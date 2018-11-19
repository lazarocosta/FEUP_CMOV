package pt.up.fe.up201405729.cmov1.cafeteriaapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.up.fe.up201405729.cmov1.sharedlibrary.Product;
import pt.up.fe.up201405729.cmov1.sharedlibrary.Voucher;

public class ResponseActivity extends AppCompatActivity {
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle("Order");
            bar.hide();
        }

        Intent i = getIntent();
        String orderStr = i.getStringExtra("Order");
        ArrayList<Product> listProducts = new ArrayList<>();
        ArrayList<Voucher> listVouchers = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(orderStr);
            if (response.has("data")) {
                JSONObject data = response.getJSONObject("data");
                JSONArray vouchers = data.getJSONArray("vouchers");
                JSONArray products = data.getJSONArray("productsPurchased");

                for (int j = 0; j < products.length(); j++) {
                    JSONObject jsonObject = products.getJSONObject(j);
                    String id = jsonObject.getString("id");
                    String name = jsonObject.getString("nameProduct");
                    int price = jsonObject.getInt("priceProduct");
                    int quantity = jsonObject.getInt("quantity");
                    listProducts.add(new Product(id, name, price, quantity));
                }

                for (int j = 0; j < vouchers.length(); j++) {
                    JSONObject jsonObject = vouchers.getJSONObject(j);
                    String productCode = jsonObject.getString("productCode");
                    String id = jsonObject.getString("id");
                    String state = jsonObject.getString("state");
                    listVouchers.add(new Voucher(id, productCode, state));
                }
            }

            ((TextView) findViewById(R.id.totalPrice)).setText(response.getString("valueSpend"));
            ((TextView) findViewById(R.id.orderNumber)).setText(response.getString("number"));
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

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
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(context, MainActivity.class);
        startActivity(i);
        finish();
    }
}
