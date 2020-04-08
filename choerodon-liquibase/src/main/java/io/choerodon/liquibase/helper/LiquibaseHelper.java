package io.choerodon.liquibase.helper;

import io.choerodon.liquibase.LiquibaseExecutor;

import org.springframework.util.StringUtils;

import java.io.File;

/**
 * Created by hailuoliu@choerodon.io on 2018/7/11.
 */
public class LiquibaseHelper {

    private DbType currentDbType = null;
    private String url;

    public LiquibaseHelper(String url) {
        this.url = url;
    }

    public DbType dbType() {
        if (this.currentDbType != null) {
            return currentDbType;
        }
        if (this.url.startsWith("jdbc:h2")) {
            this.currentDbType = DbType.H2;
        } else if (this.url.startsWith("jdbc:oracle")) {
            this.currentDbType = DbType.ORACLE;
        } else if (this.url.startsWith("jdbc:mysql")) {
            this.currentDbType = DbType.MYSQL;
        } else if (this.url.startsWith("jdbc:sqlserver")) {
            this.currentDbType = DbType.SQLSERVER;
        } else if (this.url.startsWith("jdbc:sap")) {
            this.currentDbType = DbType.HANA;
        } else if (this.url.startsWith("jdbc:postgresql")) {
            this.currentDbType = DbType.POSTGRESQL;
        }
        return currentDbType;
    }

    public boolean isSupportSequence() {
        return dbType().isSupportSequence();
    }

    public boolean isH2Base() {
        return this.url.startsWith("jdbc:h2");
    }

    public boolean isOracle() {
        return this.url.startsWith("jdbc:oracle");
    }

    public boolean isMysql() {
        return this.url.startsWith("jdbc:mysql");
    }

    public boolean isSqlServer() {
        return this.url.startsWith("jdbc:sqlserver");
    }

    public boolean isPostgresql() {
        return this.url.startsWith("jdbc:postgresql");
    }

    public boolean isHana() {
        return this.url.startsWith("jdbc:sap");
    }

    public String dataPath(String path) {
        return path;
    }

    public enum DbType {
        MYSQL("mysql", true, false),
        ORACLE("oracle", false, true),
        HANA("hana", false, true),
        SQLSERVER("sqlserver", true, false),
        H2("h2", true, false),
        DB2("db2", false, true),
        POSTGRESQL("postgresql", false, true);

        private boolean supportAutoIncrement;

        private boolean supportSequence;

        private final String value;


        DbType(String value, boolean supportAutoIncrement, boolean supportSequence) {
            this.value = value;
            this.supportAutoIncrement = supportAutoIncrement;
            this.supportSequence = supportSequence;
        }

        @Override
        public String toString() {
            return value;
        }

        public boolean isSupportAutoIncrement() {
            return supportAutoIncrement;
        }

        public boolean isSupportSequence() {
            return supportSequence;
        }
    }


}
