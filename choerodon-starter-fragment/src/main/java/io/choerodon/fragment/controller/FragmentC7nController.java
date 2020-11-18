package io.choerodon.fragment.controller;

import java.io.File;

import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Pair;
import org.hzero.core.util.Results;
import org.hzero.fragment.service.FragmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.choerodon.fragment.util.FileUtils;
import io.choerodon.swagger.annotation.Permission;

/**
 * 分片上传接口
 *
 * @author scp
 */
@Controller("fragmentC7nController.v1")
public class FragmentC7nController {

    @Autowired
    private FragmentService fragmentService;


    @PostMapping("/v1/upload/fragment-combine-c7n")
    @ApiOperation(value = "分片文件合并(独立前端使用-c7n)")
    @Permission(permissionLogin = true)
    @ResponseBody
    public ResponseEntity<String> fragmentCombineBlock(String guid, String fileName) {
        Pair<String, String> pair = fragmentService.combineBlock(guid, fileName);
        String filePath = pair.getFirst();
        FileUtils.deleteFile(new File(pair.getSecond()));
        return Results.success(filePath);
    }
}
