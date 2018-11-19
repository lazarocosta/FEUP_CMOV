package pt.up.fe.up201405729.cmov1.sharedlibrary;

import java.util.Calendar;
import java.util.Locale;

public class StringFormat {
    private static final Locale locale = Locale.US;

    public static String formatAsPrice(Double price){
        return String.format(locale, "€%.2f", price);
    }

    public static String formatAsInteger(Integer value){
        return String.format(locale, "%d", value);
    }

    public static String formatAsDate(int year, int month, int day){
        return String.format(locale, "%04d-%02d-%02d", year, month, day);
    }

    public static String formatAsDateHourMinute(Calendar calendar){
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR);
        final int minute = calendar.get(Calendar.MINUTE);
        return String.format(locale, "%04d-%02d-%02d %02d:%02d", year, month, day, hour, minute);
    }
}
