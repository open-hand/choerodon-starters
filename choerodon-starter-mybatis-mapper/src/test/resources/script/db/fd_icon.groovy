package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_icon.groovy') {
    changeSet(author: 'superleader8@gmail.com', id: '2018-03-21-fd-icon') {
        createTable(tableName: "FD_ICON") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_ICON')
            }
            column(name: 'CODE', type: 'VARCHAR(64)', remarks: '图标code') {
                constraints(unique: true, uniqueConstraintName: 'UK_FD_ICON_U1')
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: 'superleader8@gmail.com', id: '2018-06-20-fd-icon-drop') {
        dropTable(tableName: "FD_ICON")
    }
}