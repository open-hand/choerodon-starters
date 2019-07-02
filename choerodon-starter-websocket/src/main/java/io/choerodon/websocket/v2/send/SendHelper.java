package io.choerodon.websocket.v2.send;

/**
 * Created by hailuo.liu@choerodon.io on 2019/7/2.
 */
public class SendHelper {
    private Broker broker;
    public SendHelper(){
        this.broker = new Broker();
        broker.init();
    }
    public void init(){

    }
}
