# Changelog

这个项目的所有显著变化都将被记录在这个文件中。

## [0.5.1] - 2018-06-08

### 新增

- 新增[choerodon-websocket-helper](./choerodon-websocket-helper/README.md)，该依赖由更新自choerodon-socket-helper，主要提供Websocket的消息路由转发，实现devops-service与choerodon-agent的命令交互。
- 新增[choerodon-gitlab4j-api](./choerodon-gitlab4j-api/README.md)，该依赖主要修改了gitlab api java客户端，用于支撑gitlab-service与gitlab的交互。

### 修改

- `choerodon-starter-tool`初始化路由信息时设置`is_built_in`为`true`，表示内置服务。
- `choerodon-starter-core`的`CustomUserDetails`添加`is_admin`字段。
- `choerodon-starter-mybatis-mapper`单表排序支持从`@Column`注解里拿到数据库列名

### 修复
- `choerodon-starter-mybatis-mapper`修复`selectOne`查询条件匹配到多条数据时会出现的异常。
