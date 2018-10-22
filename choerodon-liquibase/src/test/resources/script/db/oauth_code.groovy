package script.db

databaseChangeLog(logicalFilePath: 'oauth_code.groovy') {
    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-03-26-oauth_code') {
        createTable(tableName: "OAUTH_CODE") {
            column(name: 'CODE', type: 'VARCHAR(32)', remarks: 'Code') {
                constraints(primaryKey: true, primaryKeyName: 'PK_OAUTH_CODE')
            }
            column(name: 'AUTHENTICATION', type: 'BLOB', remarks: '授权对象')
        }
    }
}