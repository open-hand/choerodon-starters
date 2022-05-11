package io.choerodon.onlyoffice.service.impl;

import org.springframework.web.multipart.MultipartFile;

import io.choerodon.onlyoffice.service.OnlyOfficeFileHandler;

/**
 * Created by wangxiang on 2022/5/11
 */
public abstract class AbstractOnlyOfficeFileHandler implements OnlyOfficeFileHandler {

    @Override
    public void fileProcess(MultipartFile multipartFile, Long businessId) {
        fileBusinessProcess(multipartFile, businessId);
    }

    protected abstract void fileBusinessProcess(MultipartFile multipartFile, Long businessId);
}
