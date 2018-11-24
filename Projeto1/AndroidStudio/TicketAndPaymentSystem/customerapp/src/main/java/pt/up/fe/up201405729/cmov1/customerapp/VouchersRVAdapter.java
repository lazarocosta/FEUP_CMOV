package pt.up.fe.up201405729.cmov1.customerapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import pt.up.fe.up201405729.cmov1.sharedlibrary.Voucher;

public class VouchersRVAdapter extends RecyclerView.Adapter<VouchersRVAdapter.MyViewHolder> implements Serializable {
    private ArrayList<Voucher> vouchers;

    public VouchersRVAdapter(ArrayList<Voucher> vouchers) {
        this.vouchers = vouchers;
    }

    @NonNull
    @Override
    public VouchersRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.row_voucher,parent,false );
        return new MyViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Voucher p = vouchers.get(position);
        ((TextView) holder.linearLayout.findViewById(R.id.voucher)).setText(p.getProductCodeString());
        //((TextView) holder.linearLayout.findViewById(R.id.voucher)).setText("a");

    }

    @Override
    public int getItemCount() {
        return vouchers.size();
    }

    public ArrayList<Voucher> getVouchers() {
        return vouchers;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;

        public MyViewHolder(LinearLayout linearLayout) {
            super(linearLayout);
            this.linearLayout = linearLayout;
        }
    }
}
