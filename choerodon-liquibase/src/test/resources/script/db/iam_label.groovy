package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_label.groovy') {
    changeSet(author: 'jcalaz@163.com', id: '2018-04-13-iam-label') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'IAM_LABEL_S', startValue: "1")
        }
        createTable(tableName: "IAM_LABEL") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_LABEL')
            }
            column(name: 'NAME', type: 'VARCHAR(64)', remarks: '名称') {
                constraints(nullable: false)
            }
            column(name: 'TYPE', type: 'VARCHAR(32)', remarks: '类型') {
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
        addUniqueConstraint(tableName: 'IAM_LABEL', columnNames: 'NAME, TYPE', constraintName: 'UK_IAM_LABEL_U1')
    }

    changeSet(author: 'superleader8@gmail.com', id: '2018-07-23-iam-label-add-column') {
        addColumn(tableName: 'IAM_LABEL') {
            column(name: 'LEVEL', type: "VARCHAR(32)", remarks: '层级', afterColumn: 'TYPE') {
                constraints(nullable: false)
            }
            column(name: 'DESCRIPTION', type: "VARCHAR(128)", remarks: '描述', afterColumn: 'LEVEL')
        }
    }

    changeSet(author: 'superleader8@gmail.com', id: '2018-08-22-rename') {
        renameColumn(columnDataType: 'VARCHAR(32)', newColumnName: "FD_LEVEL", oldColumnName: "LEVEL", remarks: '层级', tableName: 'IAM_LABEL')
    }
}