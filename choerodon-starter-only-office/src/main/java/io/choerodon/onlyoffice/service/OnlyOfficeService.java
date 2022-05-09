package io.choerodon.onlyoffice.service;

import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by wangxiang on 2022/5/6
 */
public interface OnlyOfficeService {
    void saveFile(JSONObject obj) throws IOException;
}
