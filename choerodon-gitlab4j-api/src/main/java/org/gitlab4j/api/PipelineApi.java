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

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.gitlab4j.api.models.Pipeline;
import org.gitlab4j.api.models.PipelineStatus;

/**
 * This class provides an entry point to all the GitLab API pipeline calls.
 */
public class PipelineApi extends AbstractApi implements Constants {

    public PipelineApi(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    /**
     * Get a list of pipelines in a project.
     * <p>
     * GET /projects/:id/pipelines
     *
     * @param projectId the project ID to get the list of pipelines for
     * @return a list containing the pipelines for the specified project ID
     * @throws GitLabApiException if any exception occurs during execution
     */
    public List<Pipeline> getPipelines(int projectId) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "projects", projectId, "pipelines");
        return (response.readEntity(new GenericType<List<Pipeline>>() {
        }));
    }

    /**
     * Get a list of pipelines in a project in the specified page range.
     * <p>
     * GET /projects/:id/pipelines
     *
     * @param projectId the project ID to get the list of pipelines for
     * @param page      the page to get
     * @param perPage   the number of Pipeline instances per page
     * @return a list containing the pipelines for the specified project ID in the specified page range
     * @throws GitLabApiException if any exception occurs during execution
     */
    public List<Pipeline> getPipelines(int projectId, int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage), "projects", projectId, "pipelines");
        return (response.readEntity(new GenericType<List<Pipeline>>() {
        }));
    }

    /**
     * Get a Pager of pipelines in a project.
     * <p>
     * GET /projects/:id/pipelines
     *
     * @param projectId    the project ID to get the list of pipelines for
     * @param itemsPerPage the number of Pipeline instances that will be fetched per page
     * @return a Pager containing the pipelines for the specified project ID
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Pager<Pipeline> getPipelines(int projectId, int itemsPerPage) throws GitLabApiException {
        return (new Pager<Pipeline>(this, Pipeline.class, itemsPerPage, null, "projects", projectId, "pipelines"));
    }

    /**
     * Get a list of pipelines in a project.
     * <p>
     * GET /projects/:id/pipelines
     *
     * @param projectId  the project ID to get the list of pipelines for
     * @param scope      the scope of pipelines, one of: RUNNING, PENDING, FINISHED, BRANCHES, TAGS
     * @param status     the status of pipelines, one of: RUNNING, PENDING, SUCCESS, FAILED, CANCELED, SKIPPED
     * @param ref        the ref of pipelines
     * @param yamlErrors returns pipelines with invalid configurations
     * @param name       the name of the user who triggered pipelines
     * @param username   the username of the user who triggered pipelines
     * @param orderBy    order pipelines by ID, STATUS, REF, USER_ID (default: ID)
     * @param sort       sort pipelines in ASC or DESC order (default: DESC)
     * @return a list containing the pipelines for the specified project ID
     * @throws GitLabApiException if any exception occurs during execution
     */
    public List<Pipeline> getPipelines(int projectId, PipelineScope scope, PipelineStatus status, String ref, boolean yamlErrors,
                                       String name, String username, PipelineOrderBy orderBy, SortOrder sort) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm()
                .withParam("scope", scope)
                .withParam("status", status)
                .withParam("ref", ref)
                .withParam("yaml_errors", yamlErrors)
                .withParam("name", name)
                .withParam("username", username)
                .withParam("order_by", orderBy)
                .withParam("sort", sort)
                .withParam(PER_PAGE_PARAM, getDefaultPerPage());

        Response response = get(Response.Status.OK, formData.asMap(), "projects", projectId, "pipelines");
        return (response.readEntity(new GenericType<List<Pipeline>>() {
        }));
    }

    /**
     * Get a list of pipelines in a project in the specified page range.
     * <p>
     * GET /projects/:id/pipelines
     *
     * @param projectId  the project ID to get the list of pipelines for
     * @param scope      the scope of pipelines, one of: RUNNING, PENDING, FINISHED, BRANCHES, TAGS
     * @param status     the status of pipelines, one of: RUNNING, PENDING, SUCCESS, FAILED, CANCELED, SKIPPED
     * @param ref        the ref of pipelines
     * @param yamlErrors returns pipelines with invalid configurations
     * @param name       the name of the user who triggered pipelines
     * @param username   the username of the user who triggered pipelines
     * @param orderBy    order pipelines by ID, STATUS, REF, USER_ID (default: ID)
     * @param sort       sort pipelines in ASC or DESC order (default: DESC)
     * @param page       the page to get
     * @param perPage    the number of Pipeline instances per page
     * @return a list containing the pipelines for the specified project ID
     * @throws GitLabApiException if any exception occurs during execution
     */
    public List<Pipeline> getPipelines(int projectId, PipelineScope scope, PipelineStatus status, String ref, boolean yamlErrors,
                                       String name, String username, PipelineOrderBy orderBy, SortOrder sort, int page, int perPage) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm()
                .withParam("scope", scope)
                .withParam("status", status)
                .withParam("ref", ref)
                .withParam("yaml_errors", yamlErrors)
                .withParam("name", name)
                .withParam("username", username)
                .withParam("order_by", orderBy)
                .withParam("sort", sort)
                .withParam("page", page)
                .withParam(PER_PAGE_PARAM, perPage);

        Response response = get(Response.Status.OK, formData.asMap(), "projects", projectId, "pipelines");
        return (response.readEntity(new GenericType<List<Pipeline>>() {
        }));
    }

    /**
     * Get a Pager of pipelines in a project.
     * <p>
     * GET /projects/:id/pipelines
     *
     * @param projectId    the project ID to get the list of pipelines for
     * @param scope        the scope of pipelines, one of: RUNNING, PENDING, FINISHED, BRANCHES, TAGS
     * @param status       the status of pipelines, one of: RUNNING, PENDING, SUCCESS, FAILED, CANCELED, SKIPPED
     * @param ref          the ref of pipelines
     * @param yamlErrors   returns pipelines with invalid configurations
     * @param name         the name of the user who triggered pipelines
     * @param username     the username of the user who triggered pipelines
     * @param orderBy      order pipelines by ID, STATUS, REF, USER_ID (default: ID)
     * @param sort         sort pipelines in ASC or DESC order (default: DESC)
     * @param itemsPerPage the number of Pipeline instances that will be fetched per page
     * @return a list containing the pipelines for the specified project ID
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Pager<Pipeline> getPipelines(int projectId, PipelineScope scope, PipelineStatus status, String ref, boolean yamlErrors,
                                        String name, String username, PipelineOrderBy orderBy, SortOrder sort, int itemsPerPage) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm()
                .withParam("scope", scope)
                .withParam("status", status)
                .withParam("ref", ref)
                .withParam("yaml_errors", yamlErrors)
                .withParam("name", name)
                .withParam("username", username)
                .withParam("order_by", orderBy)
                .withParam("sort", sort);

        return (new Pager<Pipeline>(this, Pipeline.class, itemsPerPage, formData.asMap(), "projects", projectId, "pipelines"));
    }

    /**
     * Get single pipelines in a project.
     * <p>
     * GET /projects/:id/pipelines/:pipeline_id
     *
     * @param projectId  the project ID to get the specified pipeline for
     * @param pipelineId the pipeline ID to get
     * @return a single pipelines for the specified project ID
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Pipeline getPipeline(int projectId, int pipelineId) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, "projects", projectId, "pipelines", pipelineId);
        return (response.readEntity(Pipeline.class));
    }

    /**
     * Create a pipelines in a project.
     * <p>
     * POST /projects/:id/pipelines
     *
     * @param projectId the project ID to create a pipeline in
     * @param ref       reference to commit
     * @return a Pipeline instance with the newly created pipeline info
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Pipeline createPipeline(int projectId, String ref) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm().withParam("ref", ref);

        Response response = post(Response.Status.CREATED, formData.asMap(), "projects", projectId, "pipeline");
        return (response.readEntity(Pipeline.class));
    }

    /**
     * Retry a job in specified pipelines in a project.
     * <p>
     * POST /projects/:id/pipelines/:pipeline_id/retry
     *
     * @param projectId  the project ID to retry a job for speficied pipeline
     * @param pipelineId the pipeline ID to retry a job from
     * @return pipeline instance which just retried
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Pipeline retryPipelineJob(int projectId, int pipelineId) throws GitLabApiException {
        GitLabApiForm formData = null;
        Response response = post(Response.Status.OK, formData, "projects", projectId, "pipelines", pipelineId, "retry");
        return (response.readEntity(Pipeline.class));
    }

    /**
     * Cancel jobs of specified pipelines in a project.
     * <p>
     * POST /projects/:id/pipelines/:pipeline_id/cancel
     *
     * @param projectId  the project ID to cancel jobs for speficied pipeline
     * @param pipelineId the pipeline ID to cancel jobs
     * @return pipeline instance which just canceled
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Pipeline cancelPipelineJobs(int projectId, int pipelineId) throws GitLabApiException {
        GitLabApiForm formData = null;
        Response response = post(Response.Status.OK, formData, "projects", projectId, "pipelines", pipelineId, "cancel");
        return (response.readEntity(Pipeline.class));
    }
}
