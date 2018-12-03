package pt.up.fe.up201405729.cmov1.customerapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import pt.up.fe.up201405729.cmov1.sharedlibrary.Product;
import pt.up.fe.up201405729.cmov1.sharedlibrary.StringFormat;

public class ProductsRVAdapter extends RecyclerView.Adapter<ProductsRVAdapter.MyViewHolder> implements Serializable {
    private ArrayList<Product> products;

    public ProductsRVAdapter(ArrayList<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductsRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product,parent,false );
        return new MyViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Product p = products.get(position);
        ((TextView) holder.linearLayout.findViewById(R.id.productName)).setText(p.getName());
        ((TextView) holder.linearLayout.findViewById(R.id.productPrice)).setText(StringFormat.formatAsPrice(p.getPrice()));
        ((TextView) holder.linearLayout.findViewById(R.id.productQuantity)).setText(StringFormat.formatAsInteger(p.getQuantity()));
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
