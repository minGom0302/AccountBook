package development.app.accountbook.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import development.app.accountbook.R;

public class Popup_Agree extends Activity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agree);

        findViewById(R.id.agree_closeBtn).setOnClickListener(v -> finish());
    }
}