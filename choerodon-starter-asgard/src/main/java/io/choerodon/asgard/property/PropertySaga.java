package io.choerodon.asgard.property;

public class PropertySaga {

    private String code;

    private String description;

    private String inputSchema;

    private String inputSchemaSource;

    public PropertySaga() {
    }

    public PropertySaga(String code, String description) {
        this.code = code;
        this.description = description;
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

    public String getInputSchema() {
        return inputSchema;
    }

    public void setInputSchema(String inputSchema) {
        this.inputSchema = inputSchema;
    }

    public String getInputSchemaSource() {
        return inputSchemaSource;
    }

    public void setInputSchemaSource(String inputSchemaSource) {
        this.inputSchemaSource = inputSchemaSource;
    }

    @Override
    public String toString() {
        return "PropertySaga{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", inputSchema='" + inputSchema + '\'' +
                ", inputSchemaSource='" + inputSchemaSource + '\'' +
                '}';
    }
}