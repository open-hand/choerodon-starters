package io.choerodon.config.utils;

public class CreateConfigDTO {

    private String service;

    private String version;

    private String profile;

    private String namespace;

    private String yaml;

    private String updatePolicy;

    public String getService() {
        return service;
    }

    public CreateConfigDTO setService(String service) {
        this.service = service;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public CreateConfigDTO setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getProfile() {
        return profile;
    }

    public CreateConfigDTO setProfile(String profile) {
        this.profile = profile;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public CreateConfigDTO setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getYaml() {
        return yaml;
    }

    public CreateConfigDTO setYaml(String yaml) {
        this.yaml = yaml;
        return this;
    }

    public String getUpdatePolicy() {
        return updatePolicy;
    }

    public CreateConfigDTO setUpdatePolicy(String updatePolicy) {
        this.updatePolicy = updatePolicy;
        return this;
    }

    @Override
    public String toString() {
        return "CreateConfigDTO{" +
                "service='" + service + '\'' +
                ", version='" + version + '\'' +
                ", profile='" + profile + '\'' +
                ", namespace='" + namespace + '\'' +
                ", yaml='" + yaml + '\'' +
                ", updatePolicy='" + updatePolicy + '\'' +
                '}';
    }
}
