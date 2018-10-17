package script.db

databaseChangeLog(logicalFilePath: 'script/db/oauth_password_policy.groovy') {
    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-04-11-oauth-password-policy') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'oauth_password_policy_s', startValue:"1")
        }
        createTable(tableName: "OAUTH_PASSWORD_POLICY") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_OAUTH_PASS_POLICY')
            }
            column(name: 'CODE', type: 'VARCHAR(64)', remarks: '密码策略标识') {
                constraints(nullable: false, unique: true, uniqueConstraintName: 'UK_OAUTH_PASS_POLICY_U1')
            }

            column(name: 'NAME', type: 'VARCHAR(64)', remarks: '密码策略名') {
                constraints(nullable: false)
            }
            column(name: 'ORGANIZATION_ID', type: 'BIGINT UNSIGNED', remarks: '所属的组织id') {
                constraints(nullable: false, unique: true, uniqueConstraintName: 'UK_OAUTH_PASS_POLICY_U2')
            }
            column(name: 'ORIGINAL_PASSWORD', type: 'VARCHAR(64)', remarks: '新建用户初始密码')
            column(name: 'MIN_LENGTH', type: 'INT', remarks: '密码最小长度')
            column(name: 'MAX_LENGTH', type: 'INT', remarks: '密码最大长度')
            column(name: 'MAX_ERROR_TIME', type: 'INT', remarks: '密码输入最大错误次数')
            column(name: 'DIGITS_COUNT', type: 'INT', remarks: '密码数字的数量')
            column(name: 'LOWERCASE_COUNT', type: 'INT', remarks: '密码小写字母数量')
            column(name: 'UPPERCASE_COUNT', type: 'INT', remarks: '密码大写字母数量')
            column(name: 'SPECIAL_CHAR_COUNT', type: 'INT', remarks: '密码特殊字符数量')
            column(name: 'NOT_USERNAME', type: 'TINYINT UNSIGNED', defaultValue: '0', remarks: '密码可否和与用户名相同') {
                constraints(nullable: false)
            }
            column(name: 'REGULAR_EXPRESSION', type: 'VARCHAR(128)', remarks: '密码匹配的正则表达式')
            column(name: 'NOT_RECENT_COUNT', type: 'INT', remarks: '是否可以修改为最近使用过的密码')
            column(name: 'ENABLE_PASSWORD', type: 'TINYINT UNSIGNED', defaultValue: '0', remarks: '开启密码策略') {
                constraints(nullable: false)
            }
            column(name: 'ENABLE_SECURITY', type: 'TINYINT UNSIGNED', remarks: '开启登录安全策略', defaultValue: "0") {
                constraints(nullable: false)
            }
            column(name: 'ENABLE_LOCK', type: 'TINYINT UNSIGNED', defaultValue: '0', remarks: '是否锁定') {
                constraints(nullable: false)
            }
            column(name: 'LOCKED_EXPIRE_TIME', type: 'INT', defaultValue: '0', remarks: '锁定时长(s)') {
                constraints(nullable: false)
            }
            column(name: 'ENABLE_CAPTCHA', type: 'TINYINT UNSIGNED', defaultValue: '0', remarks: '启用验证码') {
                constraints(nullable: false)
            }
            column(name: 'MAX_CHECK_CAPTCHA', type: 'INT', defaultValue: '0', remarks: '密码错误多少次需要验证码') {
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