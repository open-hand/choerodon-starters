package io.choerodon.dataset.metadata;

import io.choerodon.dataset.metadata.dto.MetadataTable;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

public interface IMetadataTableService extends IBaseService<MetadataTable> {
    MetadataTable queryTable(String name);
    List<MetadataTable> selectAllTables();
}
