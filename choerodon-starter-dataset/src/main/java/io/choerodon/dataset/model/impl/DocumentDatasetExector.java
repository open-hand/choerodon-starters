package io.choerodon.dataset.model.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.dataset.metadata.IMetadataTableService;
import io.choerodon.dataset.metadata.dto.MetadataColumn;
import io.choerodon.dataset.metadata.dto.MetadataTable;
import io.choerodon.dataset.exception.DatasetException;
import io.choerodon.dataset.model.Action;
import io.choerodon.dataset.model.DatasetExecutor;
import io.choerodon.dataset.service.IDatasetRepositoryService;
import io.choerodon.dataset.service.impl.DatasetRepositoryServiceImpl;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tk.mybatis.mapper.util.StringUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentDatasetExector implements DatasetExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentDatasetExector.class);
    private static final SpelExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();
    private static final ThreadLocal<StandardEvaluationContext> evaluationContext = new ThreadLocal<>();

    private String key;
    private IDatasetRepositoryService service;
    private Map<Action.Type, Action> actions = new ConcurrentHashMap<>();
    private ApplicationContext applicationContext;

    public enum KeyType {
        UUID, AUTO_INCREMENT
    }

    public DocumentDatasetExector(Element element, Configuration configuration, IMetadataTableService metadataTableService,
                                  IDatasetRepositoryService service, ApplicationContext applicationContext) throws DatasetException {
        this.service = service;
        this.applicationContext = applicationContext;
        String name = element.getAttribute("id") + "." + System.currentTimeMillis();
        String tableName = element.getAttribute("table");
        KeyType keyType = element.getAttribute("keyType").equals("uuid") ? KeyType.UUID : KeyType.AUTO_INCREMENT;
        MetadataTable table = metadataTableService.queryTable(tableName);
        Optional<String> key = table.getPrimaryColumns().stream().findFirst();
        if (key.isPresent()) {
            this.key = StringUtil.underlineToCamelhump(key.get().toLowerCase());
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
            actions.put(Action.Type.LANGUAGES, new CommonLanguageAction(name, this.key, tableName, tlColumns, configuration));
        }
        actions.put(Action.Type.VEILDATA, new CommonValidateAction(name, table, configuration));
        NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); ++i) {
            Node node = list.item(i);
            if (node instanceof Element) {
                Action.Type type = Action.Type.valueOf(((Element) node).getTagName().toUpperCase());
                String mybatisMapper = ((Element) node).getAttribute("statementId");
                if (!mybatisMapper.isEmpty()) {
                    actions.put(type, new MybatisMapperAction(mybatisMapper, type, ((Element) node)));
                } else {
                    switch (type) {
                        case SELECT:
                            actions.put(type, new DocumentSelectAction(name, ((Element) node), this.key, tableName, tlColumns, configuration, metadataTableService));
                            break;
                        case UPDATE:
                            actions.put(type, new DocumentUpdateAction(name, ((Element) node), this.key, tableName, tlColumns, configuration));
                            break;
                        case DELETE:
                            actions.put(type, new DocumentDeleteAction(name, ((Element) node), this.key, tableName, tlColumns, configuration));
                            break;
                        case INSERT:
                            actions.put(type, new DocumentInsertAction(name, ((Element) node), this.key, tableName, tlColumns, configuration, keyType));
                            break;
                        default:
                    }
                }
            }
        }
    }

    @Override
    public List<?> queries(SqlSession session, Map<String, Object> body, Integer pageNum, Integer pageSize, String sort, Boolean desc) throws DatasetException {
        Action action = actions.get(Action.Type.SELECT);
        if (action == null) {
            throw new DatasetException("dataset.select.notFound");
        }
        if (pageNum != null && pageSize != null) {
            PageHelper.startPage(pageNum, pageSize);
        }
        if (evaluationContext.get() == null) {
            evaluationContext.set(new StandardEvaluationContext());
            evaluationContext.get().setBeanResolver((context, beanName) -> applicationContext.getBean(beanName));
        }
        if(sort != null){
            body.put("__sort", sort);
            if(Boolean.TRUE.equals(desc)){
                body.put("__order", "DESC");
            } else {
                body.put("__order", "ASC");
            }
        }
        return (List<?>) action.process(SPEL_EXPRESSION_PARSER, evaluationContext.get(), session, body);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<?> mutations(SqlSession session, String body, Map.Entry<String, Object> parentKey) throws DatasetException {
        Action action;
        try {
            if (evaluationContext.get() == null) {
                evaluationContext.set(new StandardEvaluationContext());
                evaluationContext.get().setBeanResolver((context, beanName) -> applicationContext.getBean(beanName));
            }
            List<Map<String, Object>> maps = DatasetRepositoryServiceImpl.MAPPER.readValue(body,
                    DatasetRepositoryServiceImpl.MAPPER.getTypeFactory().constructCollectionType(List.class, Map.class));
            for (Map<String, Object> map : maps) {
                if (parentKey != null) {
                    map.put(parentKey.getKey(), parentKey.getValue());
                }
                String status = (String) map.get("__status");
                if ("delete".equals(status)) {
                    action = actions.get(Action.Type.DELETE);
                    if (action == null) {
                        throw new DatasetException("dataset.delete.notFound");
                    }
                    action.process(SPEL_EXPRESSION_PARSER, evaluationContext.get(), session, map);
                } else if ("add".equals(status)) {
                    action = actions.get(Action.Type.INSERT);
                    if (action == null) {
                        throw new DatasetException("dataset.insert.notFound");
                    }
                    action.process(SPEL_EXPRESSION_PARSER, evaluationContext.get(), session, map);
                } else if ("update".equals(status)) {
                    action = actions.get(Action.Type.UPDATE);
                    if (action == null) {
                        throw new DatasetException("dataset.update.notFound");
                    }
                    action.process(SPEL_EXPRESSION_PARSER, evaluationContext.get(), session, map);
                }
                for (String columnKey : map.keySet()) {
                    Object columnValue = map.get(columnKey);
                    if (columnValue instanceof List) {
                        DatasetExecutor executor = service.getExecutor(columnKey);
                        if (executor == null) {
                            throw new DatasetException("dataset.notFound", columnKey);
                        }
                        if ("delete".equals(status) && map.get(key) != null) {
                            //删除子数据需要先查询，然后删除
                            Map<String, Object> relative = (Map<String, Object>) ((List) columnValue).get(0);
                            List<Map<String, Object>> list = (List<Map<String, Object>>) executor.queries(session, Collections.singletonMap(key, map.get(key)), null, null, null, null);
                            for (Map<String, Object> item : list) {
                                item.put("__status", "delete");
                                item.putAll(relative);
                            }
                            map.put(columnKey, list);
                            columnValue = list;
                        }
                        List<?> result = executor.mutations(session, DatasetRepositoryServiceImpl.MAPPER.writeValueAsString(columnValue), new TreeMap.SimpleEntry<>(key, map.get(key)));
                        map.put(columnKey, result);
                    }
                }
            }
            return maps;
        } catch (IOException e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> languages(SqlSession session, Map<String, Object> body) {
        Action action = actions.get(Action.Type.LANGUAGES);
        if (action == null) {
            throw new DatasetException("dataset.language.unsupported");
        }
        if (evaluationContext.get() == null) {
            evaluationContext.set(new StandardEvaluationContext());
            evaluationContext.get().setBeanResolver((context, beanName) -> applicationContext.getBean(beanName));
        }
        return (Map<String, Object>) action.process(SPEL_EXPRESSION_PARSER, evaluationContext.get(), session, body);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Boolean> validate(SqlSession session, Map<String, Object> body, Map.Entry<String, Object> parentKey) throws DatasetException {
        Action action = actions.get(Action.Type.VEILDATA);
        if (action == null) {
            throw new DatasetException("dataset.validate.unsupported");
        }
        if (evaluationContext.get() == null) {
            evaluationContext.set(new StandardEvaluationContext());
            evaluationContext.get().setBeanResolver((context, beanName) -> applicationContext.getBean(beanName));
        }
        return (List<Boolean>) action.process(SPEL_EXPRESSION_PARSER, evaluationContext.get(), session, body);
    }
}