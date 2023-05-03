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
    public void setSpendingTypeUse(boolean isUse) { sp_e.putBoolean("category02Use", isUse).commit(); }

    // Getter
    public int getUserSeq() { return sp.getInt("userSeq", 0); }
    public String getUserId() { return sp.getString("userId", ""); }
    public String getUserName() { return sp.getString("userName", ""); }
    public String getUserNickname() { return sp.getString("userNickname", ""); }
    public Boolean getSpendingTypeUse() { return sp.getBoolean("category02Use", true); }
}
