package com.example.accountbook.fragment;

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

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.accountbook.R;
import com.example.accountbook.activity.InputOutputActivity;
import com.example.accountbook.activity.MainActivity;
import com.example.accountbook.activity.Popup_InputOutput;
import com.example.accountbook.activity.Popup_Transfer;
import com.example.accountbook.adapter.MoneyListAdapter;
import com.example.accountbook.calendar_deco.EventDecorator;
import com.example.accountbook.calendar_deco.SaturdayDecorator;
import com.example.accountbook.calendar_deco.SundayDecorator;
import com.example.accountbook.calendar_deco.TodayDecorator;
import com.example.accountbook.databinding.FragmentCalendarBinding;
import com.example.accountbook.dto.CategoryDTO;
import com.example.accountbook.dto.MoneyDTO;
import com.example.accountbook.item.Singleton_Date;
import com.example.accountbook.viewmodel.CategorySettingViewModel;
import com.example.accountbook.viewmodel.SaveMoneyViewModel;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CalendarFragment extends Fragment implements OnMonthChangedListener, OnDateSelectedListener {
    private Singleton_Date s_date; // 날짜 변환한 거 저장하기
    private SaveMoneyViewModel moneyViewModel;
    private CategorySettingViewModel categoryViewModel;
    private FragmentCalendarBinding binding;
    private List<CategoryDTO> categoryList;
    private List<MoneyDTO> forwardList;
    private int incomeSeq, spendingSeq, plusForwardSeq, minusForwardSeq;
    private String nowStrDate;
    private String[] bankCodeArray, bankValueArray, outPutBankCodeArray, outPutBankValueArray, outPutCategoryCodeArray, outPutCategoryValueArray, outPutcategory01Array;
    private Date nowDate, choiceDate;
    private EventDecorator oldEd = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false);

        init();

        return binding.getRoot();
    }

    @SuppressLint("SimpleDateFormat")
    private void init() {
        setHasOptionsMenu(true); // 상단에 메뉴 표시하기

        s_date = Singleton_Date.getInstance();
        nowDate = new Date(System.currentTimeMillis());
        nowStrDate = new SimpleDateFormat("yyyy-MM-dd").format(nowDate);
        choiceDate = nowDate;

        binding.calendar.setSelectedDate(nowDate); // 오늘 날짜로 셋팅
        binding.calendar.setTopbarVisible(false); // 달력에서 상단 타이틀 (날짜) 없애기
        binding.calendar.setOnMonthChangedListener(this); // 달력에서 월이 바뀔 때 이벤트 설정
        binding.calendar.setOnDateChangedListener(this); // 달력에서 날짜를 클릭할 때 이벤트 설정
        binding.calendar.addDecorators(new SaturdayDecorator() // 데코 추가
                , new SundayDecorator()
                , new TodayDecorator());

        // 뷰 모델 설정
        moneyViewModel = new ViewModelProvider(this).get(SaveMoneyViewModel.class);
        moneyViewModel.setMoneyInfoViewModel(getActivity(), getViewLifecycleOwner(),  nowStrDate, 99);
        categoryViewModel = new ViewModelProvider(this).get(CategorySettingViewModel.class);
        categoryViewModel.setViewModel(getActivity(), this, 1);
        setViewModel();

        // 플로팅버튼 클릭 시 이벤트 진행 > 입력 화면으로 전환
        binding.f01FloatingBtn.setOnClickListener(v -> {
            Date floatingChoiceDate = binding.calendar.getSelectedDate().getDate();
            String floatingChoiceStrDate = new SimpleDateFormat("yyyy-MM-dd").format(floatingChoiceDate);

            Intent intent = new Intent(getContext(), InputOutputActivity.class);
            intent.putExtra("date", floatingChoiceStrDate);
            inOutActivity.launch(intent);
        });
        // 오늘 날짜로 이동 클릭 시 달력을 오늘 날짜로 이동시키기
        binding.f01MoveToday.setOnClickListener(v -> {
            binding.calendar.setCurrentDate(nowDate);
            binding.calendar.setSelectedDate(CalendarDay.today());
            moneyViewModel.setCalendarDayLiveData(nowStrDate);
        });

    }


    // 달력에서 월이 바뀔 때 해당 년월을 가져와 상단 바에 작성하고 값을 저장
    // 다른 화면 갔다와도 상단바에 동일한 년월 작성하기 위해서
    // 월이 바뀔 때마다 DB에서 자료 가져오기
    @Override
    @SuppressLint("SimpleDateFormat")
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        String title = new SimpleDateFormat("yyyy년 MM월").format(date.getDate());
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setTitle(title);
        s_date.setDate(title); // 싱글톤에 월 저장해서 사용함

        // 월이 바뀜으로 DB에서 데이터 다시 가져오기 > 바뀐 월의 1일로 아래 리스트도 가져오고 선택함 > 해당 월일 경우 오늘 날짜로 셋팅
        String changeDate = new SimpleDateFormat("yyyy-MM-dd").format(date.getDate());
        String beforeDate = nowStrDate;
        // 해당 월인지 확인하여 아닐 경우 달력의 1일, 맞을 경우 오늘 날짜로 날짜 셋팅
        if(changeDate.substring(0, changeDate.length() -3).equals(beforeDate.substring(0, beforeDate.length() -3))) {
            choiceDate = nowDate;
        } else {
            choiceDate = date.getDate();
        }
        // 셋팅한 날짜로 자료 가져오기
        moneyViewModel.againSet(new SimpleDateFormat("yyyy-MM-dd").format(choiceDate), 99);
        binding.calendar.setSelectedDate(choiceDate);
    }


    // 날짜 클릭했을 때 발생할 이벤트 설정
    // 해당 날짜 리스트 가져오기
    @Override
    @SuppressLint("SimpleDateFormat")
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        choiceDate = date.getDate();
        String strDate = new SimpleDateFormat("yyyy-MM-dd").format(date.getDate());
        moneyViewModel.setCalendarDayLiveData(strDate); // 날짜 클릭 시 해당 날짜의 금액 계산과 리스트를 가져오도록 함
    }


    // live data 모음
    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    private void setViewModel() {
        // 상단 월 별 계산
        moneyViewModel.getPlusAndMinusLiveData().observe(getViewLifecycleOwner(), plusMinusList -> {
            binding.f01MonthPlus.setText("+ " + new DecimalFormat("#,###").format(plusMinusList.get(0)));
            binding.f01MonthMinus.setText("- " + new DecimalFormat("#,###").format(plusMinusList.get(1)));
            moneyViewModel.setMoneyInfo(2); // 잔액(월 마감)
        });
        // 날짜 선택에 따른 하단 리스트 보이기
        moneyViewModel.getCalendarDayLiveData().observe(getViewLifecycleOwner(), dayList -> {
            MoneyListAdapter moneyListAdapter = new MoneyListAdapter(dayList, 2, moneyViewModel, getActivity());
            binding.f01Recyclerview.setAdapter(moneyListAdapter);
            binding.f01Recyclerview.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

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
        // 날짜 선택에 따른 하단금액 계산
        moneyViewModel.getDayMoneyLiveData().observe(getViewLifecycleOwner(), plusMinusList -> {
            binding.f01DayPlus.setText("+ " + new DecimalFormat("#,###").format(plusMinusList.get(0)));
            binding.f01DayMinus.setText("- " + new DecimalFormat("#,###").format(plusMinusList.get(1)));
        });
        // money info 변화 감지하여 달력에 녹색 점 찍기
        moneyViewModel.getMoneyLiveData().observe(getViewLifecycleOwner(), moneyList -> {
            ArrayList<CalendarDay> calendarDayList = new ArrayList<>();

            for(MoneyDTO dto : moneyList) {
                String date = dto.getDate();
                if(!date.equals("0") && !dto.getCategory02().equals("01")) {
                    int year = Integer.parseInt(date.substring(0, 4));
                    // 달력이 0부터 시작해서 1월은 0, 2월은 1이다. 따라서 month 는 -1로 계산해줘야한다.
                    int month = Integer.parseInt(date.substring(5, date.length() - 3)) - 1;
                    int day = Integer.parseInt(date.substring(date.length() - 2));
                    calendarDayList.add(CalendarDay.from(year, month, day));
                }
            }

            // 기존에 등록한 녹색 점 데코 없애고 새로운 데코 넣기
            if(oldEd != null) binding.calendar.removeDecorator(oldEd);
            EventDecorator newEd = new EventDecorator(Color.GREEN, calendarDayList);
            binding.calendar.addDecorator(newEd);
            oldEd = newEd;
        });
        // 월 마감을 위해 잔액을 항상 가지고 있고 바뀔 때 마다 감지
        moneyViewModel.getMoneyInfoByDate().observe(getViewLifecycleOwner(), moneyList -> this.forwardList = moneyList);
        // bank transfer (계좌 이체) 에 넘길 계좌 코드 array, 실제 값 array 설정
        categoryViewModel.getCategoryListForShow().observe(getViewLifecycleOwner(), categoryList -> {
            bankCodeArray = new String[categoryList.size()];
            bankValueArray = new String[categoryList.size()];
            outPutBankCodeArray = new String[categoryList.size()+1];
            outPutBankValueArray = new String[categoryList.size()+1];

            for(int i=0; i<categoryList.size(); i++) {
                CategoryDTO dto = categoryList.get(i);
                bankCodeArray[i] = String.valueOf(dto.getSeq());
                bankValueArray[i] = dto.getContents();
                outPutBankCodeArray[i + 1] = String.valueOf(dto.getSeq());
                outPutBankValueArray[i + 1] = dto.getContents();
            }
        });
        // 셋팅값 가져오기 > money info 에 저장하기 위해 계좌이체 항목을 찾아야함
        // 이월처리하기 위해 forwardSeq 찾아야함
        categoryViewModel.getCategoryList().observe(getViewLifecycleOwner(), categoryList -> {
            this.categoryList = categoryList;
            for(CategoryDTO dto : categoryList) {
                switch (dto.getCode()) {
                    case "9901001":
                        incomeSeq = dto.getSeq();
                        break;
                    case "9801001":
                        spendingSeq = dto.getSeq();
                        break;
                    case "9900001":
                        plusForwardSeq = dto.getSeq();
                        break;
                    case "9889001" :
                        minusForwardSeq = dto.getSeq();
                        break;
                }
            }
        });
        // 카테고리 메뉴 가져와서 카드/계좌 분류하고 리스트도 가져옴 > 팝업 띄울 때 넘겨준다.
        categoryViewModel.getCategoryList().observe(getViewLifecycleOwner(), categoryList -> {
            String[] categoryCodeArray = new String[categoryList.size()];
            String[] categoryValueArray = new String[categoryList.size()];
            String[] category01Array = new String[categoryList.size()];
            int categorySize = 0;

            for(int i=0; i<categoryList.size(); i++) {
                CategoryDTO dto = categoryList.get(i);
                if(dto.getCategory01().equals("99") || dto.getCategory01().equals("98")) {
                    if(!dto.getCategory02().equals("01")) {
                        categoryCodeArray[categorySize] = String.valueOf(dto.getSeq());
                        categoryValueArray[categorySize] = dto.getContents();
                        category01Array[categorySize] = dto.getCategory01();
                        categorySize++;
                    }
                }
            }
            outPutCategoryCodeArray = Arrays.copyOf(categoryCodeArray, categorySize);
            outPutCategoryValueArray = Arrays.copyOf(categoryValueArray, categorySize);
            outPutcategory01Array = Arrays.copyOf(category01Array, categorySize);
        });
    }


    // startActivityForResult가 deprecated 되어 사용하지 않고 아래 방식으로 사용함
    private final ActivityResultLauncher<Intent> inOutActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK) {
            assert result.getData() != null;
            moneyViewModel.againSet(result.getData().getStringExtra("date"), 99);
        }
    });

    @SuppressLint("SimpleDateFormat")
    private final ActivityResultLauncher<Intent> transferPopup = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK) {
            assert  result.getData() != null;
            String date = result.getData().getStringExtra("date");
            int incomeCode = Integer.parseInt(result.getData().getStringExtra("incomeCode"));
            int spendingCode = Integer.parseInt(result.getData().getStringExtra("spendingCode"));
            String incomeBank = result.getData().getStringExtra("incomeBank");
            String spendingBank = result.getData().getStringExtra("spendingBank");
            String money = result.getData().getStringExtra("money");
            String memo = result.getData().getStringExtra("memo");

            moneyViewModel.insertTransferMoneyInfo(incomeCode, spendingCode, date, money, memo, incomeBank, spendingBank, incomeSeq, spendingSeq);
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

            moneyViewModel.modifyMoneyInfo(seq, settingsSeq, bankSeq, in_sp, inputDate, inputMoney, inputMemo, new SimpleDateFormat("yyyy-MM-dd").format(choiceDate));
        }
    });


    // MainActivity 에서 화면 전환할 때 호출하여 자료 최신화
    @SuppressLint("SimpleDateFormat")
    public void calendarRefresh() {
        if(moneyViewModel.getIsChange() || moneyViewModel.getIsChangeCal()) {
            if(moneyViewModel.getIsChange()) moneyViewModel.setIsChange(false);
            else if(moneyViewModel.getIsChangeCal()) moneyViewModel.setIsChangeCal(false);

            moneyViewModel.againSet(new SimpleDateFormat("yyyy-MM-dd").format(choiceDate), 99);
        }
    }

    // 좌측 상단에 메뉴(월마감, 계좌이체) 표시하기
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    // 상단 메뉴 클릭 시 실행 > 계좌이체, 월마감
    @SuppressLint({"NonConstantResourceId", "SimpleDateFormat", "DefaultLocale"})
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tool_menu01:
                String beforeDate = new SimpleDateFormat("yyyy-MM-dd").format(choiceDate);
                String saveDate = beforeDate.substring(0, 4) + "-" + String.format("%02d", Integer.parseInt(beforeDate.substring(5, 7))+1) + "-01";
                int size = forwardList.size();

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.DialogTheme);
                builder.setTitle("안내").setMessage("월마감을 하면 잔액이 이월처리됩니다.\n계속하시곘습니까?");
                builder.setPositiveButton("예", ((dialogInterface, i) -> {
                    int count = 1;
                    int isFinish = 2;
                    for(MoneyDTO dto : forwardList) {
                        if(dto.getBankContents().equals("미설정 계좌")) {
                            if(size == count) isFinish = 1;
                            if(dto.getIntMoney() < 0) {
                                moneyViewModel.insertMoneyInfo(minusForwardSeq, 0, "98", saveDate, String.valueOf(dto.getIntMoney()).replace("-", ""), dto.getBankContents()+" 이월 처리", isFinish);
                            } else {
                                moneyViewModel.insertMoneyInfo(plusForwardSeq, 0, "99", saveDate, String.valueOf(dto.getIntMoney()).replace("-", ""), dto.getBankContents()+" 이월 처리", isFinish);
                            }
                            count++;
                        }
                        for(CategoryDTO c_DTO : categoryList) {
                            if(dto.getBankCode().equals(c_DTO.getCode())) {
                                if(size == count) isFinish = 1;
                                if(dto.getIntMoney() < 0) {
                                    moneyViewModel.insertMoneyInfo(minusForwardSeq, c_DTO.getSeq(), "98", saveDate, String.valueOf(dto.getIntMoney()).replace("-", ""), dto.getBankContents()+" 이월 처리", isFinish);
                                } else {
                                    moneyViewModel.insertMoneyInfo(plusForwardSeq, c_DTO.getSeq(), "99", saveDate, String.valueOf(dto.getIntMoney()).replace("-", ""), dto.getBankContents()+" 이월 처리", isFinish);
                                }
                                count++;
                            }
                        }
                    }
                }));
                builder.setNegativeButton("아니오", ((dialogInterface, i) -> { }));

                AlertDialog alertDialog = builder.create();
                alertDialog.setOnShowListener(dialogInterface -> {
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                });
                alertDialog.show();
                break;
            case R.id.tool_menu02:
                Intent intent = new Intent(getContext(), Popup_Transfer.class);
                intent.putExtra("codeArray", bankCodeArray);
                intent.putExtra("valueArray", bankValueArray);
                intent.putExtra("date", new SimpleDateFormat("yyyy-MM-dd").format(choiceDate));
                transferPopup.launch(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}