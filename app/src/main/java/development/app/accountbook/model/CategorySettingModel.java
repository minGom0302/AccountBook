package development.app.accountbook.model;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import development.app.accountbook.dto.CategoryDTO;
import development.app.accountbook.item.RetrofitAPI;
import development.app.accountbook.item.RetrofitClient;
import development.app.accountbook.item.SharedPreferencesClient;
import development.app.accountbook.R;

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
        } else { // 카테고리가 지출이 아닐경우 카테고리2가 가려지기 때문에 셋팅값을 00으로 맞추기
            String[] dayCode = {"00"};
            String[] dayValue = {""};

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
                    List<CategoryDTO> dtoList = new ArrayList<>();
                    for(CategoryDTO dto : response.body()) {
                        if(dto.getEndDay() == 999999) {
                            dto.setStrEndDay("");
                        } else {
                            StringBuilder buffer = new StringBuilder(String.valueOf(dto.getEndDay()));
                            buffer.insert(4, "-");
                            dto.setStrEndDay(buffer.toString());
                        }
                        dtoList.add(dto);
                    }

                    categoryList.setValue(dtoList);
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
                spClient.setIsChangeCa(true);
                spClient.setIsChangeCal(true);
                Toast.makeText(activity, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Toast.makeText(activity, "잠시 후 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // insert category by api
    public void insertCategory(CategoryDTO dto) {
        api.insertCategoryInfo(dto.getUserSeq(), dto.getCode(), dto.getCategory01(), dto.getCategory02(), dto.getContents(), dto.getEndDay(), dto.getOrderSeq()).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                setCategoryList();
                spClient.setIsChangeCa(true);
                Toast.makeText(activity, "저장되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Toast.makeText(activity, "잠시 후 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // update category by api
    public void updateCategory(CategoryDTO dto, int seq) {
        api.updateCategoryInfo(seq, dto.getCode(), dto.getCategory01(), dto.getCategory02(), dto.getContents(), dto.getEndDay(), dto.getOrderSeq()).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                setCategoryList();
                spClient.setIsChangeCa(true);
                Toast.makeText(activity, "수정되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Toast.makeText(activity, "잠시 후 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateCategoryOrder(List<CategoryDTO> categoryDTOList) {
        api.updateCategoryOrder(categoryDTOList).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                Toast.makeText(activity, "수정되었습니다.", Toast.LENGTH_SHORT).show();
                setCategoryList();
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
    public boolean getIsChangeCa() { return spClient.getIsChangeCa(); }
    public void setIsChangeCa(boolean isChangeCa) { spClient.setIsChangeCa(isChangeCa); }
    // 중분류 사용 여부 저장
    public void setSpendingTypeUse(boolean isUse) {
        spClient.setSpendingTypeUse(isUse);
    }
}
