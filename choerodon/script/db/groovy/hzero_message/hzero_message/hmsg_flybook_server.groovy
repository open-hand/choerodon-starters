package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_flybook_server.groovy') {
    def weight_c = 1
    if (helper.isSqlServer()) {
        weight_c = 2
    } else if (helper.isOracle()) {
        weight_c = 3
    }
    changeSet(author: "hzero@hand-china.com", id: "hmsg_flybook_server-2020-11-30-version-2") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hmsg_flybook_server_s', startValue: "1")
        }
        createTable(tableName: "hmsg_flybook_server", remarks: "飞书配置") {
            column(name: "server_id", type: "bigint", autoIncrement: true, remarks: "配置Id") { constraints(primaryKey: true) }
            column(name: "server_code", type: "varchar(" + 30 * weight_c + ")", remarks: "配置编码") { constraints(nullable: "false") }
            column(name: "server_name", type: "varchar(" + 60 * weight_c + ")", remarks: "配置名称") { constraints(nullable: "false") }
            column(name: "app_id", type: "varchar(" + 60 * weight_c + ")", remarks: "应用Id") { constraints(nullable: "false") }
            column(name: "app_secret", type: "varchar(" + 120 * weight_c + ")", remarks: "应用秘钥") { constraints(nullable: "false") }
            column(name: "enabled_flag", type: "tinyint", defaultValue: "1", remarks: "启用标识") { constraints(nullable: "false") }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID，hpfm_tenant.tenant_id") { constraints(nullable: "false") }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }
        addUniqueConstraint(columnNames: "server_code,tenant_id", tableName: "hmsg_flybook_server", constraintName: "hmsg_flybook_server_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2021-05-18-hmsg_flybook_server") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hmsg_flybook_server", columnName: 'app_secret', newDataType: "varchar(" + 480 * weight + ")")
    }
}
