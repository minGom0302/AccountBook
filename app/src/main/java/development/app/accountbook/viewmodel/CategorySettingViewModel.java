package development.app.accountbook.viewmodel;

import android.app.Activity;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import development.app.accountbook.dto.CategoryDTO;
import development.app.accountbook.model.CategorySettingModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategorySettingViewModel extends ViewModel {
    private CategorySettingModel model;
    private MutableLiveData<List<CategoryDTO>> categoryList ;
    private final MutableLiveData<List<String[]>> categoryList01 = new MutableLiveData<>();
    private final MutableLiveData<List<String[]>> categoryList02 = new MutableLiveData<>();
    private final MutableLiveData<List<CategoryDTO>> categoryListForShow = new MutableLiveData<>();
    private int categoryListInteger;

    public void setViewModel(Activity activity, LifecycleOwner owner, int cnd) {
        model = new CategorySettingModel(activity);
        categoryListInteger = cnd;
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
            if(cnd == 0 && !dto.getCategory02().equals("01") && !dto.getCode().equals("9889001") && !dto.getCode().equals("9900001")
                    && (dto.getCategory01().equals("99") || dto.getCategory01().equals("98"))) dtoList.add(dto);
            // 계좌등록을 선택했을 떄 코드값이 97, 96인 것만 셋팅
            else if(cnd == 1 && (dto.getCategory01().equals("97") || dto.getCategory01().equals("96"))) dtoList.add(dto);
        }

        categoryListForShow.setValue(dtoList);
    }

    // 카테고리 저장
    public void insertCategory(CategoryDTO dto, int categoryType) {
        categoryListInteger = categoryType;
        model.insertCategory(dto);
    }

    public void updateCategory(CategoryDTO dto, int categoryType, int seq) {
        categoryListInteger = categoryType;
        model.updateCategory(dto, seq);
    }

    public void updateCategoryOrder(List<CategoryDTO> categoryDTOList, int categoryType) {
        categoryListInteger = categoryType;
        model.updateCategoryOrder(categoryDTOList);
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
    public MutableLiveData<List<CategoryDTO>> getCategoryListForShow() { return categoryListForShow; }
    public boolean getSpendingTypeUse() { return model.getSpendingTypeUse(); }
    public MutableLiveData<List<CategoryDTO>> getCategoryList() { return categoryList; }
    public boolean getIsChangeCa() { return model.getIsChangeCa(); }
    public void setIsChangeCa(boolean isChangeCa) { model.setIsChangeCa(isChangeCa); }
}
