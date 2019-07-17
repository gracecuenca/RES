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

    public String getKeyDisplayname() {
        return getString(KEY_DISPLAYNAME);
    }

    public void setKeyDisplayname(String name) {
        put(KEY_DISPLAYNAME, name);
    }

    public void setKeyEmail(String email) {
        put(KEY_EMAIL, email);
    }

    public String getKeyEmail() {
        return getString(KEY_EMAIL);
    }

    public void setKeyInterests(ParseFile image) {
        put(KEY_INTERESTS, image);
    }

    public ArrayList<String> getKeyInterests() {
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

    public ParseObject getKeyAddress() {
        return getParseObject(KEY_ADDRESS);
    }

    public void setKeyAddress(String timestamp) {
        put(KEY_ADDRESS, timestamp);
    }

    private static int lastPostId = 0;



}
