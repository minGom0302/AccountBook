package com.example.accountbook.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.accountbook.R;
import com.example.accountbook.adapter.CategoryAdapter_01;
import com.example.accountbook.adapter.SpinnerAdapter;
import com.example.accountbook.databinding.FragmentCategorySettingBinding;
import com.example.accountbook.dto.CategoryDTO;
import com.example.accountbook.viewmodel.CategorySettingViewModel;
import com.example.accountbook.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CategorySettingFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
    private CategorySettingViewModel viewModel;
    private UserViewModel userViewModel;
    private FragmentCategorySettingBinding binding;
    private List<String[]> categoryList01;
    private List<String[]> categoryList02;
    private List<String[]> dayList;
    private List<CategoryDTO> categoryDTOS;
    private String payDay, category01, category02;
    private InputMethodManager imm;
    private CategoryAdapter_01 adapter_01;
    private int rbCheckValue = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_setting, container, false);

        init();

        return binding.getRoot();
    }


    // 초기값 설정
    @SuppressLint({"NotifyDataSetChanged", "DefaultLocale"})
    private void init() {
        imm = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
        adapter_01 = new CategoryAdapter_01(null);
        // 클릭 이벤트 설정
        binding.settingLayout.setOnClickListener(v -> hideKeyboard());

        // 라디오버튼 선택에 따른 스피너1 설정
        binding.f04RbGroup.setOnCheckedChangeListener(this);

        // 뷰모델 연결 > Fragment끼리 동일한 ViewModel 사용하기 위해 owner 를 아래와 같이 설정
        viewModel = new ViewModelProvider(requireActivity()).get(CategorySettingViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        viewModel.setViewModel(getActivity(), getViewLifecycleOwner());

        spinnerSetting();

        // recyclerview 보여주기 위해 list 감지
        viewModel.getCategoryListForShow().observe(getViewLifecycleOwner(), categoryDTOS -> {
            this.categoryDTOS = categoryDTOS;
            adapter_01 = new CategoryAdapter_01(categoryDTOS);
            binding.f04Recyclerview.setAdapter(adapter_01);
            binding.f04Recyclerview.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

            binding.f04SaveBtn.setEnabled(false);
            binding.f04ContentsEt.setText("");
            binding.f04ContentsEt.setEnabled(false);
            binding.f04SpinnerCategory01.setEnabled(false);
            binding.f04SpinnerCategory02.setEnabled(false);
            binding.f04SpinnerDay.setEnabled(false);

            // recyclerview item 클릭 시 발생할 이벤트 > adapter 작성한 내용을 가져와서 작성
            adapter_01.setOnItemClickListener((v, categoryDTO) -> {
                binding.f04SpinnerDay.setSelection(Arrays.binarySearch(dayList.get(1), categoryDTO.getPayDay()));
                binding.f04SpinnerCategory01.setSelection(Arrays.asList(categoryList01.get(0)).indexOf(categoryDTO.getCategory01()));
                binding.f04SpinnerCategory02.setSelection(Arrays.asList(categoryList02.get(0)).indexOf(categoryDTO.getCategory02()));
                binding.f04ContentsEt.setText(categoryDTO.getContents());
                binding.f04ContentsEt.setEnabled(false);
                binding.f04SpinnerCategory01.setEnabled(false);
                binding.f04SpinnerCategory02.setEnabled(false);
                binding.f04SpinnerDay.setEnabled(false);
                binding.f04SaveBtn.setEnabled(false);
            });
            // recyclerview delete btn 클릭 시 발생할 이벤트
            adapter_01.setOnItemDeleteListener((v, categoryDTO) ->
                showDialog(0, "'" + categoryDTO.getContents() + "' 을(를) 삭제하시겠습니까?", categoryDTO)
            );
        });

        // 버튼 클릭 이벤트
        binding.f04NewBtn.setOnClickListener(v -> {
            hideKeyboard();
            binding.f04ContentsEt.setText("");
            binding.f04ContentsEt.setEnabled(true);
            binding.f04SpinnerCategory01.setEnabled(true);
            binding.f04SpinnerCategory02.setEnabled(true);
            binding.f04SpinnerDay.setEnabled(true);
            binding.f04SaveBtn.setEnabled(true);
            binding.f04SpinnerDay.setSelection(0);
            binding.f04SpinnerCategory01.setSelection(0);
            binding.f04SpinnerCategory02.setSelection(0);
        });
        binding.f04SaveBtn.setOnClickListener(v -> { // 새로운 카테고리/계좌 정보 저장
            hideKeyboard();
            if(binding.f04ContentsEt.getText().length() == 0) {
                showDialog(99, "내용을 입력해주시기 바랍니다.", null);
                return;
            }
            CategoryDTO insertDto = new CategoryDTO(userViewModel.getUserSeq(), category01 + category02 + String.format("%02d", categoryDTOS.size()+1)
                                                    , category01 , category02, binding.f04ContentsEt.getText().toString(), payDay);
            showDialog(2, "입력한 정보로 저장하시겠습니까?", insertDto);
        });
    }


    // 스피너 설정
    private void spinnerSetting() {
        // 날짜 스피너 설정
        dayList = viewModel.getDayList();
        SpinnerAdapter dayAdapter = new SpinnerAdapter(dayList.get(0), dayList.get(1), getContext(), 1);
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
                payDay = dayList.get(1)[i];
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
            SpinnerAdapter categoryAdapter01 = new SpinnerAdapter(categoryList01.get(0), categoryList01.get(1), getContext(), 1);
            binding.f04SpinnerCategory01.setAdapter(categoryAdapter01);
        } else if(type == 1) {
            SpinnerAdapter categoryAdapter02 = new SpinnerAdapter(categoryList02.get(0), categoryList02.get(1), getContext(), 1);
            binding.f04SpinnerCategory02.setAdapter(categoryAdapter02);
        }
    }


    // 라디오 버튼 선택에 따른 화면 설정
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        hideKeyboard();
        if(binding.f04Rb01.isChecked()) {
            viewModel.setCategoryList01(0); // 대분류 셋팅 (수입, 지출)
            viewModel.setCategoryList(0); // recyclerview setting
            rbCheckValue = 0;
        } else if(binding.f04Rb02.isChecked()) {
            viewModel.setCategoryList01(1); // 대분류 셋팅 (계좌, 카드)
            viewModel.setCategoryList(1); // recyclerview setting
            rbCheckValue = 1;
        }
        binding.f04ContentsEt.setText("");
        binding.f04ContentsEt.setEnabled(true);
        binding.f04SpinnerCategory01.setEnabled(true);
        binding.f04SpinnerCategory02.setEnabled(true);
        binding.f04SpinnerDay.setEnabled(true);
    }


    // 키보드 내리기
    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(binding.f04ContentsEt.getWindowToken(), 0);
    }


    // Dialog 보여주기
    private void showDialog(int cnd, String msg, CategoryDTO dto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
        builder.setTitle("안내").setMessage(msg);
        builder.setPositiveButton("예", ((dialogInterface, i) -> {
            if(cnd == 0) showDialog(1, "'" + dto.getContents() + "' (으)로 등록한 가계부 정보도 모두 삭제됩니다.\n그래도 삭제하시겠습니까?", dto);
            else if(cnd == 1) viewModel.deleteCategory(dto, rbCheckValue);
            else if(cnd == 2) viewModel.insertCategory(dto, rbCheckValue);
        }));
        builder.setNegativeButton("아니오", ((dialogInterface, i) -> { }));

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        });
        dialog.show();
    }
}