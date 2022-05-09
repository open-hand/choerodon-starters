package io.choerodon.onlyoffice.service;

import com.alibaba.fastjson.JSONObject;
import java.io.IOException;

/**
 * Created by wangxiang on 2022/5/6
 */
public interface OnlyOfficeService {
    JSONObject saveFile(JSONObject obj) throws IOException;
}
