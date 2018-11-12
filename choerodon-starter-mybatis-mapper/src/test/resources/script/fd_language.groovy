package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_language.groovy') {
    changeSet(author: 'superleader8@gmail.com', id: '2018-03-19-fd-language') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'FD_LANGUAGE_S', startValue:"1")
        }
        createTable(tableName: 'FD_LANGUAGE') {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_LANGUAGE')
            }
            column(name: "CODE", type: 'VARCHAR(32)', remarks: '语言Code') {
                constraints(unique: true, uniqueConstraintName: 'UK_FD_LANGUAGE_U1')
                constraints(nullable: false)
            }
            column(name: 'NAME', type: 'VARCHAR(32)', remarks: '语言名称') {
                constraints(unique: true, uniqueConstraintName: 'UK_FD_LANGUAGE_U2')
                constraints(nullable: false)
            }
            column(name: "DESCRIPTION", type: 'VARCHAR(128)', remarks: '描述')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        createTable(tableName: 'FD_LANGUAGE_TL') {
            column(name: "ID", type: 'BIGINT UNSIGNED', remarks: 'fd_language id') {
                constraints(nullable: false)
            }
            column(name: 'LANG', type: 'VARCHAR(16)', remarks: '语言名称') {
                constraints(nullable: false)
            }
            column(name: "DESCRIPTION", type: 'VARCHAR(255)', remarks: '描述')

        }
        addPrimaryKey(tableName: 'FD_LANGUAGE_TL', columnNames: 'id, lang', constraintName: 'PK_FD_LANGUAGE_TL')
    }
}