package io.choerodon.fragment.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Pair;
import org.hzero.fragment.config.FragmentConfig;
import org.hzero.fragment.service.FileHandler;
import org.hzero.fragment.service.FragmentService;
import org.hzero.fragment.service.impl.FragmentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/02/18 14:07
 */
@Service
public class FragmentC7nServiceImpl extends FragmentServiceImpl implements FragmentService {

    private static final Logger logger = LoggerFactory.getLogger(FragmentC7nServiceImpl.class);

    @Autowired
    private FragmentConfig fragmentConfig;

    @Override
    public String combineUpload(String guid, Long tenantId, String filename, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>(1);
        }
        Pair<String, String> pair = combineBlock(guid, filename);
        String filePath = pair.getFirst();
        String tempDtr = pair.getSecond();
        try {
            Map<String, FileHandler> map = ApplicationContextHelper.getContext().getBeansOfType(FileHandler.class);
            String url = null;
            if (!map.isEmpty()) {
                for (FileHandler handler : map.values()) {
                    url = handler.process(tenantId, filename, filePath, new FileInputStream(new File(filePath)), params);
                }
            }
            // 删除分片
            deleteFile(new File(tempDtr));
            return url;
        } catch (IOException e) {
            logger.error("exception:", e);
            deleteFile(new File(filePath));
            return null;
        } catch (Exception e) {
            logger.error("exception:", e);
            deleteFile(new File(filePath));
            throw e;
        }
    }

    @Override
    public void upload(MultipartFile file, Integer chunk, String guid) {
        try (InputStream inputStream = file.getInputStream()) {
            if (chunk == null) {
                chunk = 0;
            }
            String filePath = getRootPath() + File.separator + TEMP + File.separator + guid + File.separator + chunk;
            FileUtils.copyInputStreamToFile(inputStream, new File(filePath));
        } catch (Exception e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        }
    }

    private String getRootPath() {
        String rootPath = fragmentConfig.getRootPath();
        if (StringUtils.isBlank(rootPath)) {
            return ROOT;
        }
        return rootPath;
    }

    /**
     * 删除文件
     *
     * @param file 文件
     */
    private void deleteFile(File file) {
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
