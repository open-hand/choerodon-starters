package io.choerodon.swagger.notify;

import java.util.Objects;

/**
 * @author dengyouquan
 **/
public class NotifyTemplateScanData {
    private String businessType;
    private String title;
    private String content;
    private String type;

    public NotifyTemplateScanData() {
    }

    public NotifyTemplateScanData(String businessType, String title, String content, String type) {
        this.businessType = businessType;
        this.title = title;
        this.content = content;
        this.type = type;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "NotifyTemplateScanData{" +
                "businessType='" + businessType + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotifyTemplateScanData that = (NotifyTemplateScanData) o;
        return Objects.equals(getType(), that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType());
    }
}
