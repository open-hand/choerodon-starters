# Choerodon Tool Config

This project is a toolkit. When the microservice of choerodon uses the configuration management center to obtain the configuration, it needs to use the tool to initialize the configuration information of the service to the database of the configuration management center.

## Requirements

When using the toolkit for configuration initialization, you need to provide information such as service name, service version, and database connection.

## Usage

* Get the jar：

```
curl https://oss.sonatype.org/content/groups/public/io/choerodon/choerodon-tool-liquibase/0.5.2.RELEASE/choerodon-tool-config-0.5.2.RELEASE.jar -o choerodon-tool-config.jar
```

* Configure type that file supported：
    - yaml
    - properties

* Example:

```bash
if [ ! -f target/choerodon-tool-config.jar ]
then
    curl https://oss.sonatype.org/content/groups/public/io/choerodon/choerodon-tool-liquibase/0.5.2.RELEASE/choerodon-tool-config-0.5.2.RELEASE.jar -o target/choerodon-tool-config.jar # The version may be changed here. Please change it based on the downloaded package name.
fi
java -Dspring.datasource.url="jdbc:mysql://localhost/manager_service?useUnicode=true&characterEncoding=utf-8&useSSL=false" \
 -Dspring.datasource.username=choerodon \
 -Dspring.datasource.password=123456 \
 -Dservice.name=serviceName \ # Service name
 -Dconfig.file=src/main/resources/application-default.yml \
 -jar target/choerodon-tool-config.jar 
```

* Example:

```bash
java -Dspring.datasource.url="jdbc:mysql://localhost/manager_service?useUnicode=true&characterEncoding=utf-8&useSSL=false" \
 -Dspring.datasource.username=choerodon \
 -Dspring.datasource.password=123456 \
 -Dservice.name=serviceName \ # Service name
 -Dconfig.jar=app.jar \ # To import the rack package to which the configuration belongs, take the actual path as the standard
 -Dconfig.file=application-default.yml \ # To import the configuration file, please follow the path of the file in the rack package
 -jar /home/data/choerodon-tool-config.jar # The path of the rack package is subject to the actual.
```
Explanation：

1. `spring.datasource.url`: Data source, required.
1. `spring.datasource.username`: Username, required.
1. `spring.datasource.password`: Password, required.
1. `service.name`: Servicename, required.
1. `config.file`: Search for a configuration file name, if there are multiple configuration files with the same name in the project, the current matching policy will use the first file with the same name. The user can also increase the precision of matching by adding a prefix path, such as `src/main/resources/application-default.yml`. Make sure to use `/` instead of `\` as the path separator. The default is `Application-default.yml`.

1. `config.jar`: If the application has been used as a package of rack, you can enter the name of package of rack here, and the package of rack will be disassembled to search for the configuration file. The default value is null.
* The logic of initial configuration：

1. When initializing for the first time, the configuration is initialized and set as the default configuration.

1. During the second initialization, if the service version changes, follow these guidelines：
    - Copy the previous version configuration
    - If the value is modified, no change is required
    - If the attribute is deleted, it does not need to be deleted
    - If new attributes are added, they will be added

Find the information you want here
## Dependencies

* MySQL

##  Reporting Issues

If you find any shortcomings or bugs, please describe them in the [Issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).
    
## How to Contribute
Pull requests are welcome! Follow [this link](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) for more information on how to contribute.

## Note

1. Since the configuration of the gateway type service requires additional storage of the attributes of `zuul.route`, configure the gateway type service name or direct environment variable injection in the `application-default.yml` of this jar package.

Profile search:

When `config.jar` is `null`, it will use `.` as the root directory of the search. When `config.jar` exists, the root directory of the temporary folder (which is obtained by unzipping the jar file from the toolkit) will be the root directory of search file.