package bean;

import java.text.DecimalFormat;

import consts.TableConst;

public class CoinBean {
    static DecimalFormat decimalFormat = new DecimalFormat("#.00");

    private String symbol;
    private double regularMarketPrice;
    private double regularMarketDayHigh;
    private double regularMarketDayLow;
    private double regularMarketChange;
    private double regularMarketChangePercent;
    private long regularMarketTime;


    public CoinBean(String code) {
        this.symbol = code;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getRegularMarketPrice() {
        return regularMarketPrice;
    }

    public void setRegularMarketPrice(double regularMarketPrice) {
        this.regularMarketPrice = regularMarketPrice;
    }

    public double getRegularMarketDayHigh() {
        return regularMarketDayHigh;
    }

    public void setRegularMarketDayHigh(double regularMarketDayHigh) {
        this.regularMarketDayHigh = regularMarketDayHigh;
    }

    public double getRegularMarketDayLow() {
        return regularMarketDayLow;
    }

    public void setRegularMarketDayLow(double regularMarketDayLow) {
        this.regularMarketDayLow = regularMarketDayLow;
    }

    public double getRegularMarketChange() {
        return regularMarketChange;
    }

    public void setRegularMarketChange(double regularMarketChange) {
        this.regularMarketChange = regularMarketChange;
    }

    public double getRegularMarketChangePercent() {
        return regularMarketChangePercent;
    }

    public void setRegularMarketChangePercent(double regularMarketChangePercent) {
        this.regularMarketChangePercent = regularMarketChangePercent;
    }

    public long getTimeStamp() {
        return regularMarketTime;
    }

    public void setTimeStamp(long timeStamp) {
        this.regularMarketTime = timeStamp;
    }

    /**
     * 返回列名的VALUE 用作展示
     * @param colums 字段名
     * @param colorful 隐蔽模式
     * @return 对应列名的VALUE值 无法匹配返回""
     */
    public String getValueByColumn(String colums, boolean colorful) {
        switch (colums) {
            case TableConst.CODE:
                return this.getSymbol();
            case TableConst.UP_DOWN:
                return String.valueOf(this.getRegularMarketChange());
            case TableConst.UP_DOWN_RATIO:
                return decimalFormat.format(this.getRegularMarketChangePercent())+"%";
            case TableConst.HIGH:
                return String.valueOf(this.getRegularMarketDayHigh());
            case TableConst.LOW:
                return String.valueOf(this.getRegularMarketDayLow());
            case TableConst.PRICE:
                return String.valueOf(this.getRegularMarketPrice());
            case "更新时间":
                String timeStr = "--";
                if (this.getTimeStamp()>0){
                    timeStr = String.valueOf(this.getTimeStamp());
                }
                return timeStr;

            default:
                return "";

        }
    }
}
