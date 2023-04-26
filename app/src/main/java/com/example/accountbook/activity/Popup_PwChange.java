package com.example.accountbook.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.accountbook.R;
import com.example.accountbook.databinding.ActivityPopupPwChangeBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Popup_PwChange extends Activity implements TextWatcher {
    private ActivityPopupPwChangeBinding binding;
    private Pattern pwPattern;
    private boolean pwEquals = false;
    private boolean pwPatternOk = false;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_popup_pw_change);

        init();
    }

    // 처음 화면 설정
    private void init() {
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        // 비밀번호 정규식 검사 셋팅 > 영문, 숫자, 알파벳 3가지를 포함해 8자리 이상이여야 됨
        String pwRegex = "^(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$";
        pwPattern = Pattern.compile(pwRegex);

        // 비밀번호 확인하는 TextWatcher 를 아래에서 작성함
        binding.popupPwChangePwEt.addTextChangedListener(this);
        binding.popupPwChangeOneMoreEt.addTextChangedListener(this);
        binding.popupPwChangeLayout.setOnClickListener(v -> hideKeyboard());
        binding.popupPwChangeOkBtn.setOnClickListener(v -> showDialog(0, "입력한 정보로 변경하시겠습니까?"));
        binding.popupPwChangeCloseBtn.setOnClickListener(v -> showDialog(1, "해당 창을 종료하시겠습니까?"));
    }


    // 다이어로그 화면에 띄우기
    private void showDialog(int cnd, String msg) {
        hideKeyboard();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내").setMessage(msg);
        builder.setPositiveButton("예", ((dialogInterface, i) -> {
            if(cnd == 0) pwChange();
            else if(cnd == 1) finish();
        }));
        builder.setNegativeButton("아니오", ((dialogInterface, i) -> { }));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    // 조건 일치 확인 후 비밀번호 변경하기
    private void pwChange() {
        if(pwEquals && pwPatternOk) {
            Toast.makeText(this, "비밀번호 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        } else if(!pwPatternOk) {
            Toast.makeText(this, "비밀번호 조건에 맞지 않습니다.", Toast.LENGTH_SHORT).show();
            imm.showSoftInput(binding.popupPwChangePwEt, 0);
        } else {
            Toast.makeText(this, "비밀번호가 서로 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            imm.showSoftInput(binding.popupPwChangeOneMoreEt, 0);
        }
    }


    // 화면 밖 터치 시 아무 작동 안하도록 하기
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideKeyboard();
        return event.getAction() != MotionEvent.ACTION_OUTSIDE;
    }


    // 키보드 내리기
    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(binding.popupPwChangeOneMoreEt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(binding.popupPwChangePwEt.getWindowToken(), 0);
    }


    // TextWatcher : 비밀번호 입력칸 두개 비교하여 일치 여부 체크, 설정한 비밀번호 조건에 맞는지 확인
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // 비밀번호 조건 일치 여부 확인
        Matcher pwMatcher = pwPattern.matcher(binding.popupPwChangePwEt.getText().toString());
        pwPatternOk = pwMatcher.find();

        // 두개의 비밀번호가 서로 일치하는지 확인
        if(binding.popupPwChangePwEt.getText().toString().equals(binding.popupPwChangeOneMoreEt.getText().toString())) { // 일치
            binding.popupPwChangeTv.setText(R.string.text09);
            binding.popupPwChangeTv.setTextColor(ContextCompat.getColor(this, R.color.blue01));
            pwEquals = true;
        } else { // 불일치
            binding.popupPwChangeTv.setText(R.string.text08);
            binding.popupPwChangeTv.setTextColor(ContextCompat.getColor(this, R.color.red01));
            pwEquals = false;
        }
    }
    @Override
    public void afterTextChanged(Editable editable) { }
}