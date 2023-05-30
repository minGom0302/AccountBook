package development.app.accountbook.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import development.app.accountbook.databinding.ActivityFindBinding;
import development.app.accountbook.viewmodel.UserViewModel;
import development.app.accountbook.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindActivity extends AppCompatActivity {
    private ActivityFindBinding binding;
    private UserViewModel userViewModel;
    private InputMethodManager imm;
    private ProgressDialog loading;
    private int layoutType = 3;

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
    }


    // find id layout setting
    private void setIdLayout() {
        layoutType = 0;
        binding.findFirstLayout.setVisibility(View.GONE);
        binding.findIdLayout.setVisibility(View.VISIBLE);

        setViewModel();

        binding.findIdFindBtn.setOnClickListener(v -> setBtn(0));
        binding.findIdCloseBtn.setOnClickListener(v -> setBtn(2));
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

        binding.findLayout.setOnClickListener(v -> hideKeyboard());
        binding.findPwCloseBtn.setOnClickListener(v -> setBtn(2));
        binding.findPwAuthOk.setOnClickListener(v -> setBtn(1));
        binding.findPwFindBtn.setOnClickListener(v -> pwChange());
    }

    private void pwChange() {
        if(!pwEquals) {
            Toast.makeText(this, "비밀번호가 서로 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            imm.showSoftInput(binding.findPwPwEt, 0);
        } else if(!pwPatternOk){
            Toast.makeText(this, "비밀번호 조건에 맞지 않습니다.", Toast.LENGTH_SHORT).show();
            imm.showSoftInput(binding.findPwAgainEt, 0);
        } else {
            String pw = binding.findPwAgainEt.getText().toString();
            userViewModel.pwChange(pw);
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
                }

                loading.dismiss();
            });
        } else if(layoutType == 1) {
            userViewModel.getUserInfo().observe(this, dtoList -> {
                if(dtoList != null) {
                    if(dtoList.getSeq() != 0) {
                        binding.findPwIdEt.setEnabled(false);
                        binding.findPwAnswerEt.setEnabled(false);
                        binding.findPwAuthOk.setEnabled(false);
                        binding.findPwFindBtn.setEnabled(true);
                        binding.findPwPwChangeLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "새로운 비밀번호를 설정해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "아이디와 질문의 답에 해당하는 정보가 없습니다.\n다시 한 번 확인해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "아이디와 질문의 답에 해당하는 정보가 없습니다.\n다시 한 번 확인해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }

                loading.dismiss();
            });
        }
    }
    // button setting
    private void setBtn(int cnd) {
        hideKeyboard();
        switch (cnd) {
            case 0 : // ID 찾기 버튼
                if(binding.findIdNameEt.getText().toString().length() == 0) {
                    Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if(binding.findIdBirthEt.getText().toString().length() < 6) {
                    Toast.makeText(this, "정확한 생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if(binding.findIdAnswerEt.getText().toString().length() == 0) {
                    Toast.makeText(this, "질문에 대한 대답을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    loading = ProgressDialog.show(this, "확인중 ...", "잠시만 기다려주세요...", true, false);
                    userViewModel.idFind(binding.findIdNameEt.getText().toString(), binding.findIdBirthEt.getText().toString(), binding.findIdAnswerEt.getText().toString());
                }
                break;
            case 1 : // pw 아이디 질문 확인
                if(binding.findPwIdEt.getText().toString().length() == 0) {
                    Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if(binding.findPwAnswerEt.getText().toString().length() == 0) {
                    Toast.makeText(this, "질문에 대한 대답을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    loading = ProgressDialog.show(this, "확인중...", "잠시만 기다려주세요...", true, false);
                    userViewModel.idCheck2(binding.findPwIdEt.getText().toString(), binding.findPwAnswerEt.getText().toString());
                }
                break;
            case 2 : // 창 닫기
                endDialog();
                break;
        }
    }
    // 키보드 내리기 (type 0 : 아이디 찾기, type 1 : 비밀번호 변경)
    private void hideKeyboard() {
        if(layoutType == 0) {
            imm.hideSoftInputFromWindow(binding.findIdNameEt.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(binding.findIdAnswerEt.getWindowToken(), 0);
        } else if(layoutType == 1) {
            imm.hideSoftInputFromWindow(binding.findPwIdEt.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(binding.findPwAgainEt.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(binding.findPwPwEt.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(binding.findPwAnswerEt.getWindowToken(), 0);
        }
    }
    // 뒤로가기 시 화면 닫기
    private void endDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setTitle("안내").setMessage("첫 화면으로 돌아가시겠습니까?");
        builder.setPositiveButton("예", ((dialogInterface, i) -> {
            startActivity(new Intent(this, FindActivity.class));
            finish();
        }));
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
            endDialog();
        } else {
            super.onBackPressed();
        }
    }
}