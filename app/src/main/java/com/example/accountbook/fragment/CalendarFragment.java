package com.example.accountbook.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.accountbook.R;
import com.example.accountbook.activity.InputOutputActivity;
import com.example.accountbook.activity.MainActivity;
import com.example.accountbook.calendar_deco.SaturdayDecorator;
import com.example.accountbook.calendar_deco.SundayDecorator;
import com.example.accountbook.calendar_deco.TodayDecorator;
import com.example.accountbook.databinding.FragmentCalendarBinding;
import com.example.accountbook.item.Singleton_Date;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class CalendarFragment extends Fragment implements OnMonthChangedListener, OnDateSelectedListener {
    private Singleton_Date s_date; // 날짜 변환한 거 저장하기

    @Nullable
    @Override
    @SuppressLint("SimpleDateFormat")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentCalendarBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false);

        s_date = Singleton_Date.getInstance();

        Date date = new Date(System.currentTimeMillis());
        String strDate = new SimpleDateFormat("yyyyMMdd").format(date);
        String strMonth = new SimpleDateFormat("yyyyMM").format(date);

        // 금액 설정
        monthMoneySetting(strMonth);
        dayMoneySettings(strDate);

        binding.calendar.setSelectedDate(date); // 오늘 날짜로 셋팅
        binding.calendar.setTopbarVisible(false); // 달력에서 상단 타이틀 (날짜) 없애기
        binding.calendar.setOnMonthChangedListener(this); // 달력에서 월이 바뀔 때 이벤트 설정
        binding.calendar.setOnDateChangedListener(this); // 달력에서 날짜를 클릭할 때 이벤트 설정
        binding.calendar.addDecorators(new SaturdayDecorator() // 데코 추가
                , new SundayDecorator()
                , new TodayDecorator());

        // 플로팅버튼 클릭 시 이벤트 진행 > 입력 화면으로 전환
        binding.f01FloatingBtn.setOnClickListener(v -> {
            requireActivity().startActivity(new Intent(getContext(), InputOutputActivity.class));
        });
        // 오늘 날짜로 이동 클릭 시 달력을 오늘 날짜로 이동시키기
        binding.f01MoveToday.setOnClickListener(v -> {
            binding.calendar.setSelectedDate(date);
            binding.calendar.setCurrentDate(date);
        });

        return binding.getRoot();
    }

    // 달력에서 월이 바뀔 때 해당 년월을 가져와 상단 바에 작성하고 값을 저장
    // 다른 화면 갔다와도 상단바에 동일한 년월 작성하기 위해서
    @Override
    @SuppressLint("SimpleDateFormat")
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        String title = new SimpleDateFormat("yyyy년 MM월").format(date.getDate());
        String strDate = new SimpleDateFormat("yyyyMM").format(date.getDate());
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setTitle(title);
        s_date.setDate(title); // 싱글톤에 월 저장해서 사용함

        monthMoneySetting(strDate);
    }

    // 날짜 클릭했을 때 발생할 이벤트 설정
    // DB와 접속하여 자료를 가져오고 리사이클러뷰와 그날 금액 보여주기
    @Override
    @SuppressLint("SimpleDateFormat")
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        String strDate = new SimpleDateFormat("yyyyMMdd").format(date.getDate());
        dayMoneySettings(strDate);
    }

    private void monthMoneySetting(String strDate) {
        //Toast.makeText(getContext(), "monthMoney : " + strDate, Toast.LENGTH_SHORT).show();
    }

    private void dayMoneySettings(String strDate) {
        //Toast.makeText(getContext(), "dateMoney : " + strDate, Toast.LENGTH_SHORT).show();
    }
}