package script.db

databaseChangeLog(logicalFilePath: 'asgard_saga.groovy') {
    changeSet(id: '2018-08-06-add-table-asgard_producer_record', author: 'flyleft') {
        createTable(tableName: "asgard_producer_record") {
            column(name: 'uuid', type: 'CHAR(32)', autoIncrement: false, remarks: 'uuid') {
                constraints(primaryKey: true)
            }
            column(name: 'create_time', type: 'BIGINT UNSIGNED', remarks: '创建时间戳')
        }

    }
}