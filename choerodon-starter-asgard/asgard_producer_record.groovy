package script.db

databaseChangeLog(logicalFilePath: 'asgard_saga.groovy') {
    changeSet(id: '2018-08-06-add-table-asgard_producer_record', author: 'flyleft') {
        createTable(tableName: "asgard_producer_record") {
            column(name: 'uuid', type: 'CHAR(32)', autoIncrement: false, remarks: 'uuid') {
                constraints(primaryKey: true)
            }
            column(name: 'payload', type: 'TEXT', remarks: '创建saga的输入json') {
                constraints(nullable: false)
            }
            column(name: 'REF_TYPE', type: 'VARCHAR(128)', remarks: '关联类型') {
                constraints(nullable: false)
            }
            column(name: 'REF_ID', type: 'TEXT', remarks: '关联id') {
                constraints(nullable: false)
            }
            column(name: 'create_time', type: 'BIGINT UNSIGNED', remarks: '创建时间戳')
        }

    }
}