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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import development.app.accountbook.R;
import development.app.accountbook.adapter.SpinnerAdapter;
import development.app.accountbook.databinding.ActivityPopupTransferBinding;

import java.text.DecimalFormat;
import java.util.Calendar;

public class Popup_Transfer extends Activity implements TextWatcher {
    private ActivityPopupTransferBinding binding;
    private InputMethodManager imm;
    private String result = "";
    private String date, returnIncomeCode, returnSpendingCode, returnIncomeBank, returnSpendingBank;
    private int mYear, mMonth, mDay;
    private boolean first = true;
    private String[] codeArray, valueArray;
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
        Intent getIntent = getIntent();
        date = getIntent.getStringExtra("date");
        codeArray = getIntent.getStringArrayExtra("codeArray"); // bank code
        valueArray = getIntent.getStringArrayExtra("valueArray"); // bank contents

        binding.popupTransferDateTv.setText(date);

        binding.popupTransferMoneyEt.addTextChangedListener(this);
        binding.popupTransferDateTv.setOnClickListener(v -> dateChoice());
        binding.popupTransferOkBtn.setOnClickListener(v -> showDialog(0, "입력한 정보로 저장하시겠습니까?"));
        binding.popupTransferCloseBtn.setOnClickListener(v -> showDialog(1, "해당 창을 종료하시겠습니까?"));
        binding.popupTransferInfoBtn.setOnClickListener(v -> showDialog(99, "※ 계좌이체와 이월은 수입/지출 금액에 합산되지 않습니다.\n(잔액에서만 합산됩니다.)"));
        binding.popupTransferListBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, Popup_TransferList.class);
            intent.putExtra("date", date);
            startActivity(intent);
        });

        // spinner settings
        spinnerSettings();
    }


    // 스피너 설정
    private void spinnerSettings() {
        SpinnerAdapter bankAdapter01 = new SpinnerAdapter(codeArray, valueArray, this, 0);
        SpinnerAdapter bankAdapter02 = new SpinnerAdapter(codeArray, valueArray, this, 0);
        binding.popupTransferSpinnerIncome.setAdapter(bankAdapter01);
        binding.popupTransferSpinnerExpanding.setAdapter(bankAdapter02);
        binding.popupTransferSpinnerIncome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                returnIncomeCode = codeArray[i];
                returnIncomeBank = valueArray[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        binding.popupTransferSpinnerExpanding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                returnSpendingCode = codeArray[i];
                returnSpendingBank = valueArray[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }


    // DatePicker 열고 선택 시 날짜 셋팅하게 함
    @SuppressLint("DefaultLocale")
    private void dateChoice() {
        Calendar calendar = Calendar.getInstance();
        if(first) {
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            first = false;
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, (datePicker, year, month, day) -> {
            mYear = year;
            mMonth = month;
            mDay = day;
            String strMonth = String.format("%02d", month+1);
            String strDay = String.format("%02d", day);

            date = year + "-" + strMonth + "-" + strDay;

            binding.popupTransferDateTv.setText(date);
        }, mYear, mMonth, mDay);
        // 스피너와 캘린더뷰를 같이 보여주기 때문에 캘린더 뷰 안보이게 설정
        datePickerDialog.getDatePicker().setCalendarViewShown(false);
        // 뒷배경이 투명한 다이얼로그 생성
        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        datePickerDialog.show();
    }


    // 다이어로그 화면에 띄우기
    private void showDialog(int cnd, String msg) {
        hideKeyboard();
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setTitle("안내").setMessage(msg);
        if(cnd != 99) {
            builder.setPositiveButton("예", ((dialogInterface, i) -> {
                if (cnd == 0) okFinish();
                else if (cnd == 1) finish();
            }));
            builder.setNegativeButton("아니오", ((dialogInterface, i) -> {
            }));
        } else {
            builder.setPositiveButton("확인", (((dialogInterface, i) -> { })));
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        });
        alertDialog.show();
    }

    private void okFinish() {
        if(binding.popupTransferMoneyEt.getText().toString().equals("0")) {
            showDialog(99, "금액이 '0' 인 경우 이체할 수 없습니다.");
        } else if(returnIncomeCode.equals(returnSpendingCode)) {
            showDialog(99, "동일한 계좌로 이체할 수 없습니다.");
        } else {
            Intent intent = new Intent();
            intent.putExtra("date", date);
            intent.putExtra("incomeCode", returnIncomeCode);
            intent.putExtra("spendingCode", returnSpendingCode);
            intent.putExtra("incomeBank", returnIncomeBank);
            intent.putExtra("spendingBank", returnSpendingBank);
            intent.putExtra("money", binding.popupTransferMoneyEt.getText().toString().replaceAll(",", ""));
            intent.putExtra("memo", binding.popupTransferMemoEt.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }
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

    @Override
    public void onBackPressed() { showDialog(1, "해당 창을 종료하시겠습니까?"); }
}