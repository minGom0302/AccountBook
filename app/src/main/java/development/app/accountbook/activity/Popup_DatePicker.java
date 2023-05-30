package development.app.accountbook.activity;

import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.NumberPicker;

import development.app.accountbook.R;
import development.app.accountbook.databinding.ActivityPopupDatePickerBinding;

public class Popup_DatePicker extends Activity implements NumberPicker.OnValueChangeListener {
    private ActivityPopupDatePickerBinding binding;
    private int returnYear, returnMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_popup_date_picker);

        init();
    }

    private void init() {
        // year, month 값을 받아서 설정
        Intent intent = getIntent();
        int year = intent.getIntExtra("year", 1900);
        int month = intent.getIntExtra("month", 0);
        returnYear = year;
        returnMonth = month;

        // number picker 설정
        binding.popupDateNumberPicker01.setMaxValue(year + 10);
        binding.popupDateNumberPicker01.setMinValue(year - 10);
        binding.popupDateNumberPicker02.setMaxValue(12);
        binding.popupDateNumberPicker02.setMinValue(1);
        binding.popupDateNumberPicker01.setValue(year);
        binding.popupDateNumberPicker02.setValue(month);
        binding.popupDateNumberPicker01.setWrapSelectorWheel(false); // Max 혹은 Min 에서 끝내지 않고 Min 혹은 Max 로 넘어가지 않게 설정
        binding.popupDateNumberPicker02.setWrapSelectorWheel(false); // Max 혹은 Min 에서 끝내지 않고 Min 혹은 Max 로 넘어가지 않게 설정
        binding.popupDateNumberPicker01.setOnValueChangedListener(this);
        binding.popupDateNumberPicker02.setOnValueChangedListener(this);

        // 버튼 설정
        binding.popupDateCloseBtn.setOnClickListener(v -> finish());
        binding.popupDateOkBtn.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("year", returnYear);
            returnIntent.putExtra("month", returnMonth);
            setResult(RESULT_OK, returnIntent);
            finish();
        });
    }

    // NumberPicker 값 변화 감지하여 설정
    @Override
    public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
        if (numberPicker == binding.popupDateNumberPicker01) {
            returnYear = newValue;
        } else if (numberPicker == binding.popupDateNumberPicker02) {
            returnMonth = newValue;
        }
    }
}