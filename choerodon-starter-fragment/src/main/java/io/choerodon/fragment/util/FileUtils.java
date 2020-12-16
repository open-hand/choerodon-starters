package io.choerodon.fragment.util;

import java.io.File;

/**
 * @author scp
 * @date 2020/11/18
 * @description
 */
public class FileUtils {

    /**
     * 删除文件
     *
     * @param file 文件
     */
    public static void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File f : files) {
                deleteFile(f);
            }
        }
        file.delete();
    }
}
