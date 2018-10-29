package pt.up.fe.up201405729.cmov1.customerapp;

import java.util.ArrayList;
import java.util.Date;

public class Performance {
    private String name;
    private ArrayList<Date> dates;
    private double price;

    public Performance(String name, ArrayList<Date> dates, double price) {
        this.name = name;
        this.dates = dates;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Date> getDates() {
        return dates;
    }

    public double getPrice() {
        return price;
    }
}
