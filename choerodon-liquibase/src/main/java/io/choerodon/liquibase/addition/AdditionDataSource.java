package io.choerodon.liquibase.addition;

import io.choerodon.liquibase.helper.LiquibaseHelper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * 多数据源配置
 *
 * @author dongfan117@gmail.com
 */
public class AdditionDataSource {
    private String url;
    private String username;
    private String password;
    private String dir;
    private boolean drop;
    private DataSource dataSource;
    private LiquibaseHelper liquibaseHelper;

    public AdditionDataSource() {
    }

    /**
     * 构造函数
     *
     * @param url      DataSource url
     * @param username DataSource username
     * @param password DataSource password
     * @param dir      DataSource dir
     * @param drop     是否删除数据库
     */
    public AdditionDataSource(String url, String username, String password, String dir, boolean drop) {
        this(url, username, password, dir, drop, null);
    }

    public AdditionDataSource(String url, String username, String password, String dir, boolean drop, DataSource dataSource) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.dir = dir;
        this.drop = drop;
        this.dataSource = dataSource;
        this.liquibaseHelper = new LiquibaseHelper(this.url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public boolean isDrop() {
        return drop;
    }

    public void setDrop(boolean drop) {
        this.drop = drop;
    }

    public DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = new DriverManagerDataSource(url, username, password);
        }
        return dataSource;
    }

    public LiquibaseHelper getLiquibaseHelper() {
        return liquibaseHelper;
    }
}
