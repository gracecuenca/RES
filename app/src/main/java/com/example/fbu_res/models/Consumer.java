package com.example.fbu_res.models;

import android.util.Log;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("_User")
public class Consumer extends ParseUser {
    public static final String KEY_DISPLAYNAME = "displayName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_INTERESTS = "interests";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PHONENUMBER = "phoneNumber";
    public static final String KEY_LOCATION = "userLocation";
    public static final String KEY_PROFILE_IMG = "profile_img";
    public static final String KEY_INTERESTED_EVENTS = "interestedEvents";
    public static final String KEY_TYPE = "type";

    public void setInterestedEvents(Event event){
        ParseRelation relation = getRelation(KEY_INTERESTED_EVENTS);
        relation.add(event);
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
            Log.d("Consumer Intrests", "Couldn't retrieve list of interests");
        }
        return list;
    }

    public Consumer() {
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

}
