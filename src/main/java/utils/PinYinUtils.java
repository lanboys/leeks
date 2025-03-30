package utils;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.HashMap;

import consts.TableConst;

public class PinYinUtils {

    private static HashMap<String, String> pinYinMap = new HashMap<>();

    static {
        pinYinMap.put(TableConst.STOCK_NAME, "stock");
    }

    public static String toPinYin(String input) {
        if (pinYinMap.get(input) != null) {
            return pinYinMap.get(input);
        }
        return Pinyin.toPinyin(input, "_").toLowerCase();
    }

    public static String[] toPinYin(String[] inputs) {
        if (null == inputs) {
            return null;
        }
        String[] result = new String[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            result[i] = toPinYin(inputs[i]);
        }
        return result;
    }
}
