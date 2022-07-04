package io.choerodon.onlyoffice.service;

import org.springframework.web.multipart.MultipartFile;

import io.choerodon.onlyoffice.vo.DocumentEditCallback;

/**
 * Created by wangxiang on 2022/5/11
 */
public interface OnlyOfficeFileHandler {
    void fileProcess(MultipartFile multipartFile, DocumentEditCallback documentEditCallback);
}
