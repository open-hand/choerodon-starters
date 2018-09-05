package io.choerodon.asgard.property;

public class PropertySaga {

    private String code;

    private String description;

    private String inputSchema;

    private String inputSchemaSource;

    PropertySaga(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public PropertySaga() {
    }

    public String getInputSchema() {
        return inputSchema;
    }

    public void setInputSchema(String inputSchema) {
        this.inputSchema = inputSchema;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInputSchemaSource() {
        return inputSchemaSource;
    }

    public void setInputSchemaSource(String inputSchemaSource) {
        this.inputSchemaSource = inputSchemaSource;
    }
}