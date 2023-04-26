package com.example.accountbook.item;

import android.annotation.SuppressLint;

public class Singleton_Date {
    private static Singleton_Date instance;
    private String date;

    private Singleton_Date() { }

    @SuppressLint("SimpleDateFormat")
    public static Singleton_Date getInstance() {
        if(instance == null) {
            synchronized (Singleton_Date.class) {
                instance = new Singleton_Date();
            }
        }
        return instance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
