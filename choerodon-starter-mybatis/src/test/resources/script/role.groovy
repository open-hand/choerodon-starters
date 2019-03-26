package script

databaseChangeLog(logicalFilePath: 'script/role.groovy') {
    changeSet(author: "hailor", id: "20160609-hailor-17") {
        // sqlFile(path: helper.dataPath("script/db/data/"+dbType+"/tables/sys_role.sql"), encoding: "UTF-8")
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_ROLE_B_S', startValue: "10001")
        }
        createTable(tableName: "SYS_ROLE_B") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "ROLE_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_ROLE_B_PK")
                }
            } else {
                column(name: "ROLE_ID", type: "bigint", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_ROLE_B_PK")
                }
            }
            if (!helper.isPostgresql()) {
                column(name: "ROLE_CODE", type: "varchar(40)", remarks: "角色编码") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "SYS_ROLE_B_U1")
                }
            } else {
                column(name: "ROLE_CODE", type: "varchar(40)", remarks: "角色编码") {
                    constraints(nullable: "false")
                }
            }

            column(name: "ROLE_NAME", type: "varchar(150)", remarks: "角色名称") {
                constraints(nullable: "false")
            }
            column(name: "ROLE_DESCRIPTION", type: "varchar(240)", remarks: "角色描述")
            column(name: "START_ACTIVE_DATE", type: "DATE", remarks: "开始生效日期")
            column(name: "END_ACTIVE_DATE", type: "DATE", remarks: "截至生效日期")
            column(name: "ENABLE_FLAG", type: "VARCHAR(1)", remarks: "启用标记", defaultValue: "Y")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "ATTRIBUTE_CATEGORY", type: "varchar(30)")
            column(name: "ATTRIBUTE1", type: "varchar(240)")
            column(name: "ATTRIBUTE2", type: "varchar(240)")
            column(name: "ATTRIBUTE3", type: "varchar(240)")
            column(name: "ATTRIBUTE4", type: "varchar(240)")
            column(name: "ATTRIBUTE5", type: "varchar(240)")
            column(name: "ATTRIBUTE6", type: "varchar(240)")
            column(name: "ATTRIBUTE7", type: "varchar(240)")
            column(name: "ATTRIBUTE8", type: "varchar(240)")
            column(name: "ATTRIBUTE9", type: "varchar(240)")
            column(name: "ATTRIBUTE10", type: "varchar(240)")
            column(name: "ATTRIBUTE11", type: "varchar(240)")
            column(name: "ATTRIBUTE12", type: "varchar(240)")
            column(name: "ATTRIBUTE13", type: "varchar(240)")
            column(name: "ATTRIBUTE14", type: "varchar(240)")
            column(name: "ATTRIBUTE15", type: "varchar(240)")
        }
        if (helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "ROLE_ID,ROLE_CODE", tableName: "SYS_ROLE_B", constraintName: "SYS_ROLE_B_U1")
        }

        createTable(tableName: "SYS_ROLE_TL") {
            column(name: "ROLE_ID", type: "bigint", remarks: "角色ID") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "LANG", type: "varchar(10)", remarks: "语言") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "ROLE_NAME", type: "varchar(150)", remarks: "角色名称")
            column(name: "ROLE_DESCRIPTION", type: "varchar(240)", remarks: "角色描述")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "ATTRIBUTE_CATEGORY", type: "varchar(30)")
            column(name: "ATTRIBUTE1", type: "varchar(240)")
            column(name: "ATTRIBUTE2", type: "varchar(240)")
            column(name: "ATTRIBUTE3", type: "varchar(240)")
            column(name: "ATTRIBUTE4", type: "varchar(240)")
            column(name: "ATTRIBUTE5", type: "varchar(240)")
            column(name: "ATTRIBUTE6", type: "varchar(240)")
            column(name: "ATTRIBUTE7", type: "varchar(240)")
            column(name: "ATTRIBUTE8", type: "varchar(240)")
            column(name: "ATTRIBUTE9", type: "varchar(240)")
            column(name: "ATTRIBUTE10", type: "varchar(240)")
            column(name: "ATTRIBUTE11", type: "varchar(240)")
            column(name: "ATTRIBUTE12", type: "varchar(240)")
            column(name: "ATTRIBUTE13", type: "varchar(240)")
            column(name: "ATTRIBUTE14", type: "varchar(240)")
            column(name: "ATTRIBUTE15", type: "varchar(240)")
        }
    }
}