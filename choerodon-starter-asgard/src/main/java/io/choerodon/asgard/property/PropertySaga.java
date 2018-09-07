package io.choerodon.asgard.property;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PropertySaga {

    private String code;

    private String description;

    private String inputSchema;

    private String inputSchemaSource;

    public PropertySaga(String code, String description) {
        this.code = code;
        this.description = description;
    }

}