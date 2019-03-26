package io.choerodon.mybatis

import io.choerodon.liquibase.LiquibaseConfig
import io.choerodon.liquibase.LiquibaseExecutor
import io.choerodon.mybatis.common.CustomProvider
import io.choerodon.mybatis.common.query.Comparison
import io.choerodon.mybatis.common.query.SortType
import io.choerodon.mybatis.common.query.WhereField
import io.choerodon.mybatis.dto.Role
import io.choerodon.mybatis.dto.RoleTL
import io.choerodon.mybatis.entity.Criteria
import io.choerodon.mybatis.mapper.RoleMapper
import io.choerodon.mybatis.mapper.RoleTLMapper
import org.junit.Assert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import spock.lang.Specification

import javax.annotation.PostConstruct

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

/**
 * Created by superlee on 2018/10/24.
 */
@ComponentScan
@Import([LiquibaseConfig, ChoerodonMybatisAutoConfiguration])
@SpringBootTest(webEnvironment = NONE, classes = [TestApplication])
class MybatisMapperSpec extends Specification {
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    RoleTLMapper roleTLMapper;
    @Autowired
    LiquibaseExecutor liquibaseExecutor;

    @PostConstruct
    void init() {
        //通过liquibase初始化h2数据库
        liquibaseExecutor.execute()
    }
    def selectOneTest() {
        when:
        Role role = new Role()
        role.roleId = 10001
        Role result = roleMapper.selectOne(role)
        then:
        result.roleCode == "ADMIN"
        result.roleName == "管理员"
    }
    def selectOneTLTest() {
        when:
        RoleTL role = new RoleTL()
        role.roleId = 10001
        RoleTL result = roleTLMapper.selectOne(role)
        then:
        result.roleCode == "ADMIN"
        result.roleName == "ADMIN"
    }

    def selectOptionsTest() {
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

    def auditTest() {
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

    def multiLanguageTest() {
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
}
