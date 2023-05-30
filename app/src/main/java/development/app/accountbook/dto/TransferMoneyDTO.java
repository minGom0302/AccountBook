package development.app.accountbook.dto;

public class TransferMoneyDTO {
    private final int seq;
    private final int userSeq;
    private final String incomeBank;
    private final String expandingBank;
    private final String date;
    private final String money;
    private final String memo;

    public TransferMoneyDTO(int seq, int userSeq, String date, String money, String memo, String incomeBank, String expandingBank) {
        this.seq = seq;
        this.userSeq = userSeq;
        this.date = date;
        this.money = money;
        this.memo = memo;
        this.incomeBank = incomeBank;
        this.expandingBank = expandingBank;
    }

    public int getSeq() {
        return seq;
    }

    public int getUserSeq() {
        return userSeq;
    }

    public String getDate() {
        return date;
    }

    public String getMoney() {
        return money;
    }

    public String getMemo() {
        return memo;
    }

    public String getIncomeBank() {
        return incomeBank;
    }

    public String getExpandingBank() {
        return expandingBank;
    }
}