package io.choerodon.liquibase.enums;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/8/23
 * @Modified By:
 */
public enum DbTypeEnum {
    MYSQL("mysql"),
    ORACLE("oracle"),
    SQLSERVER("sqlserver"),
    POSTGRES("postgres");

    private String type;

    DbTypeEnum(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
