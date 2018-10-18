package io.choerodon.config.utils

import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class FileUtilSpec extends Specification {
    private FileUtil fileUtil = new FileUtil()

    def "Extra"() {
        when: "调用方法"
        fileUtil.extra("1.jar", "/")

        then: "校验结果"
        thrown(FileNotFoundException)
    }
}
