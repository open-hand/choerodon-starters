package io.choerodon.mybatis.entity;

/**
 * @author yishuida@gmail.com : 18-8-23
 */
public enum DbType {
    // oracle 数据库
    ORACLE("oracle", false, true),
    // mysql  数据库
    MYSQL("mysql", true, false),
    // sql server 数据库
    SQL_SERVER("sqlserver", true, false),
    // h2 数据库
    H2("h2", true, false),
    // hana 数据库
    HANA("hana", false, true),
    // postgre 数据库
    POSTGRE_SQL("postgresql", false, true);

    private final String value;
    private final  boolean supportAutoIncrement;
    private final boolean supportSequence;

    DbType(String value, boolean supportAutoIncrement, boolean supportSequence) {
        this.value = value;
        this.supportAutoIncrement = supportAutoIncrement;
        this.supportSequence = supportSequence;
    }

    public String getValue() {
        return value;
    }
    @Deprecated
    public boolean isSuppportAutoIncrement() {
        return supportAutoIncrement;
    }
    public boolean isSupportAutoIncrement() {
        return supportAutoIncrement;
    }
    public boolean isSupportSequence() {
        return supportSequence;
    }

    @Override
    public String toString() {
        return value;
    }

    public static DbType getDbType(String type) {
        for (DbType dbType : values()) {
            if (dbType.toString().equalsIgnoreCase(type)) {
                return dbType;
            }
        }
        return null;
    }

}