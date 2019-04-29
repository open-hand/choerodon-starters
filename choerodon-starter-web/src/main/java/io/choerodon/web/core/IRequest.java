package io.choerodon.web.core;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * service 请求上下文.
 * <p>
 * 一些与 session 相关的数据可以放在这里面,一起传给 service 的方法.
 * <p>
 * 还可以通过一个 map 附加一些额外的属性.<br>
 * 注意,这个 map 初始为 null,当首次调用 {@code setAttribute(String)} 时初始化.
 *
 * @author shengyang.zhou@hand-china.com
 */
public interface IRequest extends Serializable {

    String FIELD_USER_ID = "userId";
    String FIELD_USER_NAME = "userName";
    String FIELD_COMPANY_ID = "companyId";
    String FIELD_EMPLOYEE_CODE = "employeeCode";
    String FIELD_LOCALE = "locale";
    String FIELD_ROLE_ID = "roleId";
    String FIELD_ALL_ROLE_ID = "roleIds";

    String MDC_PREFIX = "MDC.";

    /**
     * 设置用户名.
     *
     * @param userName 用户名
     */
    void setUserName(String userName);

    String getUserName();

    /**
     * 获取用户ID.
     *
     * @return 用户ID
     */
    Long getUserId();

    /**
     * 获取扩展属性.
     *
     * @param name 扩展属性名
     * @param <T>  返回类型,自动匹配
     * @return 扩展属性, 如果 attributeMap 尚未初始化,返回 null
     */
    <T> T getAttribute(String name);

    /**
     * 获取属性Map.
     *
     * @return 一个包含扩展属性的 map,有可能返回 null.(初始为 null)
     */
    Map<String, Object> getAttributeMap();

    /**
     * 获取属性Name.
     *
     * @return 属性名 Enumeration
     */
    Set<String> getAttributeNames();

    /**
     * 获取公司ID.
     *
     * @return 公司 id
     */
    Long getCompanyId();

    /**
     * 获取语言环境.
     *
     * @return lang code
     */
    String getLocale();

    /**
     * 获取角色ID.
     *
     * @return 角色 id
     */
    Long getRoleId();

    Long[] getAllRoleId();

    void setAllRoleId(Long[] roleIds);

    /**
     * 设置角色ID.
     *
     * @param accountId 账户 id
     */
    void setUserId(Long accountId);

    String getEmployeeCode();

    void setEmployeeCode(String employeeCode);

    /**
     * 设置扩展属性.
     * <p>
     * 首次调用该方法时,初始化 attributeMap
     *
     * @param name  属性名
     * @param value 属性值
     */
    void setAttribute(String name, Object value);

    /**
     * 设置公司ID.
     *
     * @param companyId 公司 id
     */
    void setCompanyId(Long companyId);

    /**
     * 设置语言环境.
     *
     * @param locale 语言 code
     */
    void setLocale(String locale);

    /**
     * 设置角色ID.
     *
     * @param roleId 角色 id
     */
    void setRoleId(Long roleId);
}
