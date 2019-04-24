package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_role_label.groovy') {
    changeSet(author: 'jcalaz@163.com', id: '2018-04-13-iam-role-label') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'IAM_ROLE_LABEL_S', startValue:"1")
        }
        createTable(tableName: "IAM_ROLE_LABEL") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_ROLE_LABEL')
            }
            column(name: 'ROLE_ID', type: 'BIGINT UNSIGNED', remarks: '角色的id') {
                constraints(nullable: false)
            }
            column(name: 'LABEL_ID', type: 'BIGINT UNSIGNED', remarks: 'label的id') {
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
        addUniqueConstraint(tableName: 'IAM_ROLE_LABEL', columnNames: 'ROLE_ID, LABEL_ID', constraintName: 'UK_IAM_ROLE_LABEL_U1')
    }
}