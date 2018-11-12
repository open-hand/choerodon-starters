package org.gitlab4j.api;

import javax.ws.rs.core.Response;

public class ApplicationApi extends AbstractApi {


    public ApplicationApi(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    /**
     * Modifies Application Setting
     * <p>
     * PUT /application/settings
     *
     * @param allowLocalRequestsFromHooksAndServices Allow requests to the local network from hooks and services
     * @throws GitLabApiException if any exception occurs
     */
    public void modifyApplicationSetting(Boolean allowLocalRequestsFromHooksAndServices) throws GitLabApiException {

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("allow_local_requests_from_hooks_and_services", allowLocalRequestsFromHooksAndServices, true);
        put(Response.Status.OK, formData.asMap(), "application", "settings");
    }
}
