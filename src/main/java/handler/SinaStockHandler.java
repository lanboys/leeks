package handler;

import bean.StockBean;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import utils.HttpClientPool;
import utils.LogUtil;
import utils.StringUtilss;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SinaStockHandler extends StockRefreshHandler {
    private final String URL = "http://hq.sinajs.cn/list=";
    private final Pattern DEFAULT_STOCK_PATTERN = Pattern.compile("var hq_str_(\\w+?)=\"(.*?)\";");
    private final JLabel refreshTimeLabel;

    public SinaStockHandler(JTable table, JLabel label) {
        super(table);
        this.refreshTimeLabel = label;
    }

    @Override
    public void handle(List<StockBean> code) {
        if (code.isEmpty()) {
            return;
        }

        pollStock(code);
    }

    private void pollStock(List<StockBean> code) {
        //股票编码，英文分号分隔（成本价和成本接在编码后用逗号分隔）
        List<String> codeList = new ArrayList<>();
        Map<String, StockBean> codeMap = new HashMap<>();
        for (StockBean str : code) {
            codeList.add(str.getCode());
            codeMap.put(str.getCode(), str );
        }

        String params = Joiner.on(",").join(codeList);
        try {
            String res = HttpClientPool.getHttpClient().get(URL + params);
//            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS"));
//            System.out.printf("%s,%s%n", time, res);
            handleResponse(res, codeMap);
        } catch (Exception e) {
            LogUtil.info(e.getMessage());
        }
    }

    public void handleResponse(String response, Map<String, StockBean> codeMap) {
        List<String> refreshTimeList = new ArrayList<>();
        for (String line : response.split("\n")) {
            Matcher matcher = DEFAULT_STOCK_PATTERN.matcher(line);
            if (!matcher.matches()) {
                continue;
            }
            String code = matcher.group(1);
            String[] split = matcher.group(2).split(",");
            if (split.length < 32) {
                continue;
            }
            StockBean bean = new StockBean(code, codeMap);
            bean.setName(split[0]);
            BigDecimal now = new BigDecimal(split[3]);
            BigDecimal yesterday = new BigDecimal(split[2]);
            BigDecimal diff = now.add(yesterday.negate());

            bean.setNow(now.toString());
            bean.setChange(diff.toString());
            BigDecimal percent = diff.divide(yesterday, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.TEN)
                    .multiply(BigDecimal.TEN)
                    .setScale(2, RoundingMode.HALF_UP);
            bean.setChangePercent(percent.toString());
            bean.setTime(Strings.repeat("0", 8) + split[31]);
            bean.setMax(split[4]);
            bean.setMin(split[5]);

            String costPriceStr = bean.getCostPrise();
            if (StringUtilss.isNotEmpty(costPriceStr)) {
                BigDecimal costPriceDec = new BigDecimal(costPriceStr);
                BigDecimal incomeDiff = now.add(costPriceDec.negate());
                BigDecimal incomePercentDec = incomeDiff.divide(costPriceDec, 5, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.TEN)
                        .multiply(BigDecimal.TEN)
                        .setScale(3, RoundingMode.HALF_UP);
                bean.setIncomePercent(incomePercentDec.toString());

                String bondStr = bean.getBonds();
                if (StringUtilss.isNotEmpty(bondStr)) {
                    BigDecimal bondDec = new BigDecimal(bondStr);
                    BigDecimal incomeDec = incomeDiff.multiply(bondDec)
                            .setScale(2, RoundingMode.HALF_UP);
                    bean.setIncome(incomeDec.toString());
                }
            }

            updateData(bean);
            refreshTimeList.add(split[31]);
        }

        String text = refreshTimeList.stream().sorted().findFirst().orElse("");
        SwingUtilities.invokeLater(() -> refreshTimeLabel.setText(text));
    }

    @Override
    public void stopHandle() {
        LogUtil.info("leeks stock 自动刷新关闭!");
    }
}
