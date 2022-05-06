package script.db

databaseChangeLog(logicalFilePath: 'script/db/webhook_project_rel.groovy') {
    changeSet(author: 'scp', id: '2020-05-12-add-webhook_project_rel') {
        createTable(tableName: "WEBHOOK_PROJECT_REL") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_NOTIFY_RECEIVE_SETTING')
            }
            column(name: 'WEBHOOK_ID', type: 'BIGINT UNSIGNED', remarks: 'webhookId') {
                constraints(nullable: false)
            }
            column(name: 'PROJECT_ID', type: 'BIGINT UNSIGNED', remarks: '项目id') {
                constraints(nullable: false)
            }
            column(name: 'TENANT_ID', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: false)
            }
            column(name: 'SERVER_CODE', type: 'VARCHAR(64)', remarks: '服务编码') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}