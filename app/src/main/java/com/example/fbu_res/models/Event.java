package com.example.fbu_res.models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;

@ParseClassName("Event")
public class Event extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_DATE = "date";
    public static final String KEY_RADIUS = "radius";
    public static final String KEY_TYPE = "type";
    public static final String KEY_DES = "Description";
    public static final String KEY_LOC = "location";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String description) { put(KEY_NAME, description); }

    public ParseObject getLocation() { return (Address) getParseObject(KEY_LOCATION); }

    public void setLocation(Address address) {
        put(KEY_LOCATION, address);
    }

    public Date getDate() {
        return getDate(KEY_DATE);
    }

    public void setUser(ParseUser user) {
        put(KEY_DATE, user);
    }

    public String getRadius() {
        return getString(KEY_RADIUS);
    }

    public void setRadius(String timestamp) {
        put(KEY_RADIUS, timestamp);
    }

    public String geType() { return getString(KEY_TYPE); }

    public ParseFile getImage(){return getParseFile("eventImage");}

    public String getLocationString(){return getString(KEY_LOC);}

    public void setType(String type) {
        put(KEY_TYPE, type);
    }

    public String getDescription(){return getString(KEY_DES);}

    public static ArrayList<Event> createEventsList(int numPosts) {
        ArrayList<Event> events = new ArrayList<Event>();

        for (int i = 1; i <= numPosts; i++) {
            events.add(new Event());
        }

        return events;
    }
}
