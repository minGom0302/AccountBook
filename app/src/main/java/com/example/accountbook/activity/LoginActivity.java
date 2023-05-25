package com.example.accountbook.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.accountbook.R;
import com.example.accountbook.databinding.ActivityLoginBinding;
import com.example.accountbook.item.BackspaceHandler;
import com.example.accountbook.item.RetrofitClient;
import com.example.accountbook.viewmodel.UserViewModel;

public class LoginActivity extends AppCompatActivity implements Animation.AnimationListener {
    private ActivityLoginBinding binding;
    private ProgressDialog loading;
    private UserViewModel userModel;
    private InputMethodManager imm;
    private BackspaceHandler bsh;
    private String userId;
    private boolean isSaveId, isAutoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RetrofitClient.setGsonAndRetrofit();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        init();
    }


    // 초기 설정
    private void init() {
        setModel(); // 뷰모델 설정
        bsh = new BackspaceHandler(this);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        // layout 클릭 시 키보드 내리기
        binding.loginLayout.setOnClickListener(v -> hideKeyboard());

        // 클릭 감지하여 값 설정
        binding.loginAutoLoginCb.setOnClickListener(checkboxClickListener);
        binding.loginSaveIdCb.setOnClickListener(checkboxClickListener);

        // 비밀번호 입력 후 엔터 누르면 바로 로그인 실행
        binding.loginPwEt.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                login();
            }
            return true;
        });

        // 회원가입 버튼
        binding.loginRegisterBtn.setOnClickListener(v -> {
            hideKeyboard();
            startActivity(new Intent(this, SignUpActivity.class));
        });

        // 정보찾기 버튼
        binding.loginFIndInfoBtn.setOnClickListener(v -> {
            hideKeyboard();
            startActivity(new Intent(this, FindActivity.class));
        });

        // 로그인 버튼 설정
        binding.loginBtn.setOnClickListener(v -> login());

        // 에니멘이션 설정 > 초반 이미지 밑에서 올라온 다음 화면에 로그인 입력화면 보여줌
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        animation.setAnimationListener(this);
        binding.loginImgView.startAnimation(animation);
    }


    // view model 설정
    private void setModel() {
        userModel = new ViewModelProvider(this).get(UserViewModel.class);

        // login 정보가 바뀔 경우 실행
        userModel.setUserViewModel(this);
        userModel.getUserInfo().observe(this, userInfoDTO -> {
            loading.dismiss();
            if(userInfoDTO != null) { // 로그인 성공
                Toast.makeText(this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else { // 로그인 실패
                Toast.makeText(this, "아이디 혹은 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        // 체크박스 설정
        isSaveId = userModel.getSaveId();
        isAutoLogin = userModel.getAutoLogin();
        binding.loginSaveIdCb.setChecked(isSaveId);
        binding.loginAutoLoginCb.setChecked(isAutoLogin);
    }


    // login method
    private void login() {
        hideKeyboard();
        userId = binding.loginIdEt.getText().toString();
        String userPw = binding.loginPwEt.getText().toString();
        if(userPw.length() == 0) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        userModel.login(userId, userPw, isAutoLogin, isSaveId);
        loading = ProgressDialog.show(this, "로그인중 ...", "잠시만 기다려주세요...", true, false);
    }


    // 체크박스 변화 감지 > true/false 저장
    View.OnClickListener checkboxClickListener = view -> {
        if (binding.loginAutoLoginCb.equals(view)) {
            isAutoLogin = binding.loginAutoLoginCb.isChecked();
            if(isAutoLogin) {
                binding.loginSaveIdCb.setEnabled(false);
                binding.loginSaveIdCb.setChecked(true);
            } else {
                binding.loginSaveIdCb.setEnabled(true);
            }
        } else if(binding.loginSaveIdCb.equals(view)) {
            isSaveId = binding.loginSaveIdCb.isChecked();
        }
    };


    // 키보드 내리기
    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(binding.loginIdEt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(binding.loginPwEt.getWindowToken(), 0);
    }


    // Animation 설정
    @Override
    public void onAnimationStart(Animation animation) {
        // 시작할 때
        isAutoLogin = userModel.getAutoLogin();
        isSaveId = userModel.getSaveId();
        if(isAutoLogin) {
            loading = ProgressDialog.show(this, "로그인중 ...", "잠시만 기다려주세요...", true, false);
        }
    }
    @Override
    public void onAnimationEnd(Animation animation) {
        // 종료될 떄
        binding.loginEtLayout.setVisibility(View.VISIBLE);

        if(isAutoLogin) {
            Toast.makeText(this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            loading.dismiss();
        } else if(isSaveId) {
            userId = userModel.getUserId();
            binding.loginIdEt.setText(userId);
        }
    }
    @Override
    public void onAnimationRepeat(Animation animation) {
        // 반복될 때
    }


    // 뒤로가기 클릭 시 막기
    @Override
    public void onBackPressed() {
        bsh.onBackPressed("'뒤로가기'를 한번 더 누르면 종료됩니다.");
    }
}