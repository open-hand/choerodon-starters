package io.choerodon.config.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class InitConfigProperties {

    public static final String TYPE_CONFIG_SERVER = "db";

    public static final String TYPE_REGISTER_SERVER = "k8s";

    public static final String UPDATE_POLICY_NOT = "not";

    public static final String UPDATE_POLICY_ADD = "add";

    public static final String UPDATE_POLICY_OVERRIDE = "override";

    private Config config = new Config();

    private Service service = new Service();

    private Register register = new Register();

    private Gateway gateway = new Gateway();

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Register getRegister() {
        return register;
    }

    public Gateway getGateway() {
        return gateway;
    }

    public void setGateway(Gateway gateway) {
        this.gateway = gateway;
    }

    public void setRegister(Register register) {
        this.register = register;
    }

    public static class Config {
        // 初始化的配置文件名
        private String file = "application.yml";

        // 初始化文件所在jar包
        private String jar;

        // 初始化配置的方式
        private String type = TYPE_CONFIG_SERVER;

        private String profile = "default";

        private String updatePolicy = UPDATE_POLICY_NOT;

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getJar() {
            return jar;
        }

        public void setJar(String jar) {
            this.jar = jar;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

        public String getUpdatePolicy() {
            return updatePolicy;
        }

        public void setUpdatePolicy(String updatePolicy) {
            this.updatePolicy = updatePolicy;
        }
    }

    public static class Service {

        private String name;

        private String version = "v1";

        private String namespace;

        public String getName() {
            if (this.name == null) {
                throw new InitConfigException("you must set 'service.name' value");
            }
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getNamespace() {
            if (this.namespace == null) {
                throw new InitConfigException("you must set 'service.namespace' value when configType is 'k8s'");
            }
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }
    }


    public static class Register {
        private String host;

        public String getHost() {
            if (this.host == null) {
                throw new InitConfigException("you must set 'register.host' value when configType is 'k8s'");
            }
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }
    }

    public static class Gateway {
        private String[] names = new String[]{"api-gateway"};

        public String[] getNames() {
            return names;
        }

        public void setNames(String[] names) {
            this.names = names;
        }
    }

}
