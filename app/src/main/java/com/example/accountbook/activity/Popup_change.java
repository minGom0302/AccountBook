package com.example.accountbook.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
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
            returnSet(0, "입력한 내용으로 저장하시겠습니까?")
        );
        binding.popupChangeCloseBtn.setOnClickListener(v -> {
            hideKeyboard();
            finish();
        });
    }

    private void returnSet(int cnd, String msg) {
        hideKeyboard();
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setTitle("안내").setMessage(msg);
        builder.setPositiveButton("예", ((dialogInterface, i) -> {
            if(cnd == 0) {
                Intent mIntent = new Intent();
                mIntent.putExtra("nickName", binding.popupChangeNicknameEt.getText().toString());
                setResult(RESULT_OK, mIntent);
                finish();
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideKeyboard();
        return event.getAction() != MotionEvent.ACTION_OUTSIDE;
    }

    // 키보드 내리기
    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(binding.popupChangeNicknameEt.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        returnSet(1, "해당 창을 종료하시겠습니까?");
    }
}