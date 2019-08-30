package io.choerodon.web;

import org.springframework.stereotype.Component;

/**
 * @author njq.niu@hand-china.com
 * @since 2016/1/21
 */
@Component(value = "defaultPageConfiguration")
public class DefaultConfiguration {

    private String pageLogin = "/login.html";
    private String page404 = "/404.html";
    private String page403 = "/403.html";
    private String page500 = "/500.html";
    private String defaultViewPath = "";


    public String getDefaultViewPath() {
        return defaultViewPath;
    }

    public void setDefaultViewPath(String defaultViewPath) {
        this.defaultViewPath = defaultViewPath;
    }

    public String getPage404() {
        return page404;
    }

    public void setPage404(String page404) {
        this.page404 = page404;
    }

    public String getPage403() {
        return page403;
    }

    public void setPage403(String page403) {
        this.page403 = page403;
    }

    public String getPage500() {
        return page500;
    }

    public void setPage500(String page500) {
        this.page500 = page500;
    }

    public String getPageLogin() {
        return pageLogin;
    }

    public void setPageLogin(String pageLogin) {
        this.pageLogin = pageLogin;
    }

}
