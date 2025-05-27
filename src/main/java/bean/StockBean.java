package bean;

import org.apache.commons.lang3.StringUtils;

import consts.TableConst;
import utils.PinYinUtils;
import utils.StringUtilss;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

public class StockBean {

    private String code = "--";
    private String name = "--";
    private String alias = "--";
    private String now = "--";
    private String change; // 涨跌
    private String changePercent;
    private String time;
    private int order = 0;
    /**
     * 最高价
     */
    private String max;
    /**
     * 最低价
     */
    private String min;

    private String costPrise;//成本价
//    private String cost;//成本
    private String bonds;//持仓
    private String incomePercent;//收益率
    private String income;//收益

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public StockBean() {
    }

    // 配置code同时配置成本价和成本值
    public StockBean(StockBean stockBean) {

        copy(stockBean,this);

    }

    private static void copy(StockBean src,StockBean desc) {
        if (src != null) {
            Class<?> clazz = src.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(src);
                    field.set(desc, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("反射拷贝属性失败", e);
                }
            }
        }
    }

    public StockBean(String code, Map<String, StockBean> codeMap) {
        this.code = code;
        if (codeMap.containsKey(code)) {
            copy(codeMap.get(code),this);
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(String changePercent) {
        this.changePercent = changePercent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getCostPrise() {
        return costPrise;
    }

    public void setCostPrise(String costPrise) {
        this.costPrise = costPrise;
    }

    public String getBonds() {
        return bonds;
    }

    public void setBonds(String bonds) {
        this.bonds = bonds;
    }

    //    public String getCost() {
    //        return cost;
    //    }
    //
    //    public void setCost(String cost) {
    //        this.cost = cost;
    //    }

    public String getIncomePercent() {
        return incomePercent;
    }

    public void setIncomePercent(String incomePercent) {
        this.incomePercent = incomePercent;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        StockBean bean = (StockBean) o;
        return Objects.equals(code, bean.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    /**
     * 返回列名的VALUE 用作展示
     *
     * @param columnName   字段名, 中文
     * @param colorful 隐蔽模式
     * @return 对应列名的VALUE值 无法匹配返回""
     */
    public String getValueByColumnName(String columnName, boolean colorful) {
        switch (columnName) {
            case TableConst.CODE:
                return this.getCode();
            case TableConst.STOCK_NAME:
                String name = StringUtilss.isNotEmpty(this.getAlias()) ? this.getAlias() : this.getName();
                return colorful ? name : PinYinUtils.toPinYin(this.getName());
            case TableConst.PRICE:
                return this.getNow();
            case TableConst.UP_DOWN:
                String changeStr = "--";
                if (this.getChange() != null) {
                    changeStr = this.getChange().startsWith("-") ? this.getChange() : "+" + this.getChange();
                }
                return changeStr;
            case TableConst.UP_DOWN_RATIO:
                String changePercentStr = "--";
                if (this.getChangePercent() != null) {
                    changePercentStr = this.getChangePercent().startsWith("-") ? this.getChangePercent() : "+" + this.getChangePercent();
                }
                return changePercentStr + "%";
            case TableConst.HIGH:
                return this.getMax();
            case TableConst.LOW:
                return this.getMin();
            case "成本价":
                return this.getCostPrise();
            case "持仓":
                return this.getBonds();
            case "收益率":
                return this.getCostPrise() != null ? this.getIncomePercent() + "%" : this.getIncomePercent();
            case "收益":
                return this.getIncome();
            case "更新时间":
                String timeStr = "--";
                if (this.getTime() != null) {
                    timeStr = this.getTime().substring(8);
                }
                return timeStr;
            default:
                return "";

        }
    }
}
