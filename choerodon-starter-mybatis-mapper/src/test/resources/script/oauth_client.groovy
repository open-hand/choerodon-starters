package script.db

databaseChangeLog(logicalFilePath: 'script/db/oauth_client.groovy') {
    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-03-23-oauth_client') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'OAUTH_CLIENT_S', startValue:"1")
        }
        createTable(tableName: "OAUTH_CLIENT") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', remarks: '客户端ID', autoIncrement: true) {
                constraints(primaryKey: true, primaryKeyName: 'PK_OAUTH_CLIENT')
            }
            column(name: 'NAME', type: 'VARCHAR(32)', remarks: '客户端名称') {
                constraints(nullable: false)
                constraints(unique: true, uniqueConstraintName: 'UK_OAUTH_CLIENT_U1')
            }
            column(name: 'ORGANIZATION_ID', type: 'BIGINT UNSIGNED', remarks: '组织ID') {
                constraints(nullable: false)
            }
            column(name: 'RESOURCE_IDS', type: 'VARCHAR(32)', defaultValue: "default", remarks: '资源ID列表，目前只使用default')
            column(name: 'SECRET', type: 'VARCHAR(255)', remarks: '客户端秘钥')
            column(name: 'SCOPE', type: 'VARCHAR(32)', defaultValue: "default", remarks: 'Oauth授权范围')
            column(name: 'AUTHORIZED_GRANT_TYPES', type: 'VARCHAR(255)', remarks: '支持的授权类型列表')
            column(name: 'WEB_SERVER_REDIRECT_URI', type: 'VARCHAR(128)', remarks: '授权重定向URL')
            column(name: 'ACCESS_TOKEN_VALIDITY', type: 'BIGINT UNSIGNED', remarks: '客户端特定的AccessToken超时时间')
            column(name: 'REFRESH_TOKEN_VALIDITY', type: 'BIGINT UNSIGNED', remarks: '客户端特定的RefreshToken超时时间')
            column(name: 'ADDITIONAL_INFORMATION', type: 'VARCHAR(1024)', remarks: '客户端附加信息')
            column(name: 'AUTO_APPROVE', type: 'VARCHAR(32)', defaultValue: "default", remarks: '自动授权范围列表')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}