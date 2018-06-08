# 0.5.1

`2018-06-08`

- 新增[choerodon-websocket-helper](./choerodon-websocket-helper/README.md)，该依赖由更新自choerodon-socket-helper，主要提供Websocket的消息路由转发，实现devops-service与choerodon-agent的命令交互。
- 新增[choerodon-gitlab4j-api](./choerodon-gitlab4j-api/README.md)，该依赖主要修改了gitlab api java客户端，用于支撑gitlab-service与gitlab的交互。
- choerodon-starter-tool初始化路由信息时设置is_built_in为true，表示内置服务。
- choerodon-starter-core的CustomUserDetails添加is_admin字段。
- choerodon-starter-mybatis-mapper修复selectOne查询条件匹配到多条数据时会出现的异常。