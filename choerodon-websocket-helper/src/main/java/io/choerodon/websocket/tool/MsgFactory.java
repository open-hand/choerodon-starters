package io.choerodon.websocket.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.websocket.Msg;
import io.choerodon.websocket.helper.PipeRequest;
import io.choerodon.websocket.session.Session;

public class MsgFactory {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static final String LOGGER_COMMAND = "kubernetes_get_logs";
    public static Msg CloseMsg(Session session){
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
            msg.setPayload(mapper.writeValueAsString(pipeRequest));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return msg;
    }


}
