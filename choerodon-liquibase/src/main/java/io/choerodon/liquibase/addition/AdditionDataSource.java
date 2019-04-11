package io.choerodon.liquibase.addition;

import io.choerodon.liquibase.helper.LiquibaseHelper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 多数据源配置
 *
 * @author dongfan117@gmail.com
 */
public class AdditionDataSource {
    private String name;
    private String url;
    private String username;
    private String password;
    private String dir;
    private String jar;
    private boolean onlyIam;
    private boolean drop;
    private DataSource dataSource;
    private LiquibaseHelper liquibaseHelper;
    private Set<String> tables;
    private static Map<String, AdditionDataSource> tablesMap = new HashMap<>();

    public AdditionDataSource(){

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
        this(url, username, password, dir, drop, dataSource, null);
    }

    public AdditionDataSource(String url, String username, String password, String dir, boolean drop, DataSource dataSource, Set<String> tables) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.dir = dir;
        this.drop = drop;
        this.dataSource = dataSource;
        this.liquibaseHelper = new LiquibaseHelper(this.url);
        this.tables = tables;
        if (tables != null){
            tables.forEach(t -> tablesMap.put(t, this));
        }
    }

    public String getJar() {
        return jar;
    }

    public void setJar(String jar) {
        this.jar = jar;
    }

    public boolean isOnlyIam() {
        return onlyIam;
    }

    public void setOnlyIam(boolean onlyIam) {
        this.onlyIam = onlyIam;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Map<String, AdditionDataSource> getTablesMap() {
        return tablesMap;
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

    public Set<String> getTables() {
        return tables;
    }

    public void setTables(Set<String> tables) {
        this.tables = tables;
    }

    public LiquibaseHelper getLiquibaseHelper() {
        return liquibaseHelper;
    }
}
