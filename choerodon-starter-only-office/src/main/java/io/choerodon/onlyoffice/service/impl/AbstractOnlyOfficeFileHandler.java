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

    /**
     * 给业务系统的勾子函数
     * @param multipartFile
     * @param businessId
     */
    protected abstract void fileBusinessProcess(MultipartFile multipartFile, Long businessId);
}
