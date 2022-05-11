package io.choerodon.onlyoffice.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.onlyoffice.service.OnlyOfficeService;
import io.choerodon.swagger.annotation.Permission;

/**
 * Created by wangxiang on 2022/5/6
 */
@RestController
@RequestMapping("/v1/choerodon/only_office")
public class OnlyOfficeCallBackController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OnlyOfficeCallBackController.class);


    @Autowired
    private OnlyOfficeService onlyOfficeService;

    /**
     * tips:回调接口中要给唯一标识，让程序知道要回写的文件；2.post接口
     *
     * @param obj
     * @param organizationId
     * @param projectId
     * @return
     * @throws Exception
     */


    @Permission(permissionPublic = true)
    @ApiOperation("only_office保存编辑的回调")
    @PostMapping(value = "/save/file")
    public ResponseEntity<JSONObject> saveFile(@RequestBody JSONObject obj,
                                               @ApiParam(value = "组织Id") @RequestParam(name = "organization_id", required = false) Long organizationId,
                                               @ApiParam(value = "项目Id") @RequestParam(name = "project_id", required = false) Long projectId,
                                               @ApiParam(value = "业务Id") @RequestParam(name = "business_id", required = false) Long businessId,
                                               @ApiParam(value = "文件名字") @RequestParam(name = "title", required = false) String title) throws Exception {
        obj.put("organizationId", organizationId);
        obj.put("projectId", projectId);
        obj.put("title", title);
        obj.put("businessId", businessId);
        LOGGER.info("only_office保存编辑的回调:{}", JSON.toJSONString(obj));
        return ResponseEntity.ok(onlyOfficeService.saveFile(obj));
    }

}
