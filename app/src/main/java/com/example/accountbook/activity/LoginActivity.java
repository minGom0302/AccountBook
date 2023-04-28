package com.example.accountbook.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.accountbook.R;
import com.example.accountbook.databinding.ActivityLoginBinding;
import com.example.accountbook.viewmodel.CategorySettingViewModel;
import com.example.accountbook.viewmodel.UserViewModel;

public class LoginActivity extends AppCompatActivity {
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        UserViewModel model = new ViewModelProvider(this).get(UserViewModel.class);

        model.setUserViewModel(this);
        model.getUserInfo().observe(this, userInfoDTO -> {
            loading.dismiss();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        binding.loginBtn.setOnClickListener(v -> {
            model.login("admin");
            loading = ProgressDialog.show(this, "로그인중 ...", "잠시만 기다려주세요...", true, false);
        });
    }
}