package com.example.fbu_res.models;

public class BusinessSearch {
    String name;
    String URL;
    int identifier;

    public BusinessSearch(String name1, int identifier1){
        name = name1;
        identifier = identifier1;
    }

    public String getName() {
        return name;
    }

    public String getURL() {
        return URL;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setIdentifier(int identifierParam){
        identifier = identifier;
    }

    public int getIdentifier(){
        return identifier;
    }
}
