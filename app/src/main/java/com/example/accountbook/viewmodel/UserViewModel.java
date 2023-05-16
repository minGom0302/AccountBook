package com.example.accountbook.viewmodel;


import android.app.Activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.accountbook.dto.UserInfoDTO;
import com.example.accountbook.model.UserModel;

public class UserViewModel extends ViewModel {
    private UserModel userModel;
    private MutableLiveData<UserInfoDTO> userInfo;

    public void setUserViewModel(Activity activity) {
        userModel = new UserModel(activity);
        userInfo = userModel.getUserInfoLiveData();
    }

    public void login(String userId, String userPw, boolean isAutoLogin, boolean isSaveId) {
        userModel.login(userId, userPw, isAutoLogin, isSaveId);
    }

    public void pwChange(String newPw) {
        userModel.pwChange(newPw);
    }


    // Getter
    public MutableLiveData<UserInfoDTO> getUserInfo() {
        return userInfo;
    }
    public int getUserSeq() { return userModel.getUserSeq(); }
    public String getUserId() { return userModel.getUserId(); }
    public String getUserName() { return userModel.getUserName(); }
    public String getUserNickname() { return userModel.getUserNickname(); }
    public boolean getAutoLogin() { return userModel.getAutoLogin(); }
    public boolean getSaveId() { return userModel.getSaveId(); }
    // Setter
    public void setUserNickname(String userNickname) { userModel.setUserNickname(userNickname); }
    public void setAutoLogin(boolean isAutoLogin) { userModel.setAutoLogin(isAutoLogin); }
}
