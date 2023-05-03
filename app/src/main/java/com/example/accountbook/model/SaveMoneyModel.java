package com.example.accountbook.model;

import android.app.Activity;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.accountbook.dto.MoneyDTO;
import com.example.accountbook.item.RetrofitAPI;
import com.example.accountbook.item.RetrofitClient;
import com.example.accountbook.item.SharedPreferencesClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaveMoneyModel {
    private final RetrofitAPI api;
    private final SharedPreferencesClient spClient;
    private final Activity activity;
    private MutableLiveData<List<MoneyDTO>> moneyLiveData = new MutableLiveData<>();

    public SaveMoneyModel(Activity activity) {
        this.activity = activity;
        api = RetrofitClient.getRetrofit();
        spClient = new SharedPreferencesClient(activity);
    }

    public void deleteAll() {
        api.deleteMoneyAll(spClient.getUserSeq()).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                Toast.makeText(activity, "기록한 돈관련 정보를 모두 삭제했습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Toast.makeText(activity, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getMoneyDataByDate(String date) {
        api.getMoneyInfo(spClient.getUserSeq(), date).enqueue(new Callback<List<MoneyDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<MoneyDTO>> call, @NonNull Response<List<MoneyDTO>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    moneyLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MoneyDTO>> call, @NonNull Throwable t) {
                moneyLiveData.setValue(null);
            }
        });
    }


    public MutableLiveData<List<MoneyDTO>> getMoneyLiveData() {
        return moneyLiveData;
    }
}
