package io.choerodon.websocket.tool;

import java.util.UUID;

/**
 * @author crock
 */
public class UUIDTool {
    public static String genUuid(){
       return UUID.randomUUID().toString().replace("-","");
    }
}
