package utils;

import java.util.HashMap;

import consts.TableConst;

import static consts.TableConst.CODE;
import static consts.TableConst.HIGH;
import static consts.TableConst.LOW;
import static consts.TableConst.PRICE;
import static consts.TableConst.STOCK_NAME;
import static consts.TableConst.UP_DOWN;
import static consts.TableConst.UP_DOWN_RATIO;

/**
 * @Created by DAIE
 * @Date 2021/3/8 20:26
 * @Description leek面板TABLE工具类
 */
public class WindowUtils {
    //基金表头
    public static final String FUND_TABLE_HEADER_KEY = "fund_table_header_key2"; //移动表头时存储的key
    public static final String FUND_TABLE_HEADER_VALUE = "编码,基金名称,估算涨跌,当日净值,估算净值,持仓成本价,持有份额,收益率,收益,更新时间";
    //股票表头
    public static final String STOCK_TABLE_HEADER_KEY = "stock_table_header_key2"; //移动表头时存储的key
    public static final String STOCK_TABLE_HEADER_VALUE = String.join(",", STOCK_NAME, PRICE, UP_DOWN_RATIO, UP_DOWN, HIGH,
            LOW, CODE);
    // public static final String STOCK_TABLE_HEADER_VALUE = "编码,股票名称,涨跌,涨跌幅,最高价,最低价,当前价,成本价,持仓,收益率,收益,更新时间";
    //货币表头
    public static final String COIN_TABLE_HEADER_KEY = "coin_table_header_key2"; //移动表头时存储的key
    public static final String COIN_TABLE_HEADER_VALUE = "编码,当前价,涨跌,涨跌幅,最高价,最低价,更新时间";

    private static HashMap<String,String> remapPinYinMap = new HashMap<>();

    static {
        remapPinYinMap.put(PinYinUtils.toPinYin(TableConst.CODE), TableConst.CODE);
        remapPinYinMap.put(PinYinUtils.toPinYin("基金名称"), "基金名称");
        remapPinYinMap.put(PinYinUtils.toPinYin("估算净值"), "估算净值");
        remapPinYinMap.put(PinYinUtils.toPinYin("估算涨跌"), "估算涨跌");
        remapPinYinMap.put(PinYinUtils.toPinYin("更新时间"), "更新时间");
        remapPinYinMap.put(PinYinUtils.toPinYin("当日净值"), "当日净值");
        remapPinYinMap.put(PinYinUtils.toPinYin(STOCK_NAME), STOCK_NAME);
        remapPinYinMap.put(PinYinUtils.toPinYin(TableConst.PRICE), TableConst.PRICE);
        remapPinYinMap.put(PinYinUtils.toPinYin(UP_DOWN), UP_DOWN);
        remapPinYinMap.put(PinYinUtils.toPinYin(UP_DOWN_RATIO), UP_DOWN_RATIO);
        remapPinYinMap.put(PinYinUtils.toPinYin(TableConst.CODE), TableConst.CODE);
        remapPinYinMap.put(PinYinUtils.toPinYin(TableConst.LOW), TableConst.LOW);
        remapPinYinMap.put(PinYinUtils.toPinYin("名称"), "名称");

        remapPinYinMap.put(PinYinUtils.toPinYin("成本价"), "成本价");
        remapPinYinMap.put(PinYinUtils.toPinYin("持仓"), "持仓");
        remapPinYinMap.put(PinYinUtils.toPinYin("收益率"), "收益率");
        remapPinYinMap.put(PinYinUtils.toPinYin("收益"), "收益");

        remapPinYinMap.put(PinYinUtils.toPinYin("持仓成本价"), "持仓成本价");
        remapPinYinMap.put(PinYinUtils.toPinYin("持有份额"), "持有份额");
    }


    /**
     * 通过列名 获取该TABLE的列的数组下标
     *
     * @param columnNames 列名数组
     * @param columnName  要获取的列名
     * @return 返回给出列名的数组下标 匹配失败返回-1
     */
    public static int getColumnIndexByName(String[] columnNames, String columnName) {
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equals(columnName)) {
                return i;
            }
        }
        //考虑拼音编码

        return -1;
    }

    public static String remapPinYin(String pinyin) {
        return remapPinYinMap.getOrDefault(pinyin, pinyin);
    }


}
