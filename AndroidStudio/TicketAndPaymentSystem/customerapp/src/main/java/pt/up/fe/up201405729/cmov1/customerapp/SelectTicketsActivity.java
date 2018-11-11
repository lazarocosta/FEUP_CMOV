package pt.up.fe.up201405729.cmov1.customerapp;

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

import static pt.up.fe.up201405729.cmov1.sharedlibrary.Shared.qrCodeContentDelimiter;

// TODO: better interface specially for landscape
public class SelectTicketsActivity extends NavigableActivity {
    private ArrayList<Ticket> allTickets;
    private SelectTicketsRVAdapter selectTicketsRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tickets);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(R.string.select_tickets_activity_title);
        }

        allTickets = FileManager.readTickets(this);
        ArrayList<Ticket> selectableTickets = new ArrayList<>();
        for (Ticket t : allTickets)
            if (t.getState() == Ticket.State.notUsed)
                selectableTickets.add(t);

        RecyclerView selectTicketsRV = findViewById(R.id.selectTicketsRV);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        selectTicketsRV.setLayoutManager(gridLayoutManager);
        selectTicketsRVAdapter = new SelectTicketsRVAdapter(selectableTickets, this);
        selectTicketsRV.setAdapter(selectTicketsRVAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_select_tickets_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.selectTicketsActivityFinishButton) {
            Intent i;
            if (selectTicketsRVAdapter.getSelectedTickets().size() > 0) {
                String qrCodeContent = generateQRCodeContent();
                markSelectedTicketsAsUsed();
                i = new Intent(this, ShowQRCodeActivity.class);
                i.putExtra(CustomerApp.qrCodeContentKeyName, qrCodeContent);
            } else {
                Toast.makeText(this, "No ticket selected.", Toast.LENGTH_LONG).show();
                i = new Intent(this, MainActivity.class);
            }
            startActivity(i);
            finish();
        }
        return (super.onOptionsItemSelected(item));
    }

    private String generateQRCodeContent() {
        SharedPreferences preferences = getSharedPreferences(CustomerApp.sharedPreferencesKeyName, Context.MODE_PRIVATE);
        String uuid = preferences.getString("uuid", null);
        StringBuilder sb = new StringBuilder();
        sb.append(uuid);
        for (Ticket t : selectTicketsRVAdapter.getSelectedTickets())
            sb.append(qrCodeContentDelimiter).append(t.getUuid());
        return sb.toString();
    }

    private void markSelectedTicketsAsUsed() {
        HashSet<Ticket> selectedTickets = selectTicketsRVAdapter.getSelectedTickets();
        for (Ticket t : allTickets)
            if (selectedTickets.contains(t))
                t.setState(Ticket.State.used);
        FileManager.writeTickets(this, allTickets);
    }
}
