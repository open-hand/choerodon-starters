package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify_message_setting.groovy') {
    changeSet(author: 'xiangwang04@gmail.com', id: '2019-12-03-add-notify_message_setting') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'NOTIFY_MESSAGE_SETTING_S', startValue: "1")
        }
        createTable(tableName: "NOTIFY_MESSAGE_SETTING") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_NOTIFY_CONFIG')
            }
            column(name: 'NOTIFY_TYPE', type: 'VARCHAR(64)', remarks: '通知类型。比如敏捷消息，DevOps消息等。') {
                constraints(nullable: false)
            }
            column(name: 'CODE', type: 'VARCHAR(32)', remarks: '消息code，指向notify-send-setting表') {
                constraints(nullable: false)
            }
            column(name: 'PROJECT_ID', type: 'BIGINT UNSIGNED', defaultValue: "0", remarks: '项目ID。') {
                constraints(nullable: false)
            }
            column(name: 'ENV_ID', type: 'BIGINT UNSIGNED', defaultValue: "0", remarks: '环境id') {
                constraints(nullable: false)
            }
            column(name: 'EVENT_NAME', type: 'VARCHAR(64)', defaultValue: "", remarks: '资源删除事件名字') {
                constraints(nullable: false)
            }
            column(name: 'PM_ENABLE', type: "TINYINT UNSIGNED", defaultValue: "1", remarks: '是否发送站内信。1发送，0不发送')
            column(name: 'EMAIL_ENABLE', type: "TINYINT UNSIGNED", defaultValue: "0", remarks: '是否发送邮件。1发送，0不发送')
            column(name: 'SMS_ENABLE', type: 'TINYINT UNSIGNED', remarks: '是否发送短信')
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'NOTIFY_MESSAGE_SETTING', columnNames: 'NOTIFY_TYPE, CODE, PROJECT_ID, EVENT_NAME, ENV_ID', constraintName: 'UK_NOTIFY_MESSAGE_SETTING_U1')

    }

    changeSet(author: 'xiangwang04@gmail.com', id: '2020-07-24-delete-unique') {
        sql("""
               drop INDEX  UK_NOTIFY_MESSAGE_SETTING_U1 on notify_message_setting
         """)
        sql("""
            update notify_message_setting set EVENT_NAME = 'defaultValue' where PROJECT_ID = 0 and EVENT_NAME = '' 
            """)
    }
    changeSet( author: 'xiangwang04@mail.com',id: '2020-08-28-fix-message-data'){
        sql("""
           DELETE FROM 
              notify_message_setting 
            WHERE PROJECT_ID !=0 AND ENV_ID=0 AND EVENT_NAME != 'defaultValue' and NOTIFY_TYPE ='resourceDelete'
        """)
        createIndex(indexName: "UK_NOTIFY_MESSAGE_SETTING_U1", tableName: "NOTIFY_MESSAGE_SETTING") {
            column(name: "NOTIFY_TYPE")
            column(name: 'CODE')
            column(name: "PROJECT_ID")
            column(name: 'EVENT_NAME')
            column(name: "ENV_ID")
        }
    }
}