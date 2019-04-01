package io.choerodon.dataset.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.dataset.metadata.IMetadataTableService;
import io.choerodon.dataset.annotation.Dataset;
import io.choerodon.dataset.exception.DatasetException;
import io.choerodon.dataset.model.DatasetExecutor;
import io.choerodon.dataset.model.impl.DocumentDatasetExector;
import io.choerodon.dataset.model.impl.ServiceDatasetExecutor;
import io.choerodon.dataset.service.IDatasetRepositoryService;
import io.choerodon.dataset.service.IDatasetService;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xausky
 */
@Service
@Transactional(rollbackFor = DatasetException.class)
public class DatasetRepositoryServiceImpl implements IDatasetRepositoryService, ApplicationListener {
    public static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetRepositoryServiceImpl.class);
    private Map<String, DatasetExecutor> services = new ConcurrentHashMap<>();
    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    @Autowired
    private SqlSessionTemplate template;

    @Autowired
    private IMetadataTableService metadataTableService;

    @Autowired
    private ApplicationContext applicationContext;

    public void contextInitialized(ApplicationContext context) {
        template.getConfiguration().setUseGeneratedKeys(true);
        context.getBeansOfType(IDatasetService.class);
        Map datasets = context.getBeansWithAnnotation(Dataset.class);
        for (Object service : datasets.values()) {
            if (service instanceof IDatasetService) {
                Dataset dataset = AnnotationUtils.findAnnotation(service.getClass(), Dataset.class);
                services.put(dataset.value(), new ServiceDatasetExecutor((IDatasetService) service, MAPPER,
                        dataset.value(), template.getConfiguration(), metadataTableService));
            } else {
                throw new DatasetException("dataset.interface.error", service.getClass());
            }
        }
        try {
            // 获取资源目录下 dataset 里的 xml 文件
            Resource[] resources = context.getResources("classpath*:/dataset/**/*.xml");
            for (Resource resource : resources) {
                try {
                    Document document = factory.newDocumentBuilder().parse(resource.getInputStream());
                    for (int nodeIndex = 0; nodeIndex < document.getChildNodes().getLength(); ++nodeIndex) {
                        if (document.getChildNodes().item(nodeIndex) instanceof Element) {
                            NodeList list = document.getChildNodes().item(nodeIndex).getChildNodes();
                            for (int i = 0; i < list.getLength(); ++i) {
                                Node node = list.item(i);
                                if (node instanceof Element) {
                                    this.updateDataset((Element) node);
                                }
                            }
                        }
                    }

                } catch (ParserConfigurationException | SAXException | IOException e) {
                    LOGGER.warn("Dataset document parse exception: {}", e);
                }
            }
        } catch (IOException e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    public void updateDataset(Element node) {
        String id = node.getAttribute("id");
        services.put(id, new DocumentDatasetExector(node, template.getConfiguration(), metadataTableService, this, applicationContext));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<?> queries(String name, Map<String, Object> body, int pageNum, int pageSize, String sortname, boolean isDesc) {
        DatasetExecutor executor = services.get(name);
        if (executor == null) {
            throw new DatasetException("dataset.notFound", name);
        }
        return executor.queries(template, body, pageNum, pageSize, sortname, isDesc);
    }

    @Override
    public List<?> mutations(String name, String body) {
        DatasetExecutor executor = services.get(name);
        if (executor == null) {
            throw new DatasetException("dataset.notFound", name);
        }
        return executor.mutations(template, body, null);
    }

    @Override
    public Map<String, Object> languages(String name, Map<String, Object> body) {
        DatasetExecutor executor = services.get(name);
        Map<String, Object> result = executor.languages(template, body);
        body.put("success", true);
        return result;
    }

    @Override
    public List<Boolean> validate(String name, Map<String, Object> body) {
        DatasetExecutor executor = services.get(name);
        if (executor == null) {
            throw new DatasetException("dataset.notFound", name);
        }
        return executor.validate(template, body, null);
    }

    @Override
    public DatasetExecutor getExecutor(String name) {
        return services.get(name);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            contextInitialized(((ContextRefreshedEvent) event).getApplicationContext());
        }
    }
}
