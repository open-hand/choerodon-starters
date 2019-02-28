package io.choerodon.mybatis.common;

import io.choerodon.mybatis.provider.MultiLanguageSelectProvider;
import org.apache.ibatis.annotations.SelectProvider;
import tk.mybatis.mapper.provider.ExampleProvider;

import java.util.List;

public interface MultiLanguageSelectMapper<T> {

    /**
     * 根据实体中的属性进行查询，只能有一个返回值，有多个结果是抛出异常，查询条件使用等号
     *
     * @param record
     * @return
     */
    @SelectProvider(type = MultiLanguageSelectProvider.class, method = "dynamicSQL")
    T selectOne(T record);

    /**
     * 根据实体中的属性值进行查询，查询条件使用等号
     *
     * @param record
     * @return
     */
    @SelectProvider(type = MultiLanguageSelectProvider.class, method = "dynamicSQL")
    List<T> select(T record);

    /**
     * 查询全部结果
     *
     * @return
     */
    @SelectProvider(type = MultiLanguageSelectProvider.class, method = "dynamicSQL")
    List<T> selectAll();

    /**
     * 根据主键字段进行查询，方法参数必须包含完整的主键属性，查询条件使用等号
     *
     * @param key
     * @return
     */
    @SelectProvider(type = MultiLanguageSelectProvider.class, method = "dynamicSQL")
    T selectByPrimaryKey(Object key);

    /**
     * 查询基表全部结果
     *
     * @return
     */
    @SelectProvider(type = MultiLanguageSelectProvider.class, method = "dynamicSQL")
    List<T> selectAllWithoutMultiLanguage();

    /**
     * 根据Example条件进行查询
     *
     * @param example
     * @return
     */
    @SelectProvider(type = MultiLanguageSelectProvider.class, method = "dynamicSQL")
    List<T> selectByExample(Object example);

}
