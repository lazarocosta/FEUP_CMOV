package pt.up.fe.up201405729.cmov1.customerapp;

public class Ticket {
    private String uuid;
    private String showName;
    private MyDate date;
    private String roomPlace;

    public Ticket(String uuid, String showName, MyDate date, String roomPlace) {
        this.uuid = uuid;
        this.showName = showName;
        this.date = date;
        this.roomPlace = roomPlace;
    }

    public String getUuid() {
        return uuid;
    }

    public String getShowName() {
        return showName;
    }

    public MyDate getDate() {
        return date;
    }

    public String getRoomPlace() {
        return roomPlace;
    }
}
