package com.example.accountbook.model;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.accountbook.R;
import com.example.accountbook.dto.CategoryDTO;
import com.example.accountbook.item.RetrofitAPI;
import com.example.accountbook.item.RetrofitClient;
import com.example.accountbook.item.SharedPreferencesClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategorySettingModel {
    private final RetrofitAPI api;
    private final Activity activity;
    private final SharedPreferencesClient spClient;
    private final MutableLiveData<List<CategoryDTO>> categoryList = new MutableLiveData<>();

    public CategorySettingModel(Activity activity) {
        this.activity = activity;
        api = RetrofitClient.getRetrofit();
        spClient = new SharedPreferencesClient(activity);
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


    // get category by api
    public void setCategoryList() {
        api.getCategoryInfo(spClient.getUserSeq()).enqueue(new Callback<List<CategoryDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryDTO>> call, @NonNull Response<List<CategoryDTO>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    categoryList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryDTO>> call, @NonNull Throwable t) {
                categoryList.setValue(null);
            }
        });
    }


    // delete category by api
    public void deleteCategory(int seq) {
        api.deleteCategoryInfo(seq).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                setCategoryList();
                Toast.makeText(activity, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Toast.makeText(activity, "잠시 후 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // 중분류 사용 여부 저장
    public void setSpendingTypeUse(boolean isUse) {
        spClient.setSpendingTypeUse(isUse);
    }


    // insert category by api
    public void insertCategory(CategoryDTO dto) {
        api.insertCategoryInfo(dto.getUserSeq(), dto.getCode(), dto.getCategory01(), dto.getCategory02(), dto.getContents(), dto.getPayDay()).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                setCategoryList();
                Toast.makeText(activity, "저장되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Toast.makeText(activity, "잠시 후 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Getter
    public MutableLiveData<List<CategoryDTO>> getCategoryList() {
        return categoryList;
    }
    public boolean getSpendingTypeUse() { return spClient.getSpendingTypeUse(); }
}
