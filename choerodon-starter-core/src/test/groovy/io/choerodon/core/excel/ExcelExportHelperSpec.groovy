package io.choerodon.core.excel

import io.choerodon.core.api.dto.UserDTO
import io.choerodon.core.exception.CommonException
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class ExcelExportHelperSpec extends Specification {
    private int count = 2

    def "ExportExcel2003"() {
        given: "构造请求参数"
        String[] headers = new String[2]
        headers[0] = "id"
        headers[1] = "name"
        Map<String, String> propertyMap = new HashMap<>()
        propertyMap.put("id", "id");
        propertyMap.put("name", "name");
        List<UserDTO> list = new ArrayList<>()
        for (int i = 0; i < count; i++) {
            UserDTO userDTO = new UserDTO()
            userDTO.setId(i)
            userDTO.setName("name")
            list << userDTO
        }
        String sheetTitle = "user"

        when: "调用方法[异常propertyMap null]"
        ExcelExportHelper.exportExcel2003(new HashMap<String, String>(), list, sheetTitle, UserDTO)
        then: "校验结果"
        def exception = thrown(CommonException)
        exception.message.equals("excel headers are empty, please set headers!")

        when: "调用方法"
        HSSFWorkbook sheets = ExcelExportHelper.exportExcel2003(propertyMap, list, sheetTitle, UserDTO)
        then: "校验结果"
        //header
        sheets.getSheet(sheetTitle).size() == count + 1

        when: "调用方法[异常headers null]"
        ExcelExportHelper.exportExcel2003(new String[0], list, sheetTitle, UserDTO)
        then: "校验结果"
        exception = thrown(CommonException)
        exception.message.equals("excel headers are empty, please set headers!")

        when: "调用方法"
        sheets = ExcelExportHelper.exportExcel2003(headers, list, sheetTitle, UserDTO)
        then: "校验结果"
        sheets.getSheet(sheetTitle).size() == count + 1

        when: "调用方法"
        sheets = ExcelExportHelper.exportExcel2003ForBigData(headers, list, UserDTO)
        then: "校验结果"
        sheets == null
    }

//    def "ExportExcel2007"() {
//        given: "构造请求参数"
//        String[] headers = new String[2]
//        headers[0] = "id"
//        headers[1] = "name"
//        Map<String, String> propertyMap = new HashMap<>()
//        propertyMap.put("id", "id");
//        propertyMap.put("name", "name");
//        List<UserDTO> list = new ArrayList<>()
//        for (int i = 0; i < count; i++) {
//            UserDTO userDTO = new UserDTO()
//            userDTO.setId(i)
//            userDTO.setName("name")
//            list << userDTO
//        }
//        String sheetTitle = "user"
//
//        when: "调用方法[异常propertyMap null]"
//        ExcelExportHelper.exportExcel2003(new HashMap<String, String>(), list, sheetTitle, UserDTO)
//        then: "校验结果"
//        def exception = thrown(CommonException)
//        exception.message.equals("excel headers are empty, please set headers!")
//
//        when: "调用方法"
//        XSSFWorkbook sheets = ExcelExportHelper.exportExcel2007(propertyMap, list, sheetTitle, UserDTO)
//        then: "校验结果"
//        //暂未实现
//        sheets.getSheet(sheetTitle).size() == 0
//
//        when: "调用方法"
//        sheets = ExcelExportHelper.exportExcel2007(headers, list, sheetTitle, UserDTO)
//        then: "校验结果"
//        sheets == null
//    }
}
