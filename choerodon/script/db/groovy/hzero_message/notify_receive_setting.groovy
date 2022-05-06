package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify_receive_setting.groovy') {
    changeSet(author: 'youquandeng1@gmail.com', id: '2018-11-07-add-notify_receive_setting') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'NOTIFY_RECEIVE_SETTING_S', startValue:"1")
        }
        createTable(tableName: "NOTIFY_RECEIVE_SETTING") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_NOTIFY_RECEIVE_SETTING')
            }
            column(name: 'SEND_SETTING_ID', type: 'BIGINT UNSIGNED', remarks: '发送设置id') {
                constraints(nullable: false)
            }
            column(name: 'MESSAGE_TYPE', type: 'VARCHAR(16)', remarks: '模版类型:email,sms,web') {
                constraints(nullable: false)
            }
            column(name: 'SOURCE_ID', type: 'BIGINT UNSIGNED', remarks: '创建该记录的源id，可以是projectId,也可以是organizarionId等')
            column(name: 'SOURCE_TYPE', type: 'VARCHAR(32)', remarks: '创建该记录的源类型，sit/organization/project')
            column(name: 'IS_DISABLED', type: 'TINYINT UNSIGNED', defaultValue: "1", remarks: '用户是否禁用配置。1禁用，0未禁用')
            column(name: 'USER_ID', type: 'BIGINT UNSIGNED', remarks: 'user id') {
                constraints(nullable: true)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: 'longhe1996@icloud.com', id: '2019-10-22-notify_receive_setting-mpdify-column') {
        setTableRemarks(tableName: "NOTIFY_RECEIVE_SETTING", remarks: "此表用于存储用户的接收设置(记录用户拒绝接受的消息)")
        addColumn(tableName: 'NOTIFY_RECEIVE_SETTING') {
            column(name: 'SENDING_TYPE', type: 'VARCHAR(16)',defaultValue: "EMPTY STRING", remarks: '模版类型:email,sms,web,webhook') {
                constraints(nullable: false)
            }
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "UPDATE NOTIFY_RECEIVE_SETTING SET SENDING_TYPE=MESSAGE_TYPE"
        }
        dropColumn(tableName: 'NOTIFY_RECEIVE_SETTING', columnName: 'MESSAGE_TYPE')
        dropColumn(tableName: 'NOTIFY_RECEIVE_SETTING', columnName: 'IS_DISABLED')
    }

    changeSet(author: 'wangxiang@hand.com', id: '2020-09-03-fixSendingType'){
        sql("""
             UPDATE notify_receive_setting nrs 
             SET nrs.SENDING_TYPE = 'WEB' 
             WHERE
             nrs.SENDING_TYPE = 'pm'
        """)
    }

    changeSet(author: 'lihao', id: '2020-09-03-fixSendingType2') {
        sql("""
            UPDATE notify_receive_setting nrs 
             SET nrs.SENDING_TYPE = 'EMAIL' 
             WHERE
             nrs.SENDING_TYPE = 'email'
        """)
    }
}