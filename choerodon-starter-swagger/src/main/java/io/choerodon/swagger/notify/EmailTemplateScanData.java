package io.choerodon.swagger.notify;

import java.util.Objects;

public class EmailTemplateScanData {

    private String businessType;
    private String code;
    private String name;
    private String title;
    private String content;

    public EmailTemplateScanData(String businessType, String code, String name, String title, String content) {
        this.businessType = businessType;
        this.code = code;
        this.name = name;
        this.title = title;
        this.content = content;
    }

    public EmailTemplateScanData() {
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "EmailTemplateScanData{" +
                "businessType='" + businessType + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailTemplateScanData that = (EmailTemplateScanData) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code);
    }
}
