package io.choerodon.mybatis.common;


import tk.mybatis.mapper.common.ExampleMapper;
import tk.mybatis.mapper.common.Marker;
import tk.mybatis.mapper.common.RowBoundsMapper;
import tk.mybatis.mapper.common.base.BaseDeleteMapper;
import tk.mybatis.mapper.common.base.BaseInsertMapper;
import tk.mybatis.mapper.common.base.BaseUpdateMapper;
import tk.mybatis.mapper.common.base.select.ExistsWithPrimaryKeyMapper;
import tk.mybatis.mapper.common.base.select.SelectCountMapper;
import tk.mybatis.mapper.common.example.DeleteByExampleMapper;
import tk.mybatis.mapper.common.example.SelectCountByExampleMapper;
import tk.mybatis.mapper.common.example.SelectOneByExampleMapper;
import tk.mybatis.mapper.common.example.UpdateByExampleMapper;
import tk.mybatis.mapper.common.example.UpdateByExampleSelectiveMapper;

@tk.mybatis.mapper.annotation.RegisterMapper
public interface Mapper<T> extends
        SelectCountMapper<T>,
        ExistsWithPrimaryKeyMapper<T>,
        BaseInsertMapper<T>,
        BaseUpdateMapper<T>,
        BaseDeleteMapper<T>,
        SelectOneByExampleMapper<T>,
        SelectCountByExampleMapper<T>,
        DeleteByExampleMapper<T>,
        UpdateByExampleMapper<T>,
        UpdateByExampleSelectiveMapper<T>,
        RowBoundsMapper<T>,
        MultiLanguageSelectMapper<T>,
        UpdateByPrimaryKeyExampleMapper<T>,
        SelectOptionsMapper<T>,
        Marker {
}