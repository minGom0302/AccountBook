package development.app.accountbook.dto;

public class CategoryDTO {
    private int seq;
    private int userSeq;
    private String code;
    private String category01;
    private String category02;
    private String contents;
    private int endDay;
    private String strEndDay;
    private int orderSeq;

    public CategoryDTO(int userSeq, String code, String category01, String category02, String contents, int endDay, int orderSeq) {
        this.userSeq = userSeq;
        this.code = code;
        this.category01 = category01;
        this.category02 = category02;
        this.contents = contents;
        this.endDay = endDay;
        this.orderSeq = orderSeq;
    }

    public int getSeq() {
        return seq;
    }
    public void setSeq(int seq) {
        this.seq = seq;
    }
    public int getUserSeq() {
        return userSeq;
    }
    public void setUserSeq(int userSeq) {
        this.userSeq = userSeq;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getCategory01() {
        return category01;
    }
    public void setCategory01(String category01) {
        this.category01 = category01;
    }
    public String getCategory02() {
        return category02;
    }
    public void setCategory02(String category02) {
        this.category02 = category02;
    }
    public String getContents() {
        return contents;
    }
    public void setContents(String contents) {
        this.contents = contents;
    }
    public int getEndDay() {
        return endDay;
    }
    public void setEndDay(int endDay) {
        this.endDay = endDay;
    }
    public String getStrEndDay() {
        return strEndDay;
    }
    public void setStrEndDay(String strEndDay) {
        this.strEndDay = strEndDay;
    }
    public int getOrderSeq() {
        return orderSeq;
    }
    public void setOrderSeq(int orderSeq) {
        this.orderSeq = orderSeq;
    }
}
