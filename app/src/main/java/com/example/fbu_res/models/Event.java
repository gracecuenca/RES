package com.example.fbu_res.models;

import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;

public class Event extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_DATE = "date";
    public static final String KEY_RADIUS = "radius";
    public static final String KEY_TYPE = "type";

    public String getKeyName() {
        return getString(KEY_NAME);
    }

    public void setKeyName(String description) {
        put(KEY_NAME, description);
    }

    public ParseObject getKeyLocation() {
        return (Address) getParseObject(KEY_LOCATION);
    }
    public void setKeyLocation(Address address) {
        put(KEY_LOCATION, address);
    }

    public Date getKeyDate() {
        return getDate(KEY_DATE);
    }

    public void setKeyUser(ParseUser user) {
        put(KEY_DATE, user);
    }

    public String getKeyRadius() {
        return getString(KEY_RADIUS);
    }

    public void setKeyRadius(String timestamp) {
        put(KEY_RADIUS, timestamp);
    }

    public String getKeyType() {
        return getString(KEY_TYPE);
    }

    public void setKeyType(String type) {
        put(KEY_TYPE, type);
    }


    public static ArrayList<Event> createEventsList(int numPosts) {
        ArrayList<Event> events = new ArrayList<Event>();

        for (int i = 1; i <= numPosts; i++) {
            events.add(new Event());
        }

        return events;
    }
}