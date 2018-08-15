package io.choerodon.swagger.notify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class EmailTemplateProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailTemplateProcessor.class);

    private final Set<EmailTemplateScanData> scanDataSet = new HashSet<>(1 << 3);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof EmailTemplate) {
            EmailTemplate template = (EmailTemplate) bean;
            EmailTemplateScanData scanData = new EmailTemplateScanData(template.businessTypeCode(),
                    template.code(), template.name(), template.title(), template.content());
            if (validEmailTemplate(scanData)) {
                scanDataSet.add(scanData);
            }
        }
        return bean;
    }

    public Set<EmailTemplateScanData> getScanDataSet() {
        return scanDataSet;
    }

    private boolean validEmailTemplate(EmailTemplateScanData template) {
        if (StringUtils.isEmpty(template.getBusinessType())) {
            LOGGER.error("error.emailTemplate.businessTypeCodeEmpty {}", template);
            return false;
        }
        if (StringUtils.isEmpty(template.getCode())) {
            LOGGER.error("error.emailTemplate.codeEmpty {}", template);
            return false;
        }
        if (StringUtils.isEmpty(template.getName())) {
            LOGGER.error("error.emailTemplate.nameEmpty {}", template);
            return false;
        }
        if (StringUtils.isEmpty(template.getTitle())) {
            LOGGER.error("error.emailTemplate.titleEmpty {}", template);
            return false;
        }
        if (StringUtils.isEmpty(template.getContent())) {
            LOGGER.error("error.emailTemplate.contentEmpty {}", template);
            return false;
        }
        return true;
    }

}
