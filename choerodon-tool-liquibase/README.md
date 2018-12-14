# choerodon-tool-liquibase

This project is a toolkit. When the microservice of choerodon uses the database as storage, it needs to use the tool to create the table structure and initialize the database.

According to the groovy files and xlsx files in the project, it automatically performs table operation aHnd initialization data.

## Usage

* Get the jar：

```
curl https://oss.sonatype.org/content/groups/public/io/choerodon/choerodon-tool-liquibase/0.9.0.RELEASE/choerodon-tool-liquibase-0.9.0.RELEASE.jar -o choerodon-tool-liquibase.jar
```

* Example:

* H2 example

open browser visit http://localhost:8080

```
java \
 -Ddata.drop=false -Ddata.init=true \
 -Dspring.h2.console.enabled=true \
 -Dspring.main.web-environment=true \
 -Ddata.dir=src/main/resources \
 -Ddata.jar=app.jar \
 -jar choerodon-tool-liquibase.jar
```

* Mysql example
```
java \
 -Dspring.datasource.url='jdbc:mysql://localhost/choeroson_database?useUnicode=true&characterEncoding=utf-8&useSSL=false' \
 -Dspring.datasource.username=choerodon \
 -Dspring.datasource.password=123456 \
 -Ddata.drop=false -Ddata.init=true \
 -Ddata.dir=src/main/resources \
 -Ddata.update.exclusion=iam_user.password, iam_role.code, iam_role.description \
 -Ddata.jar=app.jar \
 -jar choerodon-tool-liquibase.jar
```

```
java \
-Ddata.jar=app1.jar \
-Dspring.datasource.url="jdbc:mysql://localhost:3306/database1?useUnicode=true&characterEncoding=utf-8&useSSL=false" \
-Dspring.datasource.username=choerodon \
-Dspring.datasource.password=123456 \
-Ddata.dir=db \
-Ddata.drop=true \
-Daddition.datasource.names=ds1,ds2 \
-Daddition.datasource.ds1.url="jdbc:mysql://localhost:3306/database2?useUnicode=true&characterEncoding=utf-8&useSSL=false" \
-Daddition.datasource.ds1.username=ds1 \
-Daddition.datasource.ds1.password=123456 \
-Daddition.datasource.ds1.drop=true \
-Daddition.datasource.ds1.dir=db \
-Daddition.datasource.ds2.url="jdbc:mysql://localhost:3306/database3?useUnicode=true&characterEncoding=utf-8&useSSL=false" \
-Daddition.datasource.ds2.username=ds2 \
-Daddition.datasource.ds2.password=123456 \
-Daddition.datasource.ds2.dir=db \
-Daddition.datasource.ds2.drop=true \
-jar choerodon-tool-liquibase.jar
```

Explanation：

1. spring.datasource.url: Data source, required.
2. spring.datasource.username: Username，required.
3. spring.datasource.password: password，required.
4. data.jar: The address of the data jar, it can start with http, https,file. At the end of '.jar'.
5. data.dir: File directory, applicable to the development phase, it can automatically find groovy or xlsx.
6. data.drop: Rebuild the database. default is false.
7. data.update.exclusion: Exclude the whole table or column when update the db data by excel. Use the `,` to split the table and use `.` to express the column of table


## Dependencies

* MySQL

##  Reporting Issues

If you find any shortcomings or bugs, please describe them in the [Issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).

## How to Contribute
Pull requests are welcome! Follow [this link](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) for more information on how to contribute.
