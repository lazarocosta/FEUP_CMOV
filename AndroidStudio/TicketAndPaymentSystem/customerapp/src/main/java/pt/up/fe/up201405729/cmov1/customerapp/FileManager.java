package pt.up.fe.up201405729.cmov1.customerapp;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileManager {

    public static void writeTickets(Context context, ArrayList<Ticket> tickets) {
        String filename = CustomerApp.ticketsFilename;
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            for (Ticket t : tickets)
                oos.writeObject(t);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeVouchers(Context context, ArrayList<Voucher> vouchers) {
        String filename = CustomerApp.vouchersFilename;
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            for (Voucher v : vouchers)
                oos.writeObject(v);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Ticket> readTickets(Context context) {
        String filename = CustomerApp.ticketsFilename;
        try {
            FileInputStream fis = context.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<Ticket> tickets = new ArrayList<>();
            Ticket t;
            while ((t = (Ticket) ois.readObject()) != null)
                tickets.add(t);
            ois.close();
            fis.close();
            return tickets;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static ArrayList<Voucher> readVouchers(Context context) {
        String filename = CustomerApp.vouchersFilename;
        try {
            FileInputStream fis = context.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<Voucher> vouchers = new ArrayList<>();
            Voucher v;
            while ((v = (Voucher) ois.readObject()) != null)
                vouchers.add(v);
            ois.close();
            fis.close();
            return vouchers;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
