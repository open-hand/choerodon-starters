package script.db

databaseChangeLog(logicalFilePath: 'script/db/oauth_ldap.groovy') {
    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-04-17-oauth-ldap') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'OAUTH_LDAP_S', startValue:"1")
        }
        createTable(tableName: "OAUTH_LDAP") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_OAUTH_LDAP')
            }
            column(name: 'NAME', type: 'VARCHAR(64)', remarks: 'ldap的名称') {
                constraints(nullable: false)
            }
            column(name: 'ORGANIZATION_ID', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: false, unique: true, uniqueConstraintName: 'UK_OAUTH_LDAP_U1')
            }
            column(name: 'SERVER_ADDRESS', type: 'VARCHAR(64)', remarks: 'ldap服务器地址') {
                constraints(nullable: false)
            }
            column(name: 'ENCRYPTION', type: 'VARCHAR(32)', remarks: '加密传输方式，可以为空')
            column(name: 'STATUS', type: 'VARCHAR(8)', remarks: '状态')
            column(name: 'BASE_DN', type: 'VARCHAR(255)', remarks: '基础DN')
            column(name: 'LDAP_ATTRIBUTE_NAME', type: 'VARCHAR(255)', remarks: '认证名')
            column(name: 'DOMAIN', type: 'VARCHAR(64)', remarks: '域名')
            column(name: 'DESCRIPTION', type: 'VARCHAR(255)', remarks: '描述')

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

    changeSet(author: 'superleader8@gmail.com', id: '2018-06-04-oauth-ldap-change-table') {
        dropColumn(tableName: 'OAUTH_LDAP', columnName: 'ENCRYPTION')
        dropColumn(tableName: 'OAUTH_LDAP', columnName: 'STATUS')
        dropColumn(tableName: 'OAUTH_LDAP', columnName: 'DESCRIPTION')
        dropColumn(tableName: 'OAUTH_LDAP', columnName: 'LDAP_ATTRIBUTE_NAME')
        dropColumn(tableName: 'OAUTH_LDAP', columnName: 'DOMAIN')
        addColumn(tableName: 'OAUTH_LDAP') {
            column(name: 'PORT', type: "VARCHAR(8)", defaultValue: "389", remarks: '端口号', afterColumn: 'SERVER_ADDRESS') {
                constraints(nullable: false)
            }
            column(name: 'ACCOUNT', type: "VARCHAR(128)", defaultValue: "", remarks: 'ldap登陆账户', afterColumn: 'PORT') {
                constraints(nullable: false)
            }
            column(name: 'PASSWORD', type: 'VARCHAR(128)', defaultValue: "", remarks: 'ldap登陆密码', afterColumn: 'ACCOUNT') {
                constraints(nullable: false)
            }
            column(name: 'USE_SSL', type: "BIGINT UNSIGNED", defaultValue: "0", remarks: '使用ssl加密传输方式，默认情况为不使用', afterColumn: 'PASSWORD') {
                constraints(nullable: false)
            }
            column(name: 'IS_ENABLED', type: "BIGINT UNSIGNED", defaultValue: "1", remarks: '是否启用，默认为启用', afterColumn: 'USE_SSL') {
                constraints(nullable: false)
            }
            column(name: 'IS_SYNCING', type: "BIGINT UNSIGNED", defaultValue: "0", remarks: '是否正在同步，默认为否', afterColumn: 'IS_ENABLED') {
                constraints(nullable: false)
            }
            column(name: 'DIRECTORY_TYPE', type: "VARCHAR(64)", remarks: '目录类型', afterColumn: 'BASE_DN')
            column(name: 'LOGIN_NAME_FIELD', type: "VARCHAR(64)", remarks: 'login_name对应的字段名', afterColumn: 'DIRECTORY_TYPE')
            column(name: 'REAL_NAME_FIELD', type: "VARCHAR(64)", remarks: 'real_name对应的字段名', afterColumn: 'LOGIN_NAME_FIELD')
            column(name: 'EMAIL_FIELD', type: "VARCHAR(64)", remarks: 'email对应的字段名', afterColumn: 'REAL_NAME_FIELD')
            column(name: 'PASSWORD_FIELD', type: "VARCHAR(64)", remarks: 'password对应的字段名', afterColumn: 'EMAIL_FIELD')
            column(name: 'PHONE_FIELD', type: "VARCHAR(64)", remarks: 'phone对应的字段名', afterColumn: 'PASSWORD_FIELD')
            column(name: 'TOTAL_SYNC_COUNT', type: "INTEGER UNSIGNED", remarks: '累计同步用户数量', afterColumn: 'PHONE_FIELD')
            column(name: "SYNC_BEGIN_TIME", type: "DATETIME", remarks: '同步任务开始的时间', defaultValueComputed: "CURRENT_TIMESTAMP", afterColumn: 'TOTAL_SYNC_COUNT')
        }
    }

    changeSet(author: 'superleader8@gmail.com', id: '2018-06-06-oauth-ldap-add-column') {
        addColumn(tableName: 'OAUTH_LDAP') {
            column(name: 'OBJECT_CLASS', type: "VARCHAR(64)", remarks: '对象类型', afterColumn: 'DIRECTORY_TYPE') {
                constraints(nullable: false)
            }
        }
        dropNotNullConstraint(tableName: 'OAUTH_LDAP', columnName: 'ACCOUNT', columnDataType: 'VARCHAR(128)')
        dropNotNullConstraint(tableName: 'OAUTH_LDAP', columnName: 'PASSWORD', columnDataType: 'VARCHAR(128)')
    }

    changeSet(author: 'superleader8@gmail.com', id: '2018-06-07-oauth-ldap-drop-column') {
        dropColumn(tableName: 'OAUTH_LDAP', ColumnName: 'IS_SYNCING')
        dropColumn(tableName: 'OAUTH_LDAP', ColumnName: 'PASSWORD_FIELD')
        dropColumn(tableName: 'OAUTH_LDAP', ColumnName: 'TOTAL_SYNC_COUNT')
        dropColumn(tableName: 'OAUTH_LDAP', ColumnName: 'SYNC_BEGIN_TIME')
    }

    changeSet(author: 'superleader8@gmail.com', id: '2018-10-12-oauth-ldap-add-notnull') {
        addNotNullConstraint(tableName: 'OAUTH_LDAP', defaultNullValue: 'OpenLDAP', columnName: 'DIRECTORY_TYPE', columnDataType:'VARCHAR(64)')
    }

}