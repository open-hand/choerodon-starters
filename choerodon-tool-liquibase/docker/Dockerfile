FROM alpine:3.11.5

ENV WORK_PATH /var/choerodon

RUN mkdir -p $WORK_PATH

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories \
    && apk update && apk add bash tzdata openjdk8 \
    && cp -r -f /usr/share/zoneinfo/Hongkong /etc/localtime \
    && echo -ne "Alpine Linux 3.4 image. (`uname -rsv`)\n" >> /root/.built

USER 33

COPY choerodon-tool-liquibase.jar $WORK_PATH
