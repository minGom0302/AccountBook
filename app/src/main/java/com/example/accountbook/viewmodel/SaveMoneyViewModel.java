package com.example.accountbook.viewmodel;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.accountbook.dto.MoneyDTO;
import com.example.accountbook.model.SaveMoneyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SaveMoneyViewModel extends ViewModel {
    private SaveMoneyModel model;
    private MutableLiveData<List<MoneyDTO>> moneyLiveData;
    private final MutableLiveData<List<MoneyDTO>> returnMoneyLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<MoneyDTO>> secondMoneyLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Integer>> plusAndMinusLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<MoneyDTO>> calendarDayLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Integer>> dayMoneyLiveData = new MutableLiveData<>();
    private int setMoneyInfoCnd;
    private String setCalendarDate;

    public void setSaveMoneyViewModel(Activity activity) {
        model = new SaveMoneyModel(activity);
        moneyLiveData = model.getMoneyLiveData();
    }

    public void setMoneyInfoViewModel(Activity activity, LifecycleOwner owner, String date, int cnd) {
        model = new SaveMoneyModel(activity);
        moneyLiveData = model.getMoneyLiveData();
        setMoneyInfoCnd = cnd;
        setCalendarDate = date;
        moneyLiveData.observe(owner, dtoList -> {
            setMoneyInfo(setMoneyInfoCnd);
            setPlusAndMinusLiveData();
            secondMoneyLiveData.postValue(null);
            if(cnd == 99) { // calendar 용, 나머지는 list
                setCalendarDayLiveData(setCalendarDate);
            }
        });
        if(cnd == 99) { // calendar 용, 나머지는 list
            model.getMoneyDataByDate(date.substring(0, date.length()-3) + "___");
        } else {
            model.getMoneyDataByDate(date);
        }
    }


    // 전체 기록 삭제 > saveMoneyInfo table
    public void deleteAll() {
        model.deleteAll();
    }


    // 월 바뀔 때 money info list 다시 가져오기
    public void againSet(String date, int cnd) {
        setMoneyInfoCnd = cnd;
        setCalendarDate = date;
        if(cnd == 99) {
            model.getMoneyDataByDate(date.substring(0, date.length()-3) + "___");
            Log.e("TEST", date);
        } else {
            model.getMoneyDataByDate(date);
        }
    }


    // money info list 를 상황에 맞게 리스트를 만들어서 리턴해줌
    public void setMoneyInfo(int cnd) {
        List<MoneyDTO> dtoList = new ArrayList<>();
        List<String> codeList = new ArrayList<>(); // code 값 중복 체크를 위해 리스트를 따로 만듬
        String category = "";
        if(cnd == 0) category = "99";
        else if(cnd == 1) category = "98";

        // Live Data 하나 씩 for 돌리기
        for(MoneyDTO dto : Objects.requireNonNull(moneyLiveData.getValue())) {
            // 선택에 따른 값만 추가하기 위해 조건문 설정 (수입/지출)
            if(dto.getCategory01().equals(category)) {
                // 계좌이체 내용은 빼기
                if(!dto.getCategory02().equals("01")) {
                    // 리스트에 해당 셋팅의 코드가 들어있는지 확인하는 조건
                    if (codeList.contains(dto.getSettingsCode())) {
                        // 들어 있으면 해당 표지션(값)에 금액만 더하기
                        int position = codeList.indexOf(dto.getSettingsCode());
                        String beforeMoney = dtoList.get(position).getMoney();
                        String afterMoney = String.valueOf(Integer.parseInt(beforeMoney) + Integer.parseInt(dto.getMoney()));
                        dtoList.get(position).setMoney(afterMoney);
                    } else {
                        // 들어있지 않으면 새로 추가하기
                        dtoList.add(new MoneyDTO(dto.getSettingsCode(), dto.getCategory02(), dto.getDate(), dto.getMoney(), dto.getSettingsContents()));
                        codeList.add(dto.getSettingsCode());
                    }
                }
            } else if(cnd == 2) {
                // 추가하기 전 뱅크 코드가 0인 것 걸러내기 > 리스트를 전체 가져오기 위해 null값을 0으로 대체했기에 없는 값 뺴고 계산
                if (!dto.getBankCode().equals("0")) {
                    // 잔액 표시를 위한 것 > 은행코드 확인하여 있으면 계산하고 없으면 추가
                    if (codeList.contains(dto.getBankCode())) {
                        // 들어 있으면 해당 표지션(값)에 금액만 더하기
                        int position = codeList.indexOf(dto.getBankCode());
                        int beforeMoney = dtoList.get(position).getIntMoney();
                        int afterMoney = 0;
                        if (dto.getCategory01().equals("99")) {
                            // 수입이라 +
                            afterMoney = beforeMoney + Integer.parseInt(dto.getMoney());
                        } else if (dto.getCategory01().equals("98")) {
                            // 지출이라 -
                            afterMoney = beforeMoney - Integer.parseInt(dto.getMoney());
                        }
                        dtoList.get(position).setIntMoney(afterMoney);
                    } else {
                        // 들어있지 않으면 새로 추가하기
                        int money = 0;
                        if (dto.getCategory01().equals("99")) {
                            money += Integer.parseInt(dto.getMoney());
                        } else if (dto.getCategory01().equals("98")) {
                            money -= Integer.parseInt(dto.getMoneyMemo());
                        }
                        dtoList.add(new MoneyDTO(dto.getCategory01(), money, dto.getBankCode(), dto.getBankContents()));
                        codeList.add(dto.getBankCode());
                    }
                }
            }
        }

        secondMoneyLiveData.postValue(null);
        returnMoneyLiveData.postValue(dtoList);
    }


    // total money setting
    public void setPlusAndMinusLiveData() {
        int plusMoney = 0;
        int minusMoney = 0;
        for(MoneyDTO dto : Objects.requireNonNull(moneyLiveData.getValue())) {
            if(dto.getCategory01().equals("99") && !dto.getCategory02().equals("01")) {
                plusMoney += Integer.parseInt(dto.getMoney());
            } else if(dto.getCategory01().equals("98") && !dto.getCategory02().equals("01")) {
                minusMoney += Integer.parseInt(dto.getMoney());
            }
        }
        List<Integer> integerList = new ArrayList<>();
        integerList.add(plusMoney);
        integerList.add(minusMoney);
        plusAndMinusLiveData.postValue(integerList);
    }


    // 아래 recyclerview 셋팅을 위한 live data 설정
    public void setSecondMoneyLiveData(String code, int type) {
        List<MoneyDTO> dtoList = new ArrayList<>();

        for(MoneyDTO dto : Objects.requireNonNull(moneyLiveData.getValue())) {
            if(type == 0) {
                if(dto.getSettingsCode().equals(code)) {
                    dtoList.add(dto);
                }
            } else if(type == 1) {
                if(dto.getBankCode().equals(code)) {
                    dtoList.add(dto);
                }
            }
        }

        secondMoneyLiveData.postValue(dtoList);
    }


    // Calendar Fragment 에서 날짜 클릭 시 하단에 보여줄 리스트 만들기 + 금액 계산
    public void setCalendarDayLiveData(String date) {
        List<MoneyDTO> dtoList = new ArrayList<>();
        int plusMoney = 0;
        int minusMoney = 0;

        for(MoneyDTO dto : Objects.requireNonNull(moneyLiveData.getValue())) {
            if(dto.getDate().equals(date)) {
                dtoList.add(dto);
                if(dto.getCategory01().equals("99") && !dto.getCategory02().equals("01")) {
                    plusMoney += Integer.parseInt(dto.getMoney());
                } else if(dto.getCategory01().equals("98") && !dto.getCategory02().equals("01")) {
                    minusMoney += Integer.parseInt(dto.getMoney());
                }
            }
        }

        List<Integer> integerList = new ArrayList<>();
        integerList.add(plusMoney);
        integerList.add(minusMoney);

        dayMoneyLiveData.postValue(integerList);
        calendarDayLiveData.postValue(dtoList);
    }


    // Getter
    public MutableLiveData<List<MoneyDTO>> getMoneyInfoByDate() {
        return returnMoneyLiveData;
    }
    public MutableLiveData<List<Integer>> getPlusAndMinusLiveData() {
        return plusAndMinusLiveData;
    }
    public MutableLiveData<List<MoneyDTO>> getSecondMoneyLiveData() {
        return secondMoneyLiveData;
    }
    public MutableLiveData<List<MoneyDTO>> getCalendarDayLiveData() {
        return calendarDayLiveData;
    }
    public MutableLiveData<List<Integer>> getDayMoneyLiveData() {
        return dayMoneyLiveData;
    }
    public MutableLiveData<List<MoneyDTO>> getMoneyLiveData() {
        return moneyLiveData;
    }
}
