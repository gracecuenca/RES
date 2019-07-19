package com.example.fbu_res;

import com.parse.ParseUser;

public class UserDetails {
    static String username = ParseUser.getCurrentUser().getUsername();
    static String password = "grace";
    static String chatWith = "";
}
