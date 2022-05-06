package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_hippius_server.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-11-04-hmsg_hippius_server") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hmsg_hippius_server_s', startValue: "1")
        }
        createTable(tableName: "hmsg_hippius_server", remarks: "海马汇消息配置") {
            column(name: "server_id", type: "bigint", autoIncrement: true, remarks: "") { constraints(primaryKey: true) }
            column(name: "server_code", type: "varchar(" + 30 * weight + ")", remarks: "配置编码") { constraints(nullable: "false") }
            column(name: "server_name", type: "varchar(" + 240 * weight + ")", remarks: "配置名称") { constraints(nullable: "false") }
            column(name: "channel", type: "varchar(" + 60 * weight + ")", remarks: "频道") { constraints(nullable: "false") }
            column(name: "sub_app", type: "varchar(" + 60 * weight + ")", remarks: "子应用")
            column(name: "enabled_flag", type: "tinyint", defaultValue: "1", remarks: "启用标识") { constraints(nullable: "false") }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }

        addUniqueConstraint(columnNames: "server_code,tenant_id", tableName: "hmsg_hippius_server", constraintName: "hmsg_hippius_server_u1")
    }
}