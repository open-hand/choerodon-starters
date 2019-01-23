package io.choerodon.config.execute;

import io.choerodon.config.parser.Parser;
import io.choerodon.config.parser.ParserFactory;
import io.choerodon.config.utils.ConfigFileFormat;
import io.choerodon.config.utils.FileUtil;
import io.choerodon.config.utils.InitConfigException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

abstract class AbstractExecutor implements Executor {

    private final FileUtil fileUtil = new FileUtil();

    Map<String, Object> parseFileToMap(final String configFile) throws IOException {
        File file = new File(configFile);
        ConfigFileFormat fileFormat = ConfigFileFormat.fromString(fileUtil.getFileExt(file));
        Parser parser = ParserFactory.getParser(fileFormat);
        return parser.parse(file);
    }

    String readFile(final String configFile) throws IOException {
        File file = new File(configFile);
        if (!file.exists()) {
            throw new InitConfigException("Read file is not exist, file: " + configFile);
        }
        try (FileReader reader = new FileReader(file);
             BufferedReader bReader = new BufferedReader(reader)) {
            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = bReader.readLine()) != null) {
                sb.append(s + "\n");
            }
            return sb.toString();
        }
    }

    SortConfigMap sort(Map<String, Object> map) {
        Set<String> zuulKeySet = map.keySet().stream()
                .filter(o -> o.startsWith("zuul.route")).collect(Collectors.toSet());
        Map<String, Object> zuulMap = zuulKeySet.stream()
                .filter(i -> i != null && map.get(i) != null).collect(Collectors.toMap(i -> i, map::get));
        Map<String, Object> commonMap = map.keySet().stream()
                .filter(i -> !zuulKeySet.contains(i)).collect(Collectors.toMap(i -> i, map::get));
        return new SortConfigMap(commonMap, zuulMap);
    }

    static class SortConfigMap {
        Map<String, Object> commonMap;
        Map<String, Object> zuulMap;

        SortConfigMap(Map<String, Object> commonMap, Map<String, Object> zuulMap) {
            this.commonMap = commonMap;
            this.zuulMap = zuulMap;
        }
    }
}
