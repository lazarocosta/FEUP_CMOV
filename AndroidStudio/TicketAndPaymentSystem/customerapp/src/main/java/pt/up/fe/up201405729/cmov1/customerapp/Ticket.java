package pt.up.fe.up201405729.cmov1.customerapp;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

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
        this.uuid = uuid;
        this.performanceId = performanceId;
        this.showName = showName;
        this.date = date;
        this.roomPlace = roomPlace;
        this.state = parseState(state);
    }

    public Ticket(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONObject ticket = jsonObject.getJSONObject("Ticket");
            this.uuid = ticket.getString("uuid");
            this.performanceId = ticket.getString("performanceId");
            this.showName = ticket.getString("showName");
            this.date = new MyDate(ticket.getString("date"));
            this.roomPlace = ticket.getString("roomPlace");
            this.state = parseState(ticket.getString("state"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private State parseState(String state) {
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
        return myState;
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

    @NonNull
    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject ticket = new JSONObject();
            ticket.put("uuid", uuid);
            ticket.put("performanceId", performanceId);
            ticket.put("showName", showName);
            ticket.put("date", date);
            ticket.put("roomPlace", roomPlace);
            ticket.put("state", parseState(state));
            jsonObject.put("Ticket", ticket);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String parseState(State state) {
        String stateStr;
        if (state.equals(State.used))
            stateStr = "used";
        else if (state.equals(State.notUsed))
            stateStr = "not used";
        else
            throw new IllegalArgumentException("Invalid state: " + state);
        return stateStr;
    }
}
