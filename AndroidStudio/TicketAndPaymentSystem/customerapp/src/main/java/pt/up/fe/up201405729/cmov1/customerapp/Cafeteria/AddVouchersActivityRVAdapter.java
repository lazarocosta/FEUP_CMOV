package pt.up.fe.up201405729.cmov1.customerapp.Cafeteria;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import pt.up.fe.up201405729.cmov1.customerapp.R;
import pt.up.fe.up201405729.cmov1.customerapp.Voucher;

public class AddVouchersActivityRVAdapter extends RecyclerView.Adapter<AddVouchersActivityRVAdapter.MyViewHolder> implements Serializable {
    private ArrayList<Voucher> vouchers;
    private HashSet<Voucher> selectedVouchers;
    private Context packageContext;

    public AddVouchersActivityRVAdapter(ArrayList<Voucher> vouchers, Context context) {
        this.vouchers = vouchers;
        this.selectedVouchers = new HashSet<>();
        this.packageContext = context;
    }

    @NonNull
    @Override
    public AddVouchersActivityRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cafeteria_add_vouchers_rv, parent, false);//
        return new MyViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Voucher v = vouchers.get(position);
        ((TextView) holder.linearLayout.findViewById(R.id.voucherProductCode)).setText(v.getProductCode().name());
        CheckBox checkBox = holder.linearLayout.findViewById(R.id.addVouchersCheckBox);
        checkBox.setActivated(false);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Voucher selectedVoucher = vouchers.get(holder.getAdapterPosition());
                if (v.isActivated()) {
                    if (selectedVouchers.size() > 2) {
                        Toast.makeText(packageContext, "Maximum number of vouchers reached.", Toast.LENGTH_LONG).show();
                        v.setActivated(false);
                    } else
                        selectedVouchers.add(selectedVoucher);
                } else
                    selectedVouchers.remove(selectedVoucher);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vouchers.size();
    }

    public ArrayList<Voucher> getVouchers() {
        return vouchers;
    }

    public HashSet<Voucher> getSelectedVouchers() {
        return selectedVouchers;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;

        public MyViewHolder(LinearLayout linearLayout) {
            super(linearLayout);
            this.linearLayout = linearLayout;
        }
    }
}
