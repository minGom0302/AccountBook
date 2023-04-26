package com.example.accountbook.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.accountbook.R;
import com.example.accountbook.activity.Popup_PwChange;
import com.example.accountbook.activity.Popup_change;
import com.example.accountbook.databinding.FragmentMyPageBinding;

import java.util.Objects;

public class MyPageFragment extends Fragment {
    private FragmentMyPageBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_page, container, false);

        init();

        return binding.getRoot();
    }

    private void init() {
        // user setting

        // button setting
        binding.f03NicknameBtn.setOnClickListener(v ->
            popupActivityResult.launch(new Intent(getContext(), Popup_change.class))
        );
        binding.f03ChangePwBtn.setOnClickListener(v -> {
            requireActivity().startActivity(new Intent(getContext(), Popup_PwChange.class));
        });
        binding.f03LogoutBtn.setOnClickListener(v ->
            showDialog(0, "로그아웃 하시겠습니까?")
        );
        binding.f03ContentsReset.setOnClickListener(v ->
            showDialog(1, "지금까지 기록한 내용을 모두 삭제하시겠습니까?")
        );
    }


    // 화면에 다이아로그 보여주기 > 로그아웃, 내용 초기화
    private void showDialog(int cnd, String msg) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(getContext());
        builder.setTitle("안내").setMessage(msg);
        builder.setPositiveButton("예", ((dialogInterface, i) -> {
            if(cnd == 0) {
                Toast.makeText(getContext(), "로그아웃", Toast.LENGTH_SHORT).show();
            } else if(cnd == 1) {
                Toast.makeText(getContext(), "내용 초기화", Toast.LENGTH_SHORT).show();
            }
        }));
        builder.setNegativeButton("아니오", ((dialogInterface, i) -> { }));

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    // startActivityForResult가 deprecated 되어 사용하지 않고 아래 방식으로 사용함
    private final ActivityResultLauncher<Intent> popupActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK) {
            assert result.getData() != null;
            String d = result.getData().getStringExtra("nickName");
            Toast.makeText(getContext(), "nickname : " + d, Toast.LENGTH_SHORT).show();
        }
    });
}