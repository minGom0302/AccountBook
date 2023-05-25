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
import com.example.accountbook.databinding.ActivityFindBinding;
import com.example.accountbook.item.SendSMS;
import com.example.accountbook.viewmodel.UserViewModel;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindActivity extends AppCompatActivity {
    private ActivityFindBinding binding;
    private UserViewModel userViewModel;
    private SendSMS sendSMS;
    private InputMethodManager imm;
    private Timer timer;
    private ProgressDialog loading;
    boolean isAuth;
    private int layoutType = 3;
    private String phone;

    private Pattern pwPattern;
    private boolean pwEquals, pwPatternOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find);

        // 첫 화면에서 버튼 선택에 따른 화면 레이아웃 셋팅
        binding.findFirstIdBtn.setOnClickListener(v -> setIdLayout());
        binding.findFirstPwBtn.setOnClickListener(v -> setPwLayout());

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        sendSMS = new SendSMS();
    }


    // find id layout setting
    private void setIdLayout() {
        layoutType = 0;
        binding.findFirstLayout.setVisibility(View.GONE);
        binding.findIdLayout.setVisibility(View.VISIBLE);

        setViewModel();

        // 연락처 하이픈 자동 입력
        binding.findIdPhoneEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        binding.findIdAuthSendBtn.setOnClickListener(v -> setBtn(0));
        binding.findIdAuthOk.setOnClickListener(v -> setBtn(1));
        binding.findIdFindBtn.setOnClickListener(v -> setBtn(2));
        binding.findIdCloseBtn.setOnClickListener(v -> setBtn(3));
        binding.findLayout.setOnClickListener(v -> hideKeyboard());
    }


    // find pw layout setting
    private void setPwLayout() {
        layoutType = 1;
        binding.findFirstLayout.setVisibility(View.GONE);
        binding.findPwLayout.setVisibility(View.VISIBLE);

        setViewModel();

        // 비밀번호 정규식 검사 셋팅 > 영문, 숫자, 알파벳 3가지를 포함해 8자리 이상이여야 됨
        String pwRegex = "^(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$";
        pwPattern = Pattern.compile(pwRegex);

        binding.findPwPwEt.addTextChangedListener(pwTextWatcher);
        binding.findPwAgainEt.addTextChangedListener(pwTextWatcher);
        binding.findPwPhoneEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        binding.findLayout.setOnClickListener(v -> hideKeyboard());
        binding.findPwCloseBtn.setOnClickListener(v -> setBtn(3));
        binding.findPwAuthSendBtn.setOnClickListener(v -> setBtn(4));
        binding.findPwAuthOk.setOnClickListener(v -> setBtn(5));
        binding.findPwFindBtn.setOnClickListener(v -> pwChange());
    }

    private void pwChange() {
        if(binding.findPwIdEt.getText().toString().length() == 0) {
            Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
            imm.showSoftInput(binding.findPwIdEt, 0);
        } else if(!pwEquals) {
            Toast.makeText(this, "비밀번호가 서로 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            imm.showSoftInput(binding.findPwPwEt, 0);
        } else if(!pwPatternOk){
            Toast.makeText(this, "비밀번호 조건에 맞지 않습니다.", Toast.LENGTH_SHORT).show();
            imm.showSoftInput(binding.findPwAgainEt, 0);
        } else {
            String id = binding.findPwIdEt.getText().toString();
            String pw = binding.findPwAgainEt.getText().toString();
            userViewModel.pwChange2(id, pw);
        }
    }

    TextWatcher pwTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
        @Override
        public void afterTextChanged(Editable editable) { }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // 비밀번호 조건 일치 여부 확인
            Matcher pwMatcher = pwPattern.matcher(binding.findPwPwEt.getText().toString());
            pwPatternOk = pwMatcher.find();

            // 두개의 비밀번호가 서로 일치하는지 확인
            if(binding.findPwPwEt.getText().toString().equals(binding.findPwAgainEt.getText().toString())) { // 일치
                binding.findPwEqualsTv.setText(R.string.text09);
                binding.findPwEqualsTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue01));
                pwEquals = true;
            } else { // 불일치
                binding.findPwEqualsTv.setText(R.string.text08);
                binding.findPwEqualsTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red01));
                pwEquals = false;
            }
        }
    };




    /*         공통 부분         */
    // 뷰모델 설정하기 (type 0 : 아이디 찾기, type 1 : 비밀번호 변경)
    private void setViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setUserViewModelForId(this);
        if(layoutType == 0) {
            userViewModel.getUserIdList().observe(this, idList -> {
                if(idList != null) {
                    StringBuilder returnId = new StringBuilder();
                    for (String id : idList) {
                        if(!id.equals("null")) {
                            returnId.append(id.substring(0, id.length()-4)).append("****").append("\n");
                        } else {
                            returnId.append("검색된 아이디가 없습니다.");
                        }
                    }
                    binding.findIdIdTv.setVisibility(View.VISIBLE);
                    binding.findIdIdListTv.setVisibility(View.VISIBLE);
                    binding.findIdIdListTv.setText(returnId.toString());
                    binding.findIdFindBtn.setEnabled(false);
                }

                loading.dismiss();
            });
        }
    }
    // button setting
    private void setBtn(int cnd) {
        hideKeyboard();
        switch (cnd) {
            case 0 :
                phone = binding.findIdPhoneEt.getText().toString();
                if(phone.length() != 13) {
                    Toast.makeText(this, "핸드폰 번호를 입력해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                } else {
                    endDialog(1);
                }
                break;
            case 1 :
                isAuth = sendSMS.checkCode(binding.findIdAuthEt.getText().toString());
                if(isAuth) {
                    Toast.makeText(this, "본인인증에 성공했습니다.\n아이디 찾기를 눌러주세요.", Toast.LENGTH_SHORT).show();
                    binding.findIdAuthEt.setEnabled(false);
                    binding.findIdAuthOk.setEnabled(false);
                    binding.findIdSecondLayout.setVisibility(View.GONE);
                    binding.findIdFindBtn.setEnabled(true);
                    timer.cancel();
                } else {
                    Toast.makeText(this, "코드번호를 다시 확인하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2 :
                if(isAuth && binding.findIdNameEt.getText().toString().length() != 0) {
                    loading = ProgressDialog.show(this, "확인중 ...", "잠시만 기다려주세요...", true, false);
                    binding.findIdNameEt.setEnabled(false);
                    userViewModel.idFind(binding.findIdNameEt.getText().toString(), phone.replaceAll("-", ""));
                } else {
                    Toast.makeText(this, "이름 및 본인인증을 완료해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3 :
                endDialog(0);
                break;
            case 4 :
                phone = binding.findPwPhoneEt.getText().toString();
                if(phone.length() != 13) {
                    Toast.makeText(this, "핸드폰 번호를 입력해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                } else {
                    endDialog(1);
                }
                break;
            case 5 :
                isAuth = sendSMS.checkCode(binding.findPwAuthEt.getText().toString());
                if(isAuth) {
                    Toast.makeText(this, "본인인증에 성공했습니다.", Toast.LENGTH_SHORT).show();
                    binding.findPwAuthEt.setEnabled(false);
                    binding.findPwAuthOk.setEnabled(false);
                    binding.findPwSecondLayout.setVisibility(View.GONE);
                    binding.findPwFindBtn.setEnabled(true);
                    binding.findPwPwChangeLayout.setVisibility(View.VISIBLE);
                    timer.cancel();
                } else {
                    Toast.makeText(this, "코드번호를 다시 확인하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    // 인증번호 요청
    private void codeRequest() {
        hideKeyboard();
        if(layoutType == 0) {
            binding.findIdAuthLayout.setVisibility(View.VISIBLE);
            binding.findIdSecondLayout.setVisibility(View.VISIBLE);
            binding.findIdSecondTv.setVisibility(View.VISIBLE);
            binding.findIdPhoneEt.setEnabled(false);
            binding.findIdAuthSendBtn.setEnabled(false);
            binding.findIdAuthEt.setEnabled(true);
            binding.findIdAuthOk.setEnabled(true);
            binding.findIdSecondInfoTv.setText(R.string.text26);
            binding.findIdAuthEt.setText("");
        } else if(layoutType == 1) {
            binding.findPwAuthLayout.setVisibility(View.VISIBLE);
            binding.findPwSecondLayout.setVisibility(View.VISIBLE);
            binding.findPwSecondTv.setVisibility(View.VISIBLE);
            binding.findPwPhoneEt.setEnabled(false);
            binding.findPwAuthSendBtn.setEnabled(false);
            binding.findPwAuthEt.setEnabled(true);
            binding.findPwAuthOk.setEnabled(true);
            binding.findPwSecondInfoTv.setText(R.string.text26);
            binding.findPwAuthEt.setText("");
        }

        sendSMS.SmsSend(this, phone);
        Toast.makeText(this, "인증번호 : " + sendSMS.getCodeNumber(), Toast.LENGTH_SHORT).show();

        timer = new Timer();
        TimerTask task = new TimerTask() {
            int time = 60;
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if(time != 0) {
                        if(layoutType == 0) binding.findIdSecondTv.setText(String.valueOf(time));
                        else if(layoutType == 1) binding.findPwSecondTv.setText(String.valueOf(time));
                        time--;
                    } else {
                        cancel();
                        if(layoutType == 0) {
                            binding.findIdSecondTv.setVisibility(View.GONE);
                            binding.findIdSecondInfoTv.setText(R.string.text27);
                            binding.findIdAuthSendBtn.setEnabled(true);
                            binding.findIdAuthSendBtn.setText(R.string.reSend);
                            binding.findIdAuthEt.setEnabled(false);
                            binding.findIdAuthOk.setEnabled(false);
                        } else if(layoutType == 1) {
                            binding.findPwSecondTv.setVisibility(View.GONE);
                            binding.findPwSecondInfoTv.setText(R.string.text27);
                            binding.findPwAuthSendBtn.setEnabled(true);
                            binding.findPwAuthSendBtn.setText(R.string.reSend);
                            binding.findPwAuthEt.setEnabled(false);
                            binding.findPwAuthOk.setEnabled(false);
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 1000);
    }
    // 키보드 내리기 (type 0 : 아이디 찾기, type 1 : 비밀번호 변경)
    private void hideKeyboard() {
        if(layoutType == 0) {
            imm.hideSoftInputFromWindow(binding.findIdAuthEt.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(binding.findIdNameEt.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(binding.findIdPhoneEt.getWindowToken(), 0);
        } else if(layoutType == 1) {
            imm.hideSoftInputFromWindow(binding.findPwIdEt.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(binding.findPwAgainEt.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(binding.findPwPwEt.getWindowToken(), 0);
        }
    }
    // 뒤로가기 시 화면 닫기
    private void endDialog(int cnd) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        if(cnd == 0) {
            builder.setTitle("안내").setMessage("첫 화면으로 돌아가시겠습니까?");
            builder.setPositiveButton("예", ((dialogInterface, i) -> {
                startActivity(new Intent(this, FindActivity.class));
                finish();
            }));
        } else if(cnd == 1) {
            builder.setTitle("안내").setMessage("인증 번호는 해당 기기(번호)로 입력한 연락처에 문자를 보냅니다.\n그래도 진행하시겠습니까?");
            builder.setPositiveButton("예", ((dialogInterface, i) -> codeRequest()));
        }
        builder.setNegativeButton("아니오", ((dialogInterface, i) -> {}));

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if(layoutType == 0 || layoutType == 1) {
            endDialog(0);
        } else {
            super.onBackPressed();
        }
    }
}