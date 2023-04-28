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

    public void login(String userId) {
        userModel.login(userId);
    }

    public void pwChange(String newPw) {
        userModel.pwChange(newPw);
    }


    // Getter
    public MutableLiveData<UserInfoDTO> getUserInfo() {
        return userInfo;
    }
    public int getUserSeq() { return userModel.getUserSeq(); }
    public String getUserName() { return userModel.getUserName(); }
    public String getUserNickname() { return userModel.getUserNickname(); }
    // Setter
    public void setUserNickname(String userNickname) { userModel.setUserNickname(userNickname); }
}
