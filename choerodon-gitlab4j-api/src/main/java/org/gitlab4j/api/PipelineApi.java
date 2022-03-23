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
import java.util.Map;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.gitlab4j.api.models.Pipeline;
import org.gitlab4j.api.models.PipelineSchedule;
import org.gitlab4j.api.models.PipelineStatus;
import org.gitlab4j.api.models.Variable;

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
     * Create a pipelines in a project.
     *
     * <pre><code>GitLab Endpoint: POST /projects/:id/pipeline</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param ref reference to commit
     * @param variables a Map containing the variables available in the pipeline
     * @return a Pipeline instance with the newly created pipeline info
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Pipeline createPipeline(Object projectIdOrPath, String ref, Map<String, String> variables) throws GitLabApiException {
        return (createPipeline(projectIdOrPath, ref, Variable.convertMapToList(variables)));
    }

    /**
     * Create a pipelines in a project.
     *
     * <pre><code>GitLab Endpoint: POST /projects/:id/pipeline</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param ref reference to commit
     * @param variables a Map containing the variables available in the pipeline
     * @return a Pipeline instance with the newly created pipeline info
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Pipeline createPipeline(Object projectIdOrPath, String ref, List<Variable> variables) throws GitLabApiException {

        if (ref == null || ref.trim().isEmpty()) {
            throw new GitLabApiException("ref cannot be null or empty");
        }

        if (variables == null || variables.isEmpty()) {
            GitLabApiForm formData = new GitLabApiForm().withParam("ref", ref, true);
            Response response = post(Response.Status.CREATED, formData, "projects", getProjectIdOrPath(projectIdOrPath), "pipeline");
            return (response.readEntity(Pipeline.class));
        }

        // The create pipeline REST API expects the variable data in an unusual format, this
        // class is used to create the JSON for the POST data.
        class CreatePipelineForm {
            @SuppressWarnings("unused")
            public String ref;
            @SuppressWarnings("unused")
            public List<Variable> variables;
            CreatePipelineForm(String ref, List<Variable> variables) {
                this.ref = ref;
                this.variables = variables;
            }
        }

        CreatePipelineForm pipelineForm = new CreatePipelineForm(ref, variables);
        Response response = post(Response.Status.CREATED, pipelineForm, "projects", getProjectIdOrPath(projectIdOrPath), "pipeline");
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
        Response response = post(Response.Status.CREATED, formData, "projects", projectId, "pipelines", pipelineId, "retry");
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

    /**
     * Get a list of the project pipeline_schedules for the specified project.
     *
     * <pre><code>GET /projects/:id/pipeline_schedules</code></pre>
     *
     * @param projectIdOrPath projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance, required
     * @return a list of pipeline schedules for the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public List<PipelineSchedule> getPipelineSchedules(Object projectIdOrPath) throws GitLabApiException {
        return (getPipelineSchedules(projectIdOrPath, getDefaultPerPage()).all());
    }

    /**
     * Get list of project pipeline schedules in the specified page range.
     *
     * <pre><code>GET /projects/:id/pipeline_schedules</code></pre>
     *
     * @param projectIdOrPath projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance, required
     * @param page the page to get
     * @param perPage the number of PipelineSchedule instances per page
     * @return a list of project pipeline_schedules for the specified project in the specified page range
     * @throws GitLabApiException if any exception occurs
     */
    public List<PipelineSchedule> getPipelineSchedules(Object projectIdOrPath, int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage), "projects", getProjectIdOrPath(projectIdOrPath), "pipeline_schedules");
        return (response.readEntity(new GenericType<List<PipelineSchedule>>() {}));
    }

    /**
     * Get Pager of project pipeline schedule.
     *
     * <pre><code>GET /projects/:id/pipeline_schedule</code></pre>
     *
     * @param projectIdOrPath projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance, required
     * @param itemsPerPage the number of PipelineSchedule instances that will be fetched per page
     * @return a Pager of project pipeline_schedules for the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<PipelineSchedule> getPipelineSchedules(Object projectIdOrPath, int itemsPerPage) throws GitLabApiException {
        return (new Pager<PipelineSchedule>(this, PipelineSchedule.class, itemsPerPage, null, "projects", getProjectIdOrPath(projectIdOrPath), "pipeline_schedules"));
    }


    /**
     * Get a specific pipeline schedule for project.
     *
     * <pre><code>GET /projects/:id/pipeline_schedules/:pipeline_schedule_id</code></pre>
     *
     * @param projectIdOrPath projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance, required
     * @param pipelineScheduleId the ID of the pipeline schedule to get
     * @return the project PipelineSchedule
     * @throws GitLabApiException if any exception occurs
     */
    public PipelineSchedule getPipelineSchedule(Object projectIdOrPath, Integer pipelineScheduleId) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, "projects", getProjectIdOrPath(projectIdOrPath), "pipeline_schedules", pipelineScheduleId);
        return (response.readEntity(PipelineSchedule.class));
    }

    /**
     * create a pipeline schedule for a project.
     *
     * <pre><code>POST /projects/:id/pipeline_schedules</code></pre>
     *
     * @param projectIdOrPath projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance, required
     * @param pipelineSchedule a PipelineSchedule instance to create
     * @return the added PipelineSchedule instance
     * @throws GitLabApiException if any exception occurs
     */
    public PipelineSchedule createPipelineSchedule(Object projectIdOrPath, PipelineSchedule pipelineSchedule)
            throws GitLabApiException {

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("description", pipelineSchedule.getDescription(), true)
                .withParam("ref", pipelineSchedule.getRef(), true)
                .withParam("cron", pipelineSchedule.getCron(), true)
                .withParam("cron_timezone", pipelineSchedule.getCronTimezone(), false)
                .withParam("active", pipelineSchedule.getActive(), false);
        Response response = post(Response.Status.CREATED, formData, "projects", getProjectIdOrPath(projectIdOrPath), "pipeline_schedules");
        return (response.readEntity(PipelineSchedule.class));
    }

    /**
     * Deletes a pipeline schedule from the project.
     *
     * <pre><code>DELETE /projects/:id/pipeline_schedules/:pipeline_schedule_id</code></pre>
     *
     * @param projectIdOrPath projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance, required
     * @param pipelineScheduleId the project schedule ID to delete
     * @throws GitLabApiException if any exception occurs
     */
    public void deletePipelineSchedule(Object projectIdOrPath, Integer pipelineScheduleId) throws GitLabApiException {
        Response.Status expectedStatus = (isApiVersion(GitLabApi.ApiVersion.V3) ? Response.Status.OK : Response.Status.NO_CONTENT);
        delete(expectedStatus, null, "projects", getProjectIdOrPath(projectIdOrPath), "pipeline_schedules", pipelineScheduleId);
    }

    /**
     * Modifies a pipeline schedule for project.
     *
     * <pre><code>PUT /projects/:id/pipeline_schedules/:pipeline_schedule_id</code></pre>
     *
     * @param projectIdOrPath projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance, required
     * @param pipelineSchedule the pipelineSchedule instance that contains the pipelineSchedule info to modify
     * @return the modified project schedule
     * @throws GitLabApiException if any exception occurs
     */
    public PipelineSchedule updatePipelineSchedule(Object projectIdOrPath,PipelineSchedule pipelineSchedule) throws GitLabApiException {

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("description", pipelineSchedule.getDescription(), false)
                .withParam("ref", pipelineSchedule.getRef(), false)
                .withParam("cron", pipelineSchedule.getCron(), false)
                .withParam("cron_timezone", pipelineSchedule.getCronTimezone(), false)
                .withParam("active", pipelineSchedule.getActive(), false);

        Response response = put(Response.Status.OK, formData.asMap(), "projects", getProjectIdOrPath(projectIdOrPath), "pipeline_schedules", pipelineSchedule.getId());
        return (response.readEntity(PipelineSchedule.class));
    }

    /**
     * Update the owner of the pipeline schedule of a project.
     *
     * <pre><code>POST /projects/:id/pipeline_schedules/:pipeline_schedule_id/take_ownership</code></pre>
     *
     * @param projectIdOrPath projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance, required
     * @param pipelineScheduleId the pipelineSchedule instance id that ownership has to be taken of
     * @return the modified project schedule
     * @throws GitLabApiException if any exception occurs
     */
    public PipelineSchedule takeOwnershipPipelineSchedule(Object projectIdOrPath, Integer pipelineScheduleId) throws GitLabApiException {

        Response response = post(Response.Status.OK, "", "projects", getProjectIdOrPath(projectIdOrPath),  "pipeline_schedules", pipelineScheduleId, "take_ownership");
        return (response.readEntity(PipelineSchedule.class));
    }

    /**
     * Create a pipeline schedule variable.
     *
     * <pre><code>GitLab Endpoint: POST /projects/:id/pipeline_schedules/:pipeline_schedule_id/variables</code></pre>
     *
     * @param projectIdOrPath projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance, required
     * @param pipelineScheduleId the pipelineSchedule ID
     * @param key the key of a variable; must have no more than 255 characters; only A-Z, a-z, 0-9, and _ are allowed
     * @param value the value for the variable
     * @return a Pipeline instance with the newly created pipeline schedule variable
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Variable createPipelineScheduleVariable(Object projectIdOrPath, Integer pipelineScheduleId,
                                                   String key, String value) throws GitLabApiException {

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("key", key, true)
                .withParam("value", value, true);
        Response response = post(Response.Status.CREATED, formData,
                "projects", getProjectIdOrPath(projectIdOrPath), "pipeline_schedules", pipelineScheduleId, "variables");
        return (response.readEntity(Variable.class));
    }

    /**
     * Update a pipeline schedule variable.
     *
     * <pre><code>GitLab Endpoint: PUT /projects/:id/pipeline_schedules/:pipeline_schedule_id/variables/:key</code></pre>
     *
     * @param projectIdOrPath projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance, required
     * @param pipelineScheduleId the pipelineSchedule ID
     * @param key the key of an existing pipeline schedule variable
     * @param value the new value for the variable
     * @return a Pipeline instance with the updated variable
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Variable updatePipelineScheduleVariable(Object projectIdOrPath, Integer pipelineScheduleId,
                                                   String key, String value) throws GitLabApiException {

        GitLabApiForm formData = new GitLabApiForm().withParam("value", value, true);
        Response response = this.putWithFormData(Response.Status.CREATED, formData,
                "projects", getProjectIdOrPath(projectIdOrPath), "pipeline_schedules", pipelineScheduleId, "variables", key);
        return (response.readEntity(Variable.class));
    }

    /**
     * Deletes a pipeline schedule variable.
     *
     * <pre><code>DELETE /projects/:id/pipeline_schedules/:pipeline_schedule_id/variables/:key</code></pre>
     *
     * @param projectIdOrPath projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance, required
     * @param pipelineScheduleId the pipeline schedule ID
     * @param key the key of an existing pipeline schedule variable
     * @throws GitLabApiException if any exception occurs
     */
    public void deletePipelineScheduleVariable(Object projectIdOrPath, Integer pipelineScheduleId, String key) throws GitLabApiException {
        Response.Status expectedStatus = (isApiVersion(GitLabApi.ApiVersion.V3) ? Response.Status.OK : Response.Status.NO_CONTENT);
        delete(expectedStatus, null, "projects", getProjectIdOrPath(projectIdOrPath),
                "pipeline_schedules", pipelineScheduleId, "variables", key);
    }
}
