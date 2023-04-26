package com.example.accountbook.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.accountbook.R;
import com.example.accountbook.adapter.SpinnerAdapter;
import com.example.accountbook.databinding.FragmentCategorySettingBinding;
import com.example.accountbook.viewmodel.CategorySettingViewModel;

import java.util.ArrayList;
import java.util.List;

public class CategorySettingFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
    private CategorySettingViewModel viewModel;
    private FragmentCategorySettingBinding binding;
    private List<String[]> categoryList01;
    private List<String[]> categoryList02;
    private String payDay, category01, category02;
    private InputMethodManager imm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_setting, container, false);

        init();

        return binding.getRoot();
    }


    // 초기값 설정
    private void init() {
        imm = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);

        // 클릭 이벤트 설정
        binding.settingLayout.setOnClickListener(v -> hideKeyboard());

        // 라디오버튼 선택에 따른 스피너1 설정
        binding.f04RbGroup.setOnCheckedChangeListener(this);

        // 뷰모델 연결
        viewModel = new ViewModelProvider(this).get(CategorySettingViewModel.class);
        viewModel.setViewModel(getActivity());

        // 버튼 클릭 이벤트
        binding.f04NewBtn.setOnClickListener(v -> {
            hideKeyboard();
            Toast.makeText(getContext(), "새로 입력 클릭", Toast.LENGTH_SHORT).show();
        });
        binding.f04SaveBtn.setOnClickListener(v -> {
            hideKeyboard();
            Toast.makeText(getContext(), "code : " + category01 + category02, Toast.LENGTH_SHORT).show();
        });
        spinnerSetting();
    }


    // 스피너 설정
    private void spinnerSetting() {
        // 날짜 스피너 설정
        List<String[]> dayList = viewModel.getDayList();
        SpinnerAdapter dayAdapter = new SpinnerAdapter(dayList.get(0), dayList.get(1), getContext());
        binding.f04SpinnerDay.setAdapter(dayAdapter);
        // 스피너들 설정
        binding.f04SpinnerDay.setDropDownVerticalOffset(80);
        binding.f04SpinnerCategory01.setDropDownVerticalOffset(80);
        binding.f04SpinnerCategory02.setDropDownVerticalOffset(80);

        // 값 변경 관찰 > spinner1의 값
        viewModel.getCategoryList01().observe(getViewLifecycleOwner(), categoryList01 -> {
            this.categoryList01 = categoryList01;
            setCategory(0);
        });
        // 값 변경 관찰 > spinner2의 값
        viewModel.getCategoryList02().observe(getViewLifecycleOwner(), categoryList02 -> {
            this.categoryList02 = categoryList02;
            setCategory(1);
        });

        // 날짜 스피너 선택에 따른 행동 설정
        binding.f04SpinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                hideKeyboard();
                payDay = dayList.get(0)[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                hideKeyboard();
            }
        });
        // 수입/지출/계좌/카드 선택에 따른 행동 설정
        binding.f04SpinnerCategory01.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category01 = categoryList01.get(0)[i];
                // category2 설정 > 지출 외 모두 날짜를 가져와 00값 설정
                viewModel.setCategoryList02(0, Integer.parseInt(categoryList01.get(0)[i]));
                // 지출이 아닐 경우 화면에서 가릴 거 가리기
                int visible;
                if(category01.equals("98")) {
                    visible = View.VISIBLE;
                } else {
                    visible = View.INVISIBLE;
                    binding.f04SpinnerDay.setSelection(0);
                }
                binding.f04PayDayTv.setVisibility(visible);
                binding.f04SpinnerDay.setVisibility(visible);
                binding.f04SpinnerCategory02.setVisibility(visible);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        // 고정비/변동비/준변동비 선택에 따른 행동 설정 > 지출일 경우에만 보임
        binding.f04SpinnerCategory02.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category02 = categoryList02.get(0)[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }


    // 변경사항 발생 시 스피너 값들 설정
    private void setCategory(int type) {
        if(type == 0) {
            SpinnerAdapter categoryAdapter01 = new SpinnerAdapter(categoryList01.get(0), categoryList01.get(1), getContext());
            binding.f04SpinnerCategory01.setAdapter(categoryAdapter01);
        } else if(type == 1) {
            SpinnerAdapter categoryAdapter02 = new SpinnerAdapter(categoryList02.get(0), categoryList02.get(1), getContext());
            binding.f04SpinnerCategory02.setAdapter(categoryAdapter02);
        }
    }


    // 라디오 버튼 선택에 따른 화면 설정
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        binding.f04AllCheckCb.setChecked(false);
        hideKeyboard();
        if(binding.f04Rb01.isChecked()) {
            viewModel.setCategoryList01(0);
        } else if(binding.f04Rb02.isChecked()) {
            viewModel.setCategoryList01(1);
        }
    }


    // 키보드 내리기
    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(binding.f04ContentsEt.getWindowToken(), 0);
    }
}