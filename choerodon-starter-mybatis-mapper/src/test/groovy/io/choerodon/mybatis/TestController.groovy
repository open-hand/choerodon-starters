package io.choerodon.mybatis

import io.choerodon.core.domain.Page
import io.choerodon.mapper.RoleDO
import io.choerodon.mapper.RoleMapper
import io.choerodon.mapper.RoleTlMapper
import io.choerodon.mybatis.pagehelper.PageHelper
import io.choerodon.mybatis.pagehelper.annotation.SortDefault
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.mybatis.pagehelper.domain.Sort
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * Created by superlee on 2018/10/18.
 */
@RestController
@RequestMapping(value = "/v1")
class TestController {

    @Autowired
    RoleMapper roleMapper
    @Autowired
    RoleServiceImpl roleService
    @Autowired
    RoleTlMapper roleTlMapper

    @GetMapping("/test")
    ResponseEntity<Page<RoleDO>> query(
            @SortDefault(value = "id", direction = Sort.Direction.ASC) PageRequest pageRequest) {
        Map<String, String> map = new HashMap<>()
        map.put("id", "id")
        pageRequest.resetOrder("iam_role", map)
        return new ResponseEntity<>(PageHelper.doPageAndSort(pageRequest, { -> roleMapper.selectAll()
        }), HttpStatus.OK)
    }

    @Transactional
    @GetMapping("/test_service")
    ResponseEntity<List<RoleDO>> queryByService(
            @SortDefault.SortDefaults([@SortDefault(sort = "dateRecorded", direction = Sort.Direction.DESC), @SortDefault(sort = "encounterId", direction = Sort.Direction.ASC)]) PageRequest pageRequest) {
        RoleDO roleDO = new RoleDO()
        roleDO.setEnabled(true)
        roleService.select(roleDO)
        roleService.selectOne(roleDO)
        roleService.selectByPrimaryKey(1L)
        roleService.selectCount(roleDO)
        RoleDO role = new RoleDO()
        role.setName("role1")
        role.setCode("role1")
        role.setEnabled(true)
        role.setModified(true)
        role.setEnableForbidden(true)
        role.setBuiltIn(true)
        role.setAssignable(true)
        role.setLevel("site")
        role.setDescription("description")
        roleService.insert(role)
        role.setCode("role2")
        role.setId(null)
        roleService.insertSelective(role)
        role.setId(null)
        role.setCode("role3")
        roleService.insertOptional(role, "id", "name", "code", "is_enabled", "is_modified",
                "is_enable_forbidden", "is_built_in", "is_assignable", "object_version_number", "created_by")
        RoleDO updateRole = roleService.selectByPrimaryKey(role.getId())
        roleService.updateByPrimaryKey(updateRole)
        updateRole.setObjectVersionNumber(updateRole.getObjectVersionNumber() + 1L)
        roleService.updateByPrimaryKeySelective(updateRole)
        updateRole.setObjectVersionNumber(updateRole.getObjectVersionNumber() + 1L)
        roleService.updateOptional(updateRole, "id", "name", "code", "is_enabled", "is_modified",
                "is_enable_forbidden", "is_built_in", "is_assignable", "object_version_number", "created_by")
        roleService.deleteByPrimaryKey(updateRole.getId())
        RoleDO deleteRole = new RoleDO()
        deleteRole.setCode("role1")
        roleService.delete(deleteRole)
        roleService.pageAll(0, 10)
        roleService.page(roleDO, 0, 10)
        //todo 这里有问题
//        roleService.existsWithPrimaryKey(1L)

        //insertMultiLanguage多语言被删掉的情况
        RoleDO role1 = new RoleDO()
        BeanUtils.copyProperties(role, role1)
        role1.setCode("020310")
        roleMapper.insertSelective(role1)
        long id = role1.getId()
        roleTlMapper.deleteByPrimaryKey(id)
        role1.setName("676767")
        role1.setObjectVersionNumber(1L)
        roleMapper.updateByPrimaryKeySelective(role1)


        return new ResponseEntity<>(roleService.selectAll(), HttpStatus.OK)
    }


}
