package com.example.accountbook.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.widget.Toast;

import com.example.accountbook.R;
import com.example.accountbook.databinding.ActivityInputOutputBinding;

public class InputOutputActivity extends AppCompatActivity {
    private ActivityInputOutputBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_input_output);

        init();
    }

    private void init() {
        binding.ioSpinnerBank.setDropDownVerticalOffset(80);
        binding.ioSpinnerContents.setDropDownVerticalOffset(80);

        binding.ioSaveBtn.setOnClickListener(v ->
            showDialog(0, "입력한 정보를 저장하시겠습니까?")
        );
        binding.ioCloseBtn.setOnClickListener(v ->
            showDialog(1, "입력을 멈추고 창을 닫으시겠습니까?")
        );
    }

    private void showDialog(int cnd, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내").setTitle(msg);
        builder.setPositiveButton("예", ((dialogInterface, i) -> {
            if(cnd == 0) {
                Toast.makeText(this, "데이터 저장됨", Toast.LENGTH_SHORT).show();
            } else if(cnd == 1) {
                finish();
            }
        }));
        builder.setNegativeButton("아니오", (((dialogInterface, i) -> {  })));

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        showDialog(1, "입력을 멈추고 창을 닫으시겠습니까?");
    }
}