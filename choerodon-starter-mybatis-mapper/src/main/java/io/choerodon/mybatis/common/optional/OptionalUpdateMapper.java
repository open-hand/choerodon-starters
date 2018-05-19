package io.choerodon.mybatis.common.optional;

import io.choerodon.mybatis.provider.optional.OptionalProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * Created by xausky on 3/20/17.
 */
public interface OptionalUpdateMapper<T> {

    @UpdateProvider(type = OptionalProvider.class, method = "dynamicSql")
    @Options(useCache = false, useGeneratedKeys = false)
    int updateOptional(T record);

}
