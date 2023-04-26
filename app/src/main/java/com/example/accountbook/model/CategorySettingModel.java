package com.example.accountbook.model;

import android.app.Activity;

import com.example.accountbook.R;

import java.util.ArrayList;
import java.util.List;

public class CategorySettingModel {
    private Activity activity;

    public CategorySettingModel(Activity activity) {
        this.activity = activity;
    }


    public List<String[]> dayValueWithCode() {
        String[] dayCode = activity.getResources().getStringArray(R.array.code_day_array);
        String[] dayValue = activity.getResources().getStringArray(R.array.day_array);

        List<String[]> dayList = new ArrayList<>();
        dayList.add(dayCode);
        dayList.add(dayValue);

        return dayList;
    }


    public List<String[]> categoryValueWithCode01(int type) {
        List<String[]> categoryList01 = new ArrayList<>();

        if(type == 0) { // 카테고리 선택으로 인해 수입/지출을 보여줌
            String[] categoryCode01 = activity.getResources().getStringArray(R.array.code_category01_array);
            String[] categoryValue01 = activity.getResources().getStringArray(R.array.category01_array);

            categoryList01.add(categoryCode01);
            categoryList01.add(categoryValue01);
        } else { // 계좌등록으로 인해 은행/카드가 나옴
            String[] categoryCode02 = activity.getResources().getStringArray(R.array.code_category02_array);
            String[] categoryValue02 = activity.getResources().getStringArray(R.array.category02_array);

            categoryList01.add(categoryCode02);
            categoryList01.add(categoryValue02);
        }

        return categoryList01;
    }


    public List<String[]> categoryValueWithCode02(int type, int cType) {
        List<String[]> categoryList02 = new ArrayList<>();

        if(type == 0 && cType == 98) { // 카테고리, 지출 선택일 경우
            String[] categoryCodeIn = activity.getResources().getStringArray(R.array.code_category01_expanding);
            String[] categoryValueIn = activity.getResources().getStringArray(R.array.category01_expanding_array);

            categoryList02.add(categoryCodeIn);
            categoryList02.add(categoryValueIn);
        } else { // 카테고리가 지출이 아닐경우 카테고리2가 가려지기 때문에 day로 하여 셋팅값을 00으로 맞추기
            String[] dayCode = activity.getResources().getStringArray(R.array.code_day_array);
            String[] dayValue = activity.getResources().getStringArray(R.array.day_array);

            categoryList02.add(dayCode);
            categoryList02.add(dayValue);
        }

        return categoryList02;
    }
}
