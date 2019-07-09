# choerodon-starter-websocket

> websocket 实现的前端消息推送库，通过Redis实现多实例路由，支持扩展处理器

# 依赖

* redis

# 使用

* 添加依赖

```
<dependency>
    <groupId>io.choerodon</groupId>
    <artifactId>choerodon-starter-websocket</artifactId>
    <version>${choerodon.starters.version}</version>
</dependency>
```

* 处理前端消息

前端连接 `wss://example.com/choerodon:msg` 发送和接收数据

```
{"type":"example","data":"choerodon:msg:example:1"}
----
{"type":"example","key":"choerodon:msg:example:1","data":"example data"}
```

后端有对应的实现响应这个请求

```
@Component
public class ExampleReceiveMessageHandler implements ReceiveMsgHandler<String> {
    private RelationshipDefining relationshipDefining;

    private MessageSender messageSender;

    public UnSubReceiveMessageHandler(RelationshipDefining relationshipDefining,
                                      MessageSender messageSender) {
        this.relationshipDefining = relationshipDefining;
        this.messageSender = messageSender;
    }

    @Override
    public String matchType() {
        // 类型与要监听的前端消息类型一致
        return "example";
    }

    @Override
    public void handle(WebSocketSession session, String key) {
        // key 就是前端发送数据中的 data
        if (!StringUtils.isEmpty(key)) {
            // 绑定key与seesion用于基于key推送的多实例路由
            relationshipDefining.contact(session, key);
        }
        // 处理自己的逻辑，使用 messageSender.sendWebSocket(session, new WebSocketSendPayload<>("example", key, "example data")); 来响应请求
    }

}
```

* 后端主动发送数据通知（必须先保证Key和Session被关联，多实例自动通过Redis路由）

```
messageSender.sendByKey("choerodon:msg:example:1", new WebSocketSendPayload<>("example", "choerodon:msg:example:1", "example data"));
```

前端收到数据

```
{"type":"example","key":"choerodon:msg:example:1","data":"example data"}
```