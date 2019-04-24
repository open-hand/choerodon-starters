FROM alpine:3.5

ENV WORK_PATH /var/choerodon

RUN mkdir -p $WORK_PATH

RUN apk update && apk add bash tzdata openjdk8 \
    && cp -r -f /usr/share/zoneinfo/Hongkong /etc/localtime \
    && echo -ne "Alpine Linux 3.4 image. (`uname -rsv`)\n" >> /root/.built

COPY choerodon-tool-config/target/choerodon-tool-config.jar $WORK_PATH
COPY choerodon-tool-liquibase/target/choerodon-tool-liquibase.jar $WORK_PATH
