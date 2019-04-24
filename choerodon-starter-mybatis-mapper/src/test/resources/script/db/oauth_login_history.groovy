package script.db

databaseChangeLog(logicalFilePath: 'script/db/oauth_login_history.groovy') {
    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-04-11-oauth-login-history') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'OAUTH_LOGIN_HISTORY_S', startValue:"1")
        }
        createTable(tableName: "OAUTH_LOGIN_HISTORY") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_OAUTH_LOGIN_HISTORY')
            }
            column(name: 'USER_ID', type: 'BIGINT UNSIGNED', remarks: '用户id') {
                constraints(nullable: false, unique: true, uniqueConstraintName: 'UK_OAUTH_LOGIN_HISTORY_U1')
            }

            column(name: 'LAST_LOGIN_AT', type: 'DATETIME', remarks: '用户最后一次登录的时间') {
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
    }
}