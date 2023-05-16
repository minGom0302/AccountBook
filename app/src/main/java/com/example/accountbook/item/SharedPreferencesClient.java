package com.example.accountbook.item;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesClient {
    private final SharedPreferences sp;
    private final SharedPreferences.Editor sp_e;

    // Constructor
    public SharedPreferencesClient(Activity activity) {
        sp = activity.getSharedPreferences("", Context.MODE_PRIVATE);
        sp_e = sp.edit();
    }

    // Setter
    public void setUserSeq(int seq) { sp_e.putInt("userSeq", seq).commit(); }
    public void setUserId(String userId) { sp_e.putString("userId", userId).commit(); }
    public void setUserName(String userName) { sp_e.putString("userName", userName).commit(); }
    public void setUserNickname(String userNickname) { sp_e.putString("userNickname", userNickname).commit(); }
    public void setAutoLogin(boolean isAutoLogin) { sp_e.putBoolean("isAutoLogin", isAutoLogin).commit(); }
    public void setSaveId(boolean isSaveId) { sp_e.putBoolean("isSaveId", isSaveId).commit(); }
    public void setSpendingTypeUse(boolean isUse) { sp_e.putBoolean("category02Use", isUse).commit(); }
    public void setIsChange(boolean isChange) { sp_e.putBoolean("isChange", isChange).commit(); }
    public void setIsChangeCa(boolean isChangeCa) { sp_e.putBoolean("isChangeCa", isChangeCa).commit(); }
    public void setIsChangeCal(boolean isChangeCal) { sp_e.putBoolean("isChangeCal", isChangeCal).commit(); }

    // Getter
    public int getUserSeq() { return sp.getInt("userSeq", 0); }
    public String getUserId() { return sp.getString("userId", ""); }
    public String getUserName() { return sp.getString("userName", ""); }
    public String getUserNickname() { return sp.getString("userNickname", ""); }
    public boolean getAutoLogin() { return sp.getBoolean("isAutoLogin", false); }
    public boolean getSaveId() { return sp.getBoolean("isSaveId", false); }
    public boolean getSpendingTypeUse() { return sp.getBoolean("category02Use", true); }
    public boolean getIsChange() { return sp.getBoolean("isChange", false); }
    public boolean getIsChangeCa() { return sp.getBoolean("isChangeCa", false); }
    public boolean getIsChangeCal() { return sp.getBoolean("isChangeCal", false); }
}
