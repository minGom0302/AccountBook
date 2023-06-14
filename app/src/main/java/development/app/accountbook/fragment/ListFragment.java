package development.app.accountbook.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import development.app.accountbook.activity.Popup_DatePicker;
import development.app.accountbook.databinding.FragmentListBinding;
import development.app.accountbook.dto.CategoryDTO;
import development.app.accountbook.viewmodel.SaveMoneyViewModel;
import development.app.accountbook.R;
import development.app.accountbook.activity.Popup_InputOutput;
import development.app.accountbook.adapter.CategoryListAdapter;
import development.app.accountbook.adapter.MoneyListAdapter;
import development.app.accountbook.dto.MoneyDTO;
import development.app.accountbook.viewmodel.CategorySettingViewModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
    private FragmentListBinding binding;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월");
    private SaveMoneyViewModel moneyViewModel;
    private CategorySettingViewModel categorySettingViewModel;
    private String searchDate;
    private int year, month;
    private int cnd = 0;
    private String[] outPutBankCodeArray, outPutBankValueArray, outPutCategoryCodeArray, outPutCategoryValueArray, outPutcategory01Array;
    private TextView beforeTextView = null;
    private final Calendar calendar = Calendar.getInstance();

    private String settingsCode = null;
    private int type = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);

        init();

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        setHasOptionsMenu(true);
        // 첫 화면 년월 설정
        Date date = new Date(System.currentTimeMillis());
        setDateTitle(dateFormat.format(date));
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;

        // 뷰모델 설정 및 초기데이터 가져오기
        categorySettingViewModel = new ViewModelProvider(requireActivity()).get(CategorySettingViewModel.class);
        categorySettingViewModel.setViewModel(getActivity(), getViewLifecycleOwner(), 0);
        moneyViewModel = new ViewModelProvider(this).get(SaveMoneyViewModel.class);
        moneyViewModel.setMoneyInfoViewModel(getActivity(), getViewLifecycleOwner(), searchDate, 0);

        // 카테고리 메뉴 가져와서 카드/계좌 분류하고 리스트도 가져옴 > 팝업 띄울 때 넘겨준다.
        categorySettingViewModel.getCategoryList().observe(getViewLifecycleOwner(), categoryList -> {
            String[] bankCodeArray = new String[categoryList.size()];
            String[] bankValueArray = new String[categoryList.size()];
            String[] categoryCodeArray = new String[categoryList.size()];
            String[] categoryValueArray = new String[categoryList.size()];
            String[] category01Array = new String[categoryList.size()];
            int bankSize = 1;
            int categorySize = 0;
            bankCodeArray[0] = "";
            bankValueArray[0] = "";

            for(int i=0; i<categoryList.size(); i++) {
                CategoryDTO dto = categoryList.get(i);
                if(dto.getCategory01().equals("97") || dto.getCategory01().equals("96")) {
                    bankCodeArray[bankSize] = String.valueOf(dto.getSeq());
                    bankValueArray[bankSize] = dto.getContents();
                    bankSize++;
                } else if(dto.getCategory01().equals("99") || dto.getCategory01().equals("98")) {
                    if(!dto.getCategory02().equals("01")) {
                        categoryCodeArray[categorySize] = String.valueOf(dto.getSeq());
                        categoryValueArray[categorySize] = dto.getContents();
                        category01Array[categorySize] = dto.getCategory01();
                        categorySize++;
                    }
                }
            }
            outPutBankCodeArray = Arrays.copyOf(bankCodeArray, bankSize);
            outPutBankValueArray = Arrays.copyOf(bankValueArray, bankSize);
            outPutCategoryCodeArray = Arrays.copyOf(categoryCodeArray, categorySize);
            outPutCategoryValueArray = Arrays.copyOf(categoryValueArray, categorySize);
            outPutcategory01Array = Arrays.copyOf(category01Array, categorySize);
        });
        // live data (total money) 감지하여 바뀔 경우 실행
        moneyViewModel.getPlusAndMinusLiveData().observe(getViewLifecycleOwner(), integersList -> {
            binding.f02PlusTv.setText("+ " + new DecimalFormat("#,###").format(integersList.get(0)));
            binding.f02MinusTv.setText("- " + new DecimalFormat("#,###").format(integersList.get(1)));
        });
        // live data (수입/지출 묶음) 감지하여 값이 바뀔 경우 실행
        moneyViewModel.getMoneyInfoByDate().observe(getViewLifecycleOwner(), dtoList -> {
            if(cnd == 0) { // 수입
                categoryAdapterSet(dtoList, 0, binding.f02CategoryRecyclerView02);
            } else if(cnd == 1) {
                // 지출의 고정, 변동, 준변동비를 나눠서 사용할 경우 > myPage 에서 설정
                // 지출 > 고정비, 변동비, 준변동비가 있어 3개로 돌림
                if(categorySettingViewModel.getSpendingTypeUse()) {
                    List<MoneyDTO> money01 = new ArrayList<>();
                    List<MoneyDTO> money02 = new ArrayList<>();
                    List<MoneyDTO> money03 = new ArrayList<>();
                    for (MoneyDTO dto : dtoList) {
                        switch (dto.getCategory02()) {
                            case "90":
                                money01.add(dto);
                                break;
                            case "89":
                                money02.add(dto);
                                break;
                            case "88":
                                money03.add(dto);
                                break;
                        }
                    }

                    categoryAdapterSet(money02, 0, binding.f02RView0101);
                    categoryAdapterSet(money03, 0, binding.f02RView0102);
                    categoryAdapterSet(money01, 0, binding.f02RView0103);
                } else {
                    // 지출의 고정, 변동, 준변동비를 나눠서 사용하지 않을 경우 > myPage 에서 설정
                    categoryAdapterSet(dtoList, 0, binding.f02CategoryRecyclerView02);
                }
            } else if(cnd == 2) {
                // 계좌별 보기
                categoryAdapterSet(dtoList, 1, binding.f02CategoryRecyclerView02);
            }
        });
        // 상단에서 목록 클릭 시 하단 리사이클러뷰에 내역 보여주기위해 live data 감시
        moneyViewModel.getSecondMoneyLiveData().observe(getViewLifecycleOwner(), moneyDtoList -> {
            MoneyListAdapter moneyListAdapter = new MoneyListAdapter(moneyDtoList, cnd, moneyViewModel, getActivity());
            binding.f02MoneyRecyclerView.setAdapter(moneyListAdapter);
            binding.f02MoneyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

            moneyListAdapter.setModifyItemClickListener((view, moneyDTO) -> {
                Intent intent = new Intent(getContext(), Popup_InputOutput.class);
                intent.putExtra("inputType", false);
                intent.putExtra("settingsCode", moneyDTO.getSettingsCode());
                intent.putExtra("money", moneyDTO.getMoney());
                intent.putExtra("moneySeq", moneyDTO.getMoneySeq());
                intent.putExtra("date", moneyDTO.getDate());
                intent.putExtra("memo", moneyDTO.getMoneyMemo());
                intent.putExtra("codeArray", outPutBankCodeArray);
                intent.putExtra("valueArray", outPutBankValueArray);
                intent.putExtra("bankPosition", moneyDTO.getBankContents());
                intent.putExtra("categoryCodeArray", outPutCategoryCodeArray);
                intent.putExtra("categoryValueArray", outPutCategoryValueArray);
                intent.putExtra("categoryPosition", moneyDTO.getSettingsContents());
                intent.putExtra("category01Value", outPutcategory01Array);
                modifyPopup.launch(intent);
            });
        });

        // 날짜 타이틀 클릭하면 popup 띄우기
        binding.f02DateTv.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Popup_DatePicker.class);
            intent.putExtra("year", year);
            intent.putExtra("month", month);
            Popup_datePickerResult.launch(intent);
        });
        // 라디오그룹 (수입/지출/잔액) 클릭하여 변경 시 작동할 리스너 설정
        binding.f02RbGroup.setOnCheckedChangeListener(this);
    }


    // 년월 설정 메소드
    @SuppressLint("SetTextI18n")
    private void setDateTitle(String date) {
        binding.f02DateTv.setText(date + " ▼");
        searchDate = date.replaceAll("년 ", "-").replaceAll("월", "___");
    }


    // 라디오그룹에서 수입, 지출, 잔액 선택에 따른 데이터 변경경
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if(binding.f02Rb01.isChecked()) {
            moneyViewModel.setMoneyInfo(0); // 수입
            binding.f02MoneyTvLayout.setVisibility(View.VISIBLE); // 합계 금액 보여주기
            binding.f02CategoryRecyclerView01.setVisibility(View.GONE);
            binding.f02CategoryRecyclerView02.setVisibility(View.VISIBLE);
            cnd = 0;
        } else if(binding.f02Rb02.isChecked()) {
            moneyViewModel.setMoneyInfo(1); // 지출
            binding.f02MoneyTvLayout.setVisibility(View.VISIBLE); // 합계 금액 보여주기
            if(categorySettingViewModel.getSpendingTypeUse()) {
                // 고정비, 변동비, 준변동비를 나눠서 보여줄 때
                binding.f02CategoryRecyclerView01.setVisibility(View.VISIBLE);
                binding.f02CategoryRecyclerView02.setVisibility(View.GONE);
            } else {
                // 고정비, 변동비, 준변동비를 나누지 않고 보여줄 때
                binding.f02CategoryRecyclerView01.setVisibility(View.GONE);
                binding.f02CategoryRecyclerView02.setVisibility(View.VISIBLE);
            }
            cnd = 1;
        } else if(binding.f02Rb03.isChecked()) {
            moneyViewModel.setMoneyInfo(2); // 잔액
            binding.f02MoneyTvLayout.setVisibility(View.GONE); // 합계 금액 숨기기
            binding.f02CategoryRecyclerView01.setVisibility(View.GONE);
            binding.f02CategoryRecyclerView02.setVisibility(View.VISIBLE);
            cnd = 2;
        }

        this.settingsCode = null;
        this.type = -1;
    }

    // 수입/지출/계좌 별 카테고리 리사이클러뷰 셋팅
    private void categoryAdapterSet(List<MoneyDTO> dtoList, int type, RecyclerView recyclerView) {
        CategoryListAdapter adapter = new CategoryListAdapter(dtoList, type);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        adapter.setOnItemClickListener((v, settingsCode) -> { // 클릭 시 하단에 메뉴 보여주기
            moneyViewModel.setSecondMoneyLiveData(settingsCode, type);

            // 텍스트 뷰 색상 변경
            if(beforeTextView != null) {
                beforeTextView.setTextColor(Color.parseColor("#000000"));
                beforeTextView.setBackgroundResource(R.drawable.background_rectangle03);
            }
            TextView textView = (TextView) v;
            textView.setTextColor(Color.parseColor("#FFFFFF"));
            textView.setBackgroundColor(Color.parseColor("#99CCFF"));
            beforeTextView = textView;

            this.settingsCode = settingsCode;
            this.type = type;
        });
        adapter.setInfoOnItemClickListener(v -> mShowDialog());

        if(this.settingsCode != null && this.type != -1) {
            adapter.setClickColor(this.settingsCode);
            moneyViewModel.setSecondMoneyLiveData(this.settingsCode, type);
        }
    }


    // 화면 전환할 때 호출하여 자료 최신화
    @SuppressLint("SimpleDateFormat")
    public void listRefresh() {
        if(moneyViewModel.getIsChange() || categorySettingViewModel.getIsChangeCa() || categorySettingViewModel.getSpendingTypeUse() || !categorySettingViewModel.getSpendingTypeUse()) {
            if(moneyViewModel.getIsChange()) moneyViewModel.setIsChange(false);
            if(categorySettingViewModel.getIsChangeCa()) categorySettingViewModel.setIsChangeCa(false);

            this.settingsCode = null;
            this.type = -1;
            moneyViewModel.againSet(searchDate, cnd);

            if (binding.f02Rb02.isChecked()) {
                if (categorySettingViewModel.getSpendingTypeUse()) {
                    // 고정비, 변동비, 준변동비를 나눠서 보여줄 때
                    binding.f02CategoryRecyclerView01.setVisibility(View.VISIBLE);
                    binding.f02CategoryRecyclerView02.setVisibility(View.GONE);
                } else {
                    // 고정비, 변동비, 준변동비를 나누지 않고 보여줄 때
                    binding.f02CategoryRecyclerView01.setVisibility(View.GONE);
                    binding.f02CategoryRecyclerView02.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    // 월 바꾸는 화면에서 돌아왔을 때
    @SuppressLint("DefaultLocale")
    private final ActivityResultLauncher<Intent> Popup_datePickerResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK) {
            assert result.getData() != null;
            this.year = result.getData().getIntExtra("year", calendar.get(Calendar.YEAR));
            this.month = result.getData().getIntExtra("month", calendar.get(Calendar.MONTH));
            String m = String.format("%02d", month);

            setDateTitle(year + "년 " + m + "월");

            this.settingsCode = null;
            this.type = -1;
            moneyViewModel.againSet(searchDate, cnd); // 월이 바뀌면 다시 정보 가져오기
        }
    });

    // 수정화면에서 돌아왔을 때
    @SuppressLint("SimpleDateFormat")
    private final ActivityResultLauncher<Intent> modifyPopup = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK) {
            assert  result.getData() != null;
            Intent returnIntent = result.getData();
            int seq = returnIntent.getIntExtra("seq", 0);
            int settingsSeq = returnIntent.getIntExtra("settingsSeq", 0);
            int bankSeq = returnIntent.getIntExtra("bankSeq", 0);
            String in_sp = returnIntent.getStringExtra("in_sp");
            String inputDate = returnIntent.getStringExtra("date");
            String inputMoney = returnIntent.getStringExtra("money");
            String inputMemo = returnIntent.getStringExtra("memo");

            moneyViewModel.modifyMoneyInfo(seq, settingsSeq, bankSeq, in_sp, inputDate, inputMoney, inputMemo, searchDate);
        }
    });

    // 좌측 상단에 메뉴(월마감, 계좌이체) 없애기
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.setGroupVisible(R.id.toolbar_menu, false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // 다이얼로그 띄우기
    private void mShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.DialogTheme);
        builder.setTitle("안내").setMessage("※ 계좌이체와 이월은 수입/지출 금액에 합산되지 않습니다.\n(잔액에서만 합산됩니다.)");
        builder.setPositiveButton("확인", (((dialog, which) -> {

            })));

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        });
        dialog.show();
    }
}