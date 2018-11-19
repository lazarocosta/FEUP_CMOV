package pt.up.fe.up201405729.cmov1.customerapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

import pt.up.fe.up201405729.cmov1.sharedlibrary.KeyStoreManager;

import static pt.up.fe.up201405729.cmov1.sharedlibrary.QRCodeReaderActivity.qrCodeContentDataDelimiter;
import static pt.up.fe.up201405729.cmov1.sharedlibrary.QRCodeReaderActivity.qrCodeContentDataTypeDelimiter;

public class SelectTicketsActivity extends NavigableActivity implements Toolbar.OnMenuItemClickListener {
    private final Context context = this;
    private ArrayList<Ticket> allTickets;
    private SelectTicketsRVAdapter selectTicketsRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tickets);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.select_tickets_activity_title);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(this);
        ActionMenuItemView actionMenuItemView = findViewById(R.id.toolbar_button);
        actionMenuItemView.setText(R.string.finish_string);

        allTickets = FileManager.readTickets(context);
        ArrayList<Ticket> selectableTickets = new ArrayList<>();
        for (Ticket t : allTickets)
            if (t.getState() == Ticket.State.notUsed)
                selectableTickets.add(t);

        RecyclerView selectTicketsRV = findViewById(R.id.selectTicketsRV);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        selectTicketsRV.setLayoutManager(gridLayoutManager);
        selectTicketsRVAdapter = new SelectTicketsRVAdapter(selectableTickets, context);
        selectTicketsRV.setAdapter(selectTicketsRVAdapter);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.toolbar_button:
                if (selectTicketsRVAdapter.getSelectedTickets().size() > 0) {
                    String qrCodeContent = generateQRCodeContent();
                    markSelectedTicketsAsUsed();
                    Intent i = new Intent(context, ShowQRCodeActivity.class);
                    i.putExtra(CustomerApp.qrCodeContentKeyName, qrCodeContent);
                    startActivity(i);
                    finish();
                } else
                    Toast.makeText(context, "No ticket selected.", Toast.LENGTH_LONG).show();
                return true;
            default:
                return false;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CustomerApp.selectTicketsActivityRVAdapterKeyName, selectTicketsRVAdapter);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectTicketsRVAdapter = (SelectTicketsRVAdapter) savedInstanceState.get(CustomerApp.selectProductsActivityRVAdapterKeyName);
        ((RecyclerView) findViewById(R.id.selectTicketsRV)).setAdapter(selectTicketsRVAdapter);
    }

    private String generateQRCodeContent() {
        SharedPreferences preferences = getSharedPreferences(CustomerApp.sharedPreferencesKeyName, Context.MODE_PRIVATE);
        String uuid = preferences.getString("uuid", null);
        StringBuilder sb = new StringBuilder();
        sb.append(uuid);
        if (selectTicketsRVAdapter.getSelectedTickets().size() > 0) {
            sb.append(qrCodeContentDataTypeDelimiter);
            for (Ticket t : selectTicketsRVAdapter.getSelectedTickets())
                sb.append(t.getUuid()).append(qrCodeContentDataDelimiter);
            sb.deleteCharAt(sb.length() - 1);
        }
        return KeyStoreManager.toBase64(sb.toString().getBytes());
    }

    private void markSelectedTicketsAsUsed() {
        HashSet<Ticket> selectedTickets = selectTicketsRVAdapter.getSelectedTickets();
        for (Ticket t : allTickets)
            if (selectedTickets.contains(t))
                t.setState(Ticket.State.used);
        FileManager.writeTickets(context, allTickets);
    }
}
