package development.app.accountbook.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import development.app.accountbook.R;
import development.app.accountbook.adapter.SpinnerAdapter;
import development.app.accountbook.databinding.ActivityPopupInputOutputBinding;

import java.text.DecimalFormat;
import java.util.Arrays;

public class Popup_InputOutput extends Activity implements TextWatcher {
    private ActivityPopupInputOutputBinding binding;
    private String[] codeArray;
    private String bankSeq, categorySeq, category01;
    private int moneySeq, mYear, mMonth, mDay;
    private String result = "";
    private boolean first = true;
    private boolean updateMode = false;
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
        boolean inputType = intent.getBooleanExtra("inputType", true);

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

        // input 설정
        if(inputType) {
            // 내용 설정
            updateMode = true;
            binding.popupIoContentsTv.setText(intent.getStringExtra("contents"));
            binding.popupIoOkBtn.setOnClickListener(v -> mShowDialog(0, "입력한 내용으로 저장하시겠습니까?"));
        } else { // output 설정 > 수정 할 수 있는 화면
            setEnable(false); // 사용 여부 설정
            moneySeq = intent.getIntExtra("moneySeq", 0);
            String[] category01Array = intent.getStringArrayExtra("category01Value");

            // 화면 보여주기 설정
            binding.popupIoSpinnerCategory.setVisibility(View.VISIBLE);
            binding.popupIoModifyBtn.setVisibility(View.VISIBLE);
            binding.popupIoTextTv01.setVisibility(View.GONE);
            binding.popupIoTextTv02.setVisibility(View.GONE);
            binding.popupIoTextTv03.setVisibility(View.GONE);

            // 내용 설정
            binding.popupIoTitleTv.setText("상세보기");
            binding.popupIoContentsTv.setText(intent.getStringExtra("date"));
            binding.popupIoMemoEt.setText(intent.getStringExtra("memo"));
            binding.popupIoMoneyEt.setText(commaFormat.format(Integer.parseInt(intent.getStringExtra("money"))));

            // 스피너 설정
            String[] categoryCode = intent.getStringArrayExtra("categoryCodeArray");
            String[] categoryValue = intent.getStringArrayExtra("categoryValueArray");
            int categoryPosition = Arrays.asList(categoryValue).indexOf(intent.getStringExtra("categoryPosition"));
            int bankPosition = Arrays.asList(valueArray).indexOf(intent.getStringExtra("bankPosition"));

            SpinnerAdapter categoryAdapter02 = new SpinnerAdapter(categoryCode, categoryValue, this, 0);
            binding.popupIoSpinnerCategory.setAdapter(categoryAdapter02);
            binding.popupIoSpinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    categorySeq = categoryCode[i];
                    category01 = category01Array[i];
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) { }
            });
            binding.popupIoSpinner.setSelection(bankPosition);
            binding.popupIoSpinnerCategory.setSelection(categoryPosition);

            // 클릭 이벤트 설정
            binding.popupIoContentsTv.setOnClickListener(v -> dateChoice(intent.getStringExtra("date")));
            binding.popupIoOkBtn.setOnClickListener(v -> mShowDialog(2, "입력한 내용으로 수정하시겠습니까?"));
            binding.popupIoModifyBtn.setOnClickListener(v -> {
                if(intent.getStringExtra("settingsCode").equals("9901001") || intent.getStringExtra("settingsCode").equals("9801001")) {
                    mShowDialog(3, "계좌 이체는 이체 내역을 통해 수정해주시기 바랍니다.");
                } else {
                    setEnable(true);
                    binding.popupIoTitleTv.setText("수정하기");
                    updateMode = true;
                }
            });
        }

        // 금액 입력 시 금액 콤마 찍기
        binding.popupIoMoneyEt.addTextChangedListener(this);

        // 클릭 이벤트
        binding.popupIoLayout.setOnClickListener(v -> hideKeyboard());
        binding.popupIoCloseBtn.setOnClickListener(v -> {
            if(updateMode) {
                mShowDialog(1, "입력을 멈추고 해당 화면을 닫으시겠습니까?");
            } else {
                finish();
            }
        });
    }


    // 화면 enable 한번에 설정하기
    private void setEnable(boolean isTrue) {
        binding.popupIoContentsTv.setEnabled(isTrue);
        binding.popupIoSpinner.setEnabled(isTrue);
        binding.popupIoSpinnerCategory.setEnabled(isTrue);
        binding.popupIoMoneyEt.setEnabled(isTrue);
        binding.popupIoMemoEt.setEnabled(isTrue);
        binding.popupIoOkBtn.setEnabled(isTrue);
        binding.popupIoModifyBtn.setEnabled(!isTrue);
    }


    // DatePicker 열고 선택 시 날짜 셋팅하게 함
    @SuppressLint("DefaultLocale")
    private void dateChoice(String date) {
        if(first) {
            mYear = Integer.parseInt(date.substring(0, 4));
            mMonth = Integer.parseInt(date.substring(5, 7)) - 1;
            mDay = Integer.parseInt(date.substring(8));
            first = false;
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, (datePicker, year, month, day) -> {
            mYear = year;
            mMonth = month;
            mDay = day;
            String strMonth = String.format("%02d", month+1);
            String strDay = String.format("%02d", day);

            String mDate = year + "-" + strMonth + "-" + strDay;

            binding.popupIoContentsTv.setText(mDate);
        }, mYear, mMonth, mDay);
        // 스피너와 캘린더뷰를 같이 보여주기 때문에 캘린더 뷰 안보이게 설정
        datePickerDialog.getDatePicker().setCalendarViewShown(false);
        // 뒷배경이 투명한 다이얼로그 생성
        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        datePickerDialog.show();
    }


    // 다이얼로그 띄우기
    private void mShowDialog(int cnd, String msg) {
        hideKeyboard();
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setTitle("안내").setMessage(msg);
        if(cnd != 3) {
            builder.setPositiveButton("예", ((dialogInterface, i) -> {
                if (cnd == 0) {
                    saveInfo();
                } else if (cnd == 1) {
                    finish();
                } else if (cnd == 2) {
                    modifyInfo();
                }
            }));
            builder.setNegativeButton("아니오", (((dialogInterface, i) -> { })));
        } else {
            builder.setPositiveButton("확인", ((dialogInterface, i) -> { }));
        }

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        });
        dialog.show();
    }

    // 입력 내용 저장하기
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

    // 입력 내용으로 수정하기
    private void modifyInfo() {
        if(bankSeq.equals("")) bankSeq = "0";
        // Log.e("modify", "seq : " + moneySeq + " / settingSeq : " + categorySeq + " / bankSeq : " + bankSeq + " / in_sp : " + category01 +  " / date : " + binding.popupIoContentsTv.getText().toString() + " / money : " + binding.popupIoMoneyEt.getText().toString().replaceAll(",", "") + "/ memo : " + binding.popupIoMemoEt.getText().toString() + " / transferSeq : " + 1);
        Intent intent = new Intent();
        intent.putExtra("seq", moneySeq);
        intent.putExtra("settingsSeq", Integer.parseInt(categorySeq));
        intent.putExtra("bankSeq", Integer.parseInt(bankSeq));
        intent.putExtra("in_sp", category01);
        intent.putExtra("date", binding.popupIoContentsTv.getText().toString());
        intent.putExtra("money", binding.popupIoMoneyEt.getText().toString().replaceAll(",", ""));
        intent.putExtra("memo", binding.popupIoMemoEt.getText().toString());
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
        if(updateMode) {
            mShowDialog(1, "입력을 멈추고 해당 화면을 닫으시겠습니까?");
        } else {
            finish();
        }
    }
}