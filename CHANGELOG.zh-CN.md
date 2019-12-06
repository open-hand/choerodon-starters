# Changelog

这个项目的所有显著变化都将被记录在这个文件中。

# [0.14.0]

### 修改
- 更新消息通知 DTO 适应新的表结构

### 新增
- 添加自定义的`PageableArgumentResolver`使`Pageable`可接受`size=0`以实现size为0的时候返回全部数据
- `choerodon-starter-feign-replay`新增灰度发布功能

# [0.13.0]

### 新增
添加`choerodon-starter-webmvc`模块依赖

### 修改
- 拆分base模块功能至core/mybatis模块中
- 更新web模块为webmvc模块

### 移除
移除`choerodon-starter-base`模块依赖
移除`choerodon-starter-web`模块依赖
移除`choerodon-starter-mybatis-mapper`模块依赖

# [0.12.0]
### 新增
- 数据初始化工具实现删除标记 $DEL 和只插入标记 $XXX
- Excel 工具类添加导出 excel 2007 的方法
- 更新消息通知 DTO 添加 WebHook 相关参数

### 修改
- 修复表元数据提取没有REMARK的问题
- 重构 websocket 模块新的设计参考文档 choerodon-starter-websocket/Design.md
- SagaTask返回值为简单类型时直接使用类名生成Demo数据提高体验
- Mybatis模块修复无法从BaseDTO获取排序数据的问题
- 跨服务初始化数据Excel名改为 script/meta/micro-service-init-data.xlsx
- JWT Token 检查依赖 Spring Security 实现，不再使用自定义 Filter
- 修复 Excel 转化 Json 出现大量空字符串问题
- 修复 CustomUserDetails hashCode 方法可能出现空指针异常

### 移除
- 移除 choerodon-starter-statemachine 模块

# [0.11.0]

### 新增
- 新增mybatis模块，采用依赖的方式使用common mapper
- 新增Actuator模块，实现一些微服务通用数据的提取
- 新增Message模块，实现了redis和rabbit的消息队列和订阅机制
- 新增Redis模块，实现了基于redis的缓存机制
- 新增Maven插件，实现编译时的一些数据提取

### 修改
- 完善单元测试提高测试覆盖率
- 实现长连接代替轮训访问Asgard，优化性能
- 修复一些其他的Bug和问题

# [0.10.0]

### 新增
- spring-boot升级到2.0.6.RELEASE
- spring-cloud升级至Finchley.SR2
- 添加mybatis模块
- 添加redis模块
- 添加hap的message模块

### 修改
- 状态机添加创建实例接口
- 修改liquibase初始化工具满足hap需求

# [0.9.3]

### 新增
- 添加mybatis模块
- 添加redis模块
- 添加hap的message模块

### 修改
- 状态机添加创建实例接口
- 修改liquibase初始化工具满足hap需求

## [0.9.2]

### 新增

- liquibase初始化工具支持自定义插入id，只有是整数的id才可以插入，否则自动生成
- tool-config添加了通过configMap初始化配置的方式

### 修改

- 优化了liquibase初始化数据库每个groovy表都要建立jdbc连接的问题

### 修复

- 修复了sqlserver数据库，如果#列为空报incorrect syntax的错误
 

## [0.9.1]

### 修复

- 修复websocket 处理容器执行时的一些问题

## [0.9.0]

### 新增

- 分页查询page为负数，不分页查询，将全部的查询结果放到一个page对象里
- asgard中添加新的使用`TransactionalProducer`创建saga的方式
- asgard中saga和schedule的消费端可以追踪`CustomUserDetails`
- asgard中saga和schedule线程池添加了`maxSize`配置，用于配置线程池最大数量

### 修改

- groovy初始化建表按groovy的名称顺序建表

### 修复

- 修复excel初始化数据时，如果唯一标识可以为空，会插入重复数据的问题

## [0.8.1] - 2018-12-14

### 新增

- 添加feign调用传pageRequest对象的编码器
- 事务实例添加出发层级和sourceId

### 修复

- 修复主键策略为uuid时，插入sql报错的问题
- 修复sqlserver数据库在主键非自增情况下，无法插入主键的问题

### 修改

- feign调用默认的userDetail设置为id=0

## [0.8.0] - 2018-12-03

### 新增

- tool-liquibase和tool-config添加sqlserve驱动
- 添加通知服务更新配置接口
- 添加ColumnType注解，支持自定义JdbcType

### 修改
- PageHelper中jsqlparser由0.9.5升级为1.2版本
- ConvertHelper中增加对扩展ConvertorI接口的支持

### 修复
- 修复mybatis-mapper在sqlserver数据库下插入报错的问题
- 修复sqlserver下分页插件报空指针异常的问题
- 修复JWT的密匙不合法时请求直接返回200而不是401的bug

### 移除

- 删除zipkin相关依赖和类
- 移除starter包里的limit相关sql

## [0.7.0] - 2018-11-13

### 新增

- 添加了单元测试
- 添加了发送业务类型扫描功能；邮件、短信的相关类及注解移到`core`包
- 自定义`UserDetails`中添加`Client`信息
- 添加`choerodon-starter-eureka-event` 模块

### 修改

- 将`test`，`spock`的依赖移到父模块中
- 定时任务增加层级

### 修复

- `FeignRequestInterceptor` 类型强转失败

### 移除

- 移除`choerodon-starter-metric`,`choerodon-starter-oauth-resource`的`cglib`依赖
- 移除模块`choerodon-starter-bus`
- 移除模块`choerodon-starter-config-monitor`

## [0.6.4] - 2018-09-29

### 新增

- 站内信模板扫描
- 新增`AgentEndpoint` 

## [0.6.3] - 2018-09-19

### 新增

- 定时任务通用注解
- `swagger` 的`permission` 添加了是否是内部接口的字段

### 修复

- 修复`oracle` 下`selectOne` 可能报错的问题

## [0.6.2] - 2018-09-04

### 新增

- 默认角色添加平台开发者
- 通用mapper支持oracle数据库
- choerodon-starter-swagger中邮件模版扫描添加了从classPath加载文件的功能

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