package io.choerodon.liquibase.excel;

import io.choerodon.liquibase.addition.AdditionDataSource;
import io.choerodon.liquibase.helper.LiquibaseHelper;
import liquibase.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static io.choerodon.liquibase.excel.TableData.TableCellValue;

/**
 * DbAdaptor，包含数据库连接信息及基本的操作语句
 *
 * @author dongfan117@gmail.com
 */
public class DbAdaptor {

    static final Map<String, Integer> SQL_TYPE_MAP = new HashMap<>();
    private static final String ZH_CN = "zh_CN";
    private static final String SQL_UPDATE = "update ";
    private static final String SQL_WHERE = " where  ";
    private static final String SQL_SET =" set ";

    static {
        SQL_TYPE_MAP.put("VARCHAR", Types.VARCHAR);
        SQL_TYPE_MAP.put("DATE", Types.DATE);
        SQL_TYPE_MAP.put("CLOB", Types.CLOB);
        SQL_TYPE_MAP.put("BLOB", Types.BLOB);
        SQL_TYPE_MAP.put("DECIMAL", Types.DECIMAL);
        SQL_TYPE_MAP.put("BIGINT", Types.BIGINT);
        SQL_TYPE_MAP.put("INT", Types.BIGINT);
    }

    Map<String, String> tableInsertSqlMap = new HashMap<>();
    Map<String, String> tableUpdateSqlMap = new HashMap<>();
    Map<String, String> tableUpdateTlSqlMap = new HashMap<>();
    private SimpleDateFormat sdfL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdfS = new SimpleDateFormat("yyyy-MM-dd");
    private Logger logger = LoggerFactory.getLogger(getClass());
    private DataSource dataSource;
    private Connection connection;
    private ExcelDataLoader dataProcessor;
    private boolean useSeq = false;
    private boolean override = true;
    private LiquibaseHelper helper;

