package io.choerodon.onlyoffice.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import io.choerodon.onlyoffice.service.OnlyOfficeService;

/**
 * Created by wangxiang on 2022/5/6
 */
@Service
public class OnlyOfficeServiceImpl implements OnlyOfficeService {

    @Override
    public void saveFile(JSONObject obj) {
        //1.获取文件
        //2.保存到文件服务器
        //3.将文件跟新到数据库
    }
}

