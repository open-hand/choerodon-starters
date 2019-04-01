package io.choerodon.web.core.impl;


import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.IRequestListener;

import javax.servlet.http.HttpServletRequest;

/**
 * @author shengyang.zhou@hand-china.com
 */
public class DefaultRequestListener implements IRequestListener {

    @Override
    public IRequest newInstance() {
        return new ServiceRequest();
    }

    @Override
    public void afterInitialize(HttpServletRequest httpServletRequest, IRequest request) {

    }
}
