package io.choerodon.mybatis.spring

import io.choerodon.mapper.RoleMapper
import io.choerodon.mybatis.MapperException
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

    def "SetProperties"() {
        given:
        Properties properties = new Properties()
        properties.setProperty("mappers", "com.example.MyMapper")
        properties.setProperty("notEmpty", "false")
        properties.setProperty("IDENTITY", "MYSQL")
        CommonMapperScannerConfigurer mapperScannerConfigurer = new CommonMapperScannerConfigurer()

        when:
        mapperScannerConfigurer.setProperties(properties)

        then:
        thrown(MapperException)
        mapperScannerConfigurer.getMapperHelper().getConfig().notEmpty == false
    }

}
