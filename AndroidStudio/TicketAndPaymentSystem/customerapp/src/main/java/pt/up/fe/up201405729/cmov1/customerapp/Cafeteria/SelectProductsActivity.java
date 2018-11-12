package pt.up.fe.up201405729.cmov1.customerapp.Cafeteria;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import pt.up.fe.up201405729.cmov1.customerapp.CustomerApp;
import pt.up.fe.up201405729.cmov1.customerapp.NavigableActivity;
import pt.up.fe.up201405729.cmov1.customerapp.Product;
import pt.up.fe.up201405729.cmov1.customerapp.R;

public class SelectProductsActivity extends NavigableActivity {
    private SelectProductsRVAdapter selectProductsRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_products);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(R.string.select_products_activity_title);
        }

        RecyclerView productsRV = findViewById(R.id.selectProductsRV);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        productsRV.setLayoutManager(gridLayoutManager);
        ArrayList<Product> products = new ArrayList<>();
        for (Product.Products p : Product.Products.values())
            products.add(new Product(p.name(), 1, 1));  // TODO: adjust data
        selectProductsRVAdapter = new SelectProductsRVAdapter(products);
        productsRV.setAdapter(selectProductsRVAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_cafeteria_select_products, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Context packageContext = this;
        if (item.getItemId() == R.id.cafeteriaSelectProductsActivityBuyButton) {
            ArrayList<Product> allProducts = selectProductsRVAdapter.getProducts();
            ArrayList<Product> desiredProducts = new ArrayList<>();
            for (int i = 0; i < allProducts.size(); i++) {
                Product p = allProducts.get(i);
                if (p.getQuantity() > 0)
                    desiredProducts.add(p);
            }
            if (desiredProducts.isEmpty())
                Toast.makeText(packageContext, "You should select at least one product.", Toast.LENGTH_LONG).show();
            else {
                Intent i = new Intent(packageContext, AddVouchersActivity.class);
                i.putExtra(CustomerApp.cafeteriaSelectedProductsKeyName, desiredProducts);
                startActivity(i);
                finish();
            }
        }
        return (super.onOptionsItemSelected(item));
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
