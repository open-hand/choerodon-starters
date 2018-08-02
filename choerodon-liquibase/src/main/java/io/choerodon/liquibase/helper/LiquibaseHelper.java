package io.choerodon.liquibase.helper;

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
        }
        return currentDbType;
    }

    public boolean isSupportSequence() {
        return dbType().isSupportSequence();
    }

    public boolean isH2Base() {
        return this.url.startsWith("jdbc:h2");
    }

    public enum DbType {
        MYSQL(true, false), ORACLE(false, true), HANA(true, false), SQLSERVER(true, false), H2(true, false), DB2(false, true);

        private boolean supportAutoIncrement;

        private boolean supportSequence;


        DbType(boolean supportAutoIncrement, boolean supportSequence) {
            this.supportAutoIncrement = supportAutoIncrement;
            this.supportSequence = supportSequence;
        }

        public boolean isSupportAutoIncrement() {
            return supportAutoIncrement;
        }

        public boolean isSupportSequence() {
            return supportSequence;
        }
    }


}
