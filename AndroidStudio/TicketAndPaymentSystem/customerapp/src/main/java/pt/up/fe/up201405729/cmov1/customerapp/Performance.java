package pt.up.fe.up201405729.cmov1.customerapp;

import java.io.Serializable;

public class Performance implements Comparable<Performance>, Serializable {
    private String name;
    private MyDate date;
    private Double price;

    public Performance(String name, MyDate date, Double price) {
        this.name = name;
        this.date = date;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public MyDate getDate() {
        return date;
    }

    public Double getPrice() {
        return price;
    }

    @Override
    public int compareTo(Performance o) {
        final int dateCompare = this.date.compareTo(o.date);
        final int nameCompare = this.name.compareTo(o.name);
        final int priceCompare = this.price.compareTo(o.price);
        if (dateCompare <= -1)
            return -1;
        else if (dateCompare == 0) {
            if (nameCompare <= -1)
                return -1;
            else if (nameCompare == 0)
                return priceCompare;
            else
                return 1;
        } else
            return 1;
    }
}
