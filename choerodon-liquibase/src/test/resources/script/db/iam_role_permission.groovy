package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_role_permission.groovy') {
    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-04-03-iam-role-permission') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'IAM_ROLE_PERMISSION_S', startValue:"1")
        }
        createTable(tableName: "IAM_ROLE_PERMISSION") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_ROLE_PERMISSION')
            }
            column(name: 'ROLE_ID', type: 'BIGINT UNSIGNED', remarks: '角色id')
            column(name: 'PERMISSION_ID', type: 'BIGINT UNSIGNED', remarks: '权限id')
        }
        addUniqueConstraint(tableName: 'IAM_ROLE_PERMISSION', columnNames: 'ROLE_ID, PERMISSION_ID', constraintName: 'UK_IAM_ROLE_PERM_U1')
    }
}
