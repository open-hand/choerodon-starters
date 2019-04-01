package io.choerodon.dataset.metadata.mapper;

import io.choerodon.dataset.metadata.dto.MetadataTable;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;
import java.util.Map;

public interface MetadataTableMapper extends Mapper<MetadataTable> {
    List<Map> selectMetadataTables();
    List<MetadataTable> selectAllTables();
}
