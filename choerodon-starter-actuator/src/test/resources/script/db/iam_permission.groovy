package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_permission.groovy') {
    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-04-02-iam-permission') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'IAM_PERMISSION_S', startValue: "1")
        }
        createTable(tableName: "IAM_PERMISSION") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_PERMISSION')
            }
            column(name: 'CODE', type: 'VARCHAR(128)', remarks: '权限的标识') {
                constraints(nullable: false, unique: true, uniqueConstraintName: 'UK_IAM_PERMISSION_U1')
            }

            column(name: 'PATH', type: 'VARCHAR(128)', remarks: '权限对应的api路径') {
                constraints(nullable: false)
            }

            column(name: 'METHOD', type: 'VARCHAR(64)', remarks: '请求的http方法') {
                constraints(nullable: false)
            }

            column(name: 'LEVEL', type: 'VARCHAR(64)', remarks: '权限的层级') {
                constraints(nullable: false)
            }

            column(name: 'DESCRIPTION', type: 'VARCHAR(1024)', remarks: '权限描述')

            column(name: 'ACTION', type: 'VARCHAR(64)', remarks: '权限对应的方法名') {
                constraints(nullable: false)
            }

            column(name: 'RESOURCE', type: 'VARCHAR(128)', remarks: '权限资源类型') {
                constraints(nullable: false)
            }

            column(name: 'PUBLIC_ACCESS', type: 'TINYINT UNSIGNED', remarks: '是否公开的权限') {
                constraints(nullable: false)
            }

            column(name: 'LOGIN_ACCESS', type: 'TINYINT UNSIGNED', remarks: '是否需要登录才能访问的权限') {
                constraints(nullable: false)
            }

            column(name: 'SERVICE_NAME', type: 'VARCHAR(128)', remarks: '权限所在的服务名称') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1") {
                constraints(nullable: true)
            }
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0") {
                constraints(nullable: true)
            }
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0") {
                constraints(nullable: true)
            }
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'IAM_PERMISSION', columnNames: 'ACTION,RESOURCE,SERVICE_NAME', constraintName: 'UK_IAM_PERMISSION_U2')
        addUniqueConstraint(tableName: 'IAM_PERMISSION', columnNames: 'PATH,LEVEL,SERVICE_NAME,METHOD,CODE', constraintName: 'UK_IAM_PERMISSION_U3')
    }

    changeSet(author: 'superleader8@gmail.com', id: '2018-08-27-rename') {
        renameColumn(columnDataType: 'VARCHAR(64)', newColumnName: "FD_LEVEL", oldColumnName: "LEVEL", remarks: '权限的层级', tableName: 'IAM_PERMISSION')
        renameColumn(columnDataType: 'VARCHAR(128)', newColumnName: "FD_RESOURCE", oldColumnName: "RESOURCE", remarks: '权限资源类型', tableName: 'IAM_PERMISSION')
    }

    changeSet(author: 'longhe1996@icloud.com', id: '2018-09-04-add-column-within') {
        addColumn(tableName: 'IAM_PERMISSION') {
            column(name: 'WITHIN', type: 'TINYINT UNSIGNED', defaultValue: "0", remarks: '是否为内部接口') {
                constraints(nullable: true)
            }
        }
    }

    changeSet(author: 'superlee', id: '2019-03-20-rename-within-column') {
        renameColumn(columnDataType: 'TINYINT UNSIGNED', newColumnName: "IS_WITHIN", oldColumnName: "WITHIN", remarks: '是否为内部接口', tableName: 'IAM_PERMISSION')
    }

    changeSet(author: 'superlee', id: '2019-04-16-upgrade-permission') {
        renameColumn(columnDataType: 'VARCHAR(64)', newColumnName: "RESOURCE_LEVEL", oldColumnName: "FD_LEVEL", remarks: '权限的层级', tableName: 'IAM_PERMISSION')
        renameColumn(columnDataType: 'VARCHAR(128)', newColumnName: "CONTROLLER", oldColumnName: "FD_RESOURCE", remarks: 'controller名', tableName: 'IAM_PERMISSION')
        renameColumn(columnDataType: 'VARCHAR(128)', newColumnName: "SERVICE_CODE", oldColumnName: "SERVICE_NAME", remarks: '权限所在的服务名称', tableName: 'IAM_PERMISSION')
        renameColumn(columnDataType: 'TINYINT UNSIGNED', newColumnName: "IS_PUBLIC_ACCESS", oldColumnName: "PUBLIC_ACCESS", remarks: '是否公开的权限', tableName: 'IAM_PERMISSION')
        renameColumn(columnDataType: 'TINYINT UNSIGNED', newColumnName: "IS_LOGIN_ACCESS", oldColumnName: "LOGIN_ACCESS", remarks: '是否需要登录才能访问的权限', tableName: 'IAM_PERMISSION')
        addColumn(tableName: 'IAM_PERMISSION') {
            column(name: 'PERMISSION_TYPE', type: 'VARCHAR(128)', remarks: '类型包括url/api/page等', afterColumn:'SERVICE_CODE',defaultValue: 'api')
        }
    }
}