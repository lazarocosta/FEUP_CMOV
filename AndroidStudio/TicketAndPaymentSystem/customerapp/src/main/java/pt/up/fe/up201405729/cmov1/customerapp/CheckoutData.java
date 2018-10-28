package pt.up.fe.up201405729.cmov1.customerapp;

import java.io.Serializable;
import java.util.ArrayList;

public class CheckoutData implements Serializable {
    private ArrayList<Ticket> tickets;

    public CheckoutData(ArrayList<Ticket> tickets) {
        this.tickets = tickets;
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }

    public double getTotalPrice() {
        double totalPrice = 0;
        for (Ticket t : tickets)
            totalPrice += t.getPrice();
        return totalPrice;
    }
}
