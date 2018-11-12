package io.choerodon.eureka.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.netflix.appinfo.InstanceInfo;

import java.util.Date;
import java.util.Objects;

public class EurekaEventPayload {

    private static final String VERSION_STR = "VERSION";

    private static final String DEFAULT_VERSION_NAME = "unknown";

    private String id;

    private String status;

    private String appName;

    private String version;

    private String instanceAddress;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date createTime;

    private String apiData;

    public EurekaEventPayload(InstanceInfo instanceInfo) {
        this.id = instanceInfo.getId();
        this.status = instanceInfo.getStatus().name();
        this.appName = instanceInfo.getAppName().toLowerCase();
        this.version = instanceInfo.getMetadata().get(VERSION_STR);
        this.version = version == null ? DEFAULT_VERSION_NAME : version;
        this.instanceAddress = instanceInfo.getIPAddr() + ":" + instanceInfo.getPort();
        this.createTime = new Date();
    }

    public EurekaEventPayload() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getInstanceAddress() {
        return instanceAddress;
    }

    public void setInstanceAddress(String instanceAddress) {
        this.instanceAddress = instanceAddress;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getApiData() {
        return apiData;
    }

    public void setApiData(String apiData) {
        this.apiData = apiData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EurekaEventPayload that = (EurekaEventPayload) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EurekaEventPayload{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", appName='" + appName + '\'' +
                ", version='" + version + '\'' +
                ", instanceAddress='" + instanceAddress + '\'' +
                ", createTime=" + createTime +
                ", apiData='" + apiData + '\'' +
                '}';
    }
}
