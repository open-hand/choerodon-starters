package script.db

databaseChangeLog(logicalFilePath: 'script/db/z_choerodon_message.groovy') {
    changeSet(author: 'scp', id: '2022-01-24-add-index') {
        createIndex(indexName: "CHOERODON_HMSG_MESSAGE_N4", tableName: "HMSG_MESSAGE") {
            column(name: "TEMPLATE_CODE")
        }
        createIndex(indexName: "CHOERODON_HMSG_MESSAGE_TRANSACTION_N3", tableName: "HMSG_MESSAGE_TRANSACTION") {
            column(name: "TRX_STATUS_CODE")
        }
        createIndex(indexName: "CHOERODON_HMSG_TEMPLATE_SERVER_LINE_U2", tableName: "HMSG_TEMPLATE_SERVER_LINE") {
            column(name: "TEMPLATE_CODE")
        }
    }
}