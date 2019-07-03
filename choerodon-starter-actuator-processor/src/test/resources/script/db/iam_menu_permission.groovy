package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_menu_permission.groovy') {
    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-04-03-iam-menu-permission') {
        createTable(tableName: "IAM_MENU_PERMISSION") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_MENU_PERMISSION')
            }
            column(name: 'MENU_CODE', type: 'VARCHAR(128)', remarks: '菜单id')
            column(name: 'PERMISSION_CODE', type: 'VARCHAR(128)', remarks: '权限id')
        }
        addUniqueConstraint(tableName: 'IAM_MENU_PERMISSION', columnNames: 'MENU_CODE, PERMISSION_CODE', constraintName: 'UK_IAM_MENU_PERM_U1')
    }
}
