package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_member_role.groovy') {
    changeSet(author: 'superleader8@gmail.com', id: '2018-03-27-iam-member-role') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'IAM_MEMBER_ROLE_S', startValue:"1")
        }
        createTable(tableName: "IAM_MEMBER_ROLE") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_MEMBER_ROLE')
            }
            column(name: 'ROLE_ID', type: 'BIGINT UNSIGNED', remarks: '角色id') {
                constraints(nullable: false)
            }
            column(name: 'MEMBER_ID', type: 'BIGINT UNSIGNED', remarks: '成员id,可以是userId,clientId等，与member_type对应') {
                constraints(nullable: false)
            }
            column(name: 'MEMBER_TYPE', type: 'VARCHAR(32)', defaultValue: "user", remarks: '成员类型，默认为user')

            column(name: 'SOURCE_ID', type: 'BIGINT UNSIGNED', remarks: '创建该记录的源id，可以是projectId,也可以是organizarionId等') {
                constraints(nullable: false)
            }
            column(name: 'SOURCE_TYPE', type: 'VARCHAR(32)', remarks: '创建该记录的源类型，sit/organization/project/user等') {
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
        addUniqueConstraint(tableName: 'IAM_MEMBER_ROLE', columnNames: 'ROLE_ID, MEMBER_ID, MEMBER_TYPE, SOURCE_ID, SOURCE_TYPE', constraintName: 'UK_IAM_MEMBER_ROLE_U1')
    }
}