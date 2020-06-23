# Choerodon Starter Asgard

本模块为分布式事务给你模块，需要 `hzero-asgard` 支持，对于需要定义任务（基于 Quartz），定义事务，定义事务任务，执行事务的服务需要依赖本模块。

# 定义任务

```java
    @JobTask(maxRetryCount = 2, code = "deleteAllExpiredToken", level = ResourceLevel.SITE, description = "删除所有失效token")
    public void deleteAllExpiredToken(Map<String, Object> map) {
        List<AccessTokenDTO> accessTokens = accessTokenMapper.selectAll();
        //过滤出所有失效token
        List<AccessTokenDTO> allExpired = accessTokens.stream().filter(t -> ((DefaultOAuth2AccessToken) SerializationUtils.deserialize(t.getToken())).isExpired()).collect(Collectors.toList());
        allExpired.forEach(t -> {
            accessTokenMapper.deleteByPrimaryKey(t.getTokenId());
            refreshTokenMapper.deleteByPrimaryKey(t.getRefreshToken());
        });
        logger.info("All expired tokens have been cleared.");
    }
```

如上代码定义任务定时删除失效的 Token，参数的具体含义参考 `io.choerodon.asgard.schedule.annotation.JobTask`

# 定义事务和执行事务

```java
@Saga(code = PROJECT_UPDATE, description = "iam更新项目", inputSchemaClass = ProjectEventPayload.class)
public ProjectDTO update(ProjectDTO projectDTO) {
        producer.apply(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.SITE)
                        .withRefType("organization")
                        .withSagaCode(REGISTER_ORG),
                builder -> builder
                        .withPayloadAndSerialize(payload)
                        .withRefId(String.valueOf(payload.getOrganization().getCode()))
                        .withSourceId(0L));
}
```

如上代码定义了一个更新项目的事务，并且在方法内执行事务，这两个一般写一起但是非强制，参数具体含义参考 `io.choerodon.asgard.saga.annotation.Saga` 和 `io.choerodon.asgard.saga.producer.TransactionalProducer`

# 定义事务任务

```java
@SagaTask(code = PERMISSION_REFRESH_TASK_SAGA_CODE, sagaCode = ACTUATOR_REFRESH_SAGA_CODE, seq = 1, description = "刷新权限表数据")
public String refreshPermission(String actuatorJson) throws IOException {
    Map actuator = OBJECT_MAPPER.readValue(actuatorJson, Map.class);
    String service = (String) actuator.get("service");
    Map permissionNode = (Map) actuator.get("permission");
    LOGGER.info("start to refresh permission, service: {}", service);
    String permissionJson = OBJECT_MAPPER.writeValueAsString(permissionNode);
    Map<String, PermissionDescription> descriptions = OBJECT_MAPPER.readValue(permissionJson, OBJECT_MAPPER.getTypeFactory().constructMapType(HashMap.class, String.class, PermissionDescription.class));
    parsePermissionService.processDescriptions(service, descriptions);
    return actuatorJson;
}
```

如上代码定义了刷新权限的事务任务到数据初始化事务内，参数具体含义参考 `io.choerodon.asgard.saga.annotation.SagaTask`