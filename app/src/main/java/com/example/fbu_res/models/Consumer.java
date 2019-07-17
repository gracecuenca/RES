package com.example.fbu_res.models;

import android.util.Log;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
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

    public String getKeyDisplayname() {
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

    public void setAddress(String timestamp) {
        put(KEY_ADDRESS, timestamp);
    }

    private static int lastPostId = 0;



}
