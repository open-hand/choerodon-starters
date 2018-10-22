package io.choerodon.mybatis.pagehelper

import io.choerodon.core.domain.Page
import io.choerodon.mapper.RoleDO
import io.choerodon.mapper.RoleMapper
import io.choerodon.mybatis.IntegrationTestConfiguration
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.mybatis.pagehelper.domain.Sort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by superlee on 2018/10/16.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class PageInterceptorSpec extends Specification {

    @Autowired
    RoleMapper roleMapper
    @Autowired
    TestRestTemplate testRestTemplate

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

    def "restPageQuery"() {
        when:
        ResponseEntity<Page<RoleDO>> value = testRestTemplate.getForEntity("/v1/test?page=0&size=5&sort=id", Page)
        then:
        value.statusCode.is2xxSuccessful()
        value.body.get(0).getAt("id") == 1

        when: "不传排序字段，按默认id升序排序"
        ResponseEntity<Page<RoleDO>> value1 = testRestTemplate.getForEntity("/v1/test?page=0&size=5", Page)
        then:
        value1.statusCode.is2xxSuccessful()
        value1.body.get(0).getAt("id") == 1
    }

    def "restQueryByService"() {
        when:
        def value = testRestTemplate.getForEntity("/v1/test_service", String)
        then:
        value.statusCode.is2xxSuccessful()
        value.body != null
    }

}
