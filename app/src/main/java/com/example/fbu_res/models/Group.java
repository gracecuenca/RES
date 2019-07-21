package com.example.fbu_res.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@ParseClassName("Group")
public class Group extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_NUM_MEMBS = "num_members";
    public static final String KEY_TYPE = "type";
    public static final String KEY_CHANNEL_NAME = "channel_name";
    public static final String KEY_MEMBERS = "members";


    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public int getNumMembs() {
        return getNumber(KEY_NUM_MEMBS).intValue();
    }

    public void setNumMembs(Number numMembs) {
        put(KEY_NUM_MEMBS, numMembs);
    }

    public String getType() {
        return getString(KEY_TYPE);
    }

    public void setType(String type) {
        put(KEY_NAME, type);
    }

    public String getChannelName() {
        return getString(KEY_CHANNEL_NAME);
    }

    public void setChannelName(String type) {
        put(KEY_CHANNEL_NAME, type);
    }

    public ParseRelation getMembers(){
        ParseRelation jsonMembers = getRelation(KEY_MEMBERS);
        return jsonMembers;
    }

    public void setMembers(List<ParseUser> users) {
        ParseRelation relation = getRelation(KEY_MEMBERS);
        for(int i = 0; i < users.size(); i++){
            relation.add(users.get(i));
        }
        saveInBackground();
    }
}
