package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_user_dashboard.groovy') {
    changeSet(author: 'fan@choerodon.io', id: '2018-07-23-iam-user-dashboard') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'IAM_USER_DASHBOARD_S', startValue:"1")
        }
        createTable(tableName: "IAM_USER_DASHBOARD") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_USER_DASHBOARD')
            }
            column(name: 'USER_ID', type: 'BIGINT UNSIGNED', remarks: 'user id') {
                constraints(nullable: true)
            }
            column(name: 'DASHBOARD_ID', type: 'BIGINT UNSIGNED', remarks: 'dashboard id') {
                constraints(nullable: true)
            }
            column(name: 'IS_VISIBLE', type: 'TINYINT UNSIGNED', defaultValue: "1", remarks: '是否可见') {
                constraints(nullable: true)
            }
            column(name: 'SORT', type: 'VARCHAR(128)', remarks: '顺序') {
                constraints(nullable: true)
            }
            column(name: 'LEVEL', type: 'VARCHAR(64)', remarks: '层级：site / organization / project') {
                constraints(nullable: true)
            }
            column(name: 'SOURCE_ID', type: 'BIGINT UNSIGNED', remarks: '对应项目/组织 id')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1") {
                constraints(nullable: true)
            }
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0") {
                constraints(nullable: true)
            }
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0") {
                constraints(nullable: true)
            }
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: 'superleader8@gmail.com', id: '2018-08-28-rename') {
        renameColumn(columnDataType: 'VARCHAR(64)', newColumnName: "FD_LEVEL", oldColumnName: "LEVEL", remarks: '层级：site / organization / project', tableName: 'IAM_USER_DASHBOARD')
    }
}