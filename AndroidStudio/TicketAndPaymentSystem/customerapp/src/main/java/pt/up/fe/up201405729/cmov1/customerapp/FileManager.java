package pt.up.fe.up201405729.cmov1.customerapp;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class FileManager {

    public static void writeTickets(Context context, ArrayList<Ticket> tickets) {
        String filename = CustomerApp.ticketsFilename;
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            for (Ticket t : tickets) {
                osw.write(t.toString());
                osw.write("\n");
            }
            osw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeVouchers(Context context, ArrayList<Voucher> vouchers) {
        String filename = CustomerApp.vouchersFilename;
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            for (Voucher v : vouchers) {
                osw.write(v.toString());
                osw.write("\n");
            }
            osw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Ticket> readTickets(Context context) {
        String filename = CustomerApp.ticketsFilename;
        try {
            FileInputStream fis = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            ArrayList<Ticket> tickets = new ArrayList<>();
            while (br.ready())
                tickets.add(new Ticket(br.readLine()));
            br.close();
            isr.close();
            fis.close();
            return tickets;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static ArrayList<Voucher> readVouchers(Context context) {
        String filename = CustomerApp.vouchersFilename;
        try {
            FileInputStream fis = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            ArrayList<Voucher> vouchers = new ArrayList<>();
            while (isr.ready())
                vouchers.add(new Voucher(br.readLine()));
            br.close();
            isr.close();
            fis.close();
            return vouchers;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
