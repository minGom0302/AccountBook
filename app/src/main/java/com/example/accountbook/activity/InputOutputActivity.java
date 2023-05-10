package com.example.accountbook.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.accountbook.R;
import com.example.accountbook.adapter.CategoryListAdapter;
import com.example.accountbook.adapter.SpinnerAdapter;
import com.example.accountbook.databinding.ActivityInputOutputBinding;
import com.example.accountbook.dto.CategoryDTO;
import com.example.accountbook.dto.MoneyDTO;
import com.example.accountbook.viewmodel.CategorySettingViewModel;
import com.example.accountbook.viewmodel.SaveMoneyViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputOutputActivity extends AppCompatActivity {
    private ActivityInputOutputBinding binding;
    private SaveMoneyViewModel saveMoneyViewModel;
    private CategorySettingViewModel categoryViewModel;
    private TextView choiceTv;
    private String date, in_sp;
    private int settingsSeq;
    private String[] newValueArray, newCodeArray;
    private List<CategoryDTO> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_input_output);

        init();
    }


    // 초기설정
    private void init() {
        // intent 를 통해 받아온 날짜 저장
        Intent intent = getIntent();
        date = intent.getStringExtra("date");

        // 뷰 모델 설정
        saveMoneyViewModel = new ViewModelProvider(this).get(SaveMoneyViewModel.class);
        saveMoneyViewModel.setMoneyInfoViewModel(this, this, date, 98);
        saveMoneyViewModel.getIoMoneyLiveData().observe(this, this::setRecyclerView);
        categoryViewModel = new ViewModelProvider(this).get(CategorySettingViewModel.class);
        categoryViewModel.setViewModel(this, this, 0);
        // 카테고리 메뉴 가져와서 카드/계좌 분류하고 리스트도 가져옴 > 팝업 띄울 때 넘겨준다.
        categoryViewModel.getCategoryList().observe( this, categoryList -> {
            this.categoryList = categoryList;
            String[] codeArray = new String[categoryList.size()];
            String[] valueArray = new String[categoryList.size()];
            int size = 1;
            codeArray[0] = "";
            valueArray[0] = "";
            for(int i=0; i<categoryList.size(); i++) {
                CategoryDTO dto = categoryList.get(i);
                if(dto.getCategory01().equals("97") || dto.getCategory01().equals("96")) {
                    codeArray[size] = String.valueOf(dto.getSeq());
                    valueArray[size] = dto.getContents();
                    size++;
                }
            }
            newCodeArray = Arrays.copyOf(codeArray, size);
            newValueArray = Arrays.copyOf(valueArray, size);
        });

        // 상단 날짜 설정
        binding.ioDateTv.setText(date);

        binding.ioCloseBtn.setOnClickListener(v ->
            mShowDialog()
        );
    }


    // 리사이클러뷰 셋팅 > money live data 감지하여 변경사항 발생 시 실행되는 것
    private void setRecyclerView(List<MoneyDTO> dtoList) {
        boolean cateLayoutUse = categoryViewModel.getSpendingTypeUse(); // 고정비, 변동비, 준변동비 구분 사용 여부
        List<MoneyDTO> incomeList = new ArrayList<>();
        List<MoneyDTO> exList01 = new ArrayList<>();
        List<MoneyDTO> exList02 = new ArrayList<>();
        List<MoneyDTO> exList03 = new ArrayList<>();

        for(MoneyDTO dto : dtoList) {
            // 종류별로 나눠서 담기
            if(dto.getCategory01().equals("99")) {
                incomeList.add(dto);
            } else if(dto.getCategory01().equals("98")) {
                if(cateLayoutUse) {
                    // 구분 사용
                    switch (dto.getCategory02()) {
                        case "90":
                            exList01.add(dto);
                            break;
                        case "89":
                            exList02.add(dto);
                            break;
                        case "88":
                            exList03.add(dto);
                            break;
                    }
                } else {
                    // 구분 미사용
                    exList01.add(dto);
                }
            }
        }

        CategoryListAdapter incomeAdapter = new CategoryListAdapter(incomeList, 2);
        incomeAdapter.setOnItemClickListener((v, settingsCode) ->
                lunchPopup(settingsCode, "99", (TextView) v)
        );
        binding.ioIncomeRv.setAdapter(incomeAdapter);
        binding.ioIncomeRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if(cateLayoutUse) {
            // 구분 사용 > 보여줄 화면 선택, recyclerview 3개 띄우기
            binding.ioRv01.setVisibility(View.VISIBLE);
            binding.ioRv02.setVisibility(View.GONE);

            CategoryListAdapter exAdapter01 = new CategoryListAdapter(exList01, 2);
            CategoryListAdapter exAdapter02 = new CategoryListAdapter(exList02, 2);
            CategoryListAdapter exAdapter03 = new CategoryListAdapter(exList03, 2);
            exAdapter01.setOnItemClickListener((v, settingsCode) ->
                    lunchPopup(settingsCode, "98", (TextView) v)
            );
            exAdapter02.setOnItemClickListener((v, settingsCode) ->
                    lunchPopup(settingsCode, "98", (TextView) v)
            );
            exAdapter03.setOnItemClickListener((v, settingsCode) ->
                    lunchPopup(settingsCode, "98", (TextView) v)
            );
            binding.ioExpendingRv01.setAdapter(exAdapter01);
            binding.ioExpendingRv02.setAdapter(exAdapter02);
            binding.ioExpendingRv03.setAdapter(exAdapter03);
            binding.ioExpendingRv01.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            binding.ioExpendingRv02.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            binding.ioExpendingRv03.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        } else {
            // 구분 미사용 > 보여줄 화면 선택, recyclerview 1개 선택
            binding.ioRv01.setVisibility(View.GONE);
            binding.ioRv02.setVisibility(View.VISIBLE);

            CategoryListAdapter exAdapter01 = new CategoryListAdapter(exList01, 2);
            exAdapter01.setOnItemClickListener((v, settingsCode) -> {
                lunchPopup(settingsCode, "98", (TextView) v);
            });
            binding.ioRv02.setAdapter(exAdapter01);
            binding.ioRv02.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
    }


    // Popup 띄우기
    private void lunchPopup(String settingsCode, String in_sp, TextView choiceTv) {
        this.in_sp = in_sp;
        this.choiceTv = choiceTv;

        String contents = "";
        for(CategoryDTO dto : categoryList) {
            if(dto.getCode().equals(settingsCode)) {
                contents = dto.getContents();
                this.settingsSeq = dto.getSeq();
            }
        }

        Intent intent = new Intent(this, Popup_InputOutput.class);
        intent.putExtra("contents", contents);
        intent.putExtra("codeArray", newCodeArray);
        intent.putExtra("valueArray", newValueArray);

        returnPopupActivity.launch(intent);
    }


    // 다이얼로그 띄우기
    private void mShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setTitle("안내").setMessage("해당 화면을 닫으시겠습니까?");
        builder.setPositiveButton("예", ((dialogInterface, i) -> {
            Intent intent = new Intent();
            intent.putExtra("date", date);
            setResult(RESULT_OK, intent);
            finish();
        }));
        builder.setNegativeButton("아니오", (((dialogInterface, i) -> {  })));

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        });
        dialog.show();
    }


    // startActivityForResult가 deprecated 되어 사용하지 않고 아래 방식으로 사용함 > 비밀번호 변경에 관한 건
    private final ActivityResultLauncher<Intent> returnPopupActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK) {
            assert result.getData() != null;
            int bankSeq = result.getData().getIntExtra("bankSeq", 0);
            String money = result.getData().getStringExtra("money");
            String memo = result.getData().getStringExtra("memo");

            saveMoneyViewModel.insertMoneyInfo(settingsSeq, bankSeq, in_sp, date, money, memo, 1);

            String be = choiceTv.getText().toString().replaceAll(",", "");
            String af = new DecimalFormat("#,###").format(Integer.parseInt(be) + Integer.parseInt(money));
            choiceTv.setText(af);
        }
    });


    @Override
    public void onBackPressed() {
        mShowDialog();
    }
}