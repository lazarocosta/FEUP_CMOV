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

public class TicketRVAdapter  extends RecyclerView.Adapter<TicketRVAdapter.MyViewHolder> implements Serializable {
    private ArrayList<Ticket> tickets;

    public TicketRVAdapter(ArrayList<Ticket> tickets) {
        this.tickets = tickets;
    }

    @NonNull
    @Override
    public TicketRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false );
        return new MyViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Ticket p = tickets.get(position);
        ((TextView) holder.linearLayout.findViewById(R.id.performanceName)).setText(p.getShowName());
        ((TextView) holder.linearLayout.findViewById(R.id.performanceDate)).setText(p.getDate().getHumanReadableDate());
        ((TextView) holder.linearLayout.findViewById(R.id.performancePrice)).setText(p.getRoomPlace());
        EditText editText = holder.linearLayout.findViewById(R.id.performanceTicketsQuantityET);
        editText.setText(StringFormat.formatAsInteger(1));
        editText.setEnabled(false);
        editText.setBackground(null);

    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;

        public MyViewHolder(LinearLayout linearLayout) {
            super(linearLayout);
            this.linearLayout = linearLayout;
        }
    }
}
