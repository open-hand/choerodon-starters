package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_menu.groovy') {
    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-03-29-iam-menu') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'IAM_MENU_S', startValue:"1")
        }
        createTable(tableName: "IAM_MENU") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_MENU')
            }
            column(name: 'CODE', type: 'VARCHAR(128)', remarks: '菜单的标识') {
                constraints(nullable: false)
            }

            column(name: 'NAME', type: 'VARCHAR(128)', remarks: '菜单名') {
                constraints(nullable: false)
            }
            column(name: 'LEVEL', type: 'VARCHAR(64)', remarks: '菜单层级') {
                constraints(nullable: false)
            }
            column(name: 'PARENT_ID', type: 'BIGINT UNSIGNED', remarks: '父级菜单id') {
                constraints(nullable: false)
            }
            column(name: 'TYPE', type: 'VARCHAR(64)', remarks: '菜单类型， 包括三种（root, dir, menu）') {
                constraints(nullable: false)
            }
            column(name: 'SORT', type: 'BIGINT UNSIGNED', remarks: '菜单顺序')
            column(name: 'IS_DEFAULT', type: 'TINYINT UNSIGNED', defaultValue: "1", remarks: '是否是默认菜单,0不是默认菜单，1是默认菜单') {
                constraints(nullable: false)
            }

            column(name: 'ICON', type: 'VARCHAR(128)', remarks: '图标的code值')
            column(name: 'ROUTE', type: 'VARCHAR(128)', remarks: '路由')

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
        addUniqueConstraint(tableName: 'IAM_MENU', columnNames: 'CODE, TYPE, LEVEL', constraintName: 'UK_IAM_MENU_U1')
    }

    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-03-29-iam-menu-tl') {
        createTable(tableName: "IAM_MENU_TL") {
            column(name: 'LANG', type: 'VARCHAR(16)', remarks: '语言名称') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_MENU_TL_P1')
            }
            column(name: 'ID', type: 'BIGINT UNSIGNED', remarks: '资源ID') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_MENU_TL_P2')
            }
            column(name: 'NAME', type: 'VARCHAR(64)', remarks: '菜单名')
        }
    }

    changeSet(author: 'superleader8@gmail.com', id: '2018-08-28-rename') {
        renameColumn(columnDataType: 'VARCHAR(64)', newColumnName: "FD_LEVEL", oldColumnName: "LEVEL", remarks: '菜单层级', tableName: 'IAM_MENU')
    }
}