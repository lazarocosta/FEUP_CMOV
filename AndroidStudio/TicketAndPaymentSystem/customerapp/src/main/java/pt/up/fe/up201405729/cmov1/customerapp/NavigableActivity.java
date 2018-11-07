package pt.up.fe.up201405729.cmov1.customerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class NavigableActivity extends AppCompatActivity {
    private NavigationView navigationView;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        final Context packageContext = this;
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent i = null;
                if (menuItem.getTitle().equals(getResources().getString(R.string.main_activity_title)))
                    i = new Intent(packageContext, MainActivity.class);
                else if (menuItem.getTitle().equals(getResources().getString(R.string.select_tickets_activity_title)))
                    i = new Intent(packageContext, SelectTicketsActivity.class);
                else if (menuItem.getTitle().equals(getResources().getString(R.string.list_transactions_activity_title)))
                    i = new Intent(packageContext, ListTransactionsActivity.class);
                else
                    System.err.println("This statement should not be reached.");
                if (i != null && !menuItem.isChecked())
                    startActivity(i);
                return true;
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Class c = getClass();
        if (c.equals(MainActivity.class))
            navigationView.setCheckedItem(R.id.mainActivityNavButton);
        else if (c.equals(SelectTicketsActivity.class))
            navigationView.setCheckedItem(R.id.selectTicketsActivityNavButton);
        else if (c.equals(ListTransactionsActivity.class))
            navigationView.setCheckedItem(R.id.listTransactionsActivityNavButton);
        else
            System.err.println("This statement should not be reached.");
    }
}
