package script.db

databaseChangeLog(logicalFilePath: 'script/db/oauth_ldap_history.groovy') {
    changeSet(author: 'superleader8@gmail.com', id: '2018-06-06-oauth-ldap-history') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'OAUTH_LDAP_HISTORY_S', startValue:"1")
        }
        createTable(tableName: "OAUTH_LDAP_HISTORY") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_OAUTH_LDAP_HISTORY')
            }
            column(name: 'LDAP_ID', type: 'BIGINT UNSIGNED', remarks: 'ldap id') {
                constraints(nullable: false)
            }
            column(name: 'NEW_USER_COUNT', type: "INTEGER UNSIGNED", remarks: '同步用户新增数量')
            column(name: 'UPDATE_USER_COUNT', type: "INTEGER UNSIGNED", remarks: '同步用户更新数量')
            column(name: 'ERROR_USER_COUNT', type: "INTEGER UNSIGNED", remarks: '同步用户失败数量')
            column(name: "SYNC_BEGIN_TIME", type: "DATETIME", remarks: '同步开始时间')
            column(name: "SYNC_END_TIME", type: "DATETIME", remarks: '同步结束时间')

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