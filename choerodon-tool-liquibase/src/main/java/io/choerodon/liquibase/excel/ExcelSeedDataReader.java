package io.choerodon.liquibase.excel;

import io.choerodon.core.exception.CommonException;
import liquibase.util.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel 子数据读取器
 *
 * @author dongfan117@gmail.com
 */
public class ExcelSeedDataReader {
    private InputStream inputStream;
    private Workbook workBook;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public ExcelSeedDataReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    private static final int SKIP_LINE = 6;
    private static final int SKIP_COL = 3;

    /**
     * 加载xlsx数据。
     *
     * @return 数据对象
     */
    public List<TableData> load() {
        try {
            workBook = WorkbookFactory.create(inputStream);
            List<TableData> tablesAll = new ArrayList<>();
            for (int i = 1; i < workBook.getNumberOfSheets(); i++) {
                tablesAll.addAll(getSheetData(i));
            }
            return tablesAll;
        } catch (Exception e) {
            throw new CommonException(e);
        }
    }

    private String getCellValue(Cell cell) {
        return cell == null ? "" : getCellValueByType(cell);
    }

    private String getCellValueByType(Cell cell) {
        String cellValue = "";
        DataFormatter formatter = new DataFormatter();
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = formatter.formatCellValue(cell);
                } else {
                    if (cell instanceof XSSFCell) {
                        cellValue = ((XSSFCell) cell).getRawValue();
                        break;
                    }
                    double value = cell.getNumericCellValue();
                    int intValue = (int) value;
                    if (value == intValue) {
                        cellValue = String.valueOf(intValue);
                    } else {
                        cellValue = String.valueOf(value);
                    }
                }
                break;
            case Cell.CELL_TYPE_STRING:
                cellValue = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK:
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR:
                cellValue = "";
                break;
            default:
                cellValue = cell.toString().trim();
                break;
        }
        return cellValue.trim();
    }

    private List<TableData> getSheetData(int sheetIdx) {

        Sheet sheet = workBook.getSheetAt(sheetIdx);

        List<TableData> tables = new ArrayList<>();
        int numOfRows = sheet.getLastRowNum() + 1;
        TableData currentTable = null;
        for (int rn = SKIP_LINE; rn < numOfRows; rn++) {
            Row row = sheet.getRow(rn);
            if (row == null) {
                if (addCurrentTable(tables, currentTable)) {
                    currentTable = null;
                }

                continue;
            }

            if (row.getLastCellNum() < SKIP_COL) {
                continue;
            }

            if (StringUtils.isNotEmpty(getCellValue(row.getCell(SKIP_COL)))) {
                addCurrentTable(tables, currentTable);
                currentTable = getCurrentTable(sheet, row);

                continue;
            } else {
                // data
                if (isAllEmpty(row, SKIP_COL + 1)) {
                    continue;
                }
                currentTable = getTableData(currentTable, row);
            }
        }
        addCurrentTable(tables, currentTable);
        return tables;
    }

    private boolean addCurrentTable(List<TableData> tables, TableData currentTable) {
        if (currentTable != null) {
            currentTable.makeReady();
            tables.add(currentTable);
            return true;
        }
        return false;
    }

    private TableData getCurrentTable(Sheet sheet, Row row) {
        TableData currentTable;
        int cellNum = row.getLastCellNum();
        Cell tableNameCell = row.getCell(SKIP_COL);
        String tableName = getCellValue(tableNameCell);

        currentTable = new TableData();
        currentTable.setSheet(sheet);
        currentTable.setStartLine(row.getRowNum() + 1);
        if (tableNameCell != null) {
            currentTable.setStartCol(tableNameCell.getColumnIndex() + 1);
        }
        currentTable.setName(tableName);

        logger.info("found table:{} ,sheet:{}, begin row:{}",
                tableName, sheet.getSheetName(),
                currentTable.getStartLine());

        // column
        for (int c = SKIP_COL + 1; c < cellNum; c++) {
            Cell cell = row.getCell(c);
            String colName = this.getCellValue(cell);
            if (StringUtils.isEmpty(colName)) {
                break;
            }

            currentTable.addCol(new TableData.Column(colName));
        }
        return currentTable;
    }

    private TableData getTableData(TableData currentTable, Row row) {
        TableData.TableRow tableRow = new TableData.TableRow();
        tableRow.setTable(currentTable);
        tableRow.setLineNumber(row.getRowNum() + 1);
        boolean allBlank = true;
        for (int j = SKIP_COL + 1; j < row.getLastCellNum(); j++) {
            Cell cell = row.getCell(j);
            if (currentTable != null) {
                allBlank = addTableCellValue(cell, tableRow, currentTable);
            }
            if ((currentTable != null)
                    && tableRow.getTableCellValues().size() == currentTable.getColumns().size()) {
                // 丢弃多余的数据(如果有，不继续读了)
                break;
            }
        }
        if (!allBlank) {
            int delt = currentTable.getColumns().size() - tableRow.getTableCellValues().size();
            // 不够的数据,补 null
            for (int j = 0; j < delt; j++) {
                addTableCellValue(null, tableRow, currentTable);
            }
            currentTable.getTableRows().add(tableRow);
        }
        return currentTable;
    }

    private boolean addTableCellValue(Cell cell, TableData.TableRow tableRow, TableData currentTable) {
        boolean allBlank = true;
        TableData.TableCellValue tableCellValue = new TableData.TableCellValue(cell, tableRow,
                currentTable.getColumns()
                        .get(tableRow.getTableCellValues().size()));
        tableCellValue.setValue(getCellValue(cell));
        tableRow.getTableCellValues().add(tableCellValue);
        if (!StringUtils.isEmpty(tableCellValue.getValue())) {
            allBlank = false;
        }
        return allBlank;
    }

    private boolean isAllEmpty(Row row, int startCol) {
        for (int i = startCol; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && StringUtils.isNotEmpty(getCellValue(cell))) {
                return false;
            }
        }
        return true;
    }

}