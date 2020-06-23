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

import java.util.List;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.gitlab4j.api.models.DeployKey;

/**
 * This class implements the client side API for the GitLab Deploy Keys API calls.
 */
public class DeployKeysApi extends AbstractApi {

    public DeployKeysApi(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    /**
     * Get a list of all deploy keys across all projects of the GitLab instance. This method requires admin access.
     * Only returns the first page.
     * <p>
     * GET /deploy_keys
     *
     * @return a list of DeployKey
     * @throws GitLabApiException if any exception occurs
     */
    public List<DeployKey> getDeployKeys() throws GitLabApiException {
        return (getDeployKeys(1, getDefaultPerPage()));
    }

    /**
     * Get a list of all deploy keys across all projects of the GitLab instance using the specified page and per page settings.
     * This method requires admin access.
     * <p>
     * GET /deploy_keys
     *
     * @param page    the page to get
     * @param perPage the number of deploy keys per page
     * @return the list of DeployKey in the specified range
     * @throws GitLabApiException if any exception occurs
     */
    public List<DeployKey> getDeployKeys(int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage), "deploy_keys");
        return (response.readEntity(new GenericType<List<DeployKey>>() {
        }));
    }

    /**
     * Get a Pager of all deploy keys across all projects of the GitLab instance. This method requires admin access.
     * <p>
     * GET /deploy_keys
     *
     * @param itemsPerPage the number of DeployKey instances that will be fetched per page
     * @return a Pager of DeployKey
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<DeployKey> getDeployKeys(int itemsPerPage) throws GitLabApiException {
        return (new Pager<DeployKey>(this, DeployKey.class, itemsPerPage, null, "deploy_keys"));
    }

    /**
     * Get a list of the deploy keys for the specified project. This method requires admin access.
     * Only returns the first page.
     * <p>
     * GET /projects/:id/deploy_keys
     *
     * @param projectId the ID of the project
     * @return a list of DeployKey
     * @throws GitLabApiException if any exception occurs
     */
    public List<DeployKey> getProjectDeployKeys(Integer projectId) throws GitLabApiException {
        return (getProjectDeployKeys(projectId, 1, getDefaultPerPage()));
    }

    /**
     * Get a list of the deploy keys for the specified project using the specified page and per page settings.
     * This method requires admin access.
     * <p>
     * GET /projects/:id/deploy_keys
     *
     * @param projectId the ID of the project
     * @param page      the page to get
     * @param perPage   the number of deploy keys per page
     * @return the list of DeployKey in the specified range
     * @throws GitLabApiException if any exception occurs
     */
    public List<DeployKey> getProjectDeployKeys(Integer projectId, int page, int perPage) throws GitLabApiException {

        if (projectId == null) {
            throw new RuntimeException("projectId cannot be null");
        }

        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage), "projects", projectId, "deploy_keys");
        return (response.readEntity(new GenericType<List<DeployKey>>() {
        }));
    }

    /**
     * Get a Pager of the deploy keys for the specified project. This method requires admin access.
     * <p>
     * GET /projects/:id/deploy_keys
     *
     * @param projectId    the ID of the project
     * @param itemsPerPage the number of DeployKey instances that will be fetched per page
     * @return a Pager of DeployKey
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<DeployKey> getProjectDeployKeys(Integer projectId, Integer itemsPerPage) throws GitLabApiException {

        if (projectId == null) {
            throw new RuntimeException("projectId cannot be null");
        }

        return (new Pager<DeployKey>(this, DeployKey.class, itemsPerPage, null, "projects", projectId, "deploy_keys"));
    }

    /**
     * Get a single deploy key for the specified project.
     * <p>
     * GET /projects/:id/deploy_keys/:key_id
     *
     * @param projectId the ID of the project
     * @param keyId     the ID of the deploy key to delete
     * @return the DeployKey instance for the specified project ID and key ID
     * @throws GitLabApiException if any exception occurs
     */
    public DeployKey getDeployKey(Integer projectId, Integer keyId) throws GitLabApiException {

        if (projectId == null) {
            throw new RuntimeException("projectId cannot be null");
        }

        if (keyId == null) {
            throw new RuntimeException("keyId cannot be null");
        }

        Response response = get(Response.Status.OK, null, "projects", projectId, "deploy_keys", keyId);
        return (response.readEntity(DeployKey.class));
    }

    /**
     * Creates a new deploy key for a project.
     * <p>
     * POST /projects/:id/deploy_keys
     *
     * @param projectId the ID of the project owned by the authenticated user, required
     * @param title     the new deploy key's title, required
     * @param key       the new deploy key, required
     * @param canPush   can deploy key push to the project's repository, optional
     * @return an DeployKey instance with info on the added deploy key
     * @throws GitLabApiException if any exception occurs
     */
    public DeployKey addDeployKey(Integer projectId, String title, String key, Boolean canPush) throws GitLabApiException {

        if (projectId == null) {
            throw new RuntimeException("projectId cannot be null");
        }

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("title", title, true)
                .withParam("key", key, true)
                .withParam("can_push", canPush);
        Response response = post(Response.Status.CREATED, formData, "projects", projectId, "deploy_keys");
        return (response.readEntity(DeployKey.class));
    }

    /**
     * Removes a deploy key from the project. If the deploy key is used only for this project,it will be deleted from the system.
     * <p>
     * DELETE /projects/:id/deploy_keys/:key_id
     *
     * @param projectId the ID of the project
     * @param keyId     the ID of the deploy key to delete
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteDeployKey(Integer projectId, Integer keyId) throws GitLabApiException {

        if (projectId == null) {
            throw new RuntimeException("projectId cannot be null");
        }

        if (keyId == null) {
            throw new RuntimeException("keyId cannot be null");
        }

        delete(Response.Status.OK, null, "projects", projectId, "deploy_keys", keyId);
    }

    /**
     * Enables a deploy key for a project so this can be used. Returns the enabled key when successful.
     * <p>
     * POST /projects/:id/deploy_keys/:key_id/enable
     *
     * @param projectId the ID of the project
     * @param keyId     the ID of the deploy key to enable
     * @return an DeployKey instance with info on the enabled deploy key
     * @throws GitLabApiException if any exception occurs
     */
    public DeployKey enableDeployKey(Integer projectId, Integer keyId) throws GitLabApiException {

        if (projectId == null) {
            throw new RuntimeException("projectId cannot be null");
        }

        if (keyId == null) {
            throw new RuntimeException("keyId cannot be null");
        }

        Response response = post(Response.Status.CREATED, (Form) null, "projects", projectId, "deploy_keys", keyId, "enable");
        return (response.readEntity(DeployKey.class));
    }
}
