package io.choerodon.mybatis.common;

import io.choerodon.mybatis.entity.BaseConstants;
import io.choerodon.mybatis.provider.UpdateByPrimaryKeyExampleProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * Created by jialong.zuo@hand-china.com on 2017/5/22.
 */
public interface UpdateByPrimaryKeyExampleMapper<T> {

    /**
     * 根据主键更新选定字段的值
     * @return
     */
    @UpdateProvider(type = UpdateByPrimaryKeyExampleProvider.class, method = "dynamicSQL")
    int updateByPrimaryKeyExample(@Param(BaseConstants.OPTIONS_DTO) T record, @Param("example") Object example);

}
