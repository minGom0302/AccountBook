package com.example.accountbook.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.accountbook.R;
import com.example.accountbook.databinding.ActivitySignupBinding;
import com.example.accountbook.dto.UserInfoDTO;
import com.example.accountbook.item.SendSMS;
import com.example.accountbook.viewmodel.UserViewModel;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private InputMethodManager imm;
    private Pattern pwPattern;
    private UserViewModel userModel;
    private ProgressDialog loading;
    private Timer timer;
    private UserInfoDTO userInfoDTO;
    private String id, phone;
    private SendSMS sendSMS;
    private boolean idIsUse = false;
    private boolean pwEquals = false;
    private boolean pwPatternOk = false;
    private boolean isAuth = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        init();
    }

    private void init() {
        setModel();

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        String pwRegex = "^(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$";
        pwPattern = Pattern.compile(pwRegex);
        sendSMS = new SendSMS();

        // 비밀번호 정규식 및 일치 여부 설정
        binding.signupPwEt.addTextChangedListener(textWatcher);
        binding.signupPwAgainEt.addTextChangedListener(textWatcher);
        // 연락처 하이픈 자동 입력
        binding.signupPhoneEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // 버튼 클릭 이벤트 정리
        binding.signupDuplicationBtn.setOnClickListener(v -> btnClickEvent(0));
        binding.signupAuthSendBtn.setOnClickListener(v -> btnClickEvent(1));
        binding.signupAuthOk.setOnClickListener(v -> btnClickEvent(2));
        binding.signupCb01MoreBtn.setOnClickListener(v -> btnClickEvent(3));
        binding.signupSignupBtn.setOnClickListener(v -> btnClickEvent(4));
    }

    private void setModel() {
        userModel = new ViewModelProvider(this).get(UserViewModel.class);

        userModel.setUserViewModel(this);

        userModel.getUserInfo().observe(this, userInfoDTO -> {
            loading.dismiss();
            if(userInfoDTO == null) {
                showDialog(0, "해당 아이디를 사용하시겠습니까?", "예");
            } else {
                showDialog(99, "중복된 아이디입니다.", "확인");
            }
        });
    }


    // 버튼 클릭 이벤트
    private void btnClickEvent(int cnd) {
        hideKeyboard();
        switch (cnd) {
            case 0 : // duplication 중복 체크
                id = binding.signupIdEt.getText().toString();
                if(id.length() == 0) {
                    showDialog(99, "아이디를 입력해주시기 바랍니다.", "확인");
                } else {
                    userModel.idCheck(binding.signupIdEt.getText().toString());
                    loading = ProgressDialog.show(this, "확인중 ...", "잠시만 기다려주세요...", true, false);
                }
                break;
            case 1 :
                phone = binding.signupPhoneEt.getText().toString();//.replaceAll("-", "");
                if(phone.length() != 13) {
                    showDialog(98, "핸드폰 번호를 입력해주시기 바랍니다.", "확인");
                } else {
                    codeRequest();
                }
                break;
            case 2 :
                isAuth = sendSMS.checkCode(binding.signupAuthEt.getText().toString());
                if(isAuth) {
                    Toast.makeText(this, "본인인증 완료됐습니다.", Toast.LENGTH_SHORT).show();
                    binding.signupAuthEt.setEnabled(false);
                    binding.signupAuthOk.setEnabled(false);
                    binding.signupSecondLayout.setVisibility(View.GONE);
                    timer.cancel();
                } else {
                    Toast.makeText(this, "번호를 다시 확인하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3 :
                startActivity(new Intent(this, Popup_Agree.class));
                break;
            case 4 : // sign up 회원가입 진행
                checkCondition();
                break;
        }
    }


    // 인증코드 요청
    private void codeRequest() {
        hideKeyboard();
        binding.signupSecondLayout.setVisibility(View.VISIBLE);
        binding.signupAuthLayout.setVisibility(View.VISIBLE);
        binding.signupPhoneEt.setEnabled(false);
        binding.signupAuthEt.setEnabled(true);
        binding.signupAuthOk.setEnabled(true);
        binding.signupAuthSendBtn.setEnabled(false);
        binding.signupSecondInfoTv.setText(R.string.text26);
        binding.signupAuthEt.setText("");

        sendSMS.SmsSend(this, phone);

        timer = new Timer();
        TimerTask task = new TimerTask() {
            int time = 60;
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if(time != 0) {
                        binding.signupSecondTv.setText(String.valueOf(time));
                        time--;
                    } else {
                        cancel();
                        binding.signupSecondTv.setVisibility(View.GONE);
                        binding.signupSecondInfoTv.setText(R.string.text27);
                        binding.signupAuthSendBtn.setEnabled(true);
                        binding.signupAuthSendBtn.setText(R.string.reSend);
                        binding.signupAuthEt.setEnabled(false);
                        binding.signupAuthOk.setEnabled(false);
                    }
                });
            }
        };
        timer.schedule(task, 0, 1000);
    }


    // 조건 일치 확인 후 비밀번호 변경하기
    private void checkCondition() {
        if(!idIsUse) {
            Toast.makeText(this, "아이디의 사용가능 여부를 확인해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            imm.showSoftInput(binding.signupDuplicationBtn, 0);
            return;
        } else if(!pwPatternOk) {
            Toast.makeText(this, "비밀번호 조건에 맞지 않습니다.", Toast.LENGTH_SHORT).show();
            imm.showSoftInput(binding.signupPwEt, 0);
            return;
        } else if(!pwEquals) {
            Toast.makeText(this, "비밀번호가 서로 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            imm.showSoftInput(binding.signupPwAgainEt, 0);
            return;
        } else if(binding.signupNameEt.getText().toString().equals("") || binding.signupNicknameEt.getText().toString().equals("")) {
            Toast.makeText(this, "이름 혹은 닉네임을 설정해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            return;
        } else if(!isAuth) {
            Toast.makeText(this, "본인인증을 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            return;
        } else if(!binding.signupCb01.isChecked()) {
            Toast.makeText(this, "개인정보 수집 동의를 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserId(id);
        userInfoDTO.setUserPw(binding.signupPwAgainEt.getText().toString());
        userInfoDTO.setUserName(binding.signupNameEt.getText().toString());
        userInfoDTO.setUserNickname(binding.signupNicknameEt.getText().toString());
        userInfoDTO.setUserPhone(phone.replaceAll("-", ""));
        userInfoDTO.setUserPhone(binding.signupPhoneEt.getText().toString().replaceAll("-", ""));
        userInfoDTO.setUserAgree01("1");

        showDialog(1, "입력한 내용으로 회원가입을 진행하시겠습니까?", "예");
    }


    // dialog  띄우기
    // 0 > id duplication ok, 99 > id false
    // 98 > phone 연락처 확인
    private void showDialog(int cnd, String msg, String poBtn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setTitle("안내").setMessage(msg);
        builder.setPositiveButton(poBtn, ((dialogInterface, i) -> {
            if(cnd == 0) {
                idIsUse = true;
                binding.signupIdEt.setEnabled(false);
                binding.signupDuplicationBtn.setEnabled(false);
                binding.signupIdDuplicationTv.setText(R.string.text23);
                binding.signupIdDuplicationTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue01));
            } else if(cnd == 1) {
                userModel.signUp(userInfoDTO);
            } else if(cnd == 98) {
                imm.showSoftInput(binding.signupPhoneEt, 0);
            } else if(cnd == 99) {
                imm.showSoftInput(binding.signupIdEt, 0);
            }
        }));
        if(cnd != 99) builder.setNegativeButton("아니오", ((dialogInterface, i) -> {
            if(cnd == 0) {
                imm.showSoftInput(binding.signupIdEt, 0);
            }
        }));

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        });
        dialog.show();
    }


    // 키보드 숨기기
    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(binding.signupIdEt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(binding.signupPwEt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(binding.signupPwAgainEt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(binding.signupNameEt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(binding.signupNicknameEt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(binding.signupPhoneEt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(binding.signupAuthEt.getWindowToken(), 0);
    }


    // 비밀번호 정규식 적용하기 위한 텍스트와쳐
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // 비밀번호 조건 일치 여부 확인
            Matcher pwMatcher = pwPattern.matcher(binding.signupPwEt.getText().toString());
            pwPatternOk = pwMatcher.find();

            // 두개의 비밀번호가 서로 일치하는지 확인
            if(binding.signupPwEt.getText().toString().equals(binding.signupPwAgainEt.getText().toString())) { // 일치
                binding.signupPwEqualsTv.setText(R.string.text09);
                binding.signupPwEqualsTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue01));
                pwEquals = true;
            } else { // 불일치
                binding.signupPwEqualsTv.setText(R.string.text08);
                binding.signupPwEqualsTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red01));
                pwEquals = false;
            }
        }
        @Override
        public void afterTextChanged(Editable editable) { }
    };
}