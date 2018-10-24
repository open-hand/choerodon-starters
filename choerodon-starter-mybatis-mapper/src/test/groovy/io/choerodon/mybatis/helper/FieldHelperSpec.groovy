package io.choerodon.mybatis.helper

import io.choerodon.mapper.RoleDO
import io.choerodon.mybatis.domain.EntityField
import spock.lang.Specification

import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Created by superlee on 2018/10/23.
 */
class FieldHelperSpec extends Specification {
    Class clazz = RoleDO.class

    def "Jdk6or7GetProperties"() {
        given:
        Field field = FieldHelper.class.getDeclaredField("fieldHelperInterface")
        field.setAccessible(true)
        Field modifiersField = Field.class.getDeclaredField("modifiers")
        modifiersField.setAccessible(true)
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL)
        FieldHelper.IFieldHelper fieldHelperInterface = new FieldHelper.Jdk6or7FieldHelper()
        field.set(null, fieldHelperInterface)

        when:
        List<EntityField> fields = FieldHelper.getProperties(clazz)
        then:
        fields.size() == 17

        when: "getFields"
        List<EntityField> fields1 = FieldHelper.getFields(clazz)
        then:
        fields1.size() == 17

        when: "getAll"
        List<EntityField> fields2 = FieldHelper.getAll(clazz)
        then:
        fields2.size() == 17


    }

    def "Jdk8GetProperties"() {
        given:
        Field field = FieldHelper.class.getDeclaredField("fieldHelperInterface")
        field.setAccessible(true)
        Field modifiersField = Field.class.getDeclaredField("modifiers")
        modifiersField.setAccessible(true)
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL)
        FieldHelper.IFieldHelper fieldHelperInterface = new FieldHelper.Jdk8FieldHelper()
        field.set(null, fieldHelperInterface)
        when:
        List<EntityField> fields = FieldHelper.getProperties(clazz)
        then:
        fields.size() > 0
    }

}
