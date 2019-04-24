package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_upload_history.groovy') {
    changeSet(author: 'superleader8@gmail.com', id: '2018-08-08-iam-upload-history') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'IAM_UPLOAD_HISTORY_S', startValue:"1")
        }
        createTable(tableName: "IAM_UPLOAD_HISTORY") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_UPLOAD_HISTORY')
            }
            column(name: 'USER_ID', type: 'BIGINT UNSIGNED', remarks: '用户id') {
                constraints(nullable: false)
            }
            column(name: 'URL', type: 'VARCHAR(256)', remarks: '从文件服务下载的url地址')
            column(name: 'TYPE', type: 'VARCHAR(32)', remarks: '上传的类型') {
                constraints(nullable: false)
            }
            column(name: 'SUCCESSFUL_COUNT', type: "INTEGER UNSIGNED", remarks: '导入成功的数量')
            column(name: 'FAILED_COUNT', type: "INTEGER UNSIGNED", remarks: '导入失败的数量')
            column(name: "BEGIN_TIME", type: "DATETIME", remarks: '导入开始时间')
            column(name: "END_TIME", type: "DATETIME", remarks: '导入结束时间')

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
    changeSet(author: 'superleader8@gmail.com', id: '2018-08-08-iam-upload-history-add-column') {
        addColumn(tableName: 'IAM_UPLOAD_HISTORY') {
            column(name: 'FINISHED', type: "TINYINT UNSIGNED", remarks: '生成和上传是否结束', afterColumn: 'TYPE')
        }
    }

    changeSet(author: 'superleader8@gmail.com', id: '2018-08-13-iam-upload-history-add-column') {
        addColumn(tableName: 'IAM_UPLOAD_HISTORY') {
            column(name: 'SOURCE_ID', type: 'BIGINT UNSIGNED', remarks: '创建该记录的源id，可以是projectId,也可以是organizarionId等', afterColumn: 'FINISHED')
            column(name: 'SOURCE_TYPE', type: 'VARCHAR(32)', remarks: '创建该记录的源类型，sit/organization/project', afterColumn: 'SOURCE_ID')
        }
    }
}