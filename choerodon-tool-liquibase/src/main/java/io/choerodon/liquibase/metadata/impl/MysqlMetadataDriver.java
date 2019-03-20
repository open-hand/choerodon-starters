package io.choerodon.liquibase.metadata.impl;

import javax.sql.DataSource;

/**
 * 由于BaseMetadataDriver基于Mysql编写，这个类几乎不需要实现什么方法。
 */
public class MysqlMetadataDriver extends BaseMetadataDriver {

    public MysqlMetadataDriver(DataSource source) {
        super(source);
    }

}
