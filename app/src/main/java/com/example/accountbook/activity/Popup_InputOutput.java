package com.example.accountbook.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.accountbook.R;
import com.example.accountbook.adapter.SpinnerAdapter;
import com.example.accountbook.databinding.ActivityPopupInputOutputBinding;
import com.example.accountbook.dto.CategoryDTO;
import com.example.accountbook.viewmodel.CategorySettingViewModel;
import com.example.accountbook.viewmodel.SaveMoneyViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Popup_InputOutput extends Activity implements TextWatcher {
    private ActivityPopupInputOutputBinding binding;
    private String[] codeArray;
    private String bankSeq;
    private String result = "";
    private InputMethodManager imm;
    private final DecimalFormat commaFormat = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_popup_input_output);

        init();
    }


    private void init() {
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        // 넘긴 값 셋
        Intent intent = getIntent();
        String[] valueArray = intent.getStringArrayExtra("valueArray");
        codeArray = intent.getStringArrayExtra("codeArray");

        // 스피너 설정
        SpinnerAdapter categoryAdapter01 = new SpinnerAdapter(codeArray, valueArray, this, 0);
        binding.popupIoSpinner.setAdapter(categoryAdapter01);
        binding.popupIoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bankSeq = codeArray[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        // 내용 설정
        binding.popupIoContentsTv.setText(intent.getStringExtra("contents"));

        // 금액 입력 시 금액 콤마 찍기
        binding.popupIoMoneyEt.addTextChangedListener(this);

        // 클릭 이벤트
        binding.popupIoLayout.setOnClickListener(v -> hideKeyboard());
        binding.popupIoCloseBtn.setOnClickListener(v -> mShowDialog(1, "입력을 멈추고 해당 화면을 닫으시겠습니까?"));
        binding.popupIoOkBtn.setOnClickListener(v -> mShowDialog(0, "입력한 내용으로 저장하시겠습니까?"));
    }


    // 다이얼로그 띄우기
    private void mShowDialog(int cnd, String msg) {
        hideKeyboard();
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setTitle("안내").setTitle(msg);
        builder.setPositiveButton("예", ((dialogInterface, i) -> {
            if(cnd == 0) {
                saveInfo();
            } else if(cnd == 1) {
                finish();
            }
        }));
        builder.setNegativeButton("아니오", (((dialogInterface, i) -> {  })));

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        });
        dialog.show();
    }


    private void saveInfo() {
        //이제 여기서 완료하면 bankSeq, memo, money 넘기기
        String money = binding.popupIoMoneyEt.getText().toString().replaceAll(",", "");
        String memo = binding.popupIoMemoEt.getText().toString();
        if(bankSeq.equals("")) bankSeq = "0"; // 빈칸 선택했을 경우 0으로 저장
        Intent intent = new Intent();
        intent.putExtra("bankSeq", Integer.parseInt(bankSeq));
        intent.putExtra("money", money);
        intent.putExtra("memo", memo);
        setResult(RESULT_OK, intent);
        finish();
    }

    // 키보드 내리기
    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(binding.popupIoMemoEt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(binding.popupIoMoneyEt.getWindowToken(), 0);
    }


    // TextWatcher : 금액에 콤마 찍기
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {  }
    @Override
    public void afterTextChanged(Editable editable) {  }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)){
            result = commaFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",","")));
            binding.popupIoMoneyEt.setText(result);
            binding.popupIoMoneyEt.setSelection(result.length());
        }
    }


    // 화면 밖 터치 시 아무 작동 안하도록 하기
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideKeyboard();
        return event.getAction() != MotionEvent.ACTION_OUTSIDE;
    }


    @Override
    public void onBackPressed() {
        mShowDialog(1, "입력을 멈추고 해당 화면을 닫으시겠습니까?");
    }
}