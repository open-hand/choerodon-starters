package io.choerodon.nacos.api.user;

import io.choerodon.nacos.api.exception.NacosException;

public interface UserService {
    /**
     *  创建用户
     * @param username 用户名
     * @param password 密码
     * @return
     * @throws NacosException
     */
    Object createUser(String username, String password) throws NacosException;

    /**
     * 为用户添加角色
     *
     * @param role 角色名
     * @param username 用户名
     * @return
     */
    Object addRole(String role, String username) throws NacosException;

    /**
     * 为角色分配命名空间
     *
     * @param role
     * @param resource
     * @param action
     * @return
     */
    Object addPermission(String role, String resource, String action) throws NacosException;
}
