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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.gitlab4j.api.models.*;
import org.gitlab4j.api.utils.ISO8601;

/**
 * This class implements the client side API for the GitLab commits calls.
 */
public class CommitsApi extends AbstractApi {

    public CommitsApi(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    /**
     * Get a list of repository commits in a project.
     * <p>
     * GET /projects/:id/repository/commits
     *
     * @param projectId the project ID to get the list of commits for
     * @return a list containing the commits for the specified project ID
     * @throws GitLabApiException GitLabApiException if any exception occurs during execution
     */
    public List<Commit> getCommits(int projectId) throws GitLabApiException {
        return (getCommits(projectId, null, null, null));
    }

    /**
     * Get a list of repository commits in a project.
     * <p>
     * GET /projects/:id/repository/commits
     *
     * @param projectId the project ID to get the list of commits for
     * @param page      the page to get
     * @param perPage   the number of commits per page
     * @return a list containing the commits for the specified project ID
     * @throws GitLabApiException GitLabApiException if any exception occurs during execution
     */
    public List<Commit> getCommits(int projectId, int page, int perPage) throws GitLabApiException {
        return (getCommits(projectId, null, null, null, page, perPage));
    }

    /**
     * Get a Pager of repository commits in a project.
     * <p>
     * GET /projects/:id/repository/commits
     *
     * @param projectId    the project ID to get the list of commits for
     * @param itemsPerPage the number of Commit instances that will be fetched per page
     * @return a Pager containing the commits for the specified project ID
     * @throws GitLabApiException GitLabApiException if any exception occurs during execution
     */
    public Pager<Commit> getCommits(int projectId, int itemsPerPage) throws GitLabApiException {
        return (getCommits(projectId, null, null, null, itemsPerPage));
    }

    /**
     * Get a list of repository commits in a project.
     * <p>
     * GET /projects/:id/repository/commits
     *
     * @param projectId the project ID to get the list of commits for
     * @param ref       the name of a repository branch or tag or if not given the default branch
     * @param since     only commits after or on this date will be returned
     * @param until     only commits before or on this date will be returned
     * @return a list containing the commits for the specified project ID
     * @throws GitLabApiException GitLabApiException if any exception occurs during execution
     */
    public List<Commit> getCommits(int projectId, String ref, Date since, Date until) throws GitLabApiException {
        Form formData = new GitLabApiForm()
                .withParam("ref_name", ref)
                .withParam("since", ISO8601.toString(since, false))
                .withParam("until", ISO8601.toString(until, false))
                .withParam(PER_PAGE_PARAM, getDefaultPerPage());
        Response response = get(Response.Status.OK, formData.asMap(), "projects", projectId, "repository", "commits");
        return (response.readEntity(new GenericType<List<Commit>>() {
        }));
    }

    /**
     * Get a list of repository commits in a project.
     * <p>
     * GET /projects/:id/repository/commits
     *
     * @param projectId the project ID to get the list of commits for
     * @param ref       the name of a repository branch or tag or if not given the default branch
     * @param since     only commits after or on this date will be returned
     * @param until     only commits before or on this date will be returned
     * @param path      the path to file of a project
     * @return a list containing the commits for the specified project ID
     * @throws GitLabApiException GitLabApiException if any exception occurs during execution
     */
    public List<Commit> getCommits(int projectId, String ref, Date since, Date until, String path) throws GitLabApiException {
        Form formData = new GitLabApiForm()
                .withParam(PER_PAGE_PARAM, getDefaultPerPage())
                .withParam("ref_name", ref)
                .withParam("since", ISO8601.toString(since, false))
                .withParam("until", ISO8601.toString(until, false))
                .withParam("path", (path == null ? null : urlEncode(path)));
        Response response = get(Response.Status.OK, formData.asMap(), "projects", projectId, "repository", "commits");
        return (response.readEntity(new GenericType<List<Commit>>() {
        }));
    }

    /**
     * Get a list of file commits in a project
     * <p>
     * GET /projects/:id/repository/commits?path=:file_path
     *
     * @param projectId the project ID to get the list of commits for
     * @param ref       the name of a repository branch or tag or if not given the default branch
     * @param path      the path to file of a project
     * @return a list containing the commits for the specified project ID and file
     * @throws GitLabApiException GitLabApiException if any exception occurs during execution
     */
    public List<Commit> getCommits(int projectId, String ref, String path) throws GitLabApiException {
        Form formData = new GitLabApiForm()
                .withParam(PER_PAGE_PARAM, getDefaultPerPage())
                .withParam("ref_name", ref)
                .withParam("path", (path == null ? null : urlEncode(path)));
        Response response = get(Response.Status.OK, formData.asMap(), "projects", projectId, "repository", "commits");
        return (response.readEntity(new GenericType<List<Commit>>() {
        }));
    }

    /**
     * Get a list of repository commits in a project.
     * <p>
     * GET /projects/:id/repository/commits
     *
     * @param projectId the project ID to get the list of commits for
     * @param ref       the name of a repository branch or tag or if not given the default branch
     * @param since     only commits after or on this date will be returned
     * @param until     only commits before or on this date will be returned
     * @param page      the page to get
     * @param perPage   the number of commits per page
     * @return a list containing the commits for the specified project ID
     * @throws GitLabApiException GitLabApiException if any exception occurs during execution
     */
    public List<Commit> getCommits(int projectId, String ref, Date since, Date until, int page, int perPage) throws GitLabApiException {
        Form formData = new GitLabApiForm()
                .withParam("ref_name", ref)
                .withParam("since", ISO8601.toString(since, false))
                .withParam("until", ISO8601.toString(until, false))
                .withParam(PAGE_PARAM, page)
                .withParam(PER_PAGE_PARAM, perPage);
        Response response = get(Response.Status.OK, formData.asMap(), "projects", projectId, "repository", "commits");
        return (response.readEntity(new GenericType<List<Commit>>() {
        }));
    }

    /**
     * Get a Pager of repository commits in a project.
     * <p>
     * GET /projects/:id/repository/commits
     *
     * @param projectId    the project ID to get the list of commits for
     * @param ref          the name of a repository branch or tag or if not given the default branch
     * @param since        only commits after or on this date will be returned
     * @param until        only commits before or on this date will be returned
     * @param itemsPerPage the number of Commit instances that will be fetched per page
     * @return a Pager containing the commits for the specified project ID
     * @throws GitLabApiException GitLabApiException if any exception occurs during execution
     */
    public Pager<Commit> getCommits(int projectId, String ref, Date since, Date until, int itemsPerPage) throws GitLabApiException {
        Form formData = new GitLabApiForm()
                .withParam("ref_name", ref)
                .withParam("since", ISO8601.toString(since, false))
                .withParam("until", ISO8601.toString(until, false));
        return (new Pager<Commit>(this, Commit.class, itemsPerPage, formData.asMap(), "projects", projectId, "repository", "commits"));
    }

    /**
     * Get a specific commit identified by the commit hash or name of a branch or tag.
     * <p>
     * GET /projects/:id/repository/commits/:sha
     *
     * @param projectId the project ID that the commit belongs to
     * @param sha       a commit hash or name of a branch or tag
     * @return the Commit instance for the specified project ID/sha pair
     * @throws GitLabApiException GitLabApiException if any exception occurs during execution
     */
    public Commit getCommit(int projectId, String sha) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "projects", projectId, "repository", "commits", sha);
        return (response.readEntity(Commit.class));
    }

    /**
     * Get a specific commit identified by the commit hash or name of a branch or tag statuses.
     * <p>
     * GET /projects/:id/repository/commits/:sha/statuses
     *
     * @param projectId the project ID that the commit belongs to
     * @param sha       a commit hash or name of a branch or tag
     * @return the Commit statuse instance for the specified project ID/sha pair
     * @throws GitLabApiException GitLabApiException if any exception occurs during execution
     */
    public List<CommitStatuse> getCommitStatus(int projectId, String sha) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "projects", projectId, "repository", "commits", sha, "statuses");
        return (response.readEntity(new GenericType<List<CommitStatuse>>() {
        }));
    }


    /**
     * Get the list of diffs of a commit in a project.
     * <p>
     * GET /projects/:id/repository/commits/:sha/diff
     *
     * @param projectId the project ID that the commit belongs to
     * @param sha       a commit hash or name of a branch or tag
     * @return a List of Diff instances for the specified project ID/sha pair
     * @throws GitLabApiException GitLabApiException if any exception occurs during execution
     */
    public List<Diff> getDiff(int projectId, String sha) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, "projects", projectId, "repository", "commits", sha, "diff");
        return (response.readEntity(new GenericType<List<Diff>>() {
        }));
    }

    /**
     * Get the list of diffs of a commit in a project.
     * <p>
     * GET /projects/:id/repository/commits/:sha/diff
     *
     * @param projectPath the project path that the commit belongs to
     * @param sha         a commit hash or name of a branch or tag
     * @return a List of Diff instances for the specified project ID/sha pair
     * @throws GitLabApiException GitLabApiException if any exception occurs during execution
     */
    public List<Diff> getDiff(String projectPath, String sha) throws GitLabApiException {

        try {
            projectPath = URLEncoder.encode(projectPath, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            throw (new GitLabApiException(uee));
        }

        Response response = get(Response.Status.OK, null, "projects", projectPath, "repository", "commits", sha, "diff");
        return (response.readEntity(new GenericType<List<Diff>>() {
        }));
    }

    /**
     * Get the comments of a commit in a project.
     * <p>
     * GET /projects/:id/repository/commits/:sha/comments
     *
     * @param projectId the project ID that the commit belongs to
     * @param sha       a commit hash or name of a branch or tag
     * @return a List of Comment instances for the specified project ID/sha pair
     * @throws GitLabApiException GitLabApiException if any exception occurs during execution
     */
    public List<Comment> getComments(int projectId, String sha) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, "projects", projectId, "repository", "commits", sha, "comments");
        return (response.readEntity(new GenericType<List<Comment>>() {
        }));
    }

    /**
     * Add a comment to a commit.  In order to post a comment in a particular line of a particular file,
     * you must specify the full commit SHA, the path, the line and lineType should be NEW.
     * <p>
     * POST /projects/:id/repository/commits/:sha/comments
     *
     * @param projectId the project ID that the commit belongs to
     * @param sha       a commit hash or name of a branch or tag
     * @param note      the text of the comment, required
     * @param path      the file path relative to the repository, optional
     * @param line      the line number where the comment should be placed, optional
     * @param lineType  the line type, optional
     * @return a Comment instance for the posted comment
     * @throws GitLabApiException GitLabApiException if any exception occurs during execution
     */
    public Comment addComment(int projectId, String sha, String note, String path, Integer line, LineType lineType) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm()
                .withParam("note", note, true)
                .withParam("path", path)
                .withParam("line", line)
                .withParam("line_type", lineType);
        Response response = post(Response.Status.CREATED, formData, "projects", projectId, "repository", "commits", sha, "comments");
        return (response.readEntity(Comment.class));
    }

    /**
     * Create a commit with multiple files and actions.
     *
     * <pre><code>GitLab Endpoint: POST /projects/:id/repository/commits</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param payload a CommitPayload instance holding the parameters for the commit
     * @return the created Commit instance
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Commit createCommit(Object projectIdOrPath, CommitPayload payload) throws GitLabApiException {

        // Validate the actions
        List<CommitAction> actions = payload.getActions();
        if (actions == null || actions.isEmpty()) {
            throw new GitLabApiException("actions cannot be null or empty.");
        }

        for (CommitAction action : actions) {

            // File content is required for create and update
            CommitAction.Action actionType = action.getAction();
            if (actionType == CommitAction.Action.CREATE || actionType == CommitAction.Action.UPDATE) {
                String content = action.getContent();
                if (content == null) {
                    throw new GitLabApiException("Content cannot be null for create or update actions.");
                }
            }
        }

        if (payload.getStartProject() != null) {
            payload.setStartProject(getProjectIdOrPath(payload.getStartProject()));
        }

        Response response = post(Response.Status.CREATED, payload,
                "projects", getProjectIdOrPath(projectIdOrPath), "repository", "commits");
        return (response.readEntity(Commit.class));
    }

    /**
     * Create a commit with single file and action.
     *
     * <pre><code>GitLab Endpoint: POST /projects/:id/repository/commits</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param branch tame of the branch to commit into. To create a new branch, also provide startBranch
     * @param commitMessage the commit message
     * @param startBranch the name of the branch to start the new commit from
     * @param authorEmail the commit author's email address
     * @param authorName the commit author's name
     * @param action the CommitAction to commit
     * @return the created Commit instance
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Commit createCommit(Object projectIdOrPath, String branch, String commitMessage, String startBranch,
                               String authorEmail, String authorName, CommitAction action) throws GitLabApiException {

        // Validate the action
        if (action == null) {
            throw new GitLabApiException("action cannot be null or empty.");
        }

        return (createCommit(projectIdOrPath, branch, commitMessage, startBranch,
                authorEmail, authorName, Arrays.asList(action)));
    }

    /**
     * Create a commit with multiple files and actions.
     *
     * <pre><code>GitLab Endpoint: POST /projects/:id/repository/commits</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param branch tame of the branch to commit into. To create a new branch, also provide startBranch
     * @param commitMessage the commit message
     * @param startBranch the name of the branch to start the new commit from
     * @param authorEmail the commit author's email address
     * @param authorName the commit author's name
     * @param actions the array of CommitAction to commit as a batch
     * @return the created Commit instance
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Commit createCommit(Object projectIdOrPath, String branch, String commitMessage, String startBranch,
                               String authorEmail, String authorName, List<CommitAction> actions) throws GitLabApiException {

        CommitPayload payload = new CommitPayload()
                .withBranch(branch)
                .withStartBranch(startBranch)
                .withCommitMessage(commitMessage)
                .withAuthorEmail(authorEmail)
                .withAuthorName(authorName)
                .withActions(actions);
        return (createCommit(projectIdOrPath, payload));
    }

    /**
     * Add a comment to a commit.
     * <p>
     * POST /projects/:id/repository/commits/:sha/comments
     *
     * @param projectId the project ID that the commit belongs to
     * @param sha       a commit hash or name of a branch or tag
     * @param note      the text of the comment, required
     * @return a Comment instance for the posted comment
     * @throws GitLabApiException GitLabApiException if any exception occurs during execution
     */
    public Comment addComment(int projectId, String sha, String note) throws GitLabApiException {
        return (addComment(projectId, sha, note, null, null, null));
    }
}
