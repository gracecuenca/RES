package com.example.fbu_res.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

@ParseClassName("_User")
public class Business extends ParseUser {
    public static final String KEY_DISPLAYNAME = "displayName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PHONENUMBER = "phoneNumber";
    public static final String KEY_LOCATION = "userLocation";
    public static final String KEY_PROFILE_IMG = "profile_img";

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

    public Business() {
    }

    public ParseObject getAddress() {
        return getParseObject(KEY_ADDRESS);
    }

    public void setAddress(String timestamp) {
        put(KEY_ADDRESS, timestamp);
    }

    public ParseFile getProfileImg() {
        return getParseFile(KEY_PROFILE_IMG);
    }

    public void setProfileImg(ParseFile profileImg){
        put(KEY_PROFILE_IMG, profileImg);
    }
}
