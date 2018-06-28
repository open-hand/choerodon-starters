# Choerodon Starter Oauth Core

This project is a jar package. Its main function is to provide password management and login policy management for users of the **Choerodon Microservices Framework**, including password verification, login verification, passwords, and login records.

Projects that use the jar package for password and login policy's management need to be consistent with the table structure of users, organizations, and password policies of the **Choerodon Microservices Framework**.

## Usage

```xml
<dependency>
	<groupId>io.choerodon</groupId>
	<artifactId>choerodon-starter-oauth-core</artifactId>
	<version>0.5.3.RELEASE</version>
</dependency>
```

* Maintenance of the information of password policy and login policy：

1. In ``iam-service``, use the password policy to modify the interface, update the password policy information.

1. At the business logic that has the password changed, add the following check code：

    ```java
    //Get the password policy of the corresponding organization
    BasePasswordPolicyDO basePasswordPolicyDO = basePasswordPolicyMapper.selectByPrimaryKey(basePasswordPolicyMapper.findByOrgId(organizationE.getId()));

    //Password verification
    passwordPolicyManager.passwordValidate(userE.getPassword(), baseUserDO, basePasswordPolicyDO);

    //When the password is updated and newly created, the password history is recorded. The password passed in is the encoded password.

    passwordRecord.updatePassword(userE.getId(),userE.getPassword());

    ```
1. Add the following login security check in the ``oauth-server``:

    ```java
    //Get the password policy of the current organization
    BasePasswordPolicyDO passwordPolicy = basePasswordPolicyMapper.findByOrgId(org.getId());

    //Whether to need a verification code.
    Boolean isNeedCaptcha = passwordPolicyManager.isNeedCaptcha(passwordPolicy, baseUserDO);

    //Determine whether the user's login operation needs to be locked.
    Map returnMap = passwordPolicyManager.loginValidate("password", baseUserDO, passwordPolicy);
    Object lock = null;
    if (returnMap != null) {
        lock = returnMap.get(PasswordPolicyType.MAX_ERROR_TIME.getValue());
    }
    if (lock != null && !((Boolean) lock)) {
        //DONE Lock the user
    }
    ```

## Dependencies

* mysql

## Reporting Issues

If you find any shortcomings or bugs, please describe them in the [Issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).
    
## How to Contribute
Pull requests are welcome! Follow [this link](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) for more information on how to contribute.

## Note
This jar package is a project that applies to the ``Choerodon Microservices Framework``'s password and login policy and does not currently support inconsistencies with other database table structures.