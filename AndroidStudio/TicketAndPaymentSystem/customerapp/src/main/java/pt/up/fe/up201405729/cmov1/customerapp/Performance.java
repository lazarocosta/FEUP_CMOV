package pt.up.fe.up201405729.cmov1.customerapp;

import java.util.Date;

public class Performance {
    private String name;
    private Date date;
    private Double price;

    public Performance(String name, Date date, Double price) {
        this.name = name;
        this.date = date;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public Double getPrice() {
        return price;
    }
}
