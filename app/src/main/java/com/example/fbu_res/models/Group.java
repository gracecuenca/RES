package com.example.fbu_res.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

@ParseClassName("Group")
public class Group extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_NUM_MEMBS = "num_members";
    public static final String KEY_TYPE = "type";
    public static final String KEY_CHANNEL_NAME = "channel_name";
    public static final String KEY_MEMBERS = "members";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_ASSOCIATED_EVENT = "associated_event";
    public static final String KEY_OFFICIAL = "official";




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
        put(KEY_TYPE, type);
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

    public void addMember(ParseUser user) {
        ParseRelation relation = getRelation(KEY_MEMBERS);
        relation.add(user);
        saveInBackground();
    }

    public void removeMember(ParseUser user) {
        ParseRelation relation = getRelation(KEY_MEMBERS);
        relation.remove(user);
        saveInBackground();
    }
    


    public void setAssociatedEvent(Event event){
        put(KEY_ASSOCIATED_EVENT, event);
    }

    public Event getAssociatedEvent(){
        return (Event) getParseObject(KEY_ASSOCIATED_EVENT);
    }

    public ParseUser getOwner() {
        return getParseUser(KEY_OWNER);
    }

    public void setOwner(ParseUser user) {
        put(KEY_OWNER, user);
    }

    public void setOfficial(Boolean official) {
        put(KEY_OFFICIAL, official);
    }

    public Boolean getOfficial(){
        return getBoolean(KEY_OFFICIAL);
    }

}
