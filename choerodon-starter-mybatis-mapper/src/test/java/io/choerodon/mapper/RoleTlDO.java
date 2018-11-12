package io.choerodon.mapper;

import javax.persistence.*;

/**
 * @author superlee
 */
@Table(name = "iam_role_tl")
public class RoleTlDO {
    @Id
    private Long id;
    private String lang;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
