package com.example.accountbook.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.accountbook.R;
import com.example.accountbook.databinding.ActivityPopupTransferBinding;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Popup_Transfer extends Activity implements TextWatcher {
    private ActivityPopupTransferBinding binding;
    private InputMethodManager imm;
    private String result = "";
    private String date;
    private final DecimalFormat commaFormat = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_popup_transfer);

        init();
    }


    // 초기 화면 설정
    @SuppressLint("SimpleDateFormat")
    private void init() {
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        // 오늘 날짜로 설정
        binding.popupTransferDateTv.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));

        binding.popupTransferMoneyEt.addTextChangedListener(this);
        binding.popupTransferDateTv.setOnClickListener(v -> dateChoice());
        binding.popupTransferOkBtn.setOnClickListener(v -> showDialog(0, "입력한 정보로 저장하시겠습니까?"));
        binding.popupTransferCloseBtn.setOnClickListener(v -> showDialog(1, "해당 창을 종료하시겠습니까?"));
    }


    // DatePicker 열고 선택 시 날짜 셋팅하게 함
    private void dateChoice() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> {
            // month(달)은 0부터 시작하기에 +1을 해준다.
            // 변수 month(int) 의 1월은 0임으로 +1을 해준다.
            String m = String.valueOf(month + 1);
            String d = String.valueOf(day);
            // 달과 월을 두자리 형태로 만들기 위해 작성
            // 1월 1일 경우 0101로 표시
            if(month < 10) {
                m = "0" + m;
            }
            if(day < 10) {
                d = "0" + d;
            }
            date = year + "-" + m + "-" + d;

            binding.popupTransferDateTv.setText(date);
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }


    // 다이어로그 화면에 띄우기
    private void showDialog(int cnd, String msg) {
        hideKeyboard();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내").setMessage(msg);
        builder.setPositiveButton("예", ((dialogInterface, i) -> {
            if(cnd == 0) {
                Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
            else if(cnd == 1) finish();
        }));
        builder.setNegativeButton("아니오", ((dialogInterface, i) -> { }));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    // 키보드 내리기
    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(binding.popupTransferMoneyEt.getWindowToken(), 0);
    }


    // 화면 밖 터치 시 아무 작동 안하도록 하기
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideKeyboard();
        return event.getAction() != MotionEvent.ACTION_OUTSIDE;
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
            binding.popupTransferMoneyEt.setText(result);
            binding.popupTransferMoneyEt.setSelection(result.length());
        }
    }
}