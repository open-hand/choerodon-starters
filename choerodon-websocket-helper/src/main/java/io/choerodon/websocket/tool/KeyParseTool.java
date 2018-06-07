package io.choerodon.websocket.tool;

import java.util.HashMap;
import java.util.Map;

public class KeyParseTool {

    public static boolean matchPattern(String key){
        String[] pairs = key.split("\\.");
        if(pairs.length == 0){
            return false;
        }
        for (String pair : pairs){
            String[] content = pair.split(":");
            if(content.length != 2){
                return false;
            }
        }
        return true;
    }

    public static Map<String,String> parseKey(String key){
        Map<String,String> keyMap = new HashMap<>();
        String[] pairs = key.split("\\.");
        for (String pair : pairs){
            String[] content = pair.split(":");
            if(content.length != 2){
                continue;
            }
            keyMap.put(content[0],content[1]);
        }
        return keyMap;
    }

    public static String getValue(String key,String name) {
        return parseKey(key).get(name);
    }
    public static String getNamespace(String key ) {
        return getValue(key,"env");
    }

    public static String getReleaseName(String key ) {
        return getValue(key,"release");
    }
    public static String getResourceName(String key){
        String[] pairs = key.split("\\.");
        String content = pairs[pairs.length-1];
        return content.split(":")[1];
    }
    public static String getResourceType(String key) {
        String[] pairs = key.split("\\.");
        String content = pairs[pairs.length-1];
        return content.split(":")[0];
    }
}
