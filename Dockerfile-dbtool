FROM registry.cn-shanghai.aliyuncs.com/c7n/javabase:0.9.0

ENV TOOL_LIQUIBASE_VERSION="0.15.0.RELEASE" \
    TOOL_CONFIG_VERSION="0.15.0.RELEASE"

ADD --chown=www-data:www-data https://nexus.choerodon.com.cn/repository/choerodon-maven/io/choerodon/choerodon-tool-liquibase/${TOOL_LIQUIBASE_VERSION}/choerodon-tool-liquibase-${TOOL_LIQUIBASE_VERSION}.jar \
    /var/choerodon/choerodon-tool-liquibase.jar
ADD --chown=www-data:www-data https://nexus.choerodon.com.cn/repository/choerodon-maven/io/choerodon/choerodon-tool-config/${TOOL_CONFIG_VERSION}/choerodon-tool-config-${TOOL_CONFIG_VERSION}.jar \
    /var/choerodon/choerodon-tool-config.jar

USER www-data