package com.example.accountbook.model;

import android.app.Activity;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.accountbook.dto.MoneyDTO;
import com.example.accountbook.dto.TransferMoneyDTO;
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
    private final MutableLiveData<List<MoneyDTO>> moneyLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<TransferMoneyDTO>> transferMoneyLiveData = new MutableLiveData<>();

    public SaveMoneyModel(Activity activity) {
        this.activity = activity;
        api = RetrofitClient.getRetrofit();
        spClient = new SharedPreferencesClient(activity);
    }


    // saveMoneyInfo 전체 삭제
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


    // 특정 saveMoneyInfo 삭제
    public void deleteSaveMoneyInfo(int moneySeq, String date) {
        api.deleteMoneyInfo(moneySeq).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                Toast.makeText(activity, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                getMoneyDataByDate(date.substring(0, date.length()-3) + "___");
                spClient.setIsChange(true);
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Toast.makeText(activity, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // 계좌이체 내역 삭제
    public void deleteTransferMoneyInfo(int seq, String date) {
        api.deleteTransferMoneyInfo(seq).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                Toast.makeText(activity, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                getTransferMoneyInfo(date.substring(0, date.length()-3) + "___");
                spClient.setIsChange(true);
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Toast.makeText(activity, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // 해당 월의 saveMoneyInfo 가져오기
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


    // 계좌이체 내역 가져오기
    public void getTransferMoneyInfo(String date) {
        api.getTransferMoneyInfo(spClient.getUserSeq(), date).enqueue(new Callback<List<TransferMoneyDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<TransferMoneyDTO>> call, @NonNull Response<List<TransferMoneyDTO>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    transferMoneyLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TransferMoneyDTO>> call, @NonNull Throwable t) {
                transferMoneyLiveData.setValue(null);
            }
        });
    }


    // 입력한 money info 저장하기
    public void insertMoneyInfo(int settingsSeq, int bankSeq, String in_sp, String date, String money, String memo, int isFinish) {
        api.insertMoneyInfo(spClient.getUserSeq(), settingsSeq, bankSeq, in_sp, date, money, memo).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                if(isFinish == 0) {
                    Toast.makeText(activity, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                    getMoneyDataByDate(date.substring(0, date.length()-3) + "___");
                } else if(isFinish == 1) {
                    Toast.makeText(activity, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                }
                spClient.setIsChange(true);
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Toast.makeText(activity, "잠시 후 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // 계좌이체 내역 저장
    public void insertTransferMoneyInfo(int incomeBankSeq, int expandingBankSeq, String date, String money, String memo, String incomeBank, String expandingBank, int incomeSettingsSeq, int expandingSettingsSeq) {
        api.insertTransferMoneyInfo(spClient.getUserSeq(), incomeBankSeq, expandingBankSeq, date, money, memo, incomeBank, expandingBank, incomeSettingsSeq, expandingSettingsSeq).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                Toast.makeText(activity, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                spClient.setIsChange(true);
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Toast.makeText(activity, "잠시 후 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // 입력한 money info 로 수정하기
    public void modifyMoneyInfo(int seq, int settingsSeq, int bankSeq, String in_sp, String inputDate, String money, String memo, String choiceDate) {
        api.modifyMoneyInfo(seq, settingsSeq, bankSeq, in_sp, inputDate, money, memo).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                Toast.makeText(activity, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                getMoneyDataByDate(choiceDate.substring(0, choiceDate.length()-3) + "___");
                spClient.setIsChange(true);
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Toast.makeText(activity, "잠시 후 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public MutableLiveData<List<MoneyDTO>> getMoneyLiveData() {
        return moneyLiveData;
    }
    public MutableLiveData<List<TransferMoneyDTO>> getTransferMoneyLiveData() {
        return transferMoneyLiveData;
    }
    public boolean getIsChange() { return spClient.getIsChange(); }
    public void setIsChange(boolean isChange) { spClient.setIsChange(isChange); }
    public boolean getIsChangeCal() { return spClient.getIsChangeCal(); }
    public void setIsChangeCal(boolean isChangeCal) { spClient.setIsChangeCal(isChangeCal); }
}
