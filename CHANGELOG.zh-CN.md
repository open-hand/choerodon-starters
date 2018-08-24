# Changelog

这个项目的所有显著变化都将被记录在这个文件中。

## [0.6.2] - 2018-

### 新增
### 修改
### 修复

- 修复liquibase初始化,列名为关键字无法插入的问题

### 删除

## [0.6.1] - 2018-08-21

### 修改

- 简化`webSocket`的日志

## [0.6.0] - 2018-08-15

### 新增

- `choerodon-starter-asgard`，该依赖主要用于支持基于`saga`的数据最终一致性。。更多信息可以了解[asgard-service](https://github.com/choerodon/asgard-service)。
- `choerodon-starter-core`添加了FeignException异常类，用于处理feign调用异常。
- `choerodon-starter-core`包添加excel数据转换为list对象工具类。
- `choerodon-starter-core`包添加了导出excel 2003的工具类。

## [0.5.4] - 2018-07-18

### 新增

- `choerodon-starter-mybatis-mapper`添加了插入list方法
- `choerodon-starter-core`添加excel文件转对象集合工具类

### 修复

- 修复liquibase工具包初始化excel数据时，如果最后一列为空，该行数据无法插入数据库的bug
- 修复`choerodon-starter-mybatis-mapper`中`selectCount()`方法不支持多语言表的bug

## [0.5.3] - 2018-06-28

### 修复

- `choerodon-starter-bus`修复没有@EnableEurekaClient注解时会出现的缺少`eurekaRegistration`bean的异常。
- `choerodon-starter-oauth-core`修复密码策略中正则策略异常。
- 完善`agent websocket`连接参数条件判断

## [0.5.2] - 2018-06-22

### 修改

- `choerodon-tool-config`修改为每次初始化只更新is_default的配置，而不是每次产生新配置。
- `choerodon-starter-bus`修改为服务拉取配置时，当bus消息的版本字段为空时拉取实例正在应用的配置，而不是拉取默认配置。
- `choerodon-starter-swagger`扫描到的controller去除了BasicErrorController。
- `choerodon-starter-core`添加了InitRoleCode常量类；删除了@Permission注解的roles字段默认值。
- `choerodon-tool-liquibase`数据库迁移添加了忽略指定表或者列的功能。

## [0.5.1] - 2018-06-08

### 新增

- 新增[choerodon-websocket-helper](./choerodon-websocket-helper/README.md)，该依赖由更新自choerodon-socket-helper，主要提供Websocket的消息路由转发，实现devops-service与choerodon-agent的命令交互。
- 新增[choerodon-gitlab4j-api](./choerodon-gitlab4j-api/README.md)，该依赖主要修改了gitlab api java客户端，用于支撑gitlab-service与gitlab的交互。

### 修改

- `choerodon-starter-tool`初始化路由信息时设置`is_built_in`为`true`，表示内置服务。
- `choerodon-starter-core`的`CustomUserDetails`添加`is_admin`字段。
- `choerodon-starter-mybatis-mapper`单表排序支持从`@Column`注解里拿到数据库列名。
- `choerodon-starter-mybatis-mapper`修复`selectOne`查询条件匹配到多条数据时会出现的异常。