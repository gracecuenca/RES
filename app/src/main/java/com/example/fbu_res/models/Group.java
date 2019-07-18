package com.example.fbu_res.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Group")
public class Group extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_NUM_MEMBS = "num_members";
    public static final String KEY_TYPE = "type";


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
}
