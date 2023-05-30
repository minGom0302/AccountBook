package development.app.accountbook.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import development.app.accountbook.activity.LoginActivity;
import development.app.accountbook.activity.Popup_PwChange;
import development.app.accountbook.activity.Popup_change;
import development.app.accountbook.databinding.FragmentMyPageBinding;
import development.app.accountbook.viewmodel.CategorySettingViewModel;
import development.app.accountbook.viewmodel.SaveMoneyViewModel;
import development.app.accountbook.viewmodel.UserViewModel;
import development.app.accountbook.R;

public class MyPageFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
    private FragmentMyPageBinding binding;
    private UserViewModel userViewModel;
    private SaveMoneyViewModel moneyViewModel;
    private CategorySettingViewModel categorySettingViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_page, container, false);

        init();

        return binding.getRoot();
    }

    private void init() {
        setHasOptionsMenu(true);
        // 뷰모델 연결
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        moneyViewModel = new ViewModelProvider(requireActivity()).get(SaveMoneyViewModel.class);
        categorySettingViewModel = new ViewModelProvider(requireActivity()).get(CategorySettingViewModel.class);
        moneyViewModel.setSaveMoneyViewModel(getActivity());
        categorySettingViewModel.setViewModel(getActivity(), getViewLifecycleOwner(), 0);


        // 중분류 사용 여부 설정
        if(categorySettingViewModel.getSpendingTypeUse()) {
            binding.f03YRb.setChecked(true);
        } else {
            binding.f03NRb.setChecked(true);
        }

        // 화면 유저 정보 설정
        binding.f03NameTv.setText(userViewModel.getUserName());
        binding.f03NicknameTv.setText(userViewModel.getUserNickname());

        // 중분류 사용 여부 체크
        binding.f03YNRadioGroup.setOnCheckedChangeListener(this);

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
        binding.f03InfoBtn01.setOnClickListener(v -> showDialog(99, "지출의 중분류는 고정비/변동비/준변동비를 말합니다.\n미사용 선택 시 중분류는 화면에서 보여지지 않습니다."));
    }


    // 화면에 다이아로그 보여주기 > 로그아웃, 내용 초기화
    private void showDialog(int cnd, String msg) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(getContext(), R.style.DialogTheme);
        builder.setTitle("안내").setMessage(msg);
        if(cnd != 99) {
            builder.setPositiveButton("예", ((dialogInterface, i) -> {
                if (cnd == 0) {
                    userViewModel.setAutoLogin(false);
                    requireActivity().startActivity(new Intent(getContext(), LoginActivity.class));
                    requireActivity().finishAffinity();
                } else if (cnd == 1) {
                    showDialog(2, "정말로 모든 내용을 삭제하시겠습니까?");
                } else if (cnd == 2) {
                    moneyViewModel.deleteAll();
                }
            }));
            builder.setNegativeButton("아니오", ((dialogInterface, i) -> {
            }));
        } else {
            builder.setPositiveButton("확인", ((dialogInterface, i) -> { }));
        }

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

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if(binding.f03YRb.isChecked()) {
            categorySettingViewModel.setSpendingTypeUse(true);
        } else if(binding.f03NRb.isChecked()) {
            categorySettingViewModel.setSpendingTypeUse(false);
        }
    }

    // 좌측 상단에 메뉴(월마감, 계좌이체) 없애기
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.setGroupVisible(R.id.toolbar_menu, false);
        super.onCreateOptionsMenu(menu, inflater);
    }
}