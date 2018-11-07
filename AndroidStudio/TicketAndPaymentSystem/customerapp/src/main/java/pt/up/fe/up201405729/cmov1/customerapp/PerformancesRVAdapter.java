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

public class PerformancesRVAdapter extends RecyclerView.Adapter<PerformancesRVAdapter.MyViewHolder> implements Serializable {
    private ArrayList<Performance> performances;
    private ArrayList<Integer> ticketsQuantities;
    private boolean areQuantitiesEditable;

    public PerformancesRVAdapter(ArrayList<Performance> performances, ArrayList<Integer> ticketsQuantities, boolean areQuantitiesEditable) {
        if (performances.size() != ticketsQuantities.size())
            throw new IllegalArgumentException("performances.size() should be equal to ticketsQuantities.size()");
        this.performances = performances;
        this.ticketsQuantities = ticketsQuantities;
        this.areQuantitiesEditable = areQuantitiesEditable;
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
        ((TextView) holder.linearLayout.findViewById(R.id.performanceDate)).setText(p.getDate().getHumanReadableDate());
        ((TextView) holder.linearLayout.findViewById(R.id.performancePrice)).setText(StringFormat.formatAsPrice(p.getPrice()));
        EditText editText = holder.linearLayout.findViewById(R.id.performanceTicketsQuantityET);
        editText.setText(StringFormat.formatAsInteger(ticketsQuantities.get(position)));
        if (!areQuantitiesEditable) {
            editText.setEnabled(false);
            editText.setBackground(null);
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Integer value;
                try {
                    value = Integer.parseInt(s.toString());
                } catch (NumberFormatException e) {
                    value = 0;
                }
                ticketsQuantities.set(holder.getAdapterPosition(), value);
            }
        });
    }

    @Override
    public int getItemCount() {
        return performances.size();
    }

    public ArrayList<Performance> getPerformances() {
        return performances;
    }

    public ArrayList<Integer> getTicketsQuantities() {
        return ticketsQuantities;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;

        public MyViewHolder(LinearLayout linearLayout) {
            super(linearLayout);
            this.linearLayout = linearLayout;
        }
    }
}
