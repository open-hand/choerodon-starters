package io.choerodon.websocket.helper;

import io.choerodon.websocket.session.Session;
import io.choerodon.websocket.websocket.SocketProperties;
import org.springframework.util.AntPathMatcher;

import java.util.regex.Pattern;

public class PathHelper {

    private  SocketProperties socketProperties;
    private static final String LOG_PATH_PREFIX = "/log";
    private static final String EXEC_PATH_PREFIX = "/exec";
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public PathHelper(SocketProperties socketProperties) {
        this.socketProperties = socketProperties;
    }


    public  int getSessionType(String path){
        if (path.contains(LOG_PATH_PREFIX)){
            return Session.LOG;
        }else if (path.contains(EXEC_PATH_PREFIX)){
            return Session.EXEC;
        }
        else if(antPathMatcher.match(socketProperties.getAgent(),path)){
            return Session.AGENT;
        }else if(antPathMatcher.match(socketProperties.getFront(),path)){
            return Session.COMMON;
        }
        return 0;
    }
}
