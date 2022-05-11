package io.choerodon.onlyoffice.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by wangxiang on 2022/5/11
 */
public interface OnlyOfficeFileHandler {
    void fileProcess(MultipartFile multipartFile, Long businessId);
}
