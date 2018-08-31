package io.choerodon.websocket.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.websocket.Msg;
import io.choerodon.websocket.helper.PipeRequest;
import io.choerodon.websocket.session.Session;

/**
 * @author crcokitwood
 */
public class MsgFactory {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String LOGGER_COMMAND = "kubernetes_get_logs";
    private static final String EXEC_COMMAND = "kubernetes_exec";

    public static Msg closeMsg(Session session){
        Msg msg = new Msg();
        msg.setKey(session.getRegisterKey());
        msg.setBrokerFrom(session.getUuid()+session.getRegisterKey());
        msg.setMsgType(Msg.INTER);
        return msg;
    }

    public static Msg logMsg(String id, String key,PipeRequest pipeRequest){
        Msg msg = new Msg();
        msg.setKey(key);
        msg.setBrokerFrom(id+key);
        msg.setType(LOGGER_COMMAND);
        try {
            msg.setPayload(MAPPER.writeValueAsString(pipeRequest));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public static Msg execMsg(String id, String key,PipeRequest pipeRequest){
        Msg msg = new Msg();
        msg.setKey(key);
        msg.setBrokerFrom(id+key);
        msg.setType(EXEC_COMMAND);
        try {
            msg.setPayload(MAPPER.writeValueAsString(pipeRequest));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return msg;
    }




}
