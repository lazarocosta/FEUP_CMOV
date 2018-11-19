package pt.up.fe.up201405729.cmov1.customerapp;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pt.up.fe.up201405729.cmov1.sharedlibrary.StringFormat;

public class MyDate implements Comparable<MyDate>, Serializable {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    private Date date;

    public MyDate(String date) {
        try {
            this.date = dateFormat.parse(date);
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return StringFormat.formatAsDateHourMinute(calendar);
    }
}
