package com.example.accountbook.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.accountbook.R;
import com.example.accountbook.activity.LoginActivity;
import com.example.accountbook.activity.Popup_PwChange;
import com.example.accountbook.activity.Popup_change;
import com.example.accountbook.databinding.FragmentMyPageBinding;
import com.example.accountbook.viewmodel.UserViewModel;

import java.util.Objects;

public class MyPageFragment extends Fragment {
    private FragmentMyPageBinding binding;
    private UserViewModel userViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_page, container, false);

        init();

        return binding.getRoot();
    }

    private void init() {
        // 뷰모델 연결
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // 화면 유저 정보 설정
        binding.f03NameTv.setText(userViewModel.getUserName());
        binding.f03NicknameTv.setText(userViewModel.getUserNickname());

        // button setting
        binding.f03NicknameBtn.setOnClickListener(v ->
            popupActivityResult.launch(new Intent(getContext(), Popup_change.class))
        );
        binding.f03ChangePwBtn.setOnClickListener(v ->
            popupPwActivityResult.launch(new Intent(getContext(), Popup_PwChange.class))
        );
        binding.f03LogoutBtn.setOnClickListener(v ->
            showDialog(0, "로그아웃 하시겠습니까?")
        );
        binding.f03ContentsReset.setOnClickListener(v ->
            showDialog(1, "지금까지 기록한 내용을 모두 삭제하시겠습니까?")
        );
    }


    // 화면에 다이아로그 보여주기 > 로그아웃, 내용 초기화
    private void showDialog(int cnd, String msg) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(getContext(), R.style.DialogTheme);
        builder.setTitle("안내").setMessage(msg);
        builder.setPositiveButton("예", ((dialogInterface, i) -> {
            if(cnd == 0) {
                requireActivity().startActivity(new Intent(getContext(), LoginActivity.class));
                requireActivity().finishAffinity();
            } else if(cnd == 1) {
                showDialog(2, "정말로 모든 내용을 삭제하시겠습니까?");
            } else if(cnd == 2) {
                Toast.makeText(getContext(), "내용 초기화", Toast.LENGTH_SHORT).show();
            }
        }));
        builder.setNegativeButton("아니오", ((dialogInterface, i) -> { }));

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        });
        alertDialog.show();
    }


    // startActivityForResult가 deprecated 되어 사용하지 않고 아래 방식으로 사용함 > 닉네임 변경에 관한 건
    private final ActivityResultLauncher<Intent> popupActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK) {
            assert result.getData() != null;
            String returnNickname = result.getData().getStringExtra("nickName");
            userViewModel.setUserNickname(returnNickname);
            binding.f03NicknameTv.setText(returnNickname);
        }
    });
    // startActivityForResult가 deprecated 되어 사용하지 않고 아래 방식으로 사용함 > 비밀번호 변경에 관한 건
    private final ActivityResultLauncher<Intent> popupPwActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK) {
            assert result.getData() != null;
            String pw = result.getData().getStringExtra("pw");
            userViewModel.pwChange(pw);
        }
    });
}