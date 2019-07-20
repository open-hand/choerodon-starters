# choerodon-starter-websocket
> 基于Spring Websocket封装的WebSocket框架，添加了基于Redis的多实例消息转发以及基于类型的消息处理机制。
# 外部依赖
* Redis 4.0 +
# Maven依赖
```
        <dependency>
            <groupId>io.choerodon</groupId>
            <artifactId>choerodon-starter-websocket</artifactId>
            <version>${choerodon.starters.version}</version>
        </dependency>
```
# 自定义配置(以下为默认值)
```yaml
choerodon:
  ws:
    // 心跳包间隔时间，Broker 的心跳包，连续两次无心跳会移除 Broker
    heart-beat-interval-ms: 10000
```
# 使用
## 1. 前端消息处理
* 前端连接服务的Paths属性对应端口，例子中为/choerodon:msg
* 发送自定义的消息，内容为`{"type":"sub","data":"choerodon:msg:site-msg:12977"}`,type与后端处理接口实现对应，data为附加数据。
* 收到后端的响应，内容为`{"type":"sub","key":null,"data":["choerodon:msg:site-msg:12977"]}`, 使用type区分不同消息类型，其它为附加数据。
## 2. 后端消息处理
```
@Component
public class HeartBeatMsgHandler implements MessageHandler<String> {

    private static final String HEART_BEAT = "heartBeat";
    private WebSocketHelper webSocketHelper;

    public HeartBeatMsgHandler(@Lazy WebSocketHelper webSocketHelper) {
        this.webSocketHelper = webSocketHelper;
    }

    @Override
    public void handle(WebSocketSession session, String type, String key, String payload) {
        webSocketHelper.contact(session, key);
        webSocketHelper.sendMessageBySession(session, new WebSocketSendPayload<>(HEART_BEAT, null, null));
    }

    @Override
    public String matchType() {
        return HEART_BEAT;
    }

}
```
* WebSocketHelper.addMessageHandler 方法实现 MessageHandler 类并且注册到 Spring Context
* matchType 函数返回要监听的type
* handle 内处理收到消息后的逻辑
* WebSocketHelper.sendMessageBySession 方法根据Session发送消息（用于消息处理）
* WebSocketHelper.contact 方法用于将Session与某个Key进行绑定
* 如果构造函数注入 WebSocketHelper 需要加 `@Lazy` 注解

## 3. 后端主动推送消息
* 调用 WebSocketHelper.sendMessage 方法根据Key来发送消息到与Key绑定的前端
* 需要先调用过 WebSocketHelper.contact 建立绑定关系
* 使用 Redis 消息进行路由转发，需要保证 Redis 功能正常