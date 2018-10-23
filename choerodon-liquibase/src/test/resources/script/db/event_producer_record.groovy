package script.db

databaseChangeLog(logicalFilePath: 'script/db/event_producer_record.groovy') {
    changeSet(id: '2018-05-18-add-table-event-producer-record', author: 'superleader8@gmail.com') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'EVENT_PRODUCER_RECORD_S', startValue:"1")
        }
        createTable(tableName: "EVENT_PRODUCER_RECORD") {
            column(name: 'UUID', type: 'VARCHAR(50)', autoIncrement: false, remarks: 'uuid') {
                constraints(primaryKey: true)
            }
            column(name: 'TYPE', type: 'VARCHAR(50)', remarks: '业务类型') {
                constraints(nullable: false)
            }
            column(name: 'CREATE_TIME', type: 'DATETIME', remarks: '创建时间')
        }
    }
}