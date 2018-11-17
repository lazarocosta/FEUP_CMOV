package pt.up.fe.up201405729.cmov1.customerapp.Cafeteria;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import pt.up.fe.up201405729.cmov1.customerapp.CustomerApp;
import pt.up.fe.up201405729.cmov1.customerapp.NavigableActivity;
import pt.up.fe.up201405729.cmov1.customerapp.Product;
import pt.up.fe.up201405729.cmov1.customerapp.R;

public class SelectProductsActivity extends NavigableActivity implements Toolbar.OnMenuItemClickListener {
    private SelectProductsRVAdapter selectProductsRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_products);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.select_products_activity_title);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(this);
        ActionMenuItemView actionMenuItemView = findViewById(R.id.toolbar_button);
        actionMenuItemView.setText(R.string.buy_string);

        Context context = this;
        RecyclerView productsRV = findViewById(R.id.selectProductsRV);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        productsRV.setLayoutManager(gridLayoutManager);
        ArrayList<Product> products = new ArrayList<>();
        Collections.addAll(products, Product.products);
        selectProductsRVAdapter = new SelectProductsRVAdapter(products, context);
        productsRV.setAdapter(selectProductsRVAdapter);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        Context context = this;
        switch (menuItem.getItemId()) {
            case R.id.toolbar_button:
                ArrayList<Product> allProducts = selectProductsRVAdapter.getProducts();
                ArrayList<Product> desiredProducts = new ArrayList<>();
                for (int i = 0; i < allProducts.size(); i++) {
                    Product p = allProducts.get(i);
                    if (p.getQuantity() > 0)
                        desiredProducts.add(p);
                }
                if (desiredProducts.isEmpty())
                    Toast.makeText(context, "You should select at least one product.", Toast.LENGTH_LONG).show();
                else {
                    Intent i = new Intent(context, AddVouchersActivity.class);
                    i.putExtra(CustomerApp.cafeteriaSelectedProductsKeyName, new CheckoutProducts(desiredProducts));
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
        outState.putSerializable(CustomerApp.selectProductsActivityRVAdapterKeyName, selectProductsRVAdapter);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectProductsRVAdapter = (SelectProductsRVAdapter) savedInstanceState.get(CustomerApp.selectProductsActivityRVAdapterKeyName);
        ((RecyclerView) findViewById(R.id.selectProductsRV)).setAdapter(selectProductsRVAdapter);
    }
}
