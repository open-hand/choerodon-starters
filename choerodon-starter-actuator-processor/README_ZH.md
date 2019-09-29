# Choerodon Starter Actuator Processor

本模块提供通用方法来处理跨服务初始化数据，只需如下调用即可。

```java
try (Connection connection = dataSource.getConnection()) {
    connection.setAutoCommit(false);
    MicroServiceInitData.processInitData(data, connection, new HashSet<>(Arrays.asList("IAM_PERMISSION", "IAM_MENU_B", "IAM_MENU_PERMISSION", "IAM_DASHBOARD", "IAM_DASHBOARD_ROLE", "IAM_ROLE_PERMISSION")));
    connection.commit();
}
```

方法签名为

```java
/**
 * 将微服务初始化数据的Json执行到数据库
 *
 * @param data actuator json 的 init-data 块
 * @param connection 数据库连接
 * @param tables 指定要处理的表名称
 */
public static void processInitData(JsonNode data, Connection connection, Set<String> tables);
```