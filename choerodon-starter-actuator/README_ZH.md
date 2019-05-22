# Choerodon Actuator Starter

本模块提供一些接口来为实现各个服务通用的接口和为管理服务收集数据，包括以下几种

* 权限数据，从服务的Class中提取数据
* 跨服务初始化数据，通过maven插件转化excel得到的json数据提供
* 数据库元数据，通过JDBC连接获取数据库元数据，提供Low Code 平台使用