package org.gitlab4j.api;

/*
 *   The MIT License (MIT)
 *   
 *   Copyright (c) 2017 Greg Messner <greg@messners.com>
 *   
 *   Permission is hereby granted, free of charge, to any person obtaining a copy of
 *   this software and associated documentation files (the "Software"), to deal in
 *   the Software without restriction, including without limitation the rights to
 *   use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *   the Software, and to permit persons to whom the Software is furnished to do so,
 *   subject to the following conditions:
 *   
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *   
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *   FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *   COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *   IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *   CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.gitlab4j.api.GitLabApi.ApiVersion;
import org.gitlab4j.api.models.Project;

/**
 * Access for the services API.
 * See
 * <a href="https://github.com/gitlabhq/gitlabhq/blob/master/doc/api/services.md">GitLab documentation</a>.
 * It is quite restricted as you may not retrieve the API but only set or delete.
 */
public class ServicesApi extends AbstractApi {

    public ServicesApi(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    /**
     * Activates the gitlab-ci service.
     *
     * PUT /projects/:id/services/gitlab-ci
     * @param projectId id of the project
     * @param token for authentication
     * @param projectCIUrl URL of the GitLab-CI project
     * @throws GitLabApiException if any exception occurs
     */
    public void setGitLabCI(Integer projectId, String token, String projectCIUrl) throws GitLabApiException {
        final Form formData = new Form();
        formData.param("token", token);
        formData.param("project_url", projectCIUrl);
        put(Response.Status.OK, formData.asMap(), "projects", projectId, "services", "gitlab-ci");
    }

    /**
     * Activates the gitlab-ci service.
     *
     * PUT /projects/:id/services/gitlab-ci
     * @param project the project
     * @param token for authentication
     * @param projectCIUrl URL of the GitLab-CI project
     * @throws GitLabApiException if any exception occurs
     */
    public void setGitLabCI(Project project, String token, String projectCIUrl) throws GitLabApiException {
        setGitLabCI(project.getId(), token, projectCIUrl);
    }

    /**
     * Deletes the gitlab-ci service.
     *
     * DELETE /projects/:id/services/gitlab-ci
     *
     * @param projectId id of the project
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteGitLabCI(Integer projectId) throws GitLabApiException {
        Response.Status expectedStatus = (isApiVersion(ApiVersion.V3) ? Response.Status.OK : Response.Status.NO_CONTENT);
        delete(expectedStatus, null, "projects", projectId, "services", "gitlab-ci");
    }

    /**
     * DELETE /projects/:id/services/gitlab-ci
     * @param project to delete
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteGitLabCI(Project project) throws GitLabApiException {
        deleteGitLabCI(project.getId());
    }

    /**
     * Activates HipChat notifications.
     *
     * PUT /projects/:id/services/hipchat
     *
     * @param projectId id of the project
     * @param token for authentication
     * @param room HipChat Room
     * @param server HipChat Server URL
     * @throws GitLabApiException if any exception occurs
     */
    public void setHipChat(Integer projectId, String token, String room, String server) throws GitLabApiException {
        final Form formData = new Form();
        formData.param("token", token);
        formData.param("room", room);
        formData.param("server", server);
        put(Response.Status.OK, formData.asMap(), "projects", projectId, "services", "hipchat");
    }

    /**
     * Activates HipChat notifications.
     *
     * PUT /projects/:id/services/hipchat
     *
     * @param project the Project instance to activate Hipchat for
     * @param token for authentication
     * @param room HipChat Room
     * @param server HipChat Server URL
     * @throws GitLabApiException if any exception occurs
     */
    public void setHipChat(Project project, String token, String room, String server) throws GitLabApiException {
        setHipChat(project.getId(), token, room, server);
    }

    /**
     * Deletes the gitlab-ci service.
     *
     * DELETE /projects/:id/services/hipchat
     *
     * @param projectId id of the project
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteHipChat(Integer projectId) throws GitLabApiException {
        Response.Status expectedStatus = (isApiVersion(ApiVersion.V3) ? Response.Status.OK : Response.Status.NO_CONTENT);
        delete(expectedStatus, null, "projects", projectId, "services", "hipchat");
    }

    /**
     * Deletes the gitlab-ci service.
     *
     * DELETE /projects/:id/services/hipchat
     *
     * @param project the project
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteHipChat(Project project) throws GitLabApiException {
        deleteHipChat(project.getId());
    }
}
