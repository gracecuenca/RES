package com.example.fbu_res.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Categories")
public class Categories extends ParseObject {
    public static final String name = "Name";
    public static final String image = "Image";

    public String getName(){
        return getString(name);
    }

    public ParseFile getImage(){
        return getParseFile(image);
    }
}
