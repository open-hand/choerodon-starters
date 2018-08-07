package io.choerodon.asgard.saga.property;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class PropertyEndpoint {

    private PropertyData propertyData;

    public PropertyEndpoint(PropertyData propertyData) {
        this.propertyData = propertyData;
    }

    @GetMapping(value = "/choerodon/asgard", produces = {APPLICATION_JSON_VALUE})
    PropertyData propertyData() {
        return propertyData;
    }

}
