package pt.up.fe.up201405729.cmov1.cafeteriaapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.up.fe.up201405729.cmov1.restservices.RestServices;
import pt.up.fe.up201405729.cmov1.sharedlibrary.Product;
import pt.up.fe.up201405729.cmov1.sharedlibrary.Voucher;

public class ResponseActivity extends AppCompatActivity {
    private final Context context = this;
    private ArrayList<Product> listProducts;
    private ArrayList<Voucher> listVouchers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);


        ((TextView) findViewById(R.id.totalPrice)).setText("totalprice a preencher da resposta");
        ((TextView) findViewById(R.id.orderNumber)).setText("order number a preencher da resposta");


        final RecyclerView productRV = findViewById(R.id.productsRecyclerView);
        final GridLayoutManager gridLayoutManagerProduct = new GridLayoutManager(context, 1);
        final ProductsRVAdapter productsRVAdapter = new ProductsRVAdapter(listProducts);
        productRV.setLayoutManager(gridLayoutManagerProduct);
        productRV.setAdapter(productsRVAdapter);

        final RecyclerView voucherRV = findViewById(R.id.vouchersRecyclerView);
        final GridLayoutManager gridLayoutManagerVouchers = new GridLayoutManager(context, 1);
        final VouchersRVAdapter vouchersRVAdapter = new VouchersRVAdapter(listVouchers);
        voucherRV.setLayoutManager(gridLayoutManagerVouchers);
        voucherRV.setAdapter(vouchersRVAdapter);
    }
}
