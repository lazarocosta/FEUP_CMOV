package pt.up.fe.up201405729.cmov1.customerapp;

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

public class SelectTicketsRVAdapter extends RecyclerView.Adapter<SelectTicketsRVAdapter.MyViewHolder> implements Serializable {
    private ArrayList<Ticket> tickets;
    private HashSet<Ticket> selectedTickets;
    private Context context;

    public SelectTicketsRVAdapter(ArrayList<Ticket> tickets, Context context) {
        this.tickets = tickets;
        this.selectedTickets = new HashSet<>();
        this.context = context;
    }

    @NonNull
    @Override
    public SelectTicketsRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.row_select_tickets_rv, parent, false);
        return new MyViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Ticket t = tickets.get(position);
        ((TextView) holder.linearLayout.findViewById(R.id.selectTicketsShowName)).setText(t.getShowName());
        ((TextView) holder.linearLayout.findViewById(R.id.selectTicketsShowDate)).setText(t.getDate().getHumanReadableDate());
        ((TextView) holder.linearLayout.findViewById(R.id.selectTicketsRoomPlace)).setText(t.getRoomPlace());
        CheckBox checkBox = holder.linearLayout.findViewById(R.id.selectTicketsCheckBox);
        checkBox.setChecked(false);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox c = (CheckBox) v;
                Ticket selectedTicket = tickets.get(holder.getAdapterPosition());
                if (c.isChecked()) {
                    if (selectedTickets.size() >= 4) {
                        Toast.makeText(context, "Maximum number of tickets reached.", Toast.LENGTH_LONG).show();
                        c.setChecked(false);
                    } else {
                        for (Ticket t : selectedTickets) {
                            if (!t.getPerformanceId().equals(selectedTicket.getPerformanceId())) {
                                Toast.makeText(context, "All tickets must be for the same performance.", Toast.LENGTH_LONG).show();
                                c.setChecked(false);
                                return;
                            }
                        }
                        selectedTickets.add(selectedTicket);
                    }
                } else
                    selectedTickets.remove(selectedTicket);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    public HashSet<Ticket> getSelectedTickets() {
        return selectedTickets;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;

        public MyViewHolder(LinearLayout linearLayout) {
            super(linearLayout);
            this.linearLayout = linearLayout;
        }
    }
}
