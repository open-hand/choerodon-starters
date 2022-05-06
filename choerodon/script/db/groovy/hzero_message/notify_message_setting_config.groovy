package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify_message_setting_config.groovy') {
    changeSet(author: 'xiangwang04@gmail.com', id: '2020-07-024-add-notify_message_setting_config') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'NOTIFY_MESSAGE_SETTING_S', startValue:"1")
        }
        createTable(tableName: "NOTIFY_MESSAGE_SETTING_CONFIG") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_NOTIFY_CONFIG')
            }
            column(name: 'MESSAGE_CODE', type: 'VARCHAR(64)', remarks: '发送设置表中的messageCode') {
                constraints(nullable: false)
            }
            column(name: 'EDIT', type: "TINYINT UNSIGNED", defaultValue: "1", remarks: '是否允许编辑接受配置') {
                constraints(nullable: false)
            }
            column(name: 'TENANT_ID', type: "BIGINT UNSIGNED", defaultValue: "0", remarks: '组织id') {
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