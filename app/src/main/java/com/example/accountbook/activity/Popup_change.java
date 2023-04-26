package com.example.accountbook.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.accountbook.R;
import com.example.accountbook.databinding.ActivityPopupChangeBinding;

public class Popup_change extends Activity {
    private ActivityPopupChangeBinding binding;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_popup_change);

        init();
    }

    private void init() {
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        binding.popupChangeOkBtn.setOnClickListener(v ->
            returnSet()
        );
        binding.popupChangeCloseBtn.setOnClickListener(v -> {
            hideKeyboard();
            finish();
        });
    }

    private void returnSet() {
        hideKeyboard();
        Intent mIntent = new Intent();
        mIntent.putExtra("nickName", binding.popupChangeNicknameEt.getText().toString());
        setResult(RESULT_OK, mIntent);
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideKeyboard();
        return event.getAction() != MotionEvent.ACTION_OUTSIDE;
    }

    // 키보드 내리기
    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(binding.popupChangeNicknameEt.getWindowToken(), 0);
    }

}