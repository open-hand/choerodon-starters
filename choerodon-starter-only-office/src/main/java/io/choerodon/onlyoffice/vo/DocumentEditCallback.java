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

    //status
    //
    //定义文档的状态。可以具有以下值：
    //
    //* 0 - 找不到带有密钥标识符的文档，
    //
    //* 1 - 正在编辑文档，
    //
    //* 2 - 文件已准备好保存，
    //
    //* 3 - 发生了文档保存错误，
    //
    //* 4 - 文件关闭，没有变化，
    //
    //* 6 - 正在编辑文档，但保存当前文档状态，
    //
    //* 7 - 强制保存文档时发生错误。
    private Integer status;

    @ApiModelProperty("第三方业务Id")
    private Long businessId;

    @ApiModelProperty("第三方用户Id")
    private Long userId;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
