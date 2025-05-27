package handler;

import bean.StockBean;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ui.JBColor;
import com.intellij.ui.table.JBTable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import consts.TableConst;
import utils.NumberUtil;
import utils.PinYinUtils;
import utils.StringUtilss;
import utils.WindowUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Stream;

import static consts.TableConst.HIGH;
import static consts.TableConst.LOW;
import static consts.TableConst.PRICE;
import static consts.TableConst.UP_DOWN;
import static consts.TableConst.UP_DOWN_RATIO;

public abstract class StockRefreshHandler extends DefaultTableModel {

    // 中文列名
    private static String[] columnNames;
    /**
     * 存放【编码】的位置，更新数据时用到
     */
    public int codeColumnIndex = -1;

    private JTable table;
    private boolean colorful = true;

    static {
        PropertiesComponent instance = PropertiesComponent.getInstance();
        String tableHeaderValue = instance.getValue(WindowUtils.STOCK_TABLE_HEADER_KEY);
        if (StringUtilss.isBlank(tableHeaderValue)) {
            instance.setValue(WindowUtils.STOCK_TABLE_HEADER_KEY, WindowUtils.STOCK_TABLE_HEADER_VALUE);
            tableHeaderValue = WindowUtils.STOCK_TABLE_HEADER_VALUE;
        }

        columnNames = tableHeaderValue.split(",");

        // 这一步感觉是多此一举
        // String[] configStr = tableHeaderValue.split(",");
        // columnNames = new String[configStr.length];
        // for (int i = 0; i < configStr.length; i++) {
        //     columnNames[i] = WindowUtils.remapPinYin(configStr[i]);
        //     System.out.println("列名：" + configStr[i] + " --- " + columnNames[i]);
        // }
    }

    {
        for (int i = 0; i < columnNames.length; i++) {
            if (TableConst.CODE.equals(columnNames[i])) {
                codeColumnIndex = i;
            }
        }

        if (codeColumnIndex == -1) {
            PropertiesComponent instance = PropertiesComponent.getInstance();
            instance.setValue(WindowUtils.STOCK_TABLE_HEADER_KEY, WindowUtils.STOCK_TABLE_HEADER_VALUE);
            throw new RuntimeException("code index is not found");
        }
    }

    public StockRefreshHandler(JTable table) {
        this.table = table;
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // Fix tree row height
        FontMetrics metrics = table.getFontMetrics(table.getFont());
        table.setRowHeight(Math.max(table.getRowHeight(), metrics.getHeight()));
        table.setModel(this);
        refreshColorful(!colorful);
    }

    public void refreshColorful(boolean colorful) {
        if (this.colorful == colorful) {
            return;
        }
        this.colorful = colorful;
        // 刷新表头
        if (colorful) {
            // 彩色
            setColumnIdentifiers(columnNames);
        } else {
            // 超隐蔽模式
            setColumnIdentifiers(PinYinUtils.toPinYin(columnNames));
        }
        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(this);
        Comparator<Object> doubleComparator = (o1, o2) -> {
            Double v1 = NumberUtil.toDouble(StringUtilss.remove((String) o1, "%"));
            Double v2 = NumberUtil.toDouble(StringUtilss.remove((String) o2, "%"));
            return v1.compareTo(v2);
        };
        // 给这些列设置比较器
        Stream.of(UP_DOWN_RATIO, UP_DOWN, HIGH, LOW, PRICE)
                .map(name -> WindowUtils.getColumnIndexByName(columnNames, name))
                .filter(index -> index >= 0)
                .forEach(index -> rowSorter.setComparator(index, doubleComparator));

        table.setRowSorter(rowSorter);
        columnColors(colorful);
    }

    /**
     * 从网络更新数据
     */
    public abstract void handle(List<StockBean> code);

    /**
     * 设置表格条纹（斑马线）<br>
     *
     * @param striped true设置条纹
     * @throws RuntimeException 如果table不是{@link JBTable}类型，请自行实现setStriped
     */
    public void setStriped(boolean striped) {
        if (table instanceof JBTable) {
            ((JBTable) table).setStriped(striped);
        } else {
            throw new RuntimeException("table不是JBTable类型，请自行实现setStriped");
        }
    }

    public void setupTable(List<StockBean> code) {
        for (StockBean s : code) {
            updateData(new StockBean(s));
        }
    }

    /**
     * 停止从网络更新数据
     */
    public abstract void stopHandle();

    private void columnColors(boolean colorful) {
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                double temp = NumberUtil.toDouble(StringUtilss.remove(Objects.toString(value), "%"));
                if (temp > 0) {
                    if (colorful) {
                        setForeground(JBColor.RED);
                    } else {
                        setForeground(JBColor.DARK_GRAY);
                    }
                } else if (temp < 0) {
                    if (colorful) {
                        setForeground(JBColor.GREEN);
                    } else {
                        setForeground(JBColor.GRAY);
                    }
                } else {
                    Color orgin = getForeground();
                    setForeground(orgin);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        int columnIndex1 = WindowUtils.getColumnIndexByName(columnNames, TableConst.UP_DOWN);
        int columnIndex2 = WindowUtils.getColumnIndexByName(columnNames, TableConst.UP_DOWN_RATIO);

        int columnIndex3 = WindowUtils.getColumnIndexByName(columnNames, "收益率");
        int columnIndex4 = WindowUtils.getColumnIndexByName(columnNames, "收益");

        table.getColumn(getColumnName(columnIndex1)).setCellRenderer(cellRenderer);
        table.getColumn(getColumnName(columnIndex2)).setCellRenderer(cellRenderer);

        if (columnIndex3 > -1) {
            table.getColumn(getColumnName(columnIndex3)).setCellRenderer(cellRenderer);
        }

        if (columnIndex4 > -1) {
            table.getColumn(getColumnName(columnIndex4)).setCellRenderer(cellRenderer);
        }
    }

    protected void updateData(StockBean bean) {
        if (bean.getCode() == null) {
            return;
        }
        Vector<Object> convertData = convertData(bean);
        if (convertData == null) {
            return;
        }
        // 根据code获取在第几行
        int index = findRowIndex(codeColumnIndex, bean.getCode());
        if (index >= 0) {
            // 更新行
            updateRow(index, convertData);
        } else {
            addRow(convertData);
        }
    }

    /**
     * 参考源码{@link DefaultTableModel#setValueAt}，此为直接更新行，提高点效率
     */
    protected void updateRow(int rowIndex, Vector<Object> rowData) {
        dataVector.set(rowIndex, rowData);
        // 通知listeners刷新ui
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    /**
     * 参考源码{@link DefaultTableModel#removeRow(int)}，此为直接清除全部行，提高点效率
     */
    public void clearRow() {
        int size = dataVector.size();
        if (0 < size) {
            dataVector.clear();
            // 通知listeners刷新ui
            fireTableRowsDeleted(0, size - 1);
        }
    }

    /**
     * 查找列项中的valueName所在的行
     *
     * @param columnIndex 列号
     * @param value       值
     * @return 如果不存在返回-1
     */
    protected int findRowIndex(int columnIndex, String value) {
        int rowCount = getRowCount();
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            Object valueAt = getValueAt(rowIndex, columnIndex);
            if (StringUtilss.equalsIgnoreCase(value, valueAt.toString())) {
                return rowIndex;
            }
        }
        return -1;
    }

    private Vector<Object> convertData(StockBean stockBean) {
        if (stockBean == null) {
            return null;
        }
        // 与columnNames中的元素保持一致
        Vector<Object> v = new Vector<Object>(columnNames.length);
        for (int i = 0; i < columnNames.length; i++) {
            v.addElement(stockBean.getValueByColumnName(columnNames[i], colorful));
        }
        return v;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
