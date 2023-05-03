package com.example.accountbook.viewmodel;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.accountbook.activity.MainActivity;
import com.example.accountbook.dto.CategoryDTO;
import com.example.accountbook.model.CategorySettingModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategorySettingViewModel extends ViewModel {
    private CategorySettingModel model;
    private List<String[]> dayList;
    private MutableLiveData<List<CategoryDTO>> categoryList ;
    private final MutableLiveData<List<String[]>> categoryList01 = new MutableLiveData<>();
    private final MutableLiveData<List<String[]>> categoryList02 = new MutableLiveData<>();
    private final MutableLiveData<List<CategoryDTO>> categoryListForShow = new MutableLiveData<>();
    private int categoryListInteger = 0;

    public void setViewModel(Activity activity, LifecycleOwner owner) {
        model = new CategorySettingModel(activity);

        dayList = model.dayValueWithCode();
        categoryList = model.getCategoryList(); // model에 있는 list와 연결
        categoryList.observe(owner, categoryDTOS -> setCategoryList(categoryListInteger));

        valueSettings(0, 99);
        model.setCategoryList(); // 서버에서 카테고리 리스트 가져오기
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

    // 카테고리 설정에서 선택에 따라 밑 리사이클러뷰에 보여줄 데이터 설정
    public void setCategoryList(int cnd) {
        List<CategoryDTO> dtoList = new ArrayList<>();
        for(CategoryDTO dto : Objects.requireNonNull(categoryList.getValue())) {
            // 카테고리를 선택했을 때 코드값이 99, 98인 것만 셋팅
            if(cnd == 0 && (dto.getCategory01().equals("99") || dto.getCategory01().equals("98")) && !dto.getCategory02().equals("01")) dtoList.add(dto);
            // 계좌등록을 선택했을 떄 코드값이 97, 96인 것만 셋팅
            else if(cnd == 1 && (dto.getCategory01().equals("97") || dto.getCategory01().equals("96")) && !dto.getCategory02().equals("01")) dtoList.add(dto);
        }

        categoryListForShow.setValue(dtoList);
    }

    // 카테고리 저장
    public void insertCategory(CategoryDTO dto, int categoryType) {
        categoryListInteger = categoryType;
        model.insertCategory(dto);
    }

    // 카테고리 삭제
    public void deleteCategory(CategoryDTO dto, int categoryType) {
        categoryListInteger = categoryType;
        model.deleteCategory(dto.getSeq());
    }

    // 지출 중분류 사용 여부
    public void setSpendingTypeUse(boolean isUse) {
        model.setSpendingTypeUse(isUse);
    }

    // Getter
    public MutableLiveData<List<CategoryDTO>> getCategoryListForShow() {
        return categoryListForShow;
    }
    public boolean getSpendingTypeUse() { return model.getSpendingTypeUse(); }
}
