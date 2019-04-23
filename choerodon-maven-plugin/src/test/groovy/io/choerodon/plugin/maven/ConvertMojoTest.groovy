package io.choerodon.plugin.maven

import com.fasterxml.jackson.databind.node.ObjectNode
import spock.lang.Specification

class ConvertMojoTest extends Specification {
    def "Convert Form Directory Test" () {
        ConvertMojo convertMojo = new ConvertMojo()
        when:
        ObjectNode root = convertMojo.convertFormDirectory("src/test/resources")
        then:
        root != null
        root.size() == 3
        root.get("IAM_PERMISSION").size() == 1
        println(root.toString())
    }
}
