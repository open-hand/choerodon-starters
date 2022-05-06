package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify_message_setting-target.groovy') {
    changeSet(author: 'xiangwang04@gmail.com', id: '2019-12-03-add-notify-target') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'NOTIFY_TARGET_USER_S', startValue: "1")
        }
        createTable(tableName: "NOTIFY_MESSAGE_SETTING_TARGET") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_NOTIFY_CONFIG')
            }
            column(name: 'TYPE', type: 'VARCHAR(64)', remarks: '接收对象类型。比如经办人，报告人，指定用户等。') {
                constraints(nullable: false)
            }
            column(name: 'USER_ID', type: 'BIGINT UNSIGNED', defaultValue: "0", remarks: '接收对象ID，具体只存指定接收对象的ID。') {
                constraints(nullable: false)
            }
            column(name: 'MESSAGE_SETTING_ID', type: 'BIGINT UNSIGNED', remarks: 'notify-message-setting的ID') {
                constraints(nullable: false)
            }
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'NOTIFY_MESSAGE_SETTING_TARGET', columnNames: 'TYPE, USER_ID, MESSAGE_SETTING_ID', constraintName: 'UK_notify-message-setting-target_U1')
    }

    changeSet(author: 'xiangwang04@mail.com', id: '2020-10-15-fix-message-data') {
        sql("""
            DELETE 
            FROM
              notify_message_setting_target 
            WHERE
            MESSAGE_SETTING_ID IN ( SELECT ID FROM notify_message_setting WHERE PROJECT_ID != 0 AND ENV_ID = 0 AND EVENT_NAME != 'defaultValue' AND NOTIFY_TYPE = 'resourceDelete' );
            """)
    }
}