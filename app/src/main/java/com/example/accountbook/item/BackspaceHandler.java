package com.example.accountbook.item;

import android.app.Activity;
import android.widget.Toast;

public class BackspaceHandler {
    private long backspacePressedTime = 0;
    private final Activity activity;
    private Toast toast;

    // 뒤로가기 누른 화면(Activity)를 받아 저장
    public BackspaceHandler(Activity activity) {
        this.activity = activity;
    }

    // 보여줄 Toast msg, 단 선언 시 내용을 작성할 수 있음
    private void showToast(String msg) {
        toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    // 뒤로가기 누르면서 msg를 넘겼을 때
    public void onBackPressed(String msg) {
        if (System.currentTimeMillis() > backspacePressedTime + 3000) {
            backspacePressedTime = System.currentTimeMillis();
            showToast(msg);
            return;
        }

        if (System.currentTimeMillis() <= backspacePressedTime + 3000) {
            activity.finish();
            toast.cancel();
        }
    }
}
