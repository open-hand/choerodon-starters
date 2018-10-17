package io.choerodon.core.ldap

import io.choerodon.core.convertor.ApplicationContextHelper
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import spock.lang.Specification

import javax.naming.NamingEnumeration
import javax.naming.NamingException
import javax.naming.directory.Attribute
import javax.naming.directory.Attributes
import javax.naming.directory.SearchResult
import javax.naming.ldap.LdapContext

/**
 * @author dengyouquan
 * */

class LdapUtilSpec extends Specification {
    def ldap = new Ldap()

    def setup() {
        def username = "username"
        def password = "password"
        ldap.setServerAddress("ldap://ac.hand-china.com")
        ldap.setBaseDn("ou=employee,dc=hand-china,dc=com")
        ldap.setPort("389")
        ldap.setUseSSL(true)
        ldap.setLoginNameField("employeeNumber")
        ldap.setRealNameField("displayName")
        ldap.setEmailField("mail")
        ldap.setPhoneField("mobile")
        ldap.setName(username)
        ldap.setPassword(password)
        ldap.setDirectoryType(DirectoryType.OPEN_LDAP.value())
        ldap.setObjectClass("object")
    }

    def "Authenticate"() {
        when: "方法调用"
        LdapUtil.authenticate(ldap)
        then: "结果分析"
        noExceptionThrown()
    }

    def "GetUserDn"() {
        given: "参数准备"
        def namingEnumeration = Mock(NamingEnumeration)
        def ldapContext = Mock(LdapContext)
        def userName = "userName"
        when: "方法调用"
        LdapUtil.getUserDn(ldapContext, ldap, userName)
        then: "结果分析"
        noExceptionThrown()
        namingEnumeration.hasMoreElements() >>> [true, false]
        ldapContext.search(_, _, _) >> { return namingEnumeration }
    }

    def "LdapAuthenticate"() {
        given: "参数准备"
        def ldapContext = Mock(LdapContext)
        def userDn = ""
        def password = "pwd"
        when: "方法调用"
        LdapUtil.ldapAuthenticate(ldapContext, userDn, password)
        then: "无异常抛出"
        noExceptionThrown()

        when: "调用方法[异常]"
        def result = LdapUtil.ldapAuthenticate(ldapContext, userDn, password)
        then: "校验结果"
        !result
        ldapContext.addToEnvironment(_, _) >> { throw new NamingException() }
    }

    def "AnonymousUserGetByObjectClass"() {
        given: "构造请求参数"
        LdapContext ldapContext = Mock(LdapContext)
        def namingEnumeration = Mock(NamingEnumeration)
        SearchResult searchResult = Mock(SearchResult)
        Attributes attributes = Mock(Attributes)
        Attribute attribute = Mock(Attribute)

        when: "调用方法"
        LdapUtil.anonymousUserGetByObjectClass(ldap, ldapContext)

        then: "校验结果"
        noExceptionThrown()
        namingEnumeration.hasMoreElements() >>> [true, false]
        ldapContext.search(_, _, _) >> { return namingEnumeration }
        namingEnumeration.nextElement() >> { searchResult }
        searchResult.getAttributes() >> { attributes }
        attributes.get(_) >> { attribute }
        attribute.contains(_) >> { true }

        when: "调用方法[异常]"
        LdapUtil.anonymousUserGetByObjectClass(ldap, ldapContext)

        then: "校验结果"
        noExceptionThrown()
        namingEnumeration.hasMoreElements() >>> [true, false]
        ldapContext.search(_, _, _) >> { throw new NamingException() }
    }
}
