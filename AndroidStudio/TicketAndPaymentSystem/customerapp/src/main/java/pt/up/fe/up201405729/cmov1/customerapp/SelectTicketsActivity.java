package pt.up.fe.up201405729.cmov1.customerapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

import static pt.up.fe.up201405729.cmov1.sharedlibrary.Shared.qrCodeContentDelimiter;

public class SelectTicketsActivity extends AppCompatActivity {
    private ArrayList<Ticket> ticketsBought;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle("Select tickets");
        }

        ticketsBought = new ArrayList<>();
        // Get tickets bought from the database
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_select_tickets_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Context packageContext = this;
        if (item.getItemId() == R.id.selectTicketsActivityFinishButton) {
            String qrCodeContent = generateQRCodeContent();
            Intent i = new Intent(packageContext, ShowQRCodeActivity.class);
            i.putExtra(ShowQRCodeActivity.qrCodeContentKeyName, qrCodeContent);
            startActivity(i);
            finish();
        }
        return (super.onOptionsItemSelected(item));
    }

    private String generateQRCodeContent() {
        SharedPreferences preferences = getSharedPreferences(MainActivity.sharedPreferencesKeyName, Context.MODE_PRIVATE);
        String uuid = preferences.getString("uuid", null);
        StringBuilder sb = new StringBuilder();
        sb.append(uuid);
        for (Ticket t : ticketsBought)
            sb.append(qrCodeContentDelimiter).append(t.getUuid());
        return sb.toString();
    }
}
