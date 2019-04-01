package io.choerodon.dataset.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.dataset.service.IDatasetRepositoryService;
import io.choerodon.fnd.excel.ExcelException;
import io.choerodon.fnd.excel.service.IHapExcelExportService;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/dataset/{name}")
public class DatasetController extends BaseController {
    @Autowired
    private IDatasetRepositoryService service;

    @Autowired
    private IHapExcelExportService excelExportService;

    public static final ObjectMapper mapper = new ObjectMapper();


    @ResponseBody
    @PostMapping(value = {"/queries"})
    public ResponseData queries(@PathVariable String name, @RequestBody Map<String, Object> body,
                                @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                @RequestParam(defaultValue = "0") int pageSize,
                                @RequestParam(required = false) String sortname,
                                @RequestParam(required = false) String sortorder) {
        return new ResponseData(service.queries(name, body, page, pageSize, sortname, "desc".equals(sortorder)));
    }

    @ResponseBody
    @PostMapping(value = {"/mutations"})
    public ResponseData mutations(@PathVariable String name, @RequestBody String body) {
        ResponseData responseData = new ResponseData(service.mutations(name, body));
        responseData.setTotal(null);
        return responseData;
    }

    @ResponseBody
    @PostMapping(value = "/languages")
    public ResponseData languages(@PathVariable String name, @RequestBody Map<String, Object> body) {
        return new ResponseData(Collections.singletonList(service.languages(name, body)));
    }

    @ResponseBody
    @PostMapping(value = "/validate")
    public List<Boolean> validate(@PathVariable String name, @RequestBody Map<String, Object> body) {
        return service.validate(name, body);
    }

    @PostMapping(value = "/export")
    @SuppressWarnings("unchecked")
    public void export(@PathVariable String name, String _request_data,
                       @RequestParam(required = false) String sortname,
                       @RequestParam(required = false) String sortorder,
                       @RequestParam(required = false) int total,
                       HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ExcelException {

        Map<String, Object> body = mapper.readValue(_request_data, Map.class);

        Map<String, String> exportColumns = (Map<String, String>) body.get("_HAP_EXCEL_EXPORT_COLUMNS");
        if (!ObjectUtils.isEmpty(exportColumns)) {
            excelExportService.exportAndDownloadExcelByDataSet(new ResponseData(service.queries(name, body, 1, 0, sortname, "desc".equals(sortorder))), request, response, exportColumns);
        }
    }
}