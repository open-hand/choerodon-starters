package io.choerodon.core.excel

import io.choerodon.core.api.dto.UserDTO
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class ExcelReadHelperSpec extends Specification {
    def "Read"() {
        given: "构造请求参数"
        File excelFile = new File(this.class.getResource('/userTemplates.xlsx').toURI())
        FileInputStream fileInputStream = new FileInputStream(excelFile)
        MultipartFile multipartFile = new MockMultipartFile(excelFile.getName(),
                excelFile.getName(), "application/octet-stream",
                fileInputStream)
        ExcelReadConfig excelReadConfig = new ExcelReadConfig();
        String[] skipSheetNames = { "readme" };
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("id", "id");
        propertyMap.put("name", "name");
        excelReadConfig.setSkipSheetNames(skipSheetNames);
        excelReadConfig.setPropertyMap(propertyMap);

        when: "调用方法"
        List<UserDTO> list = ExcelReadHelper.read(excelFile, UserDTO, excelReadConfig)
        then: "校验结果"
        list.size() == 3

        when: "调用方法"
        list = ExcelReadHelper.read(excelFile, UserDTO, null)
        then: "校验结果"
        list.size() == 3

        when: "调用方法"
        list = ExcelReadHelper.read(multipartFile, UserDTO, excelReadConfig)
        then: "校验结果"
        list.size() == 3

        when: "调用方法"
        list = ExcelReadHelper.read(multipartFile, UserDTO, null)
        then: "校验结果"
        list.size() == 3
    }
}
