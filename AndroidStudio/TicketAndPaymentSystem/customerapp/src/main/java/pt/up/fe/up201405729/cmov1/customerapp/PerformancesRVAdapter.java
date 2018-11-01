package pt.up.fe.up201405729.cmov1.customerapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class PerformancesRVAdapter extends RecyclerView.Adapter<PerformancesRVAdapter.MyViewHolder> implements Serializable {
    private ArrayList<Performance> performances;
    private String[] ticketsQuantities;

    public PerformancesRVAdapter(ArrayList<Performance> performances) {
        this.performances = performances;
        this.ticketsQuantities = new String[performances.size()];
        Arrays.fill(this.ticketsQuantities, "0");
    }

    @NonNull
    @Override
    public PerformancesRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new MyViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Performance p = performances.get(position);
        ((TextView) holder.linearLayout.findViewById(R.id.performanceName)).setText(p.getName());
        ((TextView) holder.linearLayout.findViewById(R.id.performanceDate)).setText(p.getDate().toString());
        ((TextView) holder.linearLayout.findViewById(R.id.performancePrice)).setText(String.format(Locale.US, "€%.2f", p.getPrice()));
        ((EditText) holder.linearLayout.findViewById(R.id.performanceTicketsQuantityET)).setText(ticketsQuantities[position]);
        ((EditText) holder.linearLayout.findViewById(R.id.performanceTicketsQuantityET)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                ticketsQuantities[holder.getAdapterPosition()] = s.toString();
                System.out.println(ticketsQuantities[holder.getAdapterPosition()]);
            }
        });
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
