package io.choerodon.swagger.notify

import spock.lang.Specification

class DataSpec extends Specification {
    def "NotifyScanData"() {
        given: "空构造器"
        def notifyScanData = new NotifyScanData()
        def String[] arr1 = []
        and: "准备NotifyBusinessTypeScanData"
        def businessTypeScanData = new HashSet<NotifyBusinessTypeScanData>()
        def data1 = new NotifyBusinessTypeScanData("code", "name", "description",
                "level", 1,
                false, false, true, "categoryCode", false, false, false, false, arr1, "", false, false)
        def data2 = new NotifyBusinessTypeScanData()
        data2.setName(data1.getName())
        data2.setCode(data1.getCode())
        data2.setDescription(data1.getDescription())
        data2.setLevel(data1.getLevel())
        data2.setManualRetry(data1.getManualRetry())
        data2.setRetryCount(data1.getRetryCount())
        data2.setSendInstantly(data1.getSendInstantly())
        data2.setCategoryCode(data1.getCategoryCode())
        data2.setEmailEnabledFlag(data1.getEmailEnabledFlag())
        data2.setPmEnabledFlag(data1.getPmEnabledFlag())
        data2.setSmsEnabledFlag(data1.getSmsEnabledFlag())
        data2.setWebhookEnabledFlag(data1.getWebhookEnabledFlag())
        businessTypeScanData.add(data1)
        businessTypeScanData.add(data2)

        and: "准备NotifyTemplateProcessor"
        def templateScanData = new HashSet<NotifyTemplateScanData>()
        def data3 = new NotifyTemplateScanData()
        data3.setType("type")
        data3.setContent("content")
        data3.setBusinessType("bussinessType")
        data3.setTitle("title")
        templateScanData.add(data3)

        when: "set"
        notifyScanData.setBusinessTypeScanData(businessTypeScanData)
        notifyScanData.setTemplateScanData(templateScanData)

        then: "get"
        notifyScanData.getBusinessTypeScanData() == businessTypeScanData
        notifyScanData.getBusinessTypeScanData().size() == 2
        notifyScanData.getTemplateScanData().equals(templateScanData)
        notifyScanData.getTemplateScanData().size() == 1

    }
}
