package pt.up.fe.up201405729.cmov1.customerapp;

import java.util.Locale;

public class StringFormat {
    private static final Locale locale = Locale.US;

    public static String formatAsPrice(Double price){
        return String.format(locale, "€%.2f", price);
    }

    public static String formatAsInteger(Integer value){
        return String.format(locale, "%d", value);
    }
}
