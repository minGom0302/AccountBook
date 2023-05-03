package com.example.accountbook.dto;

public class MoneyDTO {
    private int userSeq;
    private String settingsCode;
    private String category01;
    private String category02;
    private String settingsContents;
    private int settingsSeq;
    private String date;
    private String money;
    private String bankCode;
    private String bankContents;
    private String moneyMemo;
    private int intMoney;

    public MoneyDTO(String settingsCode, String category02, String date, String money, String settingsContents) {
        this.settingsCode = settingsCode;
        this.date = date;
        this.money = money;
        this.settingsContents = settingsContents;
        this.category02 = category02;
    }

    public MoneyDTO(String category01, int intMoney, String bankCode, String bankContents) {
        this.category01 = category01;
        this.intMoney = intMoney;
        this.bankCode = bankCode;
        this.bankContents = bankContents;
    }

    public int getUserSeq() {
        return userSeq;
    }

    public String getSettingsCode() {
        return settingsCode;
    }

    public String getCategory01() {
        return category01;
    }

    public String getCategory02() {
        return category02;
    }

    public String getSettingsContents() {
        return settingsContents;
    }

    public int getSettingsSeq() {
        return settingsSeq;
    }

    public String getDate() {
        return date;
    }

    public String getMoney() {
        return money;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getBankContents() {
        return bankContents;
    }

    public String getMoneyMemo() {
        return moneyMemo;
    }

    public int getIntMoney() {
        return intMoney;
    }

    public void setUserSeq(int userSeq) {
        this.userSeq = userSeq;
    }

    public void setSettingsCode(String settingsCode) {
        this.settingsCode = settingsCode;
    }

    public void setCategory01(String category01) {
        this.category01 = category01;
    }

    public void setCategory02(String category02) {
        this.category02 = category02;
    }

    public void setSettingsContents(String settingsContents) {
        this.settingsContents = settingsContents;
    }

    public void setSettingsSeq(int settingsSeq) {
        this.settingsSeq = settingsSeq;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public void setBankContents(String bankContents) {
        this.bankContents = bankContents;
    }

    public void setMoneyMemo(String moneyMemo) {
        this.moneyMemo = moneyMemo;
    }

    public void setIntMoney(int intMoney) {
        this.intMoney = intMoney;
    }
}