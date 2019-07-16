package com.example.fbu_res.models;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

public class Event extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_TIMESTAMP = "createdAt";
    public static final String KEY_OBJECT_ID = "objectId";

    public String getKeyDescription() {
        return getString(KEY_DESCRIPTION);
    }


    public String getKeyObjectId() {
        return getObjectId();
    }

    public void setKeyDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getKeyImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setKeyImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseUser getKeyUser() {
        return getParseUser(KEY_USER);
    }

    public void setKeyUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getKeyTimestamp() {
        return getString(KEY_TIMESTAMP);
    }

    public void setKeyTimestamp(String timestamp) {
        put(KEY_TIMESTAMP, timestamp);
    }

    public static class Query extends ParseQuery<Event> {

        public Query() {
            super(Event.class);
        }
        public Query getTop() {
            setLimit(20);
            return this;
        }

        public Query withUser() {
            include("user");
            return this;
        }
    }

    private static int lastPostId = 0;


    public static ArrayList<Event> createPostsList(int numPosts) {
        ArrayList<Event> posts = new ArrayList<Event>();

        for (int i = 1; i <= numPosts; i++) {
            posts.add(new Event());
        }

        return posts;
    }
}
