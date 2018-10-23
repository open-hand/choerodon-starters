package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_role.groovy') {
    changeSet(author: 'jcalaz@163.com', id: '2018-03-21-iam-role') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'IAM_ROLE_S', startValue:"1")
        }
        createTable(tableName: "IAM_ROLE") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_ROLE')
            }
            column(name: 'NAME', type: 'VARCHAR(64)', remarks: '角色名') {
                constraints(nullable: false)
            }
            column(name: 'CODE', type: 'VARCHAR(128)', remarks: '角色编码') {
                constraints(nullable: false, unique: true, uniqueConstraintName: 'UK_IAM_ROLE_U1')
            }
            column(name: 'DESCRIPTION', type: 'VARCHAR(255)', remarks: '角色描述full description')
            column(name: 'LEVEL', type: 'VARCHAR(32)', remarks: '角色级别') {
                constraints(nullable: false)
            }
            column(name: 'IS_ENABLED', type: 'TINYINT UNSIGNED', defaultValue: "1", remarks: '是否启用。1启用，0未启用') {
                constraints(nullable: false)
            }
            column(name: 'IS_MODIFIED', type: 'TINYINT UNSIGNED', defaultValue: "1", remarks: '是否可以修改。1表示可以，0不可以') {
                constraints(nullable: false)
            }
            column(name: 'IS_ENABLE_FORBIDDEN', type: 'TINYINT UNSIGNED', defaultValue: "1", remarks: '是否可以被禁用') {
                constraints(nullable: false)
            }
            column(name: 'IS_BUILT_IN', type: 'TINYINT UNSIGNED', defaultValue: "0", remarks: '是否内置。1表示是，0表示不是') {
                constraints(nullable: false)
            }
            column(name: 'IS_ASSIGNABLE', type: 'TINYINT UNSIGNED', defaultValue: "1", remarks: '是否禁止在更高的层次上分配，禁止project role在organization上分配。1表示可以，0表示不可以') {
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

        createTable(tableName: "IAM_ROLE_TL") {
            column(name: 'LANG', type: 'VARCHAR(8)', remarks: '语言code') {
                constraints(nullable: false)
            }
            column(name: 'ID', type: 'BIGINT UNSIGNED', remarks: 'role表id') {
                constraints(nullable: false)
            }
            column(name: 'NAME', type: 'VARCHAR(64)', remarks: '多语言字段') {
                constraints(nullable: false)
            }
        }
        addPrimaryKey(tableName: 'IAM_ROLE_TL', columnNames: 'ID, LANG', constraintName: 'PK_IAM_ROLE_TL')
    }

    changeSet(author: 'superleader8@gmail.com', id: '2018-08-27-rename') {
        renameColumn(columnDataType: 'VARCHAR(32)', newColumnName: "FD_LEVEL", oldColumnName: "LEVEL", remarks: '角色级别', tableName: 'IAM_ROLE')
    }
}