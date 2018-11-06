# 使用

**添加依赖**

```xml
<dependency>
    <groupId>io.choerodon</groupId>
    <artifactId>choerodon-starter-eureka-event</artifactId>
    <version>${choerodon.starters.version}</version>
</dependency>
```

**运行主类执行初始化**

```java
@SpringBootApplication
@EnableEurekaClient
public class AsgardApplication {

    public static void main(String[] args) {
        //此处执行初始化
        EurekaEventHandler.getInstance().init();
        SpringApplication.run(AsgardApplication.class, args);
    }
}

```


**继承AbstractEurekaEventObserver并确保该类被spring扫描到**

```
@Component
public class EurekaEventObserver extends AbstractEurekaEventObserver {

    @Override
    public void receiveUpEvent(EurekaEventPayload payload) {
        //接收服务启动事件后的处理逻辑
    }

    @Override
    public void receiveDownEvent(EurekaEventPayload payload) {
       //接收服务下线事件后的处理逻辑
    }
   
}
```

# 说明

**配置**

```yaml
choerodon:
  eureka:
    event:
      max-cache-size: 300 # 存储的最大失败数量
      retry-time: 5 # 自动重试次数
      retry-interval: 3 # 自动重试间隔(秒)
      skip-services: register-server, api-gateway, gateway-helper, oauth-server, config-server, event-store-service # 跳过的服务

```

**内置查询消费失败接口和手动重试接口**

```java
@RestController
@RequestMapping(value = "/v1/eureka/events")
public class EurekaEventEndpoint {

    private EurekaEventService eurekaEventService;

    public EurekaEventEndpoint(EurekaEventService eurekaEventService) {
        this.eurekaEventService = eurekaEventService;
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "获取未消费的事件列表")
    @GetMapping
    public List<EurekaEventPayload> list(@RequestParam(value = "service", required = false) String service) {
        return eurekaEventService.unfinishedEvents(service);
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "手动重试未消费成功的事件")
    @PostMapping("retry")
    public List<EurekaEventPayload> retry(@RequestParam(value = "id", required = false) String id,
                                          @RequestParam(value = "service", required = false) String service) {
        return eurekaEventService.retryEvents(id, service);
    }

}
```

