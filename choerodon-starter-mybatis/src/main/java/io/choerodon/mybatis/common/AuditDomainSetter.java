package io.choerodon.mybatis.common;

import java.util.Date;

public interface AuditDomainSetter {

    default void setCreationDate(Date creationDate){

    };

    default void setCreatedBy(Long createdBy){

    };

    default void setLastUpdateDate(Date lastUpdateDate) {

    }

    default void setLastUpdatedBy(Long lastUpdatedBy) {

    }
}
