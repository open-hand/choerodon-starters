package io.choerodon.liquibase


import spock.lang.Specification
/**
 *
 * @author zmf
 * @since 2018-10-19
 *
 */
class CusFileSystemResourceAccessorSpec extends Specification {
    def "ConvertToPath"() {
        given: "准备上下文"
        CusFileSystemResourceAccessor accessor = new CusFileSystemResourceAccessor("/tmp")

        when: "调用方法"
        def value = accessor.convertToPath("/tmp/a.txt")

        then: "校验结果"
        value == "/tmp/a.txt"
    }
}
