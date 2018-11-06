package pt.up.fe.up201405729.cmov1.customerapp;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyDate implements Comparable<MyDate>, Serializable {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    private Date date;
    private String resultDate;

    public MyDate(String date) {
        try {
            this.date = dateFormat.parse(date);

            String pattern = "[^T]*";
            String pattern2 = "(\\d\\d:\\d\\d)";

            Pattern r = Pattern.compile(pattern);
            Pattern r2 = Pattern.compile(pattern2);

            Matcher m = r.matcher(date.toString());
            Matcher m2 = r2.matcher(date.toString());

            if(m.find() && m2.find() ) {
                this.resultDate= m.group() + " " + m2.group();
                System.out.println(this.resultDate);
            }else
                this.resultDate="";
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return dateFormat.format(date);
    }

    @Override
    public int compareTo(MyDate o) {
        return this.toString().compareTo(o.toString());
    }

    public String getHumanReadableDate() {
     if(!this.resultDate.equals("")){
            return this.resultDate;
        } else 
            return date.toString();
    }
}
