package io.choerodon.dataset.model.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.dataset.metadata.IMetadataTableService;
import io.choerodon.dataset.metadata.dto.MetadataColumn;
import io.choerodon.dataset.metadata.dto.MetadataTable;
import io.choerodon.dataset.exception.DatasetException;
import io.choerodon.dataset.model.DatasetExecutor;
import io.choerodon.dataset.service.IDatasetService;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationUtils;
import tk.mybatis.mapper.util.StringUtil;

import javax.persistence.Table;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author xausky
 */
public class ServiceDatasetExecutor implements DatasetExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDatasetExecutor.class);
    private Class<?> generic;
    private ObjectMapper mapper;
    private IDatasetService service;
    private CommonLanguageAction languageAction;
    private CommonValidateAction validateAction;

    public ServiceDatasetExecutor(IDatasetService service, ObjectMapper mapper, String name,
                                  Configuration configuration, IMetadataTableService metadataTableService) {
        this.mapper = mapper;
        this.service = service;
        this.generic = GenericTypeResolver.resolveTypeArgument(service.getClass(), IDatasetService.class);
        Table tableAnnotation = AnnotationUtils.findAnnotation(generic, javax.persistence.Table.class);
        if (tableAnnotation == null) {
            LOGGER.warn("DTO class not annotation @Table {}, skip multi-language support.", generic);
            return;
        }
        String tableName = tableAnnotation.name().toLowerCase();
        MetadataTable table = metadataTableService.queryTable(tableName);
        if (table == null) {
            throw new DatasetException("dataset table notFound: " + tableName);
        }
        Optional<String> key = table.getPrimaryColumns().stream().findFirst();
        String key1;
        if (key.isPresent()) {
            key1 = StringUtil.underlineToCamelhump(key.get().toLowerCase());
        } else {
            throw new DatasetException("dataset key notFound: " + tableName);
        }
        Set<String> tlColumns = new TreeSet<>();
        for (MetadataColumn column : table.getColumns()) {
            if (Boolean.TRUE.equals(column.getMultiLanguage())) {
                tlColumns.add(StringUtil.underlineToCamelhump(column.getColumnName().toLowerCase()));
            }
        }
        if (!tlColumns.isEmpty()) {
            languageAction = new CommonLanguageAction(name, key1, tableName, tlColumns, configuration);
        }
        validateAction = new CommonValidateAction(name, table, configuration);
    }

    @Override
    public List<?> queries(SqlSession session, Map<String, Object> body, Integer pageNum, Integer pageSize, String sortname, Boolean isDesc) {
        return service.queries(body, pageNum, pageSize, sortname, isDesc);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<?> mutations(SqlSession session, String json, Map.Entry<String, Object> parentKey) {
        try {
            List objs = mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, generic));
            return service.mutations(objs);
        } catch (IOException e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> languages(SqlSession session, Map<String, Object> body) {
        if (languageAction == null) {
            throw new DatasetException("dataset.language.unsupported");
        }
        return (Map<String, Object>) languageAction.invoke(session, body);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Boolean> validate(SqlSession session, Map<String, Object> body, Map.Entry<String, Object> parentKey) throws DatasetException {
        if (validateAction == null) {
            throw new DatasetException("dataset.validate.unsupported");
        }
        return (List<Boolean>)validateAction.invoke(session, body);
    }
}
