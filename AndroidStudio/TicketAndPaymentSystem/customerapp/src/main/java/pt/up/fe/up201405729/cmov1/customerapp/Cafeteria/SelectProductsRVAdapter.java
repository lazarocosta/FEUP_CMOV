package pt.up.fe.up201405729.cmov1.customerapp.Cafeteria;

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

import pt.up.fe.up201405729.cmov1.customerapp.Product;
import pt.up.fe.up201405729.cmov1.customerapp.R;
import pt.up.fe.up201405729.cmov1.customerapp.StringFormat;

public class SelectProductsRVAdapter extends RecyclerView.Adapter<SelectProductsRVAdapter.MyViewHolder> implements Serializable {
    private ArrayList<Product> products;

    public SelectProductsRVAdapter(ArrayList<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public SelectProductsRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cafeteria_select_products_rv,parent,false );
        return new MyViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Product p = products.get(position);
        ((TextView) holder.linearLayout.findViewById(R.id.productName)).setText(p.getName());
        ((TextView) holder.linearLayout.findViewById(R.id.productPrice)).setText(StringFormat.formatAsPrice(p.getPrice()));
        EditText editText = holder.linearLayout.findViewById(R.id.productQuantityET);
        editText.setText(StringFormat.formatAsInteger(p.getQuantity()));
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
                try {
                    products.get(holder.getAdapterPosition()).setQuantity(value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    s.clear();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;

        public MyViewHolder(LinearLayout linearLayout) {
            super(linearLayout);
            this.linearLayout = linearLayout;
        }
    }
}
