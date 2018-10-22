package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_menu_permission.groovy') {
    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-04-03-iam-menu-permission') {
        createTable(tableName: "IAM_MENU_PERMISSION") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_MENU_PERMISSION')
            }
            column(name: 'MENU_ID', type: 'BIGINT UNSIGNED', remarks: '菜单id')
            column(name: 'PERMISSION_ID', type: 'BIGINT UNSIGNED', remarks: '权限id')
        }
        addUniqueConstraint(tableName: 'IAM_MENU_PERMISSION', columnNames: 'MENU_ID, PERMISSION_ID', constraintName: 'UK_IAM_MENU_PERM_U1')
    }

    changeSet(author: 'jcalaz@163.com', id: '2018-06-21-iam-menu-permission-code') {
        dropTable(tableName: 'IAM_MENU_PERMISSION')
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'IAM_MENU_PERMISSION_S', startValue:"1")
        }
        createTable(tableName: "IAM_MENU_PERMISSION") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_MENU_PERMISSION')
            }
            column(name: 'MENU_ID', type: 'BIGINT UNSIGNED', remarks: '菜单id')
            column(name: 'PERMISSION_CODE', type: 'VARCHAR(128)', remarks: '权限的标识')
        }
        addUniqueConstraint(tableName: 'IAM_MENU_PERMISSION', columnNames: 'MENU_ID, PERMISSION_CODE', constraintName: 'UK_IAM_MENU_PERM_U1')
    }
}
