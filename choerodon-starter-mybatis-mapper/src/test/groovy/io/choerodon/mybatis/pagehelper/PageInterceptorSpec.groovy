package io.choerodon.mybatis.pagehelper

import io.choerodon.core.domain.Page
import io.choerodon.mapper.RoleDO
import io.choerodon.mapper.RoleMapper
import io.choerodon.mybatis.IntegrationTestConfiguration
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.mybatis.pagehelper.domain.Sort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by superlee on 2018/10/16.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class PageInterceptorSpec extends Specification {

    @Autowired
    RoleMapper roleMapper

    def order = new Sort.Order(Sort.Direction.ASC, "name")
    def sort = new Sort(order)

    def "notPageAndSort"() {
        given:
        RoleDO do1 = new RoleDO()
        do1.setName("a")
        do1.setCode("a")
        do1.setEnabled(true)
        do1.setModified(true)
        do1.setEnableForbidden(true)
        do1.setBuiltIn(true)
        do1.setEnableForbidden(true)

        roleMapper.insert(do1)

        when:
        def value = roleMapper.selectAll()

        then:
        value.size() == 9
    }

    def "onlyPage"() {
        when:
        Page<RoleDO> value = PageHelper.doPage(0, 1, { -> roleMapper.selectAll() })

        then:
        value.content.size() == 1
    }

    def "doPageAndSort"() {

        when:
        Page<RoleDO> value = PageHelper.doPageAndSort(new PageRequest(0, 1, sort), { -> roleMapper.selectAll() })

        then:
        value.getContent().get(0).getName() == "a"
    }

    def "onlySort"() {
        when:
        List<RoleDO> value = PageHelper.doSort(sort, { -> roleMapper.selectAll() })
        then:
        value.get(0).getName() == "a"
    }

    def "updateRole"() {
        when:
        def role = new RoleDO()
        role.setCode("a")
        RoleDO roleDO = roleMapper.selectOne(role)
//        int objectNumberVersion = roleDO.getObjectVersionNumber()
//        roleDO.setObjectVersionNumber(0)
//        roleMapper.updateByPrimaryKey(roleDO)
//        roleDO.setObjectVersionNumber(objectNumberVersion)
        roleMapper.updateByPrimaryKey(roleDO)

        RoleDO r = roleMapper.selectOne(role)
        then:
        r.getObjectVersionNumber() == 2
    }

    def "deleteRole"() {
        when:
        def role = new RoleDO()
        role.setCode("a")
        roleMapper.delete(role)

        then:
        roleMapper.selectOne(role) == null

    }

}
