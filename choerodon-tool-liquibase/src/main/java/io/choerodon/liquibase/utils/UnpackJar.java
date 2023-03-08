package io.choerodon.liquibase.utils;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

/**
 * @author scp
 * @date 2020/4/17
 * @description
 */
@Component
public class UnpackJar {
    private static final Logger logger = LoggerFactory.getLogger(UnpackJar.class);
    private static final String SUFFIX_XLSX = ".xlsx";
    private static final String SUFFIX_GROOVY = ".groovy";
    private static final String SUFFIX_XML = ".xml";
    private static final String SUFFIX_SQL = ".sql";
    private static final String SUFFIX_JAR = ".jar";
    private static final String PREFIX_SCRIPT_DB = "script/";
    private static final String PREFIX_SPRING_BOOT_CLASSES = "BOOT-INF/classes/";

    /**
     * 从jar包输入流解压需要的文件到目标目录
     *
     * @param inputStream jar包输入流
     * @param dir         目标目录
     * @param dep         是否是Spring Boot依赖包的解压， 只有为false时
     * @throws IOException 出现IO错误
     */
    private void extraJarStream(InputStream inputStream, String dir, boolean dep, boolean jarInit, List<String> skipFileList) throws IOException {
        JarEntry entry = null;
        JarInputStream jarInputStream = new JarInputStream(inputStream);
        while ((entry = jarInputStream.getNextJarEntry()) != null) {
            String name = entry.getName();
            if (((name.endsWith(SUFFIX_GROOVY)
                    || name.endsWith(SUFFIX_XML)
                    || name.endsWith(SUFFIX_XLSX)
                    || name.endsWith(SUFFIX_SQL)) && name.contains(PREFIX_SCRIPT_DB))) {
                if (name.startsWith(PREFIX_SPRING_BOOT_CLASSES)) {
                    name = name.substring(PREFIX_SPRING_BOOT_CLASSES.length());
                }
                File file = new File(dir + name);
                if (isSkipFile(skipFileList, file.getName())) {
                    file.deleteOnExit();
                    continue;
                }
                if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                    throw new IOException("create dir fail: " + file.getParentFile().getAbsolutePath());
                }
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    StreamUtils.copy(jarInputStream, outputStream);
                }
            } else if (name.endsWith(SUFFIX_JAR) && jarInit) {
                extraJarStream(jarInputStream, dir, true, true, skipFileList);
            }
        }
    }

    public void extra(String jar, String dir, boolean jarInit, String skipFile) throws IOException {
        boolean isUrl = jar.startsWith("https://") || jar.startsWith("http://") || jar.startsWith("file://");
        try (InputStream inputStream = isUrl ? new URL(jar).openStream() : new FileInputStream(jar)) {
            File temp = new File(dir);
            FileUtils.deleteDirectory(temp);
            if (!temp.mkdir()) {
                throw new IOException("create dir fail.");
            }
            List<String> skipFileList = null;
            if (!StringUtils.isEmpty(skipFile)) {
                skipFileList = Arrays.asList(skipFile.split(",").clone());
            }
            extraJarStream(inputStream, dir, false, jarInit, skipFileList);
        }
        logger.info("Jar extra {} done", jar);
    }

    private Boolean isSkipFile(List<String> skipFileList, String fileName) {
        boolean index = false;
        if (!CollectionUtils.isEmpty(skipFileList)) {
            for (String t : skipFileList) {
                if (t.equals(fileName)) {
                    index = true;
                    break;
                }
            }
        }
        return index;
    }

}

