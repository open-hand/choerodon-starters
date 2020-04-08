package io.choerodon.liquibase


import org.junit.runner.RunWith
import org.powermock.api.support.membermodification.MemberModifier
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import spock.lang.Specification

/**
 *
 * @author zmf
 * @since 2018-10-19
 *
 */
@RunWith(PowerMockRunner)
@PowerMockRunnerDelegate(Sputnik)
@PrepareForTest(CusFileSystemResourceAccessor)
class CusFileSystemResourceAccessorSpec extends Specification {
//    def "ConvertToPath"() {
//        given: "准备上下文"
//        CusFileSystemResourceAccessor accessor = new CusFileSystemResourceAccessor()
//        def resource = this.class.getClassLoader().getResource("resource-jar.jar").getFile()
//
//        when: "调用方法"
//        def value = accessor.convertToPath(resource)
//
//        then: "校验结果"
//        value == resource
//    }
//
//    def "Convert To Path For Exception"() {
//        given: "准备上下文"
//        CusFileSystemResourceAccessor accessor = new CusFileSystemResourceAccessor(this.class.getClassLoader().getResource("script/db").getFile())
//        MemberModifier.field(CusFileSystemResourceAccessor, "baseDirectory").set(accessor, new File("script/db/fd_icon.groovy"))
//
//        when: "调用方法"
//        accessor.convertToPath("asfasfas")
//
//        then: "校验结果"
//        noExceptionThrown()
//    }
}
