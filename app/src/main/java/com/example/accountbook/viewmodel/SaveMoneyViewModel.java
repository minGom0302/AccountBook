package com.example.accountbook.viewmodel;

import android.app.Activity;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.accountbook.dto.MoneyDTO;
import com.example.accountbook.dto.TransferMoneyDTO;
import com.example.accountbook.model.SaveMoneyModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SaveMoneyViewModel extends ViewModel {
    private SaveMoneyModel model;
    private MutableLiveData<List<MoneyDTO>> moneyLiveData;
    private MutableLiveData<List<TransferMoneyDTO>> transferMoneyLiveData;
    private final MutableLiveData<List<MoneyDTO>> returnMoneyLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<MoneyDTO>> secondMoneyLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Integer>> plusAndMinusLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<MoneyDTO>> calendarDayLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Integer>> dayMoneyLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<MoneyDTO>> ioMoneyLiveData = new MutableLiveData<>();
    private int setMoneyInfoCnd;
    private String setCalendarDate;

    public void setSaveMoneyViewModel(Activity activity) {
        model = new SaveMoneyModel(activity);
        moneyLiveData = model.getMoneyLiveData();
    }
    // 계좌이체용
    public void setSaveMoneyTransferViewModel(Activity activity, String date) {
        model = new SaveMoneyModel(activity);
        transferMoneyLiveData = model.getTransferMoneyLiveData();
        model.getTransferMoneyInfo(date);
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
            } else if(cnd == 98) {
                setIoMoneyLiveData();
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


    // 특정 기록 선택 삭제 > saveMoneyInfo table
    public void deleteSaveMoneyInfo(int moneySeq) {
        model.deleteSaveMoneyInfo(moneySeq, setCalendarDate);
    }


    // 계좌이체 내역 삭제
    public void deleteTransferMoneyInfo(int seq, String date) {
        model.deleteTransferMoneyInfo(seq, date);
    }


    // 월 바뀔 때 money info list 다시 가져오기
    public void againSet(String date, int cnd) {
        setMoneyInfoCnd = cnd;
        setCalendarDate = date;
        if(cnd == 99) {
            model.getMoneyDataByDate(date.substring(0, date.length()-3) + "___");
        } else if (cnd == 98){
            model.getTransferMoneyInfo(date);
        } else {
            model.getMoneyDataByDate(date);
        }
    }


    // money info list 를 상황에 맞게 리스트를 만들어서 리턴해줌
    public void setMoneyInfo(int cnd) {
        setMoneyInfoCnd = cnd;
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
                if (!dto.getDate().equals("0")) {
                    if(dto.getBankCode().equals("0")) {
                        dto.setBankCode("미설정");
                        dto.setBankContents("미설정 계좌");
                    }
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
                            money -= Integer.parseInt(dto.getMoney());
                        }
                        if(dto.getBankContents().equals("0")) {
                            dto.setBankContents("");
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
            if(dto.getDate().equals(date) && !dto.getCategory02().equals("01")) {
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


    // InputOutputActivity > 데이터 가공해서 넘기기
    private void setIoMoneyLiveData() {
        List<String> codeList = new ArrayList<>();
        List<MoneyDTO> dtoList = new ArrayList<>();

        for(MoneyDTO dto : Objects.requireNonNull(moneyLiveData.getValue())) {
            if(!dto.getCategory02().equals("01")) { // 계좌이체 뺀 것들만 담기
                if(codeList.contains(dto.getSettingsCode())) {
                    // 들어 있으면 해당 표지션(값)에 금액만 더하기
                    int position = codeList.indexOf(dto.getSettingsCode());
                    String beforeMoney = dtoList.get(position).getMoney();
                    String afterMoney = String.valueOf(Integer.parseInt(beforeMoney) + Integer.parseInt(dto.getMoney()));
                    dtoList.get(position).setMoney(afterMoney);
                } else {
                    // 들어있지 않으면 새로 추가하기
                    dtoList.add(dto);
                    codeList.add(dto.getSettingsCode());
                }
            }
        }

        ioMoneyLiveData.postValue(dtoList);
    }


    // 계좌이체 내역 저장
    public void insertTransferMoneyInfo(int incomeBankSeq, int expandingBankSeq, String date, String money, String memo, String incomeBank, String expandingBank, int incomeSettingsSeq, int expandingSettingsSeq) {
        model.insertTransferMoneyInfo(incomeBankSeq, expandingBankSeq, date, money, memo, incomeBank, expandingBank, incomeSettingsSeq, expandingSettingsSeq);
    }


    // money info 저장하기
    public void insertMoneyInfo(int settingsSeq, int bankSeq, String in_sp, String date, String money, String memo, int isFinish) {
        model.insertMoneyInfo(settingsSeq, bankSeq, in_sp, date, money, memo, isFinish);
    }


    // money info 수정하기
    public void modifyMoneyInfo(int seq, int settingsSeq, int bankSeq, String in_sp, String inputDate, String money, String memo, String choiceDate) {
        model.modifyMoneyInfo(seq, settingsSeq, bankSeq, in_sp, inputDate, money, memo, choiceDate);
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
    public MutableLiveData<List<MoneyDTO>> getIoMoneyLiveData() {
        return ioMoneyLiveData;
    }
    public MutableLiveData<List<TransferMoneyDTO>> getTransferMoneyLiveData() {
        return transferMoneyLiveData;
    }

    // 화면 이동 시 값 체크하여 변화가 있을 경우 갱신을 해준다. > calendar/list
    // 카테고리에서 삭제 후 이동할 때 갱신을 도와준다.
    public boolean getIsChange() { return model.getIsChange(); }
    public void setIsChange(boolean isChange) { model.setIsChange(isChange); }
    public boolean getIsChangeCal() { return model.getIsChangeCal(); }
    public void setIsChangeCal(boolean isChangeCal) { model.setIsChangeCal(isChangeCal); }
}
