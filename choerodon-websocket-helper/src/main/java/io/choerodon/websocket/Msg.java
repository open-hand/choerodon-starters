package io.choerodon.websocket;

import java.io.Serializable;
import java.util.*;

/**
 * @author jiatong.li
 *
 * */
public class Msg implements Serializable{

    public static final int DEFAULT = 0;
    public static final int COMMAND = 1;
    public static final int AGENT = 2;
    public static final int PIPE = 3;
    public static final int INFORM = 4;
    public static final int PIPE_EXEC = 6;
    public static final int FRONT_PIP_EXEC = 7;

    //dispatch 到对应socket所在实例之后进行 发送而是找到对应
    // socket session执行相应逻辑
    public static final int INTER =5;
    private String id;
    private String key;
    private Map<String,Set<String>> brokersTO;
    private String type;
    private boolean dispatch = true;
    private String payload;
    private String brokerFrom;
    private String message;
    private int msgType;
    private String envId;
    private Long commandId;
    private byte[] bytesPayload;


    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void addBrokerSocket(String brokerTo, String socketId){
        if(brokersTO == null){
            brokersTO = new HashMap<>();
        }
        if(brokersTO.keySet().contains(brokerTo)){
            brokersTO.get(brokerTo).add(socketId);
        }else {
            Set<String> sockets = new HashSet<>();
            sockets.add(socketId);
            brokersTO.put(brokerTo,sockets);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getBrokerFrom() {
        return brokerFrom;
    }

    public void setBrokerFrom(String brokerFrom) {
        this.brokerFrom = brokerFrom;
    }

    public Map<String, Set<String>> getBrokersTO() {
        return brokersTO;
    }

    public void setBrokersTO(Map<String, Set<String>> brokersTO) {
        this.brokersTO = brokersTO;
    }

    public Msg simpleMsg(){
        Msg simpleMsg = new Msg();
        simpleMsg.setKey(this.getKey());
        simpleMsg.setType(this.getType());
        simpleMsg.setPayload(this.getPayload());
        simpleMsg.setMessage(this.getMessage());
        return simpleMsg;
    }
    public boolean isDispatch() {
        return dispatch;
    }

    public void setDispatch(boolean dispatch) {
        this.dispatch = dispatch;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte[] getBytesPayload() {
        return bytesPayload;
    }

    public void setBytesPayload(byte[] bytesPayload) {
        this.bytesPayload = bytesPayload;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                ", brokersTO=" + brokersTO +
                ", type=" + type +
                ", dispatch=" + dispatch +
                ", payload='" + payload + '\'' +
                ", brokerFrom='" + brokerFrom + '\'' +
                ", msgType=" + msgType +
                '}';
    }

    public String getEnvId() {
        return envId;
    }

    public void setEnvId(String envId) {
        this.envId = envId;
    }

    public Long getCommandId() {
        return commandId;
    }

    public void setCommandId(Long commandId) {
        this.commandId = commandId;
    }
}
