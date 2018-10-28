package pt.up.fe.up201405729.cmov1.customerapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class PerformancesRVAdapter extends RecyclerView.Adapter<PerformancesRVAdapter.MyViewHolder> {
    private ArrayList<Performance> performances;

    public PerformancesRVAdapter(ArrayList<Performance> performances) {
        this.performances = performances;
    }

    @NonNull
    @Override
    public PerformancesRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mTextView.setText(performances.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return performances.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public MyViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }
}
