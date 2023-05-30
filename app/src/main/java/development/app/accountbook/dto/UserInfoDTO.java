package development.app.accountbook.dto;

public class UserInfoDTO {
    private int seq;
    private String userId;
    private String userPw;
    private String userName;
    private String userNickname;
    private String userPhone;
    private String userAgree01;
    private String userAgree02;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAgree01() {
        return userAgree01;
    }

    public void setUserAgree01(String userAgree01) {
        this.userAgree01 = userAgree01;
    }

    public String getUserAgree02() {
        return userAgree02;
    }

    public void setUserAgree02(String userAgree02) {
        this.userAgree02 = userAgree02;
    }
}
