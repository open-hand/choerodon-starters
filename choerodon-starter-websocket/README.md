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
    // 监听的WS路由
    paths: /choerodon:msg/**
    // 是否需要OAuth授权
    oauth: true
    // OAuth授权信息接口地址
    oauth-url: http://oauth-server/oauth/api/user
    // 心跳包间隔时间
    heart-beat-interval-ms: 10000
```
# 使用
## 1. 前端消息处理
* 前端连接服务的Paths属性对应端口，例子中为/choerodon:msg，如果启用OAuth认证需要传入AccessToken。
* 定时发送心跳包，内容为`{"type":"heartBeat"}`响应为`{"type":"heartBeat","key":null,"data":null}`
* 发送自定义的消息，内容为`{"type":"sub","data":"choerodon:msg:site-msg:12977"}`,type与后端处理接口实现对应，data为附加数据。
* 收到后端的响应，内容为`{"type":"sub","key":null,"data":["choerodon:msg:site-msg:12977"]}`, 使用type区分不同消息类型，其它为附加数据。
## 2. 后端消息处理
```
@Component
public class HeartBeatMsgHandler implements ReceiveMsgHandler<String> {
    private static final String HEART_BEAT = "heartBeat";
    private MessageSender messageSender;
    public HeartBeatMsgHandler(MessageSender messageSender) {
        this.messageSender = messageSender;
    }
    @Override
    public void handle(WebSocketSession session, String key) {
        if (!StringUtils.isEmpty(key)) {
        // 将 key 与 session 绑定，用于后端主动推送消息，按照需求决定是否进行绑定
            relationshipDefining.contact(key, session);
        }
        // 发送响应消息回前端
        messageSender.sendWebSocket(session, new WebSocketSendPayload<>(HEART_BEAT, null, null));
    }
    @Override
    public String matchType() {
        return HEART_BEAT;
    }
}
```
* 实现 ReceiveMsgHandler 类并且注册到 Spring Context
* matchType 函数返回要监听的type
* handle内处理收到消息后的逻辑
* MessageSender.sendWebSocket 方法根据Session发送消息（用于消息处理）
* RelationshipDefining.contact 方法用于将Session与某个Key进行绑定

## 3. 后端主动推送消息
* 调用 MessageSender.sendByKey 方法根据Key来发送消息到与Key绑定的前端
* 需要先调用过 RelationshipDefining.contact 建立绑定关系
* 使用 Redis 消息进行路由转发，需要保证 Redis 功能正常