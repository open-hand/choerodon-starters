# Changelog

这个项目的所有显著变化都将被记录在这个文件中。

## [0.5.3] - 2018-06-22

### 修复

- `choerodon-starter-bus`修复没有@EnableEurekaClient注解时会出现的缺少`eurekaRegistration`bean的异常。
- `choerodon-starter-oauth-core`修复密码策略中正则策略异常。

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