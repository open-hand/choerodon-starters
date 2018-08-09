package db

databaseChangeLog(logicalFilePath:'saga_task_instance_record.groovy') {
    changeSet(id: '2018-08-06-add-table-saga_task_instance_record', author: 'flyleft') {
        createTable(tableName: "saga_task_instance_record") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: false, remarks: '消息id') {
                constraints(primaryKey: true)
            }
            column(name: 'create_time', type: 'BIGINT UNSIGNED', remarks: '创建时间戳')
        }
    }
}