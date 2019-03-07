package io.choerodon.mybatis.common;

import io.choerodon.mybatis.entity.BaseConstants;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.mybatis.provider.SelectOptionsProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @author njq.niu@hand-china.com
 */
public interface SelectOptionsMapper<T> {

    /**
     * 按照主键有条件查询.
     *
     * @param record
     * @return dto
     */
    @SelectProvider(type = SelectOptionsProvider.class, method = "selectOptionsByPrimaryKey")
    T selectOptionsByPrimaryKey(T record);


    /**
     * 有条件查询.
     *
     * @param record
     * @param criteria
     * @return list
     */
    @SelectProvider(type = SelectOptionsProvider.class, method = "selectOptions")
    List<T> selectOptions(@Param(BaseConstants.OPTIONS_DTO) T record, @Param(BaseConstants.OPTIONS_CRITERIA) Criteria criteria);
}
