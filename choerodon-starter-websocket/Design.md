### 关系存储
#### redis存储
1，broker注册列表 ，注册表Key(BrokerManager.generateRegisterKey)->[brokerName]
2，broker心跳时间，broker心跳Key(BrokerManager.getBrokerHeartbeatKey)->当前时间
3，broker订阅的messageKey列表, broker订阅Key(BrokerManager.getBrokerKeyMapKey)-> [messageKey]
4，broker对应的消息channel,消息channel的Key为brokerName

#### brokder 本地存储
1，session订阅的messageKey列表
2，订阅messageKey的session列表

### Broker启动  BrokerManager.start
1, 向broker注册表注册
2, 刷新broker心跳时间
3, 启动定时器，按间隔执行：
3.1, 向broker注册表注册
3.2, 刷新broker心跳时间
3.3, 取得1中注册的所有brokerName,通过key choerodon:websocket:broker:${brokerName()}取得值，跟当前时间进行对比，如果大于两个间隔，则认定为失效，执行Broker删除操作

### Broker 失效  BrokerManager.removeDeathBroker
1, 删除注册表中的broker的注册信息
2, 删除broker心跳信息
3, TODO-删除broker订阅key的消息   --这里比较有风险，如果broker由于网络原因，不能及时刷新redis,再次连接上后，会接收不到跨broker的消息。

### 建立连接 MessageHandlerAdapter.afterConnectionEstablished
1,检查连接合法性,在SocketHandlerRegistration的beforeHandshake中实现  
2,可以根据情况，在SocketHandlerRegistration的afterConnectionEstablished方法中建立messageKey与session的关系

### 断开连接 MessageHandlerAdapter.afterConnectionClosed
1，删除session关联的所有key，brokerKeySessionMapper.unsubscribeAll

### session订阅Key DefaultBrokerKeySessionMapper.subscribe
1，添加关系到 session订阅的messageKey列表
2，添加关系到 订阅messageKey的session列表
3，添加关系到 broker订阅的messageKey列表

### session取消订阅Key DefaultBrokerKeySessionMapper.unsubscribe
1，从 session订阅的messageKey列表 中删除关系
2，从 订阅messageKey的session列表 中删除关系
3，如果 订阅messageKey的session列表 已经为空，则从 broker订阅的messageKey列表 中删除关系

### 接收消息  MessageHandlerAdapter.handleTextMessage,MessageHandlerAdapter.handleBinaryMessage
1, 接收到消息后,根据消息类型，调用不同的Handler进行处理
1.1, TextMessage 匹配TextMessageHandler ,需要path和type都匹配
1.2, BinaryMessage 匹配BinaryMessageHandler,需要匹配path

### 发送消息 DefaultSmartMessageSender.sendByKey
1, 按messageKey发文本(json)送消息
1.1, 查找本地 订阅messageKey的session列表 的存储是否存在,如果存在则调用Session发送,将payload中的数据转换为json数据
1.2, 查找Redis broker订阅的messageKey列表 是否存在值，如存在则发送消息到broker对应的消息channel ,将payload中的数据转换为json数据

2, 按key发送二进制消息
2.1, 查找本地 订阅messageKey的session列表 的存储是否存在,如果存在则调用Session发送,直接将二进制消息封装为BinaryMessage
2.2, 查找Redis broker订阅的messageKey列表 是否存在值，如存在则发送消息到broker对应的消息channel ，将payload中的数据转换为json数据，其payload的二进制数据转换为String,并在json数据中标记为二进制消息

### Redis Broker Channel转发的消息处理
1，接收到文本消息

2，接收到的消息为二进制消息
