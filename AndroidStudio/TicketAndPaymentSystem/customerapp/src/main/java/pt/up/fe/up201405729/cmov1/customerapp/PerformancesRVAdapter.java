package pt.up.fe.up201405729.cmov1.customerapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class PerformancesRVAdapter extends RecyclerView.Adapter<PerformancesRVAdapter.MyViewHolder> {
    private ArrayList<Performance> performances;

    public PerformancesRVAdapter(ArrayList<Performance> performances) {
        this.performances = performances;
    }

    @NonNull
    @Override
    public PerformancesRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new MyViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Performance p = performances.get(position);
        ((TextView) holder.linearLayout.findViewById(R.id.performanceName)).setText(p.getName());
        ((TextView) holder.linearLayout.findViewById(R.id.performanceDate)).setText(p.getDate().toString());
        ((TextView) holder.linearLayout.findViewById(R.id.performancePrice)).setText(String.format(Locale.US, "€%.2f", p.getPrice()));
    }

    @Override
    public int getItemCount() {
        return performances.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;

        public MyViewHolder(LinearLayout linearLayout) {
            super(linearLayout);
            this.linearLayout = linearLayout;
        }
    }
}
