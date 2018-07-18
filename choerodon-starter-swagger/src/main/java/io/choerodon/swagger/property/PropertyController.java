package io.choerodon.swagger.property;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@ApiIgnore
public class PropertyController {

    private PropertyData propertyData;

    public PropertyController(PropertyData propertyData) {
        this.propertyData = propertyData;
    }

    @GetMapping(value = "/choerodon/properties", produces = {APPLICATION_JSON_VALUE})
    PropertyData propertyData() {
        return propertyData;
    }

}
