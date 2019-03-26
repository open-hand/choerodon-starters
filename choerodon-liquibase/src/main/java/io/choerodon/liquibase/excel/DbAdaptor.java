package io.choerodon.liquibase.excel;

import io.choerodon.liquibase.addition.AdditionDataSource;
import io.choerodon.liquibase.exception.LiquibaseException;
import io.choerodon.liquibase.helper.LiquibaseHelper;
import io.choerodon.liquibase.utils.CellDataConverter;
import liquibase.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    private static final String SQL_SET = " set ";

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
    private Logger logger = LoggerFactory.getLogger(getClass());
    private DataSource dataSource;
    private Connection connection;
    private Map<String, Connection> connectionMap = new HashMap<>();
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

    //这里的连接不在这个方法关闭，而在closeConnection方法中关闭
    @SuppressWarnings("squid:S2095")
    public void initConnection() throws SQLException {
        connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        for (String table: AdditionDataSource.getTablesMap().keySet()){
            Connection conn = AdditionDataSource.getTablesMap().get(table).getDataSource().getConnection();
            conn.setAutoCommit(false);
            connectionMap.put(table, conn);
        }
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
                logger.error("commit or rollback exception: {}", e);
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("close connect exception: {}", e);
                }
            }
        }
        for (Connection connection:connectionMap.values()){
            if(connection != null){
                try (Connection c = connection) {
                    if (commit) {
                        c.commit();
                    } else {
                        c.rollback();
                    }
                } catch (SQLException e) {
                    logger.error("commit or rollback exception: {}", e);
                } finally {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        logger.error("close connect exception: {}", e);
                    }
                }
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
                String value = tableCellValue.getValue();
                String tableName = tableCellValue.getColumn().getName();
                if (StringUtils.isEmpty(value)) {
                    list.add(tableName + " IS NULL");
                } else {
                    list.add(tableName + " = ?");
                    params.add(tableCellValue);
                }
            }
        }
        sb.append(StringUtils.join(list, " AND "));
        Connection connection = this.connection;
        if(connectionMap.containsKey(tableRow.getTable().getName())){
            connection = connectionMap.get(tableRow.getTable().getName());
        }
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
                    throw new LiquibaseException("check unique found more than one, row:"
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
     * @param tableRow        要更新的TableRow
     * @param excludedColumns 要排除的列的集合
     * @param logInfo         日志集合
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
        Connection connection = this.connection;
        if(connectionMap.containsKey(tableRow.getTable().getName())){
            connection = connectionMap.get(tableRow.getTable().getName());
        }
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
                if (excludedColumn != null && excludedColumn.equalsIgnoreCase(column)) {
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
                    sb.append(tableCellValue.getColumn().getName());
                    sb.append("=?,");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(SQL_WHERE);
            for (TableCellValue tableCellValue : uniques) {
                sb.append(tableCellValue.getColumn().getName());
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
        long genVal = 0L;
        TableData.TableCellValue genTableCellValue = null;
        boolean isGeneratedColumnInserted = tableRow.isGeneratedColumnInserted();
        TableData table = tableRow.getTable();
        String tableName = table.getName();
        long maxId = table.getMaxId();
        Connection connection = this.connection;
        if(connectionMap.containsKey(tableRow.getTable().getName())){
            connection = connectionMap.get(tableRow.getTable().getName());
        }
        try (PreparedStatement ps = connection.prepareStatement(sql,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            int count = 1;
            for (TableData.TableCellValue tableCellValue : tableRow.getTableCellValues()) {
                if (tableCellValue.getColumn().isGen()) {
                    genTableCellValue = tableCellValue;
                    if (isGeneratedColumnInserted) {
                        genVal = Long.valueOf(genTableCellValue.getValue());
                        ps.setLong(count++, genVal);
                        //记录当前插入数据手动设置id的最大值
                        table.setMaxId(genVal > maxId ? genVal : maxId);
                    } else if (sequencePk()) {
                        genVal = getSeqNextVal(tableName);
                        if (genVal < maxId) {
                            //由于使用excel中手动设置的id,这种情况后续使用sequence.nextval的值可能导致主键冲突，所以要更新一下sequence的值
                            long step = maxId - genVal;
                            updateSequence(tableName, step);
                            genVal = getSeqNextVal(tableName);
                        }
                        if (genVal == maxId) {
                            genVal = getSeqNextVal(tableName);
                        }
                        ps.setLong(count++, genVal);
                    }
                    continue;
                }
                if (tableCellValue.getColumn().getLang() == null
                        || ZH_CN.equals(tableCellValue.getColumn().getLang())) {
                    if (!tableCellValue.isValuePresent() && tableCellValue.isFormula()) {
                        ps.setLong(count++, -1L);
                    } else {
                        setParam(ps, tableCellValue, count++);
                    }
                }
            }
            try {
                ps.executeUpdate();
                tableRow.setInsertFlag(true);
            } catch (SQLException sqle) {
                logger.error("[{}]error insert row:{}  sql:{}", tableName, tableRow, sql);
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
            genTableCellValue.updateValue(String.valueOf(genVal));
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
     * @return insert count
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
                throw new LiquibaseException("can not insert :" + tableRow);
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
        Connection connection = this.connection;
        if(connectionMap.containsKey(tableRow.getTable().getName())){
            connection = connectionMap.get(tableRow.getTable().getName());
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
        String value = tableCellValue.getValue();
        String type = tableCellValue.getColumn().getType();
        Object object = CellDataConverter.covert(value, type);
        if (object == null) {
            ps.setNull(nn, SQL_TYPE_MAP.get(tableCellValue.getColumn().getType()));
        } else if (object instanceof String) {
            ps.setString(nn, (String) object);
        } else if (object instanceof Long) {
            ps.setLong(nn, (Long) object);
        } else if (object instanceof LocalDate) {
            ps.setDate(nn, Date.valueOf((LocalDate) object));
        } else if (object instanceof LocalDateTime) {
            ps.setDate(nn, Date.valueOf(((LocalDateTime) object).toLocalDate()));
        } else if (object instanceof Double) {
            ps.setDouble(nn, (Double) object);
        } else {
            ps.setObject(nn, object);
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
        Connection connection = this.connection;
        if(connectionMap.containsKey(tableRow.getTable().getName())){
            connection = connectionMap.get(tableRow.getTable().getName());
        }
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

    private String tlTableName(String tableName) {
        String s = tableName.replaceAll("_", "");
        boolean allIsUpperCase = true;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLowerCase(c)) {
                allIsUpperCase = false;
                break;
            }
        }
        if (allIsUpperCase) {
            if(tableName.endsWith("_B")){
                tableName = tableName.substring(0, tableName.length() - 2);
            }
            return tableName + "_TL";
        } else {
            if(tableName.endsWith("_b")){
                tableName = tableName.substring(0, tableName.length() - 2);
            }
            return tableName + "_tl";
        }
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
        Connection connection = this.connection;
        if(connectionMap.containsKey(tableRow.getTable().getName())){
            connection = connectionMap.get(tableRow.getTable().getName());
        }
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

    protected String prepareInsertSql(TableData.TableRow tableRow) {
        boolean isGeneratedColumnInserted = tableRow.isGeneratedColumnInserted();
        String tableName = tableRow.getTable().getName();
        String sqlKey = tableName + "#" + isGeneratedColumnInserted;
        String sql = tableInsertSqlMap.get(sqlKey);
        if (sql == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ").append(tableName).append("(");
            int count = 0;
            for (TableData.TableCellValue tableCellValue : tableRow.getTableCellValues()) {
                boolean isGen = tableCellValue.getColumn().isGen();
                if (isGen && !isGeneratedColumnInserted && !sequencePk()) {
                    continue;
                }
                if (tableCellValue.getColumn().getLang() == null
                        || ZH_CN.equals(tableCellValue.getColumn().getLang())) {
                    count++;
                    sb.append(tableCellValue.getColumn().getName());
                    sb.append(",");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(") VALUES (");
            for (int i = 0; i < count; i++) {
                sb.append("?,");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
            sql = sb.toString();
            if (helper.isSqlServer() && isGeneratedColumnInserted) {
                StringBuilder builder = new StringBuilder();
                builder.append("set IDENTITY_INSERT ").append(tableName).append(" ").append("on;");
                builder.append(sql).append(";");
                builder.append("set IDENTITY_INSERT ").append(tableName).append(" ").append("off");
                sql = builder.toString();
            }
            tableInsertSqlMap.put(sqlKey, sql);
        }
        return sql;
    }

    protected Long getSeqNextVal(String tableName) throws SQLException {
        StringBuilder builder = new StringBuilder();
        builder.append("select ").append(tableName).append("_s.nextval from dual");
        Connection connection = this.connection;
        if(connectionMap.containsKey(tableName)){
            connection = connectionMap.get(tableName);
        }
        try (PreparedStatement ps = connection.prepareStatement(builder.toString())) {
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            } catch (SQLException e) {
                logger.error("error get sequence nextVal, tableName:{}", tableName);
                throw e;
            }
        }
    }

    private void updateSequence(String tableName, long step) throws SQLException {
        long defaultStep = 1L;
        StringBuilder builder = new StringBuilder();
        String alterSql = builder.append("alter sequence ").append(tableName).append("_s increment by ").toString();
        String sql = alterSql + step;
        execUpdateSequence(sql, tableName);

        getSeqNextVal(tableName);

        sql = alterSql + defaultStep;
        execUpdateSequence(sql, tableName);
    }

    private void execUpdateSequence(String sql, String table) throws SQLException {
        PreparedStatement statement = null;
        try {
            Connection connection = this.connection;
            if(connectionMap.containsKey(table)){
                connection = connectionMap.get(table);
            }
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }

    }

    protected boolean sequencePk() {
        return useSeq;
    }

}
