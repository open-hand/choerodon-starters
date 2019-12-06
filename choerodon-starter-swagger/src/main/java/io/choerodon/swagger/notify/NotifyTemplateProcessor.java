package io.choerodon.swagger.notify;

import io.choerodon.core.notify.NotifyBusinessType;
import io.choerodon.core.notify.NotifyTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class NotifyTemplateProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyTemplateProcessor.class);

    private final Set<NotifyTemplateScanData> templateScanData = new HashSet<>(1 << 3);

    private final Set<NotifyBusinessTypeScanData> businessTypeScanData = new HashSet<>(1 << 3);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof NotifyTemplate) {
            NotifyTemplate template = (NotifyTemplate) bean;
            NotifyTemplateScanData scanData = new NotifyTemplateScanData(template.businessTypeCode(), template.title(), template.content(), template.type());
            if (validNotifyTemplate(scanData)) {
                templateScanData.add(scanData);
            }
        }
        NotifyBusinessType business = AnnotationUtils.findAnnotation(bean.getClass(), NotifyBusinessType.class);
        if (business != null) {
            businessTypeScanData.add(new NotifyBusinessTypeScanData(business.code(), business.name(), business.description(),
                    business.level().getValue(), business.retryCount(), business.isSendInstantly(), business.isManualRetry(), business.isAllowConfig(), business.categoryCode(), business.emailEnabledFlag(),
                    business.pmEnabledFlag(), business.smsEnabledFlag(), business.webhookEnabledFlag(), business.targetUserType(), business.notifyType().getTypeName(),
                    business.proEmailEnabledFlag(), business.proPmEnabledFlag()));
        }
        return bean;
    }

    public Set<NotifyTemplateScanData> getTemplateScanData() {
        return templateScanData;
    }

    public Set<NotifyBusinessTypeScanData> getBusinessTypeScanData() {
        return businessTypeScanData;
    }

    private boolean validNotifyTemplate(final NotifyTemplateScanData template) {
        if (StringUtils.isEmpty(template.getBusinessType())) {
            LOGGER.error("error.notifyTemplate.businessTypeCodeEmpty {}", template);
            return false;
        }

        if (StringUtils.isEmpty(template.getTitle())) {
            LOGGER.error("error.notifyTemplate.titleEmpty {}", template);
            return false;
        }
        if (StringUtils.isEmpty(template.getContent())) {
            LOGGER.error("error.notifyTemplate.contentEmpty {}", template);
            return false;
        }
        template.setContent(readTemplate(template.getContent()));
        return true;
    }

    private String readTemplate(final String contentPath) {
        String trimContentPath = contentPath.trim();
        if (!trimContentPath.startsWith("classpath://")) {
            return trimContentPath;
        }
        StringBuilder sb = new StringBuilder();
        trimContentPath = trimContentPath.substring(12, trimContentPath.length());
        ClassPathResource templateResource = new ClassPathResource(trimContentPath);
        try (InputStreamReader reader = new InputStreamReader(templateResource.getInputStream());
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                sb.append(s).append("\n");
            }
        } catch (IOException e) {
            LOGGER.warn("error.NotifyTemplateProcessor.readTemplate.IOException {}", e);
            return trimContentPath;
        }
        return sb.toString();
    }
}
