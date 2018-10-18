package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_system_setting.groovy') {
    changeSet(author: 'zmfblue@qq.com', id: '2018-03-21-iam-system-setting') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'IAM_SYSTEM_SETTING_S', startValue: "1")
        }
        createTable(tableName: "IAM_SYSTEM_SETTING") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_SYSTEM_SETTING')
            }
            column(name: 'FAVICON', type: 'VARCHAR(255)', remarks: '平台徽标链接') {
                constraints(nullable: false)
            }
            column(name: 'SYSTEM_LOGO', type: 'VARCHAR(255)', remarks: '平台导航栏图形标链接')
            column(name: 'SYSTEM_NAME', type: 'VARCHAR(100)', remarks: '平台简称') {
                constraints(nullable: false)
            }
            column(name: 'SYSTEM_TITLE', type: 'VARCHAR(255)', remarks: '平台全称')
            column(name: 'DEFAULT_PASSWORD', type: 'VARCHAR(50)', remarks: '平台默认密码') {
                constraints(nullable: false)
            }
            column(name: 'DEFAULT_LANGUAGE', type: 'VARCHAR(50)', remarks: '平台默认语言') {
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