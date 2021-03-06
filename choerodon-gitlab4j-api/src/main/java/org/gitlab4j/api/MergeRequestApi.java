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

import org.gitlab4j.api.GitLabApi.ApiVersion;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.MergeRequestParams;

/**
 * This class implements the client side API for the GitLab merge request calls.
 */
public class MergeRequestApi extends AbstractApi {

    public MergeRequestApi(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    /**
     * Get all merge requests for the specified project.
     * <p>
     * GET /projects/:id/merge_requests
     *
     * @param projectId the project ID to get the merge requests for
     * @return all merge requests for the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public List<MergeRequest> getMergeRequests(Integer projectId) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "projects", projectId, "merge_requests");
        return (response.readEntity(new GenericType<List<MergeRequest>>() {
        }));
    }

    /**
     * Get all merge requests for the specified project.
     * <p>
     * GET /projects/:id/merge_requests
     *
     * @param projectId the project ID to get the merge requests for
     * @param page      the page to get
     * @param perPage   the number of MergeRequest instances per page
     * @return all merge requests for the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public List<MergeRequest> getMergeRequests(Integer projectId, int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage), "projects", projectId, "merge_requests");
        return (response.readEntity(new GenericType<List<MergeRequest>>() {
        }));
    }

    /**
     * Get all merge requests for the specified project.
     * <p>
     * GET /projects/:id/merge_requests
     *
     * @param projectId    the project ID to get the merge requests for
     * @param itemsPerPage the number of MergeRequest instances that will be fetched per page
     * @return all merge requests for the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<MergeRequest> getMergeRequests(Integer projectId, int itemsPerPage) throws GitLabApiException {
        return (new Pager<MergeRequest>(this, MergeRequest.class, itemsPerPage, null, "projects", projectId, "merge_requests"));
    }

    /**
     * Get information about a single merge request.
     * <p>
     * GET /projects/:id/merge_requests/:merge_request_id
     *
     * @param projectId      the project ID of the merge request
     * @param mergeRequestId the ID of the merge request
     * @return the specified MergeRequest instance
     * @throws GitLabApiException if any exception occurs
     */
    public MergeRequest getMergeRequest(Integer projectId, Integer mergeRequestId) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, "projects", projectId, "merge_requests", mergeRequestId);
        return (response.readEntity(MergeRequest.class));
    }

    /**
     * Get all merge requests with a specific state for the specified project.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/merge_requests?state=:state</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param state           the state parameter can be used to get only merge requests with a given state (opened, closed, or merged) or all of them (all).
     * @return all merge requests for the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public List<MergeRequest> getMergeRequests(Object projectIdOrPath, MergeRequestState state) throws GitLabApiException {
        return (getMergeRequests(projectIdOrPath, state, getDefaultPerPage()).all());
    }

    /**
     * Get all merge requests for the specified project.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/merge_requests</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param state           the state parameter can be used to get only merge requests with a given state (opened, closed, or merged) or all of them (all).
     * @param page            the page to get
     * @param perPage         the number of MergeRequest instances per page
     * @return all merge requests for the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public List<MergeRequest> getMergeRequests(Object projectIdOrPath, MergeRequestState state, int page, int perPage) throws GitLabApiException {
        Form formData = new GitLabApiForm()
                .withParam("state", state)
                .withParam(PAGE_PARAM, page)
                .withParam(PER_PAGE_PARAM, perPage);
        Response response = get(Response.Status.OK, formData.asMap(), "projects", getProjectIdOrPath(projectIdOrPath), "merge_requests");
        return (response.readEntity(new GenericType<List<MergeRequest>>() {
        }));
    }

    /**
     * Get all merge requests for the specified project.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/merge_requests</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param state           the state parameter can be used to get only merge requests with a given state (opened, closed, or merged) or all of them (all).
     * @param itemsPerPage    the number of MergeRequest instances that will be fetched per page
     * @return all merge requests for the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<MergeRequest> getMergeRequests(Object projectIdOrPath, MergeRequestState state, int itemsPerPage) throws GitLabApiException {
        Form formData = new GitLabApiForm()
                .withParam("state", state);
        return (new Pager<MergeRequest>(this, MergeRequest.class, itemsPerPage, formData.asMap(), "projects", getProjectIdOrPath(projectIdOrPath), "merge_requests"));
    }

    /**
     * Get a list of merge request commits.
     * <p>
     * GET /projects/:id/merge_requests/:merge_request_iid/commits
     *
     * @param projectId      the project ID for the merge request
     * @param mergeRequestId the ID of the merge request
     * @return a list containing the commits for the specified merge request
     * @throws GitLabApiException GitLabApiException if any exception occurs during execution
     */
    public List<Commit> getCommits(int projectId, int mergeRequestId) throws GitLabApiException {
        return (getCommits(projectId, mergeRequestId, 1, getDefaultPerPage()));
    }

    /**
     * Get a list of merge request commits.
     * <p>
     * GET /projects/:id/merge_requests/:merge_request_iid/commits
     *
     * @param projectId      the project ID for the merge request
     * @param mergeRequestId the ID of the merge request
     * @param page           the page to get
     * @param perPage        the number of commits per page
     * @return a list containing the commits for the specified merge request
     * @throws GitLabApiException GitLabApiException if any exception occurs during execution
     */
    public List<Commit> getCommits(int projectId, int mergeRequestId, int page, int perPage) throws GitLabApiException {
        Form formData = new GitLabApiForm().withParam("owned", true).withParam(PAGE_PARAM, page).withParam(PER_PAGE_PARAM, perPage);
        Response response = get(Response.Status.OK, formData.asMap(), "projects", projectId, "merge_requests", mergeRequestId, "commits");
        return (response.readEntity(new GenericType<List<Commit>>() {
        }));
    }

    /**
     * Get a Pager of merge request commits.
     * <p>
     * GET /projects/:id/merge_requests/:merge_request_iid/commits
     *
     * @param projectId      the project ID for the merge request
     * @param mergeRequestId the ID of the merge request
     * @param itemsPerPage   the number of Commit instances that will be fetched per page
     * @return a Pager containing the commits for the specified merge request
     * @throws GitLabApiException GitLabApiException if any exception occurs during execution
     */
    public Pager<Commit> getCommits(int projectId, int mergeRequestId, int itemsPerPage) throws GitLabApiException {
        return (new Pager<Commit>(this, Commit.class, itemsPerPage, null,
                "projects", projectId, "merge_requests", mergeRequestId, "commits"));
    }

    /**
     * Creates a merge request.
     *
     * <pre><code>GitLab Endpoint: POST /projects/:id/merge_requests</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param params a MergeRequestParams instance holding the info to create the merge request
     * @return the created MergeRequest instance
     * @throws GitLabApiException if any exception occurs
     * @since GitLab Starter 8.17, GitLab CE 11.0.
     */
    public MergeRequest createMergeRequest(Object projectIdOrPath, MergeRequestParams params) throws GitLabApiException {
        GitLabApiForm form = params.getForm(true);
        Response response = post(Response.Status.CREATED, form, "projects", getProjectIdOrPath(projectIdOrPath), "merge_requests");
        return (response.readEntity(MergeRequest.class));
    }

    /**
     * Updates an existing merge request. You can change branches, title, or even close the MR.
     * <p>
     * PUT /projects/:id/merge_requests/:merge_request_id
     *
     * @param projectId      the ID of a project
     * @param mergeRequestId the internal ID of the merge request to update
     * @param targetBranch   the target branch, optional
     * @param title          the title for the merge request
     * @param assigneeId     the Assignee user ID, optional
     * @param description    the description of the merge request, optional
     * @param stateEvent     new state for the merge request, optional
     * @param labels         comma separated list of labels, optional
     * @param milestoneId    the ID of a milestone, optional
     * @return the updated merge request
     * @throws GitLabApiException if any exception occurs
     */
    public MergeRequest updateMergeRequest(Integer projectId, Integer mergeRequestId, String targetBranch,
                                           String title, Integer assigneeId, String description, StateEvent stateEvent, String labels,
                                           Integer milestoneId) throws GitLabApiException {

        if (projectId == null) {
            throw new GitLabApiException("projectId cannot be null");
        }

        if (mergeRequestId == null) {
            throw new GitLabApiException("mergeRequestId cannot be null");
        }

        Form formData = new GitLabApiForm()
                .withParam("target_branch", targetBranch)
                .withParam("title", title)
                .withParam("assignee_id", assigneeId)
                .withParam("description", description)
                .withParam("state_event", stateEvent)
                .withParam("labels", labels)
                .withParam("milestone_id", milestoneId);

        Response response = put(Response.Status.OK, formData.asMap(), "projects", projectId, "merge_requests", mergeRequestId);
        return (response.readEntity(MergeRequest.class));
    }

    /**
     * Updates an existing merge request. You can change branches, title, or even close the MR.
     * <p>
     * PUT /projects/:id/merge_requests/:merge_request_id
     *
     * @param projectId      the ID of a project
     * @param mergeRequestId the ID of the merge request to update
     * @param sourceBranch   the source branch
     * @param targetBranch   the target branch
     * @param title          the title for the merge request
     * @param description    the description of the merge request
     * @param assigneeId     the Assignee user ID, optional
     * @return the updated merge request
     * @throws GitLabApiException if any exception occurs
     * @deprecated as of release 4.4.3
     */
    @Deprecated
    public MergeRequest updateMergeRequest(Integer projectId, Integer mergeRequestId, String sourceBranch, String targetBranch, String title, String description,
                                           Integer assigneeId) throws GitLabApiException {

        if (projectId == null) {
            throw new GitLabApiException("projectId cannot be null");
        }

        if (mergeRequestId == null) {
            throw new GitLabApiException("mergeRequestId cannot be null");
        }

        Form formData = new Form();
        addFormParam(formData, "source_branch", sourceBranch, false);
        addFormParam(formData, "target_branch", targetBranch, false);
        addFormParam(formData, "title", title, false);
        addFormParam(formData, "description", description, false);
        addFormParam(formData, "assignee_id", assigneeId, false);

        Response response = put(Response.Status.OK, formData.asMap(), "projects", projectId, "merge_requests", mergeRequestId);
        return (response.readEntity(MergeRequest.class));
    }


    public void deleteMergeRequest(Integer projectId, Integer mergeRequestId) throws GitLabApiException {

        if (projectId == null) {
            throw new GitLabApiException("projectId cannot be null");
        }

        if (mergeRequestId == null) {
            throw new GitLabApiException("mergeRequestId cannot be null");
        }

        Response.Status expectedStatus = (isApiVersion(ApiVersion.V3) ? Response.Status.OK : Response.Status.NO_CONTENT);
        delete(expectedStatus, null, "projects", projectId, "merge_requests", mergeRequestId);
    }

    /**
     * Merge changes to the merge request. If the MR has any conflicts and can not be merged,
     * you'll get a 405 and the error message 'Branch cannot be merged'. If merge request is
     * already merged or closed, you'll get a 406 and the error message 'Method Not Allowed'.
     * If the sha parameter is passed and does not match the HEAD of the source, you'll get
     * a 409 and the error message 'SHA does not match HEAD of source branch'.  If you don't
     * have permissions to accept this merge request, you'll get a 401.
     * <p>
     * PUT /projects/:id/merge_requests/:merge_request_iid/merge
     *
     * @param projectId                  the ID of a project
     * @param mergeRequestId             the internal ID of the merge request
     * @param mergeCommitMessage,        custom merge commit message, optional
     * @param shouldRemoveSourceBranch,  if true removes the source branch, optional
     * @param mergeWhenPipelineSucceeds, if true the MR is merged when the pipeline, optional
     * @return the merged merge request
     * @throws GitLabApiException if any exception occurs
     */
    public MergeRequest acceptMergeRequest(Integer projectId, Integer mergeRequestId,
                                           String mergeCommitMessage, Boolean shouldRemoveSourceBranch, Boolean mergeWhenPipelineSucceeds)
            throws GitLabApiException {

        if (projectId == null) {
            throw new GitLabApiException("projectId cannot be null");
        }

        if (mergeRequestId == null) {
            throw new GitLabApiException("mergeRequestId cannot be null");
        }

        Form formData = new GitLabApiForm()
                .withParam("merge_commit_message", mergeCommitMessage)
                .withParam("should_remove_source_branch", shouldRemoveSourceBranch)
                .withParam("merge_when_pipeline_succeeds", mergeWhenPipelineSucceeds);

        Response response = put(Response.Status.OK, formData.asMap(), "projects", projectId, "merge_requests", mergeRequestId, "merge");
        return (response.readEntity(MergeRequest.class));
    }

    /**
     * Cancel merge when pipeline succeeds. If you don't have permissions to accept this merge request,
     * you'll get a 401. If the merge request is already merged or closed, you get 405 and
     * error message 'Method Not Allowed'. In case the merge request is not set to be merged when the
     * pipeline succeeds, you'll also get a 406 error.
     * <p>
     * PUT /projects/:id/merge_requests/:merge_request_iid/cancel_merge_when_pipeline_succeeds
     *
     * @param projectId      the ID of a project
     * @param mergeRequestId the internal ID of the merge request
     * @return the updated merge request
     * @throws GitLabApiException if any exception occurs
     */
    public MergeRequest cancelMergeRequest(Integer projectId, Integer mergeRequestId) throws GitLabApiException {

        if (projectId == null) {
            throw new GitLabApiException("projectId cannot be null");
        }

        if (mergeRequestId == null) {
            throw new GitLabApiException("mergeRequestId cannot be null");
        }

        Response response = put(Response.Status.OK, null, "projects", projectId, "merge_requests", mergeRequestId, "cancel_merge_when_pipeline_succeeds");
        return (response.readEntity(MergeRequest.class));
    }

    /**
     * Get the merge request with approval information.
     * <p>
     * Note: This API endpoint is only available on 8.9 EE and above.
     * <p>
     * GET /projects/:id/merge_requests/:merge_request_iid/approvals
     *
     * @param projectId      the project ID of the merge request
     * @param mergeRequestId the internal ID of the merge request
     * @return a MergeRequest instance with approval information included
     * @throws GitLabApiException if any exception occurs
     */
    public MergeRequest getMergeRequestApprovals(Integer projectId, Integer mergeRequestId) throws GitLabApiException {

        if (projectId == null) {
            throw new GitLabApiException("projectId cannot be null");
        }

        if (mergeRequestId == null) {
            throw new GitLabApiException("mergeRequestId cannot be null");
        }

        Response response = get(Response.Status.OK, null, "projects", projectId, "merge_requests", mergeRequestId, "approvals");
        return (response.readEntity(MergeRequest.class));
    }

    /**
     * Approve a merge request.
     * <p>
     * Note: This API endpoint is only available on 8.9 EE and above.
     * <p>
     * POST /projects/:id/merge_requests/:merge_request_iid/approve
     *
     * @param projectId      the project ID of the merge request
     * @param mergeRequestId the internal ID of the merge request
     * @param sha            the HEAD of the merge request, optional
     * @return a MergeRequest instance with approval information included
     * @throws GitLabApiException if any exception occurs
     */
    public MergeRequest approveMergeRequest(Integer projectId, Integer mergeRequestId, String sha) throws GitLabApiException {

        if (projectId == null) {
            throw new GitLabApiException("projectId cannot be null");
        }

        if (mergeRequestId == null) {
            throw new GitLabApiException("mergeRequestId cannot be null");
        }

        Form formData = new GitLabApiForm().withParam("sha", sha);
        Response response = post(Response.Status.OK, formData, "projects", projectId, "merge_requests", mergeRequestId, "approve");
        return (response.readEntity(MergeRequest.class));
    }

    /**
     * Unapprove a merge request.
     * <p>
     * Note: This API endpoint is only available on 8.9 EE and above.
     * <p>
     * POST /projects/:id/merge_requests/:merge_request_iid/unapprove
     *
     * @param projectId      the project ID of the merge request
     * @param mergeRequestId the internal ID of the merge request
     * @return a MergeRequest instance with approval information included
     * @throws GitLabApiException if any exception occurs
     */
    public MergeRequest unapproveMergeRequest(Integer projectId, Integer mergeRequestId) throws GitLabApiException {

        if (projectId == null) {
            throw new GitLabApiException("projectId cannot be null");
        }

        if (mergeRequestId == null) {
            throw new GitLabApiException("mergeRequestId cannot be null");
        }

        Response response = post(Response.Status.OK, (Form) null, "projects", projectId, "merge_requests", mergeRequestId, "unapprove");
        return (response.readEntity(MergeRequest.class));
    }
}
