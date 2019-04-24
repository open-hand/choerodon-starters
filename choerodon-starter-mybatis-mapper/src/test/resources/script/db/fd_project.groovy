package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_project.groovy') {
    changeSet(author: 'jcalaz@163.com', id: '2018-03-21-fd-project') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'FD_PROJECT_S', startValue:"1")
        }
        createTable(tableName: "FD_PROJECT") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_PROJECT')
            }
            column(name: 'NAME', type: 'VARCHAR(32)', remarks: '项目名') {
                constraints(nullable: false)
            }
            column(name: 'CODE', type: 'VARCHAR(14)', remarks: '项目code') {
                constraints(nullable: false, unique: true, uniqueConstraintName: 'UK_FD_PROJECT_U1')
            }
            column(name: 'ORGANIZATION_ID', type: 'BIGINT UNSIGNED', remarks: '组织ID')
            column(name: 'IS_ENABLED', type: 'TINYINT UNSIGNED', defaultValue: "1", remarks: '是否启用。1启用，0未启用') {
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
        addUniqueConstraint(tableName: 'FD_PROJECT', columnNames: 'CODE, ORGANIZATION_ID', constraintName: 'UK_FD_PROJECT_U2')
    }

    changeSet(author: 'superleader8@gmail.com', id: '2018-05-24-drop-unique') {
//        if (helper.isMysql()) {
            dropUniqueConstraint(constraintName: "UK_FD_PROJECT_U1", tableName: "FD_PROJECT")
//        }
    }
}