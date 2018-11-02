package pt.up.fe.up201405729.cmov1.customerapp;

import java.io.Serializable;
import java.util.ArrayList;

public class CheckoutData implements Serializable {
    private ArrayList<Performance> performances;
    private ArrayList<Integer> ticketsQuantities;

    public CheckoutData(ArrayList<Performance> performances, ArrayList<Integer> ticketsQuantities) {
        if (performances.size() != ticketsQuantities.size())
            throw new IllegalArgumentException("performances.size() should be equal to ticketsQuantities.size()");
        this.performances = performances;
        this.ticketsQuantities = ticketsQuantities;
    }

    public ArrayList<Performance> getPerformances() {
        return performances;
    }

    public ArrayList<Integer> getTicketsQuantities() {
        return ticketsQuantities;
    }

    public Double getTotalPrice() {
        Double totalPrice = 0.0;
        for (int i = 0; i < performances.size(); i++)
            totalPrice += performances.get(i).getPrice() * ticketsQuantities.get(i);
        return totalPrice;
    }
}
