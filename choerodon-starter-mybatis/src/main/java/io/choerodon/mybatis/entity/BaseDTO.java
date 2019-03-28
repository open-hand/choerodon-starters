package io.choerodon.mybatis.entity;

import io.choerodon.base.entity.BaseEntity;
import tk.mybatis.mapper.annotation.Version;

/**
 * 带有标准字段的 DTO 基类.
 *
 * @author shengyang.zhou@hand-china.com
 */
public class BaseDTO extends BaseEntity {

    @Version
    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        super.setObjectVersionNumber(objectVersionNumber);
    }
}
