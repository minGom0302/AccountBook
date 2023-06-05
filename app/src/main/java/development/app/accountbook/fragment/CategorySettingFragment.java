package development.app.accountbook.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.RadioGroup;

import development.app.accountbook.activity.Popup_DatePicker;
import development.app.accountbook.adapter.CategoryAdapter_01;
import development.app.accountbook.adapter.SpinnerAdapter;
import development.app.accountbook.databinding.FragmentCategorySettingBinding;
import development.app.accountbook.dto.CategoryDTO;
import development.app.accountbook.item.ItemTouchHelperCallback;
import development.app.accountbook.viewmodel.CategorySettingViewModel;
import development.app.accountbook.viewmodel.UserViewModel;
import development.app.accountbook.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CategorySettingFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
    private CategorySettingViewModel viewModel;
    private UserViewModel userViewModel;
    private FragmentCategorySettingBinding binding;
    private List<String[]> categoryList01;
    private List<String[]> categoryList02;
    private List<CategoryDTO> categoryDTOS;
    private String category01, category02, code;
    private int year, month, categorySeq, orderSeq;
    private InputMethodManager imm;
    private CategoryAdapter_01 adapter_01;
    private int rbCheckValue = 0;
    private boolean isModifyMode = false;
    private boolean isOrderModifyMode = false;
    private ItemTouchHelper itemTouchHelper;
    private ProgressDialog loading;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_setting, container, false);

        init();

        return binding.getRoot();
    }


    // 초기값 설정
    @SuppressLint({"NotifyDataSetChanged", "DefaultLocale"})
    private void init() {
        setHasOptionsMenu(true);
        imm = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
        adapter_01 = new CategoryAdapter_01(null);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        // 클릭 이벤트 설정
        binding.settingLayout.setOnClickListener(v -> hideKeyboard());

        // 라디오버튼 선택에 따른 스피너1 설정
        binding.f04RbGroup.setOnCheckedChangeListener(this);

        // 뷰모델 연결 > Fragment끼리 동일한 ViewModel 사용하기 위해 owner 를 아래와 같이 설정
        viewModel = new ViewModelProvider(requireActivity()).get(CategorySettingViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        viewModel.setViewModel(getActivity(), getViewLifecycleOwner(), 0);

        spinnerSetting();

        // recyclerview 보여주기 위해 list 감지
        viewModel.getCategoryListForShow().observe(getViewLifecycleOwner(), categoryDTOS -> {
            this.categoryDTOS = categoryDTOS;
            adapter_01 = new CategoryAdapter_01(categoryDTOS);
            binding.f04Recyclerview.setAdapter(adapter_01);
            binding.f04Recyclerview.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

            setLayoutAndBtn(3, "", "", 0, 0, false);

            // recyclerview item 클릭 시 발생할 이벤트 > adapter 작성한 내용을 가져와서 작성
            adapter_01.setOnItemClickListener((v, categoryDTO) -> {
                this.categorySeq = categoryDTO.getSeq();
                this.code = categoryDTO.getCode();
                this.orderSeq = categoryDTO.getOrderSeq();
                setLayoutAndBtn(0, categoryDTO.getContents(), categoryDTO.getStrEndDay(), Arrays.asList(categoryList01.get(0)).indexOf(categoryDTO.getCategory01())
                        , Arrays.asList(categoryList02.get(0)).indexOf(categoryDTO.getCategory02()), false);
            });
            // recyclerview delete btn 클릭 시 발생할 이벤트
            adapter_01.setOnItemDeleteListener((v, categoryDTO) -> showDialog(0, "'" + categoryDTO.getContents() + "' 을(를) 삭제하시겠습니까?", categoryDTO));

            if(loading != null && loading.isShowing()) {
                loading.dismiss();
            }
        });

        // end day 클릭 이벤트
        binding.f04EndDayTv.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Popup_DatePicker.class);
            intent.putExtra("year", year);
            intent.putExtra("month", month);
            Popup_datePickerResult.launch(intent);
        });

        // infoBtn 클릭 이벤트
        binding.f04InfoBtn.setOnClickListener(v ->
            showDialog(3, "지정한 일자까지만 해당 카테고리와 입력한 가계부 정보가 보여집니다.\n빈칸으로 입력하면 제한 없이 계속 보여집니다.", null)
        );
        // 버튼 클릭 이벤트
        binding.f04NewBtn.setOnClickListener(v -> {
            hideKeyboard();
            isModifyMode = false;
            setLayoutAndBtn(1, "", "", 0, 0, true);
        });
        binding.f04ModifyBtn.setOnClickListener(v -> {
            isModifyMode = true;
            setLayoutAndBtn(2, "", "", 0, 0, true);
        });
        binding.f04SaveBtn.setOnClickListener(v -> { // 새로운 카테고리/계좌 정보 저장
            hideKeyboard();
            if(binding.f04ContentsEt.getText().length() == 0) {
                showDialog(3, "내용을 입력해주시기 바랍니다.", null);
                return;
            }
            int endDay;
            if (binding.f04EndDayTv.getText().toString().equals("")) {
                endDay = 999999;
            } else {
                endDay = Integer.parseInt(binding.f04EndDayTv.getText().toString().replaceAll("-", ""));
            }

            if(isModifyMode) {
                CategoryDTO modifyDto = new CategoryDTO(userViewModel.getUserSeq(), code
                        , category01, category02, binding.f04ContentsEt.getText().toString(), endDay, orderSeq);
                showDialog(4, "입력한 정보로 수정하시겠습니까?", modifyDto);
            } else {
                int size = categoryDTOS.size() == 0 ? 2 : categoryDTOS.size()+2;
                CategoryDTO insertDto = new CategoryDTO(userViewModel.getUserSeq(), category01 + category02 + String.format("%03d", size)
                        , category01 , category02, binding.f04ContentsEt.getText().toString(), endDay, size);
                showDialog(2, "입력한 정보로 저장하시겠습니까?", insertDto);
            }
        });
        // 순서 수정버튼 이벤트
        binding.f04ModifyOrderBtn.setOnClickListener(v -> {
            if(isOrderModifyMode) {
                isOrderModifyMode = false;
                binding.f04ModifyOrderBtn.setText(R.string.text32);
                binding.f04ModifyOrderInfoTv.setVisibility(View.GONE);
                itemTouchHelper.attachToRecyclerView(null);

                loading = ProgressDialog.show(getContext(), "수정중 ...", "잠시만 기다려주세요...", true, false);

                List<CategoryDTO> moveEndCategoryList = adapter_01.getCategoryList();
                for(CategoryDTO category : moveEndCategoryList) {
                    category.setOrderSeq(moveEndCategoryList.indexOf(category));
                }

                viewModel.updateCategoryOrder(moveEndCategoryList, rbCheckValue);
            } else {
                isOrderModifyMode = true;
                binding.f04ModifyOrderBtn.setText(R.string.complete);
                binding.f04ModifyOrderInfoTv.setVisibility(View.VISIBLE);
                itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter_01));
                itemTouchHelper.attachToRecyclerView(binding.f04Recyclerview);
            }
        });
    }


    // 스피너 설정
    private void spinnerSetting() {
        // 스피너들 설정
        binding.f04SpinnerCategory01.setDropDownVerticalOffset(80);
        binding.f04SpinnerCategory02.setDropDownVerticalOffset(80);

        // 값 변경 관찰 > spinner1의 값
        viewModel.getCategoryList01().observe(getViewLifecycleOwner(), categoryList01 -> {
            this.categoryList01 = categoryList01;
            setCategory(0);
        });
        // 값 변경 관찰 > spinner2의 값
        viewModel.getCategoryList02().observe(getViewLifecycleOwner(), categoryList02 -> {
            this.categoryList02 = categoryList02;
            setCategory(1);
        });
        // 수입/지출/계좌/카드 선택에 따른 행동 설정
        binding.f04SpinnerCategory01.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category01 = categoryList01.get(0)[i];
                // category2 설정 > 지출 외 모두 날짜를 가져와 00값 설정
                viewModel.setCategoryList02(0, Integer.parseInt(categoryList01.get(0)[i]));
                // 지출이 아닐 경우 화면에서 가릴 거 가리기
                int visible;
                if(category01.equals("98")) {
                    visible = View.VISIBLE;
                } else {
                    visible = View.INVISIBLE;
                }
                binding.f04SpinnerCategory02.setVisibility(visible);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        // 고정비/변동비/준변동비 선택에 따른 행동 설정 > 지출일 경우에만 보임
        binding.f04SpinnerCategory02.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category02 = categoryList02.get(0)[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }


    // 변경사항 발생 시 스피너 값들 설정
    private void setCategory(int type) {
        if(type == 0) {
            SpinnerAdapter categoryAdapter01 = new SpinnerAdapter(categoryList01.get(0), categoryList01.get(1), getContext(), 1);
            binding.f04SpinnerCategory01.setAdapter(categoryAdapter01);
        } else if(type == 1) {
            SpinnerAdapter categoryAdapter02 = new SpinnerAdapter(categoryList02.get(0), categoryList02.get(1), getContext(), 1);
            binding.f04SpinnerCategory02.setAdapter(categoryAdapter02);
        }
    }


    // 라디오 버튼 선택에 따른 화면 설정
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        hideKeyboard();
        if(binding.f04Rb01.isChecked()) {
            binding.f04EndDayLayout.setVisibility(View.VISIBLE);
            viewModel.setCategoryList01(0); // 대분류 셋팅 (수입, 지출)
            viewModel.setCategoryList(0); // recyclerview setting
            rbCheckValue = 0;
        } else if(binding.f04Rb02.isChecked()) {
            binding.f04EndDayLayout.setVisibility(View.INVISIBLE);
            viewModel.setCategoryList01(1); // 대분류 셋팅 (계좌, 카드)
            viewModel.setCategoryList(1); // recyclerview setting
            rbCheckValue = 1;
        }
    }


    // 키보드 내리기
    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(binding.f04ContentsEt.getWindowToken(), 0);
    }


    // 위쪽 입력화면과 버튼 설정
    private void setLayoutAndBtn(int cnd, String contents, String endDay, int spinner01, int spinner02, boolean isTrue) {
        if(cnd != 2) {
            binding.f04ContentsEt.setText(contents);
            binding.f04EndDayTv.setText(endDay);
            binding.f04SpinnerCategory01.setSelection(spinner01);
            binding.f04SpinnerCategory02.setSelection(spinner02);
        }

        binding.f04ContentsEt.setEnabled(isTrue);
        binding.f04EndDayTv.setEnabled(isTrue);
        binding.f04SpinnerCategory01.setEnabled(isTrue);
        binding.f04SpinnerCategory02.setEnabled(isTrue);
        binding.f04NewBtn.setEnabled(cnd != 1);
        binding.f04SaveBtn.setEnabled(isTrue);
        binding.f04ModifyBtn.setEnabled(cnd == 0);
    }


    // Dialog 보여주기
    private void showDialog(int cnd, String msg, CategoryDTO dto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
        if(cnd == 0 || cnd == 1) {
            builder.setTitle("경고").setMessage(msg);
        } else {
            builder.setTitle("안내").setMessage(msg);
        }
        if(cnd != 3) {
            builder.setPositiveButton("예", ((dialogInterface, i) -> {
                if (cnd == 0)
                    showDialog(1, "'" + dto.getContents() + "' (으)로 등록한 가계부 정보도 모두 삭제됩니다.\n그래도 삭제하시겠습니까?", dto);
                else if (cnd == 1) viewModel.deleteCategory(dto, rbCheckValue);
                else if (cnd == 2) viewModel.insertCategory(dto, rbCheckValue);
                else if (cnd == 4) viewModel.updateCategory(dto, rbCheckValue, categorySeq);
            }));
            builder.setNegativeButton("아니오", ((dialogInterface, i) -> { }));
        } else {
            builder.setPositiveButton("확인", (((dialogInterface, i) -> { })));
        }

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        });
        dialog.show();
    }


    // startActivityForResult가 deprecated 되어 사용하지 않고 아래 방식으로 사용함 > 월 바꾼 것을 리턴받아 자료를 다시 가져옴
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private final ActivityResultLauncher<Intent> Popup_datePickerResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK) {
            assert result.getData() != null;
            this.year = result.getData().getIntExtra("year", calendar.get(Calendar.YEAR));
            this.month = result.getData().getIntExtra("month", calendar.get(Calendar.MONTH));
            binding.f04EndDayTv.setText(year + "-" + String.format("%02d", month));
        }
    });


    // 좌측 상단에 메뉴(월마감, 계좌이체) 없애기
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.setGroupVisible(R.id.toolbar_menu, false);
        super.onCreateOptionsMenu(menu, inflater);
    }
}