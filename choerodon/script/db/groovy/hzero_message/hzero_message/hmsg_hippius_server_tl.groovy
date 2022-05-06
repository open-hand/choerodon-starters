package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_hippius_server_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-11-04-hmsg_hippius_server_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hmsg_hippius_server_tl_s', startValue: "1")
        }
        createTable(tableName: "hmsg_hippius_server_tl", remarks: "") {
            column(name: "server_id", type: "bigint", remarks: "海马汇消息配置ID") { constraints(nullable: "false") }
            column(name: "lang", type: "varchar(" + 30 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "server_name", type: "varchar(" + 240 * weight + ")", remarks: "配置名称") { constraints(nullable: "false") }
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
        }

        addUniqueConstraint(columnNames: "server_id,lang", tableName: "hmsg_hippius_server_tl", constraintName: "hmsg_hippius_server_tl_u1")
    }
}