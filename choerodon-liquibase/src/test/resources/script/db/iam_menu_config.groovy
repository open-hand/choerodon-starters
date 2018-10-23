package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_menu_config.groovy') {
    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-04-03-iam-menu-config') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'IAM_MENU_CONFIG_S', startValue:"1")
        }
        createTable(tableName: "IAM_MENU_CONFIG") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_MENU_CONFIG')
            }
            column(name: 'MENU_ID', type: 'BIGINT UNSIGNED', remarks: '菜单id') {
                constraints(nullable: true)
            }
            column(name: 'DOMAIN', type: 'VARCHAR(128)', remarks: '域名')
            column(name: 'DEVOPS_SERVICE_GROUP', type: 'VARCHAR(128)', remarks: '服务组')
            column(name: 'DEVOPS_SERVICE_TYPE', type: 'VARCHAR(128)', remarks: '服务类型')
            column(name: 'DEVOPS_SERVICE_CODE', type: 'VARCHAR(128)', remarks: '服务代码')

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
