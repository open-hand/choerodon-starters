package io.choerodon.liquibase.excel;

import io.choerodon.liquibase.addition.AdditionDataSource;
import io.choerodon.liquibase.exception.LiquibaseException;

import liquibase.exception.CustomChangeException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;

/**
 * Excel 数据加载类
 *
 * @author dongfan117@gmail.com
 */
public class ExcelDataLoader {

    List<TableData> tables = null;

    DbAdaptor dbAdaptor;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String filePath;

    private Map<String, Set<String>> updateExclusionMap = new HashMap<>();

    /**
     * A=1,Z=26,AA=27,AB=28
     *
     * @param str str
     * @return 结果
     */
    static int toColIndex(String str) {
        int value = 0;
        int pp = 1;
        for (int i = str.length() - 1; i >= 0; i--) {
            char cc = str.charAt(i);
            value += (cc - 'A' + 1) * pp;
            pp *= 26;
        }
        return value;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Map<String, Set<String>> getUpdateExclusionMap() {
        return updateExclusionMap;
    }

    public void setUpdateExclusionMap(Map<String, Set<String>> updateExclusionMap) {
        this.updateExclusionMap = updateExclusionMap;
    }

    /**
     * 处理数据
     */
    public void processData() {
        try {
            dbAdaptor.initConnection();
        } catch (SQLException e) {
            throw new LiquibaseException(e);
        }

        long begin = System.currentTimeMillis();

        try {
            processTableCopy();
            if (dbAdaptor.isOverride()) {
                processUpdate();
            }
            logger.info("SUCCESS");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new LiquibaseException(e);
        } finally {
            logger.info("data process finish, time: {} ms", (System.currentTimeMillis() - begin));
        }

    }

    private void processTableCopy() throws SQLException {
        List<TableData> tablesCopy = new ArrayList<>(tables);

        int round = 0;
        while (true) {
            round++;
            logger.info("---- begin round {} ----", round);
            long begin = System.currentTimeMillis();
            int roundProcessCount = 0;
            for (TableData tableData : tablesCopy) {
                roundProcessCount += processTable(tableData);
            }

            logger.info("---- round {} summary ----", round);
            roundSummary(tablesCopy);

            logger.info("---- total process {},time:{}ms ----", roundProcessCount,
                    (System.currentTimeMillis() - begin));
            if (tablesCopy.isEmpty()) {
                break;
            }
            if (roundProcessCount == 0 && dbAdaptor.weakInsert(tablesCopy) == 0) {
                throw new LiquibaseException(errorLog(tablesCopy) + " rows can not process.");
            }
        }
    }

    private void processUpdate() throws SQLException {
        logger.info("begin update exists datas...");
        int uc = 0;
        for (TableData tableData : tables) {
            boolean skipWholeTable = false;
            for (Map.Entry<String, Set<String>> entry : updateExclusionMap.entrySet()) {
                String tableName = entry.getKey();
                Set<String> columnSet = entry.getValue();
                //如果只有表名没有列集合，跳过整张表
                if ((columnSet == null || columnSet.isEmpty())) {
                    if (tableName.equalsIgnoreCase(tableData.getName())) {
                        updateExclusionMap.remove(tableName);
                        logger.info("skip update table : {}", tableData.getName());
                        skipWholeTable = true;
                        break;
                    }
                }
            }
            if (skipWholeTable) {
                continue;
            }
            Set<String> exclusionColumns = updateExclusionMap.get(tableData.getName().toLowerCase());
            Set<String> logInfo = new HashSet<>();
            for (TableData.TableRow tableRow : tableData.getTableRows()) {
                if (tableRow.isExistsFlag()) {
                    uc += dbAdaptor.doUpdate(tableRow, exclusionColumns, logInfo);
                }
            }
            //输出跳过列的log
            for (String log : logInfo) {
                logger.info(log);
            }
        }
        logger.info("update complete, update row:{} (include tl)", uc);
    }

    private int getMaxTableNameLength(List<TableData> list) {
        int max = 10;
        for (TableData tableData : list) {
            max = Math.max(max, tableData.getName().length());
        }
        return max;
    }

    private void roundSummary(List<TableData> tablesCopy) {
        int mtnl = getMaxTableNameLength(tablesCopy);
        for (int i = 0; i < tablesCopy.size(); i++) {
            String summary = String.format("%-" + mtnl
                            + "s   %s", tablesCopy.get(i).getName(),
                    tablesCopy.get(i).processSummary());
            logger.info(summary);
            if (tablesCopy.get(i).complete()) {
                tablesCopy.remove(i);
                i--;
            }
        }
    }

    private int errorLog(List<TableData> tablesCopy) {
        logger.error("**** can not process rows below ****");
        int count = 0;
        for (TableData tableData : tablesCopy) {
            logger.error("{} :", tableData.getName());
            for (TableData.TableRow tableRow : tableData.getTableRows()) {
                if (tableRow.isProcessFlag()) {
                    logger.error("    {}", tableRow);
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 处理表
     *
     * @param tableData tableData
     * @return 结果
     * @throws SQLException sql异常
     */
    public int processTable(TableData tableData) throws SQLException {
        int cc = 0;
        for (TableData.TableRow tableRow : tableData.getTableRows()) {
            cc += dbAdaptor.processTableRow(tableRow);
        }
        return cc;
    }

    void updateCellFormula(TableData.TableCellValue tableCellValue) {
        tableCellValue.getTableRow().getTable().getSheet().getWorkbook()
                .getCreationHelper().createFormulaEvaluator()
                .evaluateFormulaCell(tableCellValue.getCell());
    }

    /**
     * 更新单元格
     *
     * @param tableCellValue 值
     * @return 操作结果
     */
    public boolean tryUpdateCell(TableData.TableCellValue tableCellValue) {
        List<String> relatedCells = tableCellValue.getRelatedCell();
        int ready = 0;
        for (String cellNum : relatedCells) {
            TableData.TableCellValue relatedTableCellValue = findCell(cellNum, tableCellValue);
            if (relatedTableCellValue == null) {
                throw new LiquibaseException("invalid reference:" + cellNum);
            }
            if (relatedTableCellValue.isValuePresent()) {
                ready++;
            }
        }
        // 引用的所有 cell 值已就绪（一般只会引用 外键）
        if (ready == relatedCells.size()) {
            // 重新计算公式值
            updateCellFormula(tableCellValue);
            // 标记未已有值 状态
            tableCellValue.updateValue(tableCellValue.getCell().getRichStringCellValue().getString());
        } else {
            return false;
        }
        return true;
    }

    TableData.TableCellValue findCell(String cellNum, TableData.TableCellValue tableCellValue) {
        String sheetName = null;
        int ii = cellNum.indexOf('!');
        if (ii > 0) {
            sheetName = cellNum.substring(0, ii);
            cellNum = cellNum.substring(ii + 1);
        }
        int idx = 0;
        for (int i = 0; i < cellNum.length(); i++) {
            if (Character.isDigit(cellNum.charAt(i))) {
                idx = i;
                break;
            }
        }
        if (idx == 0 || idx >= cellNum.length()) {
            throw new LiquibaseException(cellNum + " is not a value CellNum.");
        }
        int col = toColIndex(cellNum.substring(0, idx));
        int row = Integer.parseInt(cellNum.substring(idx));
        for (TableData tableData : tables) {
            if ((sheetName == null && tableCellValue.getTableRow().getTable().getSheet() != tableData.getSheet())
                    || (sheetName != null && !sheetName.equals(tableData.getSheet().getSheetName()))) {
                continue;
            }
            int sl = tableData.getStartLine();
            int sc = tableData.getStartCol();
            if (sl < row && sl + tableData.getTableRows().size() >= row) {
                TableData.TableRow tableRow = tableData
                        .getTableRows().get(row - tableData.getStartLine() - 1);
                return tableRow.getTableCellValues().get(col - sc - 1);
            }
        }
        return null;
    }

    /**
     * 根据数据流和数据源处理将excel 中的数据初始化到数据库中
     *
     * @param inputStream 输入流
     * @param ad          数据源
     * @throws CustomChangeException 异常
     */
    public void execute(InputStream inputStream, AdditionDataSource ad) throws CustomChangeException {
        try {
            ExcelSeedDataReader dataReader = new ExcelSeedDataReader(inputStream);
            tables = dataReader.load();
            dbAdaptor = new DbAdaptor(this, ad);
            dbAdaptor.setDataSource(ad.getDataSource());
            processData();
            dbAdaptor.closeConnection(true);
        } catch (Exception e) {
            if (dbAdaptor != null) {
                dbAdaptor.closeConnection(false);
            }
            if (e.getCause() instanceof InvalidFormatException) {
                logger.warn("invalid input stream, maybe your excel is not save and close");
            }
            throw new CustomChangeException(e);
        }
    }
}
