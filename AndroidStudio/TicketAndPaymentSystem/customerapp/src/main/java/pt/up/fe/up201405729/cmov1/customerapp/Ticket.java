package pt.up.fe.up201405729.cmov1.customerapp;

import java.io.Serializable;
import java.util.Objects;

public class Ticket implements Serializable {
    public enum State {used, notUsed}
    private String uuid;
    private String performanceId;
    private String showName;
    private MyDate date;
    private String roomPlace;
    private State state;

    public Ticket(String uuid, String performanceId, String showName, MyDate date, String roomPlace, String state) {
        State myState;
        switch (state) {
            case "used":
                myState = State.used;
                break;
            case "not used":
                myState = State.notUsed;
                break;
            default:
                throw new IllegalArgumentException("Invalid state: " + state);
        }
        this.uuid = uuid;
        this.performanceId = performanceId;
        this.showName = showName;
        this.date = date;
        this.roomPlace = roomPlace;
        this.state = myState;
    }

    public String getUuid() {
        return uuid;
    }

    public String getPerformanceId() {
        return performanceId;
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket)) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(uuid, ticket.uuid) &&
                Objects.equals(performanceId, ticket.performanceId) &&
                Objects.equals(showName, ticket.showName) &&
                Objects.equals(date, ticket.date) &&
                Objects.equals(roomPlace, ticket.roomPlace) &&
                state == ticket.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, performanceId, showName, date, roomPlace, state);
    }
}
