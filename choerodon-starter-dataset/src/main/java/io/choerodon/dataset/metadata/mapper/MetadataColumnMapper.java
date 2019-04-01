package io.choerodon.dataset.metadata.mapper;

import io.choerodon.dataset.metadata.dto.MetadataColumn;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MetadataColumnMapper extends Mapper<MetadataColumn> {
    List<MetadataColumn> selectColumnByTableName(@Param("tableName") String tableName);
}
