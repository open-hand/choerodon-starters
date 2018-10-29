package io.choerodon.statemachine.dto;


import com.google.common.base.MoreObjects;

/**
 * @author shinan.chen
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
public class ConfigCodeDTO {

    private String code;

    private String name;

    private String description;

    private String type;

    public ConfigCodeDTO() {
    }

    public ConfigCodeDTO(String code, String name, String description, String type) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("code", code)
                .add("name", name)
                .add("description", description)
                .add("type", type)
                .toString();
    }
}
