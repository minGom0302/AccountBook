package development.app.accountbook.viewmodel;


import android.app.Activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import development.app.accountbook.model.UserModel;
import development.app.accountbook.dto.UserInfoDTO;

import java.util.List;

public class UserViewModel extends ViewModel {
    private UserModel userModel;
    private MutableLiveData<UserInfoDTO> userInfo;
    private MutableLiveData<List<String>> userIdList;

    public void setUserViewModel(Activity activity) {
        userModel = new UserModel(activity);
        userInfo = userModel.getUserInfoLiveData();
    }

    public void setUserViewModelForId(Activity activity) {
        userModel = new UserModel(activity);
        userIdList = userModel.getUserIdList();
    }

    public void login(String userId, String userPw, boolean isAutoLogin, boolean isSaveId) {
        userModel.login(userId, userPw, isAutoLogin, isSaveId);
    }
    // 로그인한 후 안에서 비밀번호 변경할 때 사용하는 메서드
    public void pwChange(String newPw) {
        userModel.pwChange(newPw);
    }
    // 비밀번호 찾기에서 변경할 때 사용하는 메서드
    public void pwChange2(String userId, String newPw) {
        userModel.pwChange2(userId, newPw);
    }
    // 아이디 찾기
    public void idFind(String userName, String userPhone) {
        userModel.idFind(userName, userPhone);
    }

    // sign_up
    public void idCheck(String id) {
        userModel.idCheck(id);
    }
    public void signUp(UserInfoDTO userInfoDTO) {
        userModel.signUp(userInfoDTO);
    }


    // Getter
    public MutableLiveData<UserInfoDTO> getUserInfo() {
        return userInfo;
    }
    public MutableLiveData<List<String>> getUserIdList() {
        return userIdList;
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
