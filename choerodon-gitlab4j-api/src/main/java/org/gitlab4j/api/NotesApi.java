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

import org.gitlab4j.api.models.Note;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

public class NotesApi extends AbstractApi {

    public NotesApi(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    /**
     * Get a list of the issues's notes. Only returns the first page
     * <p>
     * GET /projects/:id/issues/:issue_iid/notes
     *
     * @param projectId the project ID to get the issues for
     * @param issueIid  the issue ID to get the notes for
     * @return a list of the issues's notes
     * @throws GitLabApiException if any exception occurs
     */
    public List<Note> getIssueNotes(Integer projectId, Integer issueIid) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "projects", projectId, "issues", issueIid, "notes");
        return (response.readEntity(new GenericType<List<Note>>() {
        }));
    }

    /**
     * Get a list of the issue's notes using the specified page and per page settings.
     * <p>
     * GET /projects/:id/issues/:issue_iid/notes
     *
     * @param projectId the project ID to get the issues for
     * @param issueIid  the issue IID to get the notes for
     * @param page      the page to get
     * @param perPage   the number of notes per page
     * @return the list of notes in the specified range
     * @throws GitLabApiException if any exception occurs
     */
    public List<Note> getIssueNotes(Integer projectId, Integer issueIid, int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage), "projects", projectId, "issues", issueIid, "notes");
        return (response.readEntity(new GenericType<List<Note>>() {
        }));
    }

    /**
     * Get a Pager of issues's notes.
     * <p>
     * GET /projects/:id/issues/:issue_iid/notes
     *
     * @param projectId    the project ID to get the issues for
     * @param issueIid     the issue IID to get the notes for
     * @param itemsPerPage the number of notes per page
     * @return the list of notes in the specified range
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Note> getIssueNotes(Integer projectId, Integer issueIid, int itemsPerPage) throws GitLabApiException {
        return (new Pager<Note>(this, Note.class, itemsPerPage, null, "projects", projectId, "issues", issueIid, "notes"));
    }

    public Note getIssueNote(Integer projectId, Integer issueIid, Integer noteId) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "projects", projectId, "issues", issueIid, "notes", noteId);
        return (response.readEntity(Note.class));
    }

    public Note createIssueNote(Integer projectId, Integer issueIid, String body) throws GitLabApiException {
        return (createIssueNote(projectId, issueIid, body, null));
    }

    public Note createIssueNote(Integer projectId, Integer issueIid, String body, Date createdAt) throws GitLabApiException {
        if (projectId == null) {
            throw new RuntimeException("projectId cannot be null");
        }
        GitLabApiForm formData = new GitLabApiForm()
                .withParam("body", body, true)
                .withParam("created_at", createdAt);
        Response response = post(Response.Status.CREATED, formData, "projects", projectId, "issues", issueIid, "notes");
        return (response.readEntity(Note.class));
    }

    public Note updateIssueNote(Integer projectId, Integer issueIid, Integer noteId, String body) throws GitLabApiException {
        if (projectId == null) {
            throw new RuntimeException("projectId cannot be null");
        }
        GitLabApiForm formData = new GitLabApiForm()
                .withParam("body", body, true);
        Response response = put(Response.Status.CREATED, formData.asMap(), "projects", projectId, "issues", issueIid, "notes", noteId);
        return (response.readEntity(Note.class));
    }

    public void deleteIssueNote(Integer projectId, Integer issueIid, Integer noteId) throws GitLabApiException {
        if (projectId == null) {
            throw new RuntimeException("projectId cannot be null");
        }
        if (issueIid == null) {
            throw new RuntimeException("issueIid cannot be null");
        }
        if (noteId == null) {
            throw new RuntimeException("noteId cannot be null");
        }
        Response.Status expectedStatus = (isApiVersion(GitLabApi.ApiVersion.V3) ? Response.Status.OK : Response.Status.NO_CONTENT);
        delete(expectedStatus, getDefaultPerPageParam(), "projects", projectId, "issues", issueIid, "notes", noteId);
    }

    /**
     * Gets a list of all notes for a single merge request
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/merge_requests/:merge_request_iid/notes</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param mergeRequestIid the issue ID to get the notes for
     * @return a list of the merge request's notes
     * @throws GitLabApiException if any exception occurs
     */
    public List<Note> getMergeRequestNotes(Object projectIdOrPath, Integer mergeRequestIid) throws GitLabApiException {
        return (getMergeRequestNotes(projectIdOrPath, mergeRequestIid, null, null, getDefaultPerPage()).all());
    }
    /**
     * Get a Pager of all notes for a single merge request
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/merge_requests/:merge_request_iid/notes</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param mergeRequestIid the merge request IID to get the notes for
     * @param sortOrder return merge request notes sorted in the specified sort order, default is DESC
     * @param orderBy return merge request notes ordered by CREATED_AT or UPDATED_AT, default is CREATED_AT
     * @param itemsPerPage the number of notes per page
     * @return the list of notes in the specified range
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Note> getMergeRequestNotes(Object projectIdOrPath, Integer mergeRequestIid,
                                            SortOrder sortOrder, Note.OrderBy orderBy, int itemsPerPage) throws GitLabApiException {

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("sort", sortOrder)
                .withParam("order_by", orderBy)
                .withParam(PAGE_PARAM, 1)
                .withParam(PER_PAGE_PARAM, itemsPerPage);
        return (new Pager<Note>(this, Note.class, itemsPerPage, formData.asMap(),
                "projects", getProjectIdOrPath(projectIdOrPath), "merge_requests", mergeRequestIid, "notes"));
    }
}
