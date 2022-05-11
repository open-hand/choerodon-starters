package io.choerodon.onlyoffice.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by wangxiang on 2022/5/6
 */
public class DocumentEditCallback {
    @ApiModelProperty("文件的后缀")
    private String fileType;

    @ApiModelProperty("fileId")
    private String key;

    @ApiModelProperty("新文件的下载地址")
    private String url;

    @ApiModelProperty("文件的名称")
    private String title;

    private Integer status;

    @ApiModelProperty("第三方业务Id")
    private Long businessId;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }
}
