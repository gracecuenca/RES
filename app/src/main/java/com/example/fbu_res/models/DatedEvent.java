package com.example.fbu_res.models;

import java.util.ArrayList;
import java.util.Date;

public class DatedEvent {

    Date date;
    ArrayList<Event> events;

    public Date getDate() { return date; }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

}
