package io.choerodon.core.oauth;

import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import java.util.Objects;

/**
 * 定制的clientDetail对象，添加了组织ID
 *
 * @author wuguokai
 */
public class CustomClientDetails extends BaseClientDetails {

    private Long organizationId;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CustomClientDetails that = (CustomClientDetails) o;
        return Objects.equals(organizationId, that.organizationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), organizationId);
    }
}
