package io.choerodon.mybatis

import io.choerodon.liquibase.LiquibaseConfig
import io.choerodon.liquibase.LiquibaseExecutor
import io.choerodon.base.provider.CustomProvider
import io.choerodon.mybatis.common.query.Comparison
import io.choerodon.mybatis.common.query.SortType
import io.choerodon.mybatis.common.query.WhereField
import io.choerodon.mybatis.dto.Role
import io.choerodon.mybatis.dto.RoleTL
import io.choerodon.mybatis.entity.Criteria
import io.choerodon.mybatis.mapper.RoleMapper
import io.choerodon.mybatis.mapper.RoleTLMapper
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import spock.lang.Specification

import javax.annotation.PostConstruct

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

/**
 * Created by superlee on 2018/10/24.
 */
@Import(IntegrationTestConfiguration)
@SpringBootTest(webEnvironment = NONE)
class MybatisMapperSpec extends Specification {
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    RoleTLMapper roleTLMapper;
    @Autowired
    SqlSessionFactory sessionFactory;

    @TestConfiguration
    static class TestConfig {
        @Bean
        CustomProvider testCustomProvider() {
            return new CustomProvider(){
                @Override
                String currentLanguage() {
                    return "en_GB";
                }

                @Override
                Long currentPrincipal() {
                    return 1133L;
                }

                @Override
                Set<String> getSupportedLanguages() {
                    return Collections.singleton("en_GB");
                }
            };
        }
    }

    def "Test Interceptor inject"() {
        when:
        Configuration configuration = sessionFactory.getConfiguration()
        then:
        configuration.getInterceptors().size() == 4
    }

    def "Select Sort Test"() {
        when:
        Role role = new Role()
        role.setSortname("roleName")
        role.setSortorder("ASC")
        def ascResult = roleMapper.select(role)
        role.setSortorder("DESC")
        def descResult = roleMapper.select(role)
        then:
        ascResult.get(0).getRoleId() != descResult.get(0).getRoleId()
    }

    def "Select TL Sort Test"() {
        when:
        RoleTL role = new RoleTL()
        role.setSortname("roleName")
        role.setSortorder("ASC")
        def ascResult = roleTLMapper.select(role)
        role.setSortorder("DESC")
        def descResult = roleTLMapper.select(role)
        then:
        ascResult.get(0).getRoleId() != descResult.get(0).getRoleId()
    }

    def "Select One Test"() {
        when:
        Role role = new Role()
        role.roleId = 10001
        Role result = roleMapper.selectOne(role)
        then:
        result.roleCode == "ADMIN"
        result.roleName == "管理员"
    }
    def "Select One TL Test"() {
        when:
        RoleTL role = new RoleTL()
        role.roleId = 10001
        RoleTL result = roleTLMapper.selectOne(role)
        then:
        result.roleCode == "ADMIN"
        result.roleName == "ADMIN"
    }

    def "Select Options Test"() {
        when:
        RoleTL role = new RoleTL()
        Criteria criteria = new Criteria(role)
        criteria.select("roleName")
        criteria.sort("roleName", SortType.ASC)
        criteria.where("roleId")
        criteria.where(new WhereField("roleId", Comparison.EQUAL))
        List<RoleTL> result = roleTLMapper.selectOptions(role, criteria)
        then:
        result.size() == 2
    }

    def "Delete By Primary Key Test"() {
        when:
        Role role = new Role()
        role.roleId = 10002
        role.roleName = "hoqwihd"
        role.roleCode = "dwwddqd"
        then:
        roleMapper.deleteByPrimaryKey(role) > 0
    }

    def "Update Version Test" () {
        when:
        RoleTL role = roleTLMapper.selectByPrimaryKey(10001)
        long oldVersionNumber = role.objectVersionNumber;
        role.roleId = 10001
        role.roleName = "wdqqfqdq"
        roleTLMapper.updateByPrimaryKeySelective(role)
        RoleTL versionRole = roleTLMapper.selectByPrimaryKey(10001)
        then:
        role.objectVersionNumber == oldVersionNumber + 1
        versionRole.objectVersionNumber == oldVersionNumber + 1
    }

    def "Audit Test"() {
        when:
        RoleTL role = new RoleTL()
        role.roleId = 10001
        role = roleTLMapper.selectByPrimaryKey(role);
        then:
        role != null
        role.getCreatedBy() != null
        role.getCreationDate() != null
        when:
        role.setRoleName("测试角色-改");
        roleTLMapper.updateByPrimaryKeyOptions(role, new Criteria(role));
        role = roleTLMapper.selectByPrimaryKey(role);
        then:
        role != null
        role.getCreatedBy() != null
        role.getCreationDate() != null
    }

    def "Multi Language Test"() {
        when:
        RoleTL role = new RoleTL()
        role.setRoleCode("Test-" + System.currentTimeMillis())
        role.setRoleName("测试角色")
        roleTLMapper.insertSelective(role)
        then:
        role.roleId != null
        when:
        roleTLMapper.delete(role)
        then:
        noExceptionThrown()
    }

    def "Extension Attribute Test"() {
        RoleTL role = roleTLMapper.selectByPrimaryKey(10001)
        when:
        role.setAttribute1("Test")
        roleTLMapper.updateByPrimaryKey(role)
        RoleTL result = roleTLMapper.selectOne(role)
        then:
        result.getAttribute1() == "Test"
    }

    def "Extension Attribute Disable Test"() {
        RoleTL role = roleTLMapper.selectByPrimaryKey(10001)
        when:
        role.setAttribute1("Test")
        roleMapper.updateByPrimaryKey(role)
        Role result = roleMapper.selectOne(role)
        then:
        result.getAttribute1() == null
    }
}
