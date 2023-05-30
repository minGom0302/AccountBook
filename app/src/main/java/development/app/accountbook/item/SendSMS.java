package development.app.accountbook.item;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class SendSMS {
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 2323;
    private String codeNumber;

    public void SmsSend(Activity activity, String phone) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없다면
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            // 권한이 있다면
            codeNumber = getRandomNumber();

            SmsManager smsManager = SmsManager.getDefault();
            try {
                smsManager.sendTextMessage(phone, null, "본인인증 번호 요청 : " + codeNumber, null, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getCodeNumber() { return this.codeNumber; }

    public boolean checkCode(String codeNumber) {
        return this.codeNumber.equals(codeNumber);
    }

    private String getRandomNumber() {
        Random random = new Random();
        StringBuilder resultNum = new StringBuilder();

        for(int i=0; i<6; i++) {
            String ranNum = String.valueOf(random.nextInt(9));
            resultNum.append(ranNum);
        }

        return resultNum.toString();
    }
}
