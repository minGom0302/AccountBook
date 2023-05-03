package com.example.accountbook.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.accountbook.R;
import com.example.accountbook.activity.InputOutputActivity;
import com.example.accountbook.activity.MainActivity;
import com.example.accountbook.adapter.MoneyListAdapter;
import com.example.accountbook.calendar_deco.EventDecorator;
import com.example.accountbook.calendar_deco.SaturdayDecorator;
import com.example.accountbook.calendar_deco.SundayDecorator;
import com.example.accountbook.calendar_deco.TodayDecorator;
import com.example.accountbook.databinding.FragmentCalendarBinding;
import com.example.accountbook.dto.MoneyDTO;
import com.example.accountbook.item.Singleton_Date;
import com.example.accountbook.viewmodel.SaveMoneyViewModel;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

public class CalendarFragment extends Fragment implements OnMonthChangedListener, OnDateSelectedListener {
    private Singleton_Date s_date; // 날짜 변환한 거 저장하기
    private SaveMoneyViewModel moneyViewModel;
    private FragmentCalendarBinding binding;
    private String nowStrDate;
    private Date nowDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false);

        init();

        return binding.getRoot();
    }


    @SuppressLint("SimpleDateFormat")
    private void init() {
        s_date = Singleton_Date.getInstance();
        nowDate = new Date(System.currentTimeMillis());
        nowStrDate = new SimpleDateFormat("yyyy-MM-dd").format(nowDate);

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
        setViewModel();

        // 플로팅버튼 클릭 시 이벤트 진행 > 입력 화면으로 전환
        binding.f01FloatingBtn.setOnClickListener(v -> {
            requireActivity().startActivity(new Intent(getContext(), InputOutputActivity.class));
        });
        // 오늘 날짜로 이동 클릭 시 달력을 오늘 날짜로 이동시키기
        binding.f01MoveToday.setOnClickListener(v -> {
            binding.calendar.setCurrentDate(nowDate);
            binding.calendar.setSelectedDate(CalendarDay.today());
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
        Date choiceDate;
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
        String strDate = new SimpleDateFormat("yyyy-MM-dd").format(date.getDate());
        moneyViewModel.setCalendarDayLiveData(strDate); // 날짜 클릭 시 해당 날짜의 금액 계산과 리스트를 가져오도록 함
    }


    // live data 모음
    @SuppressLint("SetTextI18n")
    private void setViewModel() {
        // 상단 월 별 계산
        moneyViewModel.getPlusAndMinusLiveData().observe(getViewLifecycleOwner(), plusMinusList -> {
            binding.f01MonthPlus.setText("+ " + new DecimalFormat("#,###").format(plusMinusList.get(0)));
            binding.f01MonthMinus.setText("- " + new DecimalFormat("#,###").format(plusMinusList.get(1)));
        });
        // 날짜 선택에 따른 하단 리스트 보이기
        moneyViewModel.getCalendarDayLiveData().observe(getViewLifecycleOwner(), dayList -> {
            MoneyListAdapter moneyListAdapter = new MoneyListAdapter(dayList, 2);
            binding.f01Recyclerview.setAdapter(moneyListAdapter);
            binding.f01Recyclerview.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        });
        // 날짜 선택에 따른 하단금액 계산
        moneyViewModel.getDayMoneyLiveData().observe(getViewLifecycleOwner(), plusMinusList -> {
            binding.f01DayPlus.setText("+ " + new DecimalFormat("#,###").format(plusMinusList.get(0)));
            binding.f01DayMinus.setText("- " + new DecimalFormat("#,###").format(plusMinusList.get(1)));
        });

        moneyViewModel.getMoneyLiveData().observe(getViewLifecycleOwner(), moneyList -> {
            ArrayList<CalendarDay> calendarDayList = new ArrayList<>();

            for(MoneyDTO dto : moneyList) {
                String date = dto.getDate();
                if(!date.equals("0")) {
                    int year = Integer.parseInt(date.substring(0, 4));
                    // 달력이 0부터 시작해서 1월은 0, 2월은 1이다. 따라서 month 는 -1로 계산해줘야한다.
                    int month = Integer.parseInt(date.substring(5, date.length() - 3)) - 1;
                    int day = Integer.parseInt(date.substring(date.length() - 2));
                    calendarDayList.add(CalendarDay.from(year, month, day));
                }
            }

            binding.calendar.addDecorator(new EventDecorator(Color.RED, calendarDayList));
        });
    }
}