package pt.up.fe.up201405729.cmov1.customerapp.Cafeteria;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

import pt.up.fe.up201405729.cmov1.customerapp.CustomerApp;
import pt.up.fe.up201405729.cmov1.customerapp.FileManager;
import pt.up.fe.up201405729.cmov1.customerapp.NavigableActivity;
import pt.up.fe.up201405729.cmov1.customerapp.Product;
import pt.up.fe.up201405729.cmov1.customerapp.R;
import pt.up.fe.up201405729.cmov1.customerapp.ShowQRCodeActivity;
import pt.up.fe.up201405729.cmov1.customerapp.Voucher;

import static pt.up.fe.up201405729.cmov1.sharedlibrary.Shared.qrCodeContentDelimiter;

public class AddVouchersActivity extends NavigableActivity {
    private AddVouchersActivityRVAdapter addVouchersActivityRVAdapter;
    private CheckoutProducts checkoutProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vouchers);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle("Add vouchers");
        }

        Intent i = getIntent();
        checkoutProducts = (CheckoutProducts) i.getSerializableExtra(CustomerApp.cafeteriaSelectedProductsKeyName);

        Context context = this;
        RecyclerView addVouchersRV = findViewById(R.id.addVouchersRV);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        addVouchersRV.setLayoutManager(gridLayoutManager);
        ArrayList<Voucher> allVouchers = FileManager.readVouchers(context);
        ArrayList<Voucher> vouchers = new ArrayList<>();
        for (Voucher v : allVouchers)
            if (v.getState().equals(Voucher.State.notUsed))
                vouchers.add(v);
        addVouchersActivityRVAdapter = new AddVouchersActivityRVAdapter(vouchers, context);
        addVouchersRV.setAdapter(addVouchersActivityRVAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_cafeteria_add_vouchers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Context context = this;
        if (item.getItemId() == R.id.cafeteriaAddVouchersActivityContinueButton) {
            HashSet<Voucher> vouchers = addVouchersActivityRVAdapter.getSelectedVouchers();
            if (vouchers.size() > 2)
                Toast.makeText(context, "You should select at most two vouchers.", Toast.LENGTH_LONG).show();
            else {
                String qrCodeContent = generateQRCodeContent();
                updateStoredVouchers();
                Intent i = new Intent(context, ShowQRCodeActivity.class);
                i.putExtra(CustomerApp.qrCodeContentKeyName, qrCodeContent);
                startActivity(i);
                finish();
            }
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CustomerApp.addVouchersActivityRVAdapterKeyName, addVouchersActivityRVAdapter);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        addVouchersActivityRVAdapter = (AddVouchersActivityRVAdapter) savedInstanceState.get(CustomerApp.addVouchersActivityRVAdapterKeyName);
        ((RecyclerView) findViewById(R.id.selectProductsRV)).setAdapter(addVouchersActivityRVAdapter);
    }

    private String generateQRCodeContent() {
        SharedPreferences preferences = getSharedPreferences(CustomerApp.sharedPreferencesKeyName, Context.MODE_PRIVATE);
        String uuid = preferences.getString("uuid", null);
        StringBuilder sb = new StringBuilder();
        sb.append(uuid);
        for (Product p : checkoutProducts.getProducts()) {
            sb.append(qrCodeContentDelimiter).append(p.getName());
            sb.append(qrCodeContentDelimiter).append(p.getQuantity());
        }
        for (Voucher v : addVouchersActivityRVAdapter.getSelectedVouchers())
            sb.append(qrCodeContentDelimiter).append(v.getUuid());

        // TODO: sign data
        return sb.toString();
    }

    private void updateStoredVouchers() {
        ArrayList<Voucher> vouchers = new ArrayList<>();
        HashSet<Voucher> selectedVouchers = addVouchersActivityRVAdapter.getSelectedVouchers();
        for (Voucher v : addVouchersActivityRVAdapter.getVouchers())
            if (!selectedVouchers.contains(v))
                vouchers.add(v);
        FileManager.writeVouchers(this, vouchers);
    }
}
