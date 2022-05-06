package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_flybook_server_tl.groovy') {
    def weight_c = 1
    if (helper.isSqlServer()) {
        weight_c = 2
    } else if (helper.isOracle()) {
        weight_c = 3
    }
    changeSet(author: "hzero@hand-china.com", id: "hmsg_flybook_server_tl-2020-11-30-version-2") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hmsg_flybook_server_tl_s', startValue: "1")
        }
        createTable(tableName: "hmsg_flybook_server_tl", remarks: "飞书配置多语言") {
            column(name: "server_id", type: "bigint", remarks: "配置Id") { constraints(nullable: "false") }
            column(name: "server_name", type: "varchar(" + 60 * weight_c + ")", remarks: "配置名称")
            column(name: "lang", type: "varchar(" + 16 * weight_c + ")", remarks: "语言")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户Id") { constraints(nullable: "false") }
        }
        addUniqueConstraint(columnNames: "server_id,lang", tableName: "hmsg_flybook_server_tl", constraintName: "hmsg_flybook_server_tl_u1")
    }
}
