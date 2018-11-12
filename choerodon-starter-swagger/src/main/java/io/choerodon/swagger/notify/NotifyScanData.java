package io.choerodon.swagger.notify;

import java.util.Set;

public class NotifyScanData {

    private Set<NotifyTemplateScanData> templateScanData;

    private Set<NotifyBusinessTypeScanData> businessTypeScanData;

    public Set<NotifyTemplateScanData> getTemplateScanData() {
        return templateScanData;
    }

    public void setTemplateScanData(Set<NotifyTemplateScanData> templateScanData) {
        this.templateScanData = templateScanData;
    }

    public Set<NotifyBusinessTypeScanData> getBusinessTypeScanData() {
        return businessTypeScanData;
    }

    public void setBusinessTypeScanData(Set<NotifyBusinessTypeScanData> businessTypeScanData) {
        this.businessTypeScanData = businessTypeScanData;
    }

    public NotifyScanData(Set<NotifyTemplateScanData> templateScanData, Set<NotifyBusinessTypeScanData> businessTypeScanData) {
        this.templateScanData = templateScanData;
        this.businessTypeScanData = businessTypeScanData;
    }

    public NotifyScanData() {
    }
}
