package com.example.fbu_res.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

@ParseClassName("_User")
public class User extends ParseUser {
    public static final String KEY_DISPLAYNAME = "displayName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_INTERESTS = "interests";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PHONENUMBER = "phoneNumber";
    public static final String KEY_LOCATION = "userLocation";
    public static final String KEY_PROFILE_IMG = "profile_img";
    public static final String KEY_INTERESTED_EVENTS = "interestedEvents";
    public static final String KEY_TYPE = "type";
    public static final String KEY_CREATED_EVENTS = "createdEvents";
    public static final String KEY_DM_USERS = "dm_users";
    public static final String KEY_FRIENDS = "friends";
    public static final String KEY_INTERESTED_MAP = "interestedEventsHashmap";
    public static final String KEY_CREATED_MAP = "createdEventsHashmap";

    public void addInterestedMap(Date date, Event event){
        DatedEvent datedEvent;
        ArrayList<Event> events;
        TreeMap<Date, DatedEvent> treeMap;
        if(getInterestedMap() == null){ // tree does not exist yet
            datedEvent = new DatedEvent();
            datedEvent.setDate(date);
            events = new ArrayList<Event>();
            events.add(event);
            datedEvent.setEvents(events);
            treeMap = new TreeMap<Date, DatedEvent>();
            treeMap.put(date, datedEvent);
        }
        else if(getInterestedMap().containsKey(date)){ // tree exists and key already in
            treeMap = getInterestedMap();
            datedEvent = treeMap.get(date);
            events = datedEvent.getEvents();
            events.add(event);
            datedEvent.setEvents(events);
            treeMap.put(date, datedEvent);
        } else { // tree exists but key not in
            treeMap = getInterestedMap();
            datedEvent = new DatedEvent();
            datedEvent.setDate(date);
            events = new ArrayList<Event>();
            events.add(event);
            datedEvent.setEvents(events);
            treeMap.put(date, datedEvent);
        }
        put(KEY_INTERESTED_MAP, treeMap);
        saveInBackground();
    }

    public void removeInterestedMap(Date date,Event event){
        getInterestedMap().get(date).getEvents().remove(event);
        saveInBackground();
    }

    public TreeMap<Date, DatedEvent> getInterestedMap(){
        return (TreeMap) get(KEY_INTERESTED_MAP);
    }

    public void addInterestedEvent(Event event){
        ParseRelation relation = getRelation(KEY_INTERESTED_EVENTS);
        relation.add(event);
        saveInBackground();
    }

    public void removeInterestedEvent(Event event){
        ParseRelation relation = getRelation(KEY_INTERESTED_EVENTS);
        relation.remove(event);
        saveInBackground();
    }

    public ParseRelation getInterestedEvents(){
        ParseRelation interestedEvents = getRelation(KEY_INTERESTED_EVENTS);
        return interestedEvents;
    }

    public void setLocation(ParseGeoPoint geoPoint){
        put(KEY_LOCATION, geoPoint);
    }

    public ParseGeoPoint getLocation(){
        return getParseGeoPoint(KEY_LOCATION);
    }

    public String getDisplayname() {
        return getString(KEY_DISPLAYNAME);
    }

    public void setDisplayname(String name) {
        put(KEY_DISPLAYNAME, name);
    }

    public void setEmail(String email) {
        put(KEY_EMAIL, email);
    }

    public String getEmail() {
        return getString(KEY_EMAIL);
    }

    public void setPhonenumber(String phoneNumber) {
        put(KEY_PHONENUMBER, phoneNumber);
    }

    public String getPhonenumber() {
        return getString(KEY_PHONENUMBER);
    }

    public void setInterests(ParseFile image) {
        put(KEY_INTERESTS, image);
    }

    public ArrayList<String> getInterests() {
        JSONArray arr = new JSONArray();
        ArrayList<String> list = new ArrayList<String>();
        try {
            for (int i = 0; i < arr.length(); i++) {
                list.add(arr.getJSONObject(i).getString("name"));
            }
        } catch (JSONException e) {
            Log.d("User Intrests", "Couldn't retrieve list of interests");
        }
        return list;
    }

    public User() {
    }

    public ParseObject getAddress() {
        return getParseObject(KEY_ADDRESS);
    }

    public void setAddress(Address address) {
        put(KEY_ADDRESS, address);
    }

    private static int lastPostId = 0;


    public ParseFile getProfileImg() {
        return getParseFile(KEY_PROFILE_IMG);
    }

    public void setProfileImg(ParseFile profileImg){
        put(KEY_PROFILE_IMG, profileImg);
    }

    public void setType(String type) {
        put(KEY_TYPE, type);
    }

    public String getType() {
        return getString(KEY_TYPE);
    }

    public void addCreatedEvents(Event event){
        ParseRelation relation = getRelation(KEY_CREATED_EVENTS);
        relation.add(event);
        saveInBackground();
    }

    public ParseRelation getCreatedEvents(){
        ParseRelation relation = getRelation(KEY_CREATED_EVENTS);
        return relation;
    }

    public void addDMUser(User user){
        ParseRelation relation = getRelation(KEY_DM_USERS);
        relation.add(user);
        saveInBackground();
    }

    public ParseRelation getDMUsers(){
        ParseRelation relation = getRelation(KEY_DM_USERS);
        return relation;
    }

    public void addFriend(User user){
        ParseRelation relation = getRelation(KEY_FRIENDS);
        relation.add(user);
        saveInBackground();
    }

    public ParseRelation getFriends(){
        ParseRelation relation = getRelation(KEY_FRIENDS);
        return relation;
    }

}
