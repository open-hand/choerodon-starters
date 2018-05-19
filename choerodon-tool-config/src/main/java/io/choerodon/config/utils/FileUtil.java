package io.choerodon.config.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

import java.io.*;
import java.net.URL;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import io.choerodon.core.exception.CommonException;


/**
 * 对jar包文件的进行解析的工具类
 *
 * @author wuguokai
 */
public class FileUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 架包拆解
     *
     * @param jar 架包名
     * @param dir 架包文件拆解目录
     * @throws IOException IO异常
     */
    public void extra(String jar, String dir) throws IOException {
        LOGGER.info("jar拆解");
        boolean re = jar.startsWith("https://") || jar.startsWith("http://") || jar.startsWith("file://");
        try (
                InputStream inputStream = re ? new URL(jar).openStream() : new FileInputStream(jar);
                JarInputStream jarInputStream = new JarInputStream(inputStream);
        ) {
            JarEntry entry;
            File temp = new File(dir);
            FileUtils.deleteDirectory(temp);
            if (!temp.mkdir()) {
                throw new IOException("create dir fail.");
            }
            while ((entry = jarInputStream.getNextJarEntry()) != null) {
                String name = entry.getName();
                File file = new File(dir + name);
                if (entry.isDirectory()) {
                    if (!file.mkdirs()) {
                        throw new IOException("create dir fail.");
                    }
                } else if (name.endsWith(".yaml") || name.endsWith(".yml") || name.endsWith(".properties")) {
                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        StreamUtils.copy(jarInputStream, outputStream);
                    }
                }
            }
            LOGGER.info("jar拆解完成");
        }
    }

    /**
     * 用于获取jar中匹配参数设定的文件夹，若无设置文件夹参数，则默认匹配根目录进行全扫描
     *
     * @param fileList       文件夹集合
     * @param configFileName 要寻找的配置文件名
     * @return 配置文件路径
     */
    public String getDirInJar(List<File> fileList, String configFileName) {
        if (configFileName == null) {
            throw new IllegalArgumentException("必须指定jar包中配置文件名");
        }
        String res = null;
        for (File file : fileList) {
            String normalPath = file.getPath().replace("\\", "/");
            int index = normalPath.indexOf(configFileName);
            if (index != -1 && index + configFileName.length() == normalPath.length()) {
                res = normalPath;
                break;
            }
        }
        if (res == null) {
            throw new CommonException(configFileName + " not exist.");
        }
        LOGGER.info("文件路径获取:{} ", res);
        return res;
    }

    /**
     * 载入jar时，递归获取所有文件夹
     *
     * @param file 需要获取文件夹的根路径
     * @return 所有文件夹的集合
     */
    public List<File> getDirRecursive(File file) {
        ArrayList<File> dirList = new ArrayList<>();
        if (!file.isDirectory()) {
            return dirList;
        }
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            dirList.addAll(Arrays.asList(files));
            List<File> tmpList = dirList.stream()
                    .map(this::getDirRecursive).flatMap(List::stream)
                    .collect(Collectors.toList());
            dirList.addAll(tmpList);
        }
        return dirList;
    }

    /**
     * 获取文件后缀
     *
     * @param file 文件
     * @return 文件后缀字符串
     */
    public String getFileExt(File file) {
        String fileName = file.getName();
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
