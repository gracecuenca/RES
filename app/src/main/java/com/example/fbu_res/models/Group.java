package com.example.fbu_res.models;

import com.parse.ParseFile;
import com.parse.ParseObject;

public class Group extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_NUM_MEMBS = "num_members";
    public static final String KEY_TYPE = "type";


    public String getKeyName() {
        return getString(KEY_NAME);
    }

    public void setKeyName(String name) {
        put(KEY_NAME, name);
    }

    public ParseFile getKeyImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setKeyImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public int getKeyNumMembs() {
        return getNumber(KEY_NUM_MEMBS).intValue();
    }

    public void setKeyNumMembs(Number numMembs) {
        put(KEY_NUM_MEMBS, numMembs);
    }

    public String getKeyType() {
        return getString(KEY_TYPE);
    }

    public void setKeyType(String type) {
        put(KEY_NAME, type);
    }
}
