package com.example.accountbook.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static void setGsonAndRetrofit() {
        Gson gson = new GsonBuilder().setLenient().create();
        String URL = "https://port-0-spring-accountbook-17xqnr2llgx9g8iq.sel3.cloudtype.app/";
        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static RetrofitAPI getRetrofit() {
        return retrofit.create(RetrofitAPI.class);
    }
}
