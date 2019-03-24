package com.example.user.twitter;

import android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException;

import java.util.Date;
import java.util.HashMap;

public class twit {

    String writer;
    String message;
    public Date timestamp;
    public String picId;
    public String imageName;
    public HashMap<String, Boolean> likes;
    public String id;



    twit() {}

    twit(String writer, String message) {
        this.writer = writer;
        this.message = message;
    }

}
