package development.app.accountbook.model;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import development.app.accountbook.activity.LoginActivity;
import development.app.accountbook.item.RetrofitAPI;
import development.app.accountbook.item.RetrofitClient;
import development.app.accountbook.dto.UserInfoDTO;
import development.app.accountbook.item.SharedPreferencesClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserModel {
    private final RetrofitAPI api;
    private final MutableLiveData<UserInfoDTO> userInfo = new MutableLiveData<>();
    private final MutableLiveData<List<String>> userIdList = new MutableLiveData<>();
    private final SharedPreferencesClient spClient;
    private final Activity activity;

    public UserModel(Activity activity) {
        this.activity = activity;
        api = RetrofitClient.getRetrofit();
        spClient = new SharedPreferencesClient(activity);
    }

    public void login(String userId, String userPw, boolean isAutoLogin, boolean isSaveId) {
        api.getUserInfo(userId, userPw).enqueue(new Callback<UserInfoDTO>() {
            @Override
            public void onResponse(@NonNull Call<UserInfoDTO> call, @NonNull Response<UserInfoDTO> response) {
                if(response.isSuccessful() && response.body() != null) {
                    UserInfoDTO dto = response.body();
                    userInfo.postValue(dto);

                    spClient.setUserSeq(dto.getSeq());
                    spClient.setUserId(dto.getUserId());
                    spClient.setUserName(dto.getUserName());
                    spClient.setUserNickname(dto.getUserNickname());
                    spClient.setAutoLogin(isAutoLogin);
                    spClient.setSaveId(isSaveId);
                } else {
                    userInfo.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserInfoDTO> call, @NonNull Throwable t) {
                userInfo.postValue(null);
                Toast.makeText(activity, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void nicknameChange(String userNickname) {
        api.nicknameChange(getUserSeq(), userNickname).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                Toast.makeText(activity, "닉네임 변경이 완료됐습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Toast.makeText(activity, "서버에 저장되지 못했습니다.\n잠시 후 다시 변경해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void pwChange(String newPw) {
        api.pwChange(getUserSeq(), newPw).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                Toast.makeText(activity, "비밀번호 변경이 완료되었습니다.\n다시 로그인해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                activity.finishAffinity();
                activity.startActivity(new Intent(activity, LoginActivity.class));
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Toast.makeText(activity, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void idCheck(String id) {
        api.idCheck(id).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful() && response.body() != null) {
                    UserInfoDTO dto = new UserInfoDTO();
                    dto.setUserId(response.body());
                    userInfo.postValue(dto);
                } else {
                    // 아이디 중복 아닐 때
                    userInfo.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(activity, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void idCheck2(String id, String answer) {
        api.checkId2(id, answer).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                if(response.isSuccessful() && response.body() != null) {
                    UserInfoDTO dto = new UserInfoDTO();
                    dto.setSeq(response.body());
                    spClient.setUserSeq(response.body());
                    userInfo.postValue(dto);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                userInfo.postValue(null);
                Toast.makeText(activity, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void idFind(String userName, String userBirth, String userAnswer) {
        api.idFind(userName, userBirth, userAnswer).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    userIdList.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                userIdList.postValue(null);
                Toast.makeText(activity, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void signUp(UserInfoDTO userInfoDTO) {
        api.signUp(userInfoDTO.getUserId(), userInfoDTO.getUserPw(), userInfoDTO.getUserName(), userInfoDTO.getUserNickname(), userInfoDTO.getUserBirth(), userInfoDTO.getUserAgree01(), userInfoDTO.getUserAnswer()).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                Toast.makeText(activity, "회원가입이 완료되었습니다.\n로그인해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                activity.finish();
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Toast.makeText(activity, "잠시 후 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Getter
    public MutableLiveData<UserInfoDTO> getUserInfoLiveData() {
        return userInfo;
    }
    public MutableLiveData<List<String>> getUserIdList() {
        return userIdList;
    }
    public int getUserSeq() { return spClient.getUserSeq(); }
    public String getUserId() { return spClient.getUserId(); }
    public String getUserName() { return spClient.getUserName(); }
    public String getUserNickname() { return spClient.getUserNickname(); }
    public boolean getAutoLogin() { return spClient.getAutoLogin(); }
    public boolean getSaveId() { return spClient.getSaveId(); }
    // Setter
    public void setUserNickname(String userNickname) {
        spClient.setUserNickname(userNickname);
        nicknameChange(userNickname);
    }
    public void setAutoLogin(boolean isAutoLogin) {
        spClient.setAutoLogin(isAutoLogin);
    }
}
