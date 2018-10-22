package script.db

databaseChangeLog(logicalFilePath: 'oauth_refresh_token.groovy') {
    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-03-26-oauth_refresh_token') {
        createTable(tableName: "OAUTH_REFRESH_TOKEN") {
            column(name: 'TOKEN_ID', type: 'VARCHAR(128)', remarks: 'Refresh Token ID') {
                constraints(primaryKey: true, primaryKeyName: 'TOKEN_ID')
            }
            column(name: 'TOKEN', type: 'BLOB', remarks: 'Token对象')
            column(name: 'AUTHENTICATION', type: 'BLOB', remarks: '授权对象')
        }
    }
}