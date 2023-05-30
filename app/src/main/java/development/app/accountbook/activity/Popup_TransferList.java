package development.app.accountbook.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;

import development.app.accountbook.databinding.ActivityPopupTransferListBinding;
import development.app.accountbook.viewmodel.SaveMoneyViewModel;
import development.app.accountbook.R;
import development.app.accountbook.adapter.BankListAdapter;

public class Popup_TransferList extends AppCompatActivity {
    private ActivityPopupTransferListBinding binding;
    private SaveMoneyViewModel moneyViewModel;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_popup_transfer_list);

        init();
    }


    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    private void init() {
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        String strDate = date.substring(0, date.length() - 3).replaceAll("-", "년 ") + "월 ▼";

        moneyViewModel = new ViewModelProvider(this).get(SaveMoneyViewModel.class);
        moneyViewModel.setSaveMoneyTransferViewModel(this, date.substring(0, date.length() - 3) + "___");
        moneyViewModel.getTransferMoneyLiveData().observe(this, transferList -> {
            BankListAdapter adapter = new BankListAdapter(transferList);
            binding.popupTransferListRv.setAdapter(adapter);
            binding.popupTransferListRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            adapter.setOnItemDeleteListener((v, transferDTO) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
                builder.setTitle("경고").setMessage("'" + transferDTO.getExpandingBank() + "' 에서 '" + transferDTO.getIncomeBank() + "' 로 이체한 내역을 삭제하시겠습니까?");
                builder.setPositiveButton("예", (dialogInterface, i) -> moneyViewModel.deleteTransferMoneyInfo(transferDTO.getSeq(), date));
                builder.setNegativeButton("아니오", (dialogInterface, i) -> { });

                AlertDialog alertDialog = builder.create();
                alertDialog.setOnShowListener(dialogInterface -> {
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                });
                alertDialog.show();
            });
        });

        binding.popupTransferListDateTv.setOnClickListener(v -> {
            Intent datePickerIntent = new Intent(this, Popup_DatePicker.class);
            datePickerIntent.putExtra("year", Integer.parseInt(date.substring(0, 4)));
            datePickerIntent.putExtra("month", Integer.parseInt(date.substring(5, 7)));
            Popup_datePickerResult.launch(datePickerIntent);
        });
        binding.popupTransferListDateTv.setText(strDate);
        binding.popupTransferListOkBtn.setOnClickListener(v -> finish());
    }


    // 화면 밖 터치 시 아무 작동 안하도록 하기
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return event.getAction() != MotionEvent.ACTION_OUTSIDE;
    }

    // startActivityForResult가 deprecated 되어 사용하지 않고 아래 방식으로 사용함 > 월 바꾼 것을 리턴받아 자료를 다시 가져옴
    @SuppressLint("DefaultLocale")
    private final ActivityResultLauncher<Intent> Popup_datePickerResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK) {
            assert result.getData() != null;
            int year = result.getData().getIntExtra("year", Integer.parseInt(date.substring(0, 4)));
            int month = result.getData().getIntExtra("month", Integer.parseInt(date.substring(5, 7)));
            String m = String.format("%02d", month);
            date = year + "-" + m;

            String strDate = date.replaceAll("-", "년 ") + "월 ▼";
            binding.popupTransferListDateTv.setText(strDate);
            moneyViewModel.againSet(date + "___", 98); // 월이 바뀌면 다시 정보 가져오기
        }
    });
}