package io.choerodon.websocket.tool;

import io.choerodon.websocket.Msg;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SerializeTool {
    private static final Logger logger = LoggerFactory.getLogger(SerializeTool.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static Msg readMsg(String content){
        Msg msg = null;
        try{
            msg = OBJECT_MAPPER.readValue(content,Msg.class);
        }catch (IOException ex){
            logger.error("read msg from json error",ex);
        }
        return msg;
    }
}
