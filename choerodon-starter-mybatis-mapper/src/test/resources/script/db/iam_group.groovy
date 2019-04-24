package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_group.groovy') {
    changeSet(author: 'jcalaz@163.com', id: '2018-03-21-iam-group') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'IAM_GROUP_S', startValue:"1")
        }
        createTable(tableName: "IAM_GROUP") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_GROUP')
            }
            column(name: 'NAME', type: 'VARCHAR(32)', remarks: '组名称') {
                constraints(nullable: false, unique: true, uniqueConstraintName: 'UK_IAM_GROUP_U1')
            }
            column(name: 'CODE', type: 'VARCHAR(32)', remarks: '组code') {
                constraints(nullable: false, unique: true, uniqueConstraintName: 'UK_IAM_GROUP_U2')
            }
            column(name: 'DESCRIPTION', type: 'VARCHAR(128)', remarks: '组描述') {
                constraints(nullable: false)
            }
            column(name: 'ORGANIZATION_ID', type: 'BIGINT UNSIGNED', remarks: '组织id') {
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