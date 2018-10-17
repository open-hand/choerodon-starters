package io.choerodon.mybatis.spring

import io.choerodon.mapper.RoleMapper
import io.choerodon.mybatis.common.Marker
import io.choerodon.mybatis.helper.MapperHelper
import spock.lang.Specification

/**
 * Created by superlee on 2018/10/17.
 */
class CommonMapperScannerConfigurerSpec extends Specification {

    def commonMapperScannerConfigurer = new CommonMapperScannerConfigurer()

    def "SetMarkerInterface"() {
        when:
        commonMapperScannerConfigurer.setMarkerInterface(Marker.class)
        commonMapperScannerConfigurer.setMapperHelper(new MapperHelper())

        then:
        true
        commonMapperScannerConfigurer.getMapperHelper().isExtendCommonMapper(RoleMapper.class) == false
    }

    def "GetMapperHelper"() {
    }

    def "SetMapperHelper"() {
    }

    def "SetProperties"() {
    }

}
