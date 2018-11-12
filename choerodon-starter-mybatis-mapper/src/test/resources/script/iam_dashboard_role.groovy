package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_dashboard_role.groovy') {
    changeSet(author: 'fan@choerodon.io', id: '2018-09-04-iam-dashboard-role') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'IAM_DASHBOARD_ROLE_S', startValue:"1")
        }

        createTable(tableName: "IAM_DASHBOARD_ROLE") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_DASHBOARD_ROLE')
            }
            column(name: 'DASHBOARD_ID', type: 'BIGINT UNSIGNED', remarks: 'dashboardId') {
                constraints(nullable: false)
            }
            column(name: 'ROLE_ID', type: 'BIGINT UNSIGNED', remarks: '角色id') {
                constraints(nullable: false)
            }
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
        addUniqueConstraint(tableName: 'IAM_DASHBOARD_ROLE', columnNames: 'DASHBOARD_ID, ROLE_ID', constraintName: 'UK_IAM_DASHBOARD_ROLE_U1')
    }

}