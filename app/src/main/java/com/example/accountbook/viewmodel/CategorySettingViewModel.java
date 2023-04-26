package com.example.accountbook.viewmodel;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.accountbook.model.CategorySettingModel;

import java.util.List;

public class CategorySettingViewModel extends ViewModel {
    private CategorySettingModel model;
    private List<String[]> dayList;
    private final MutableLiveData<List<String[]>> categoryList01 = new MutableLiveData<>();
    private final MutableLiveData<List<String[]>> categoryList02 = new MutableLiveData<>();

    public void setViewModel(Activity activity) {
        model = new CategorySettingModel(activity);

        dayList = model.dayValueWithCode();
        valueSettings(0, 99);
    }

    // 초기 값으로 카테고리, 수입으로 설정
    public void valueSettings(int type, int categoryType) {
        categoryList01.setValue(model.categoryValueWithCode01(type));
        categoryList02.setValue(model.categoryValueWithCode02(type, categoryType));
    }

    // 날짜 넘기기
    public List<String[]> getDayList() {
        return dayList;
    }

    // 카테고리/계좌의 값 넘기기
    public MutableLiveData<List<String[]>> getCategoryList01() {
        return categoryList01;
    }

    // 카테고리/계좌 값 설정
    public void setCategoryList01(int type) {
        categoryList01.postValue(model.categoryValueWithCode01(type));
    }

    // 카테고리 설정 시 수입/지출에 따른 값 넘기기
    public MutableLiveData<List<String[]>> getCategoryList02() {
        return categoryList02;
    }

    // 카테고리 설정 시 수입/지출에 따른 값 설정
    public void setCategoryList02(int type, int categoryType) {
        categoryList02.postValue(model.categoryValueWithCode02(type, categoryType));
    }
}
