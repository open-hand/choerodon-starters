package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_lookup.groovy') {
    changeSet(author: 'superleader8@gmail.com', id: '2018-03-19-fd-lookup') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'FD_LOOKUP_S', startValue:"1")
        }
        createTable(tableName: 'FD_LOOKUP') {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_LOOKUP')
            }
            column(name: 'CODE', type: 'VARCHAR(32)', remarks: '代码') {
                constraints(nullable: false)
            }
            column(name: 'DESCRIPTION', type: 'VARCHAR(128)', remarks: '描述')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        createTable(tableName: 'FD_LOOKUP_TL') {
            column(name: 'ID', type: 'BIGINT UNSIGNED', remarks: '关联lookup id') {
                constraints(nullable: false)
            }
            column(name: 'LANG', type: 'VARCHAR(16)', remarks: '语言名称') {
                constraints(nullable: false)
            }
            column(name: 'DESCRIPTION', type: 'VARCHAR(255)', remarks: '描述')

        }
        addUniqueConstraint(tableName: 'FD_LOOKUP', columnNames: 'CODE', constraintName: 'UK_FD_LOOKUP_U1')
        addPrimaryKey(tableName: 'FD_LOOKUP_TL', columnNames: 'ID, LANG', constraintName: 'PK_FD_LOOKUP_TL')
    }
}