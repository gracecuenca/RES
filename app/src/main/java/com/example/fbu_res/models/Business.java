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

public class Business extends ParseUser {
    public static final String KEY_DISPLAYNAME = "displayName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PHONENUMBER = "phoneNumber";
    public static final String KEY_LOCATION = "userLocation";
    public static final String KEY_PROFILE_IMG = "profile_img";
    public static final String KEY_TYPE = "type";
    public static String designation = "";

    public static String getKeyDisplayname() {
        return KEY_DISPLAYNAME;
    }

    public static String getKeyEmail() {
        return KEY_EMAIL;
    }

    public static String getKeyAddress() {
        return KEY_ADDRESS;
    }

    public static String getKeyPhonenumber() {
        return KEY_PHONENUMBER;
    }

    public static String getKeyLocation() {
        return KEY_LOCATION;
    }

    public static String getKeyProfileImg() {
        return KEY_PROFILE_IMG;
    }

    public static String getKeyType() {
        return KEY_TYPE;
    }

    public static void setDesignation(String name){
        designation = name;
    }
}