    public DbAdaptor(ExcelDataLoader dataProcessor, AdditionDataSource ad) {
        this.dataProcessor = dataProcessor;
        this.helper = ad.getLiquibaseHelper();
        this.useSeq = helper.isSupportSequence();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isOverride() {
        return override;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }

    public void initConnection() throws SQLException {
        connection = dataSource.getConnection();
        connection.setAutoCommit(false);
    }

    /**
     * 关闭连接.
     *
     * @param commit 是否commit
     */
    public void closeConnection(boolean commit) {
        if (connection != null) {
            try (Connection c = connection) {
                if (commit) {
                    c.commit();
                } else {
                    c.rollback();
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
    }

    protected Connection getConnection() {
        return connection;
    }

    /**
     * 处理表行.
     *
     * @param tableRow 表行
     * @return 新插入, 或检测存在 则返回 1,否则0
     * @throws SQLException SQL异常
     */
    public int processTableRow(TableData.TableRow tableRow) throws SQLException {
        if (tableRow.isProcessFlag()) {
            return 0;
        }
        Long cu = checkExists(tableRow);
        if (cu == null && tableRow.canInsert()) {
            doInsert(tableRow);
            doInsertTl(tableRow);
            return 1;
        } else if (cu != null && cu >= 0) {
            tableRow.setProcessFlag(true);
            doInsertTl(tableRow);
            return 1;
        }

        return 0;
    }

    /**
     * 检查行是否存在.
     *
     * @param tableRow 表行
     * @return 反回 -1: 不能检查唯一性（唯一性字段值不确定），返回 0:存在，但没有自动生成的主键， 返回 null:不存在， 返回 大于0:存在，现有主键
     * @throws SQLException SQL异常
     */
    protected Long checkExists(TableData.TableRow tableRow) throws SQLException {
        boolean uniquePresent = true;
        TableCellValue genTableCellValue = null;
        for (TableCellValue tableCellValue : tableRow.getTableCellValues()) {
            if (tableCellValue.getColumn().isGen()) {
                genTableCellValue = tableCellValue;
            }
            if (tableCellValue.isFormula() && !tableCellValue.isValuePresent()) {
                dataProcessor.tryUpdateCell(tableCellValue);
            }
            if (tableCellValue.getColumn().isUnique()) {
                uniquePresent = uniquePresent && tableCellValue.isValuePresent();
            }
        }
        if (!uniquePresent) {
            logger.info("[{}] check exists: ?? row:{} ,result :(-1)not ready",
                    tableRow.getTable().getName(), tableRow);
            return -1L;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("select ").append(genTableCellValue == null ? "0" : genTableCellValue.getColumn().getName())
                .append(" from ").append(tableRow.getTable().getName());
        sb.append(" where ");
        List<String> list = new ArrayList<>();
        List<TableCellValue> params = new ArrayList<>();
        for (TableData.TableCellValue tableCellValue : tableRow.getTableCellValues()) {
            if (tableCellValue.getColumn().isUnique()) {
                list.add(tableCellValue.getColumn().getName() + "=?");
                params.add(tableCellValue);
            }
        }
        sb.append(StringUtils.join(list, " AND "));
        try (PreparedStatement ps = connection.prepareStatement(sb.toString())) {
            int index = 1;
            for (TableCellValue tableCellValue : params) {
                setParam(ps, tableCellValue, index++);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs == null || !rs.next()) {
                    logger.info("[{}] check exists: <> row:{} ,result :not exists",
                            tableRow.getTable().getName(), tableRow);
                    return null;
                }
                Long pk = rs.getLong(1);
                if (rs.next()) {
                    throw new IllegalStateException("check unique found more than one,row:"
                            + tableRow);
                }
                logger.info("[{}] check exists: == row:{} ,result :{}", tableRow.getTable().getName(), tableRow, pk);
                if (genTableCellValue != null) {
                    genTableCellValue.updateValue("" + pk);
                }
                tableRow.setExistsFlag(true);
                return pk;
            } catch (SQLException e) {
                logger.error("[{}]error check unique, row:{}, sql:{}",
                        tableRow.getTable().getName(), tableRow, sb);
                throw e;
            }

        }

    }

    /**
     * 更新TableRow.
     * 调用此方法之前,需要确保所有值已就绪。
     *
     * @param tableRow 要更新的TableRow
     * @return update count
     * @throws SQLException SQL异常
     */
    protected int doUpdate(TableData.TableRow tableRow, Set<String> excludedColumns, Set<String> logInfo) throws SQLException {
        int updateCount = 0;
        TableCellValue genTableCellValue = null;
        List<TableCellValue> uniques = new ArrayList<>();
        List<TableCellValue> normals = new ArrayList<>();
        for (TableData.TableCellValue tableCellValue : tableRow.getTableCellValues()) {
            if (tableCellValue.isFormula() && !tableCellValue.isValuePresent()) {
                dataProcessor.tryUpdateCell(tableCellValue);
            }
            if (tableCellValue.getColumn().isGen()) {
                genTableCellValue = tableCellValue;
            } else if (tableCellValue.getColumn().isUnique()) {
                if (!excluded(tableCellValue.getColumn().getName(), excludedColumns)) {
                    uniques.add(tableCellValue);
                } else {
                    logInfo.add(processLog(tableRow, tableCellValue));
                }
            } else {
                if (!excluded(tableCellValue.getColumn().getName(), excludedColumns)) {
                    normals.add(tableCellValue);
                } else {
                    logInfo.add(processLog(tableRow, tableCellValue));
                }
            }
        }
        if (genTableCellValue != null) {
            uniques.clear();
            uniques.add(genTableCellValue);
        }
        if (normals.isEmpty()) {
            return updateCount;
        }
        //更新表的sql
        String sql = prepareTableUpdateSql(tableRow, uniques, normals);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 1;
            for (TableCellValue tableCellValue : normals) {
                if (tableCellValue.getColumn().getLang() == null
                        || ZH_CN.equalsIgnoreCase(tableCellValue.getColumn().getLang())) {
                    setParam(ps, tableCellValue, index++);
                }
            }
            for (TableCellValue tableCellValue : uniques) {
                setParam(ps, tableCellValue, index++);
            }
            updateCount += ps.executeUpdate();
            tableRow.setUpdateFlag(true);
        } catch (SQLException e) {
            logger.error("[{}]update error, row:{} ,sql:{}",
                    tableRow.getTable().getName(), tableRow, sql);
            throw e;
        }

        if (genTableCellValue == null || tableRow.getTable().getLangs().isEmpty()) {
            return updateCount;
        }
        //更新多语言表的sql
        sql = prepareTableUpdateTlSql(tableRow, genTableCellValue, normals);

        for (String lang : tableRow.getTable().getLangs()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                int index = 1;
                for (TableData.TableCellValue tableCellValue : normals) {
                    if (lang.equals(tableCellValue.getColumn().getLang())) {
                        setParam(ps, tableCellValue, index++);
                    }
                }
                setParam(ps, genTableCellValue, index++);
                ps.setString(index, lang);
                updateCount += ps.executeUpdate();
            } catch (SQLException e) {
                logger.error("[{}]error update tl ,row:{},sql:{}",
                        tableRow.getTable().getName(), tableRow, sql);
                throw e;
            }
        }
        return updateCount;

    }

    private String processLog(TableData.TableRow tableRow, TableCellValue tableCellValue) {
        StringBuilder sb = new StringBuilder();
        sb.append("skip update table : ");
        sb.append(tableRow.getTable().getName());
        sb.append(" column: ");
        sb.append(tableCellValue.getColumn().getName());
        return sb.toString();
    }

    private boolean excluded(String column, Set<String> excludedColumns) {
        if (excludedColumns != null) {
            for (String excludedColumn : excludedColumns) {
                if (excludedColumn != null && excludedColumn.equals(column)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String prepareTableUpdateSql(TableData.TableRow tableRow,
                                         List<TableCellValue> uniques,
                                         List<TableCellValue> normals) {
        String sql = tableUpdateSqlMap.get(tableRow.getTable().getName());
        if (sql == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(SQL_UPDATE).append(tableRow.getTable().getName()).append(SQL_SET);
            for (TableCellValue tableCellValue : normals) {
                if (tableCellValue.getColumn().getLang() == null
                        || ZH_CN.equalsIgnoreCase(tableCellValue.getColumn().getLang())) {
                    appendColumn(sb, tableCellValue);
                    sb.append("=?,");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(SQL_WHERE);
            for (TableCellValue tableCellValue : uniques) {
                appendColumn(sb, tableCellValue);
                sb.append("=? AND ");
            }
            sb.delete(sb.length() - 4, sb.length());
            sql = sb.toString();
            tableUpdateSqlMap.put(tableRow.getTable().getName(), sql);
        }
        return sql;
    }

    private String prepareTableUpdateTlSql(TableData.TableRow tableRow,
                                           TableCellValue genTableCellValue,
                                           List<TableCellValue> normals) {
        String sql = tableUpdateTlSqlMap.get(tableRow.getTable().getName());
        if (sql == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(SQL_UPDATE).append(tlTableName(tableRow.getTable().getName())).append(SQL_SET);

            for (TableData.TableCellValue tableCellValue : normals) {
                if (ZH_CN.equalsIgnoreCase(tableCellValue.getColumn().getLang())) {
                    sb.append(tableCellValue.getColumn().getName()).append("=?,");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(SQL_WHERE).append(genTableCellValue.getColumn().getName()).append("=? and lang=?");
            sql = sb.toString();
            tableUpdateTlSqlMap.put(tableRow.getTable().getName(), sql);
        }
        return sql;
    }

    /**
     * 在调用本方法之前，需确保 该行记录具备插入条件.
     *
     * @param tableRow tableRow
     * @return 返回结果码
     * @throws SQLException 异常
     */
    protected Long doInsert(TableData.TableRow tableRow) throws SQLException {
        String sql = prepareInsertSql(tableRow);
        Long genVal = null;
        TableData.TableCellValue genTableCellValue = null;
        try (PreparedStatement ps = connection.prepareStatement(sql,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            int nn = 1;
            for (TableData.TableCellValue tableCellValue : tableRow.getTableCellValues()) {
                if (tableCellValue.getColumn().isGen()) {
                    genTableCellValue = tableCellValue;
                    if (sequencePk()) {
                        genVal = getSeqNextVal(tableRow.getTable().getName());
                        ps.setLong(nn++, genVal);
                    }
                    continue;
                }
                if (tableCellValue.getColumn().getLang() == null
                        || ZH_CN.equals(tableCellValue.getColumn().getLang())) {
                    if (!tableCellValue.isValuePresent() && tableCellValue.isFormula()) {
                        ps.setLong(nn++, -1L);
                    } else {
                        setParam(ps, tableCellValue, nn++);
                    }
                }
            }
            try {
                ps.executeUpdate();
                tableRow.setInsertFlag(true);
            } catch (SQLException sqle) {
                logger.error("[{}]error insert row:{}  sql:{}",
                        tableRow.getTable().getName(), tableRow, sql);
                throw sqle;
            }
            logger.info("insert row:{}", tableRow);
            tableRow.getTable().setInsert(tableRow.getTable().getInsert() + 1);
            if (!sequencePk() && genTableCellValue != null) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs != null && rs.next()) {
                        genVal = rs.getLong(1);
                    } else {
                        logger.warn("no gen pk,row:{}", tableRow);
                    }
                }
            }
        }
        if (genTableCellValue != null) {
            genTableCellValue.updateValue("" + genVal);
        }
        tableRow.setProcessFlag(true);

        return genVal;

    }

    private List<TableData.TableCellValue> getUnpresentFormulaTds(TableData.TableRow tableRow) {
        List<TableData.TableCellValue> formulaTableCellValues = new ArrayList<>();
        for (TableData.TableCellValue tableCellValue : tableRow.getTableCellValues()) {
            if (tableCellValue.isFormula() && !tableCellValue.isValuePresent()) {
                formulaTableCellValues.add(tableCellValue);
            }
        }
        return formulaTableCellValues;
    }

    /**
     * 插入不存在的行,暂时不理会不能确定的值.
     *
     * @param tables getTable()
     * @throws SQLException 异常
     */
    public int weakInsert(List<TableData> tables) throws SQLException {
        List<TableData.TableRow> tempList = new ArrayList<>();
        for (TableData tableData : tables) {
            for (TableData.TableRow tableRow : tableData.getTableRows()) {
                if (tableRow.isProcessFlag()) {
                    continue;
                }

                Long cu = checkExists(tableRow);
                if (cu == null) {
                    doInsert(tableRow);
                    tableRow.setProcessFlag(false);
                    logger.info("weak insert row:{}", tableRow);
                    doInsertTl(tableRow);
                    tempList.add(tableRow);
                } else if (cu > 0) {
                    doInsertTl(tableRow);
                    tempList.add(tableRow);
                }
            }
        }
        int count = 0;
        do {
            count = 0;
            for (TableData.TableRow tableRow : tempList) {
                if (tableRow.isProcessFlag()) {
                    continue;
                }
                List<TableData.TableCellValue> formulaTableCellValues =
                        getUnpresentFormulaTds(tableRow);
                int cc = 0;
                for (TableData.TableCellValue tableCellValue : formulaTableCellValues) {
                    if (dataProcessor.tryUpdateCell(tableCellValue)) {
                        cc++;
                        doPostUpdate(tableRow, tableCellValue, Long.parseLong(""
                                + tableCellValue.getValue()));
                        count++;
                    }
                }
                if (cc == formulaTableCellValues.size()) {
                    tableRow.setProcessFlag(true);
                    tableRow.setInsertFlag(true);
                }
            }
        } while (count > 0);
        for (TableData.TableRow tableRow : tempList) {
            if (!tableRow.isProcessFlag()) {
                throw new RuntimeException("can not insert :" + tableRow);
            }
        }

        return tempList.size();
    }

    /**
     * 当执行 weakInsert 后,使用该方法修复数据.
     *
     * @param tableRow       tableRow
     * @param tableCellValue tableCellValue
     * @param value          value
     * @throws SQLException SQLException
     */
    protected void doPostUpdate(TableData.TableRow tableRow,
                                TableCellValue tableCellValue,
                                Long value) throws SQLException {
        TableData.TableCellValue genTableCellValue = null;
        for (TableCellValue d : tableRow.getTableCellValues()) {
            if (d.getColumn().isGen()) {
                genTableCellValue = d;
                break;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(SQL_UPDATE).append(tableRow.getTable().getName()).append(SQL_SET)
                .append(tableCellValue.getColumn().getName()).append("=? where ");
        if (genTableCellValue != null) {
            sb.append(genTableCellValue.getColumn().getName()).append("=?");
        }
        try (PreparedStatement ps = connection.prepareStatement(sb.toString())) {
            ps.setLong(1, value);
            if (genTableCellValue != null) {
                setParam(ps, genTableCellValue, 2);
            }
            ps.executeUpdate();
            logger.debug("update getColumn():{} value:{}  row:{}",
                    tableCellValue.getColumn().getName(), value, tableRow);
        } catch (SQLException ee) {
            logger.error("[{}]error post update getColumn():{},row:{} ,sql:{}",
                    tableRow.getTable().getName(), tableCellValue.getColumn().getName(), tableRow, sb);
            throw ee;
        }

    }

    private void setParam(PreparedStatement ps,
                          TableData.TableCellValue tableCellValue,
                          int nn) throws SQLException {
        Object vv = convertDataType(tableCellValue.getValue(), tableCellValue.getColumn().getType());
        if (vv == null) {
            ps.setNull(nn, SQL_TYPE_MAP.get(tableCellValue.getColumn().getType()));
        } else if (vv instanceof String) {
            ps.setString(nn, (String) vv);
        } else if (vv instanceof Long) {
            ps.setLong(nn, (Long) vv);
        } else if (vv instanceof Date) {
            ps.setDate(nn, new java.sql.Date(((Date) vv).getTime()));
        } else if (vv instanceof Double) {
            ps.setDouble(nn, (Double) vv);
        } else {
            ps.setObject(nn, vv);
        }
    }

    protected boolean checkTlExists(TableData.TableRow tableRow,
                                    String lang) throws SQLException {
        StringBuilder sb = new StringBuilder();
        TableCellValue genTableCellValue = null;
        for (TableCellValue tableCellValue : tableRow.getTableCellValues()) {
            if (tableCellValue.getColumn().isGen() || tableCellValue.getColumn().isUnique()) {
                genTableCellValue = tableCellValue;
                break;
            }
        }
        if (genTableCellValue == null) {
            return true;
        }
        sb.append("select 1 from ").append(tlTableName(tableRow.getTable().getName()));
        sb.append(" where ").append(genTableCellValue.getColumn().getName()).append("=?");
        sb.append(" AND LANG=?");

        String sql = sb.toString();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, genTableCellValue.getValue());
            ps.setObject(2, lang);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            } catch (SQLException e) {
                logger.error("[{}]error check tl exists, row:{} ,sql:{}",
                        tableRow.getTable().getName(), tableRow, sql);
                throw e;
            }
        }

    }

    private String tlTableName(String str) {
        return str + "_tl";
    }

    /**
     * 为支持的语言插入多语言数据.
     * <br>
     * 不支持多语言的表,没有任何副作用<br>
     * 插入之前,会检查等价数据是否存在
     *
     * @param tableRow tableRow
     * @return 操作结果
     * @throws SQLException 异常
     */
    protected int doInsertTl(TableData.TableRow tableRow) throws SQLException {
        int cc = 0;
        for (String lang : tableRow.getTable().getLangs()) {
            // 没有多语言支持的话,这个循环不会执行
            // 当基表插入成功以后,tl 表肯定可以插入成功(如果不存在)
            cc += doInsertTl(tableRow, lang);
        }
        return cc;
    }

    protected int doInsertTl(TableData.TableRow tableRow, String lang) throws SQLException {
        if (checkTlExists(tableRow, lang)) {
            return 0;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tlTableName(tableRow.getTable().getName())).append("(");
        List<String> keys = new LinkedList<>();
        List<TableData.TableCellValue> values = new LinkedList<>();
        TableData.TableCellValue unique = null;
        boolean isGen = false;
        for (TableData.TableCellValue tableCellValue : tableRow.getTableCellValues()) {
            if (tableCellValue.getColumn().isUnique()) {
                unique = tableCellValue;
            }
            if (tableCellValue.getColumn().isGen()) {
                isGen = true;
                keys.add(tableCellValue.getColumn().getName());
                values.add(tableCellValue);
            } else if (lang.equals(tableCellValue.getColumn().getLang())) {
                keys.add(tableCellValue.getColumn().getName());
                values.add(tableCellValue);
            }
        }
        if (!isGen && unique != null) {
            keys.add(unique.getColumn().getName());
            values.add(unique);
        }
        keys.add("lang");
        sb.append(StringUtils.join(keys, ","));
        sb.append(")VALUES(");
        for (int i = 0; i < values.size(); i++) {
            sb.append("?").append(",");
        }
        sb.append("?)");

        String sql = sb.toString();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int nn = 1;
            for (TableData.TableCellValue tableCellValue : values) {
                setParam(ps, tableCellValue, nn++);
            }
            ps.setString(nn, lang);
            try {
                ps.executeUpdate();
            } catch (SQLException sqle) {
                logger.error("[{}]error insert tl row:{}  sql:{}",
                        tableRow.getTable().getName(), tableRow, sql);
                throw sqle;
            }
        }

        logger.info("insert tl for row : {}  lang:{}", tableRow, lang);
        return 1;

    }

    protected Object convertDataType(String value, String type) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if ("DATE".equalsIgnoreCase(type)) {
            try {
                if (value.length() <= 10) {
                    return sdfS.parse(value);
                }
                return sdfL.parse(value);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if ("DECIMAL".equalsIgnoreCase(type) || "NUMBER".equalsIgnoreCase(type)
                || "BIGINT".equalsIgnoreCase(type)) {
            if (value.length() == 0) {
                return null;
            }
            if (value.contains(".")) {
                return Double.parseDouble(value);
            }
            return Long.parseLong(value);
        }
        return value;

    }

    protected String prepareInsertSql(TableData.TableRow tableRow) {
        String sql = tableInsertSqlMap.get(tableRow.getTable().getName());
        if (sql == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ").append(tableRow.getTable().getName()).append("(");
            int cc = 0;
            for (TableData.TableCellValue tableCellValue : tableRow.getTableCellValues()) {
                if (tableCellValue.getColumn().isGen() && !sequencePk()) {
                    continue;
                }
                if (tableCellValue.getColumn().getLang() == null
                        || ZH_CN.equals(tableCellValue.getColumn().getLang())) {
                    cc++;
                    appendColumn(sb, tableCellValue);
                    sb.append(",");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(") VALUES (");
            for (int i = 0; i < cc; i++) {
                sb.append("?,");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
            sql = sb.toString();
            tableInsertSqlMap.put(tableRow.getTable().getName(), sql);
        }
        return sql;
    }

    private void appendColumn(StringBuilder sb, TableCellValue tableCellValue) {
        //mysql处理保留字段处理时加撇号``,oracle是加"", sqlserver是加[]
        //oracle数据库列名使用了保留字段，加双引号处理
        String columnName = tableCellValue.getColumn().getName();
        if (helper.isOracle()) {
            sb.append("\"").append(columnName.toUpperCase()).append("\"");
        } else if (helper.isMysql()) {
            sb.append("`").append(columnName).append("`");
        } else if (helper.isSqlServer()) {
            sb.append("[").append(columnName).append("]");
        } else {
            sb.append(columnName);
        }
    }

    protected Long getSeqNextVal(String tableName) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("select "
                + tableName + "_s.nextval from dual")) {
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            } catch (SQLException e) {
                logger.error("error get sequence nextVal, tableName:{}" + tableName);
                throw e;
            }
        }
    }

    protected boolean sequencePk() {
        return useSeq;
    }

}
