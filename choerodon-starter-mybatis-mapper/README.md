# Choerodon Starter Mybatis Mapper

The mybatis basic toolkit integrates two open source projects [Generic Mapper](https://github.com/abel533/Mapper) and [PageHelper](https://github.com/pagehelper/Mybatis-PageHelper). Based on the requirements of business logical, streamlining and modification of source code, extending audit fields, multilingual functions, and modifying paging plugins, adding functionality such as inserting or updating specified columns.

The toolkit is used if there is a database operation in the Choerodon microservice.

## Feature

``PageHelper`` nested result query is paged. Abstracting the general methods of paging queries.

## Installation and Getting Started

```xml
<dependency>
    <groupId>io.choerodon</groupId>
    <artifactId>choerodon-starter-mybatis-mapper</artifactId>
    <version>0.6.3.RELEASE</version>
</dependency>
```

## Documentation

[General original author documentation of Mapper.](https://mapperhelper.github.io/docs/)

[PageHelper original author use method](https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md)

[The introduction of Universal Mapper Implementation Principle](http://gitbook.cn/books/59ed3c942f5a1d7161bad162/index.html)

## Usage

Universal Mapper Usage：

Build user table：

```sql
CREATE TABLE `user`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'name',
  `object_version_number` bigint(20) UNSIGNED NULL DEFAULT 1,
  `created_by` bigint(20) UNSIGNED NULL DEFAULT 0,
  `creation_date` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `last_updated_by` bigint(20) UNSIGNED NULL DEFAULT 0,
  `last_update_date` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`pk_id`) USING BTREE,
  UNIQUE INDEX `name`(`uk_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 867 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

INSERT INTO `user` VALUES (1, 'Daenerys Targaryen', 1, 0, '2018-04-11 03:09:38', 0, '2018-04-11 03:09:38');
```

The corresponding data object is as follows：

```java
//The  name of corresponding database table
@Table(name = "user")
//Supports 4 audit fields (created_by, creation_date, last_update_by, last_update_date)
@ModifyAudit
//object_version_number
@VersionAudit
public class UserDO extends AuditDomain {
    //Specifies the primary key for checking back to the primary key after insertion
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String name;

    //Omit the methods of get and set.
```

`choerodon-starter-mybatis-mapper` sets the folder that ends with the mapper under the scan project, so the mapper file and the corresponding xml file should be laid down below the mapper folder. The iam structure is as follows:

```xml
.
+-- src
    +-- main
        +-- docker
        +-- java
        |    +-- io
        |        +-- choerodon
        |            +-- iam
        |               +-- infra
        |                   +-- mapper
        +-- resource
            +-- mapper
```

The mapper file is as follows:

```java
public interface UserMapper extends BaseMapper<UserDO> {
    //Just an example, it can be replaced by the automatically generated method selectAll()
    List<UserDO> selectAllUsers();
}
```

It inherits the mapper interface of `BaseMapper`, including most operations of additions, deletions, and modifications in single tables, can satisfy most simple database operations.

If have complex business logic, write sql by hand. That is, create an xml file with the same name as the mapper class in the mapper folder under resource. For example: New `UserMapper.xml`, which id and method name are the same.

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >

<mapper namespace="io.choerodon.iam.infra.mapper.UserMapper">
     <select id="selectAllUsers" resultType="io.choerodon.manager.infra.dataobject.UserDO">
        select * from user
    </select>
</mapper>
```

So far, call ``userMapper`` in the repository to do addition, deletion, and modification.

```java
@Component
public class UserRepositoryImpl implements UserRepository {
    private UserMapper userMapper;
    public UserRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    List<UserDO> selectAllUsers() {
        return userMapper.selectAllUsers();
    }
}
```

At the same time, we have created a new BaseService classwith the way of encapsulating the BaseMapper methods in a combined fashion. Note the `insertOptional` and `updateOptional` methods, both of which have a mutable parameter. For example, the corresponding column of the database only inserts or updates the data of the specified column.

If do not use the `insertOptional` and `updateOptional` methods of `BaseService`, while call `userMapper.insertOptional(userDO)`, as follows:

```java
String columns = "id,name,object_version_number";
OptionalHelper.optional(Arrays.asList(columns));
userMapper.insertOptional(userDO);
```

`PageHelper` usage：

If have the following request.

`http://localhost:8030/v1/organization/1/users?page=0&size=10&sort=id,desc&sort=organizationId,phone,asc`

Page is the start page, the default value is 0, size is the current page number of records, the default value is 20. Sort is the sort field, and the weight decreases from left to right. The above example shows that the query is first sorted in descending order of id. If the ids are the same, they are sorted in ascending order by `organizationId`. If the `organizationId` is the same, they are sorted in ascending order by phone.

Controller：

```java
@ApiOperation(value = "Paging query")
@CustomPageRequest
@GetMapping
public ResponseEntity<Page<UserDTO>> list(@PathVariable(name = "organization_id") Long organizationId,
                                          @ApiIgnore
                                          //If the request url does not import sort field, set the default sort: ascending order according to id.
                                          @SortDefault(value = "id", direction = Sort.Direction.ASC)
                                                PageRequest pageRequest,
                                          @RequestParam(required = false) String name)
    return new ResponseEntity<>(organizationUserService.pagingQuery(pageRequest, name), HttpStatus.OK);
}
```

The `PageRequest` object encapsulates the incoming page, size, and sort parameters. Paging queries use the `PageHelper` object to call paging methods.

```java
    //page
    int page = pageRequest.getPage();
    int size = pageRequest.getSize();
    Page<UserDO> users = PageHelper.doPage(page, size, () -> userMapper.selectAllUsers());
```

```java
    //Page and sort
    Page<UserDO> users = PageHelper.doPageAndSort(pageRequest, () -> userMapper.selectAllUsers());
```

```java
    //sort
    Sort sort = PageHelper.getSort();
    List<UserDO> users = PageHelper.doSort(sort, () -> userMapper.selectAllUsers());
```

Sorting usage:

1.Single table：

Single-table query operations do not involve aliases and are simple to use. Written as follows:

```java
public ResponseEntity<Page<IconDTO>> pagingQuery(@SortDefault(value = "lastUpdateDate", direction = Sort.Direction.ASC) PageRequest pageRequest)
```

In the controller parameter, use the ``pageRequest`` object to receive the page, size, and sort fields in the url. The `@SortDefault` annotation value is the default sort field. If it is a hump, splicing sql will be underlined; directions are ascending (`Sort.Direction.ASC`) and descending (`Sort.Direction.DESC`). Page and size do not import allocation defaults. For sort, if the controller is configured with the `@SortDefault` annotation, the default value is generated with the annotation, and sort is null if no annotation is configured.

The method called is as follows:

```java
//If use the generate sort method, the sql of xml do not write order by.
PageHelper.doPageAndSort(pageRequest, ()-> mapper.fulltextSearch(userDO, param));
```

2.Related multi-table query (only suitable for simple associated table query, if it is very complicated query, please write sql):

```sql
    SELECT
        fo.id,
        fo.code,
        fo.name,
        fo.password_policy_id,
        fp.id AS project_id,
        fp.name AS project_name,
        fp.code AS project_code,
        fp.organization_id,
        fp.is_enabled
    FROM
        fd_organization as fo
    LEFT
        JOIN fd_project as fp
    ON
        fo.id = fp.organization_id
    WHERE
        fp.is_enabled = true
```

If it is `sort=organizationCode,projectCode,desc`, due to aliasing in sql and the sorting field name passed in the front also has a factor of naming, so need to create a HashMap for key-value mapping, organizationCode points to fo.code, projectCode points to fp.code.

Define fo as the main table and fp as the dependent table. The following example is sorted by the code and name fields of the organization table and the code and name fields of the project table:


 ```java
    Map<String, String> map = new HashMap<>();
    map.put("organizationCode", "fo.code");
    map.put("projectCode", "fp.code");
    map.put("organizationName", "fo.name");
    map.put("projectName", "fp.name");
    //Set the alias of the main table, when the value of the @SortDefault = id, the value will be spliced to the fo.id
    pageRequest.resetOrder("fo", map);
    PageHelper.doPageAndSort(pageRequest, ()-> mapper.fulltextSearch());
 ```

`pageRequest.resetOrder("fo", map)`: resets the order object in the sort. The first parameter is the main table alias. If there is no alias, write `fd_organization` and the second parameter is the map mapping. The role of the primary table alias is: If `sort=id, asc`, will be spliced at the end of the sql `order by fo.id asc`, if sort by project_id, can only put map inside, use `map.put(" projectId", "fp.id")` is implemented as a form.

After setting pageRequest, call `PageHelper.doPageAndSort()`.

Note：

Single-table dynamic sorting has field validation. If the incoming field is not a database field, throw exception.

Multi-table dynamic sorting does not have illegal field validation. If the fields are illegal, throw ``SqlGrammarException`` when sql is executed, but sql injection is prevented.

In sql, the splicing of order by can only be spliced at the end of the sql statement, if there is a paging parameter, at the end of the sql statement, paging parameters before splicing. Does not support nested result queries, splicing order by in the middle of sql. If customize the order by position, currently only manually write SQL, the sort field passed in the form of parameters.

## Dependencies

- `choerodon-starter-core`: `Page` object and `PageInfo` object
- `mybatis-spring-boot-starter`
- `com.github.jsqlparse`
- `com.fasterxml.jackson.core`
- `javax.persistence`
- `mysql-connector-java`
- `swagger-annotations`

## Reporting Issues

If you find any shortcomings or bugs, please describe them in the [Issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).

## How to Contribute

Pull requests are welcome! Follow [this link](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) for more information on how to contribute.

## Note

* If the property is not a property of a field which in the table, the `@Transient` annotation must be added so that dynamic sql will not get the column.

* In the entity class, add the `@MultiLanguage` annotation to the class. Multi-language support enabled, multi-language fields need to add `@MultiLanguageField` annotation.

* Universal Mapper does not support devtools hot loading, devtools excludes entity package, configuration method reference: [spring-boot-devtools-customizing-classload](https://docs.spring.io/spring-boot/docs/current/Reference/html/using-boot-devtools.html#using-boot-devtools-customizing-classload)

* Only the first method of Mybatis Select that follows the PageHelper.startPage method will be paged.

* Please do not configure multiple paging plugins.

* Please do not configure multiple paging plugins in the system (When using Spring, `mybatis-serviceConfig.xml` and Spring `<bean>` configuration methods, please select one, do not configure multiple paging plugins at the same time)!

* Pagination plugin does not support pagination with the statement of for update.
* For SQL with for update, it will throw a runtime exception, for such SQL suggested manual paging, after all, such sql need to pay attention.

*The paging plugin does not support nested result mappings. Since the nested result method causes the result set to be collapsed, the total number of paged query results after folding is reduced, so the number of paged results cannot be guaranteed to be correct.