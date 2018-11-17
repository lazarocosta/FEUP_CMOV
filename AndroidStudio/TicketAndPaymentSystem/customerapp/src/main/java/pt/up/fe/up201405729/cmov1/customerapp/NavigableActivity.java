package pt.up.fe.up201405729.cmov1.customerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.HashMap;

import pt.up.fe.up201405729.cmov1.customerapp.Cafeteria.SelectProductsActivity;

public abstract class NavigableActivity extends AppCompatActivity {
    private static Integer checkedItem = null;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        final Context context = this;
        final Class myActivity = getClass();
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                final HashMap<String, Class> selectableActivities = new HashMap<>();
                selectableActivities.put(getResources().getString(R.string.main_activity_title), MainActivity.class);
                selectableActivities.put(getResources().getString(R.string.select_tickets_activity_title), SelectTicketsActivity.class);
                selectableActivities.put(getResources().getString(R.string.list_transactions_activity_title), ListTransactionsActivity.class);
                selectableActivities.put(getResources().getString(R.string.select_products_activity_title), SelectProductsActivity.class);

                Class selectedActivity = selectableActivities.get(menuItem.getTitle().toString());
                if (selectedActivity == null)
                    throw new IllegalArgumentException("This statement should not be reached.");
                else if (!myActivity.equals(selectedActivity)) {
                    Intent i = new Intent(context, selectedActivity);
                    startActivity(i);
                }
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
        else if (c.equals(SelectProductsActivity.class))
            navigationView.setCheckedItem(R.id.selectProductsActivityNavButton);
        else if (checkedItem != null)
            navigationView.setCheckedItem(checkedItem);
        MenuItem menuItem = navigationView.getCheckedItem();
        if (menuItem != null)
            checkedItem = menuItem.getItemId();
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawerLayout.closeDrawer(navigationView);
    }
}
