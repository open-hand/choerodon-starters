/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Greg Messner <greg@messners.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.gitlab4j.api.GitLabApi.ApiVersion;
import org.gitlab4j.api.models.*;

/**
 * This class provides an entry point to all the GitLab API project calls.
 */
public class ProjectApi extends AbstractApi implements Constants {

    public ProjectApi(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    /**
     * Get a list of projects accessible by the authenticated user.
     * <p>
     * GET /projects
     *
     * @return a list of projects accessible by the authenticated user
     * @throws GitLabApiException if any exception occurs
     */
    public List<Project> getProjects() throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "projects");
        return (response.readEntity(new GenericType<List<Project>>() {
        }));
    }

    /**
     * Get a list of projects accessible by the authenticated user and in the specified page range.
     * <p>
     * GET /projects
     *
     * @param page    the page to get
     * @param perPage the number of projects per page
     * @return a list of projects accessible by the authenticated user
     * @throws GitLabApiException if any exception occurs
     */
    public List<Project> getProjects(int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage), "projects");
        return (response.readEntity(new GenericType<List<Project>>() {
        }));
    }

    /**
     * Get a Pager instance of projects accessible by the authenticated user.
     * <p>
     * GET /projects
     *
     * @param itemsPerPage the number of Project instances that will be fetched per page
     * @return a Pager instance of projects accessible by the authenticated user
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Project> getProjects(int itemsPerPage) throws GitLabApiException {
        return (new Pager<Project>(this, Project.class, itemsPerPage, null, "projects"));
    }

    /**
     * Transfer a project to a new namespace.  This was added in GitLab 11.1
     *
     * <pre><code>GitLab Endpoint: PUT /projects/:id/transfer.</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance, required
     * @param namespace the namespace to transfer the project to
     * @return the updated Project
     * @throws GitLabApiException if any exception occurs
     */
    public Project transferProject(Object projectIdOrPath, String namespace) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm().withParam("namespace", namespace, true);
        Response response = put(Response.Status.OK, formData.asMap(), "projects", getProjectIdOrPath(projectIdOrPath), "transfer");
        return (response.readEntity(Project.class));
    }

    /**
     * Get a list of projects accessible by the authenticated user and matching the supplied filter parameters.
     * All filter parameters are optional.
     * <p>
     * GET /projects
     *
     * @param archived   limit by archived status
     * @param visibility limit by visibility public, internal, or private
     * @param orderBy    return projects ordered by id, name, path, created_at, updated_at, or last_activity_at fields, default is created_at
     * @param sort       return projects sorted in asc or desc order. Default is desc
     * @param search     return list of projects matching the search criteria
     * @param simple     return only the ID, URL, name, and path of each project
     * @param owned      limit by projects owned by the current user
     * @param membership limit by projects that the current user is a member of
     * @param starred    limit by projects starred by the current user
     * @param statistics include project statistics
     * @return a list of projects accessible by the authenticated user and matching the supplied parameters
     * @throws GitLabApiException if any exception occurs
     * @deprecated Will be removed in version 5.0, replaced by {@link #getProjects(Boolean, Visibility,
     * ProjectOrderBy, SortOrder, String, Boolean, Boolean, Boolean, Boolean, Boolean)}
     */
    public List<Project> getProjects(Boolean archived, Visibility visibility, String orderBy,
                                     String sort, String search, Boolean simple, Boolean owned, Boolean membership,
                                     Boolean starred, Boolean statistics) throws GitLabApiException {

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("archived", archived)
                .withParam("visibility", visibility)
                .withParam("order_by", orderBy)
                .withParam("sort", sort)
                .withParam("search", search)
                .withParam("simple", simple)
                .withParam("owned", owned)
                .withParam("membership", membership)
                .withParam("starred", starred)
                .withParam("statistics", statistics)
                .withParam(PER_PAGE_PARAM, getDefaultPerPage());

        Response response = get(Response.Status.OK, formData.asMap(), "projects");
        return (response.readEntity(new GenericType<List<Project>>() {
        }));
    }

    /**
     * Get a list of projects accessible by the authenticated user and matching the supplied filter parameters.
     * All filter parameters are optional.
     * <p>
     * GET /projects
     *
     * @param archived   limit by archived status
     * @param visibility limit by visibility public, internal, or private
     * @param orderBy    return projects ordered by ID, NAME, PATH, CREATED_AT, UPDATED_AT, or
     *                   LAST_ACTIVITY_AT fields, default is CREATED_AT
     * @param sort       return projects sorted in asc or desc order. Default is desc
     * @param search     return list of projects matching the search criteria
     * @param simple     return only the ID, URL, name, and path of each project
     * @param owned      limit by projects owned by the current user
     * @param membership limit by projects that the current user is a member of
     * @param starred    limit by projects starred by the current user
     * @param statistics include project statistics
     * @return a list of projects accessible by the authenticated user and matching the supplied parameters
     * @throws GitLabApiException if any exception occurs
     */
    public List<Project> getProjects(Boolean archived, Visibility visibility, ProjectOrderBy orderBy,
                                     SortOrder sort, String search, Boolean simple, Boolean owned, Boolean membership,
                                     Boolean starred, Boolean statistics) throws GitLabApiException {

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("archived", archived)
                .withParam("visibility", visibility)
                .withParam("order_by", orderBy)
                .withParam("sort", sort)
                .withParam("search", search)
                .withParam("simple", simple)
                .withParam("owned", owned)
                .withParam("membership", membership)
                .withParam("starred", starred)
                .withParam("statistics", statistics)
                .withParam(PER_PAGE_PARAM, getDefaultPerPage());

        Response response = get(Response.Status.OK, formData.asMap(), "projects");
        return (response.readEntity(new GenericType<List<Project>>() {
        }));
    }

    /**
     * Get a list of projects accessible by the authenticated user and matching the supplied filter parameters.
     * All filter parameters are optional.
     * <p>
     * GET /projects
     *
     * @param archived   limit by archived status
     * @param visibility limit by visibility public, internal, or private
     * @param orderBy    return projects ordered by ID, NAME, PATH, CREATED_AT, UPDATED_AT, or
     *                   LAST_ACTIVITY_AT fields, default is CREATED_AT
     * @param sort       return projects sorted in asc or desc order. Default is desc
     * @param search     return list of projects matching the search criteria
     * @param simple     return only the ID, URL, name, and path of each project
     * @param owned      limit by projects owned by the current user
     * @param membership limit by projects that the current user is a member of
     * @param starred    limit by projects starred by the current user
     * @param statistics include project statistics
     * @param page       the page to get
     * @param perPage    the number of projects per page
     * @return a list of projects accessible by the authenticated user and matching the supplied parameters
     * @throws GitLabApiException if any exception occurs
     */
    public List<Project> getProjects(Boolean archived, Visibility visibility, ProjectOrderBy orderBy,
                                     SortOrder sort, String search, Boolean simple, Boolean owned, Boolean membership,
                                     Boolean starred, Boolean statistics, int page, int perPage) throws GitLabApiException {

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("archived", archived)
                .withParam("visibility", visibility)
                .withParam("order_by", orderBy)
                .withParam("sort", sort)
                .withParam("search", search)
                .withParam("simple", simple)
                .withParam("owned", owned)
                .withParam("membership", membership)
                .withParam("starred", starred)
                .withParam("statistics", statistics)
                .withParam(PAGE_PARAM, page)
                .withParam(PER_PAGE_PARAM, perPage);

        Response response = get(Response.Status.OK, formData.asMap(), "projects");
        return (response.readEntity(new GenericType<List<Project>>() {
        }));
    }

    /**
     * Get a Pager of projects accessible by the authenticated user and matching the supplied filter parameters.
     * All filter parameters are optional.
     * <p>
     * GET /projects
     *
     * @param archived     limit by archived status
     * @param visibility   limit by visibility public, internal, or private
     * @param orderBy      return projects ordered by ID, NAME, PATH, CREATED_AT, UPDATED_AT, or
     *                     LAST_ACTIVITY_AT fields, default is CREATED_AT
     * @param sort         return projects sorted in asc or desc order. Default is desc
     * @param search       return list of projects matching the search criteria
     * @param simple       return only the ID, URL, name, and path of each project
     * @param owned        limit by projects owned by the current user
     * @param membership   limit by projects that the current user is a member of
     * @param starred      limit by projects starred by the current user
     * @param statistics   include project statistics
     * @param itemsPerPage the number of Project instances that will be fetched per page
     * @return a Pager of projects accessible by the authenticated user and matching the supplied parameters
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Project> getProjects(Boolean archived, Visibility visibility, ProjectOrderBy orderBy,
                                      SortOrder sort, String search, Boolean simple, Boolean owned, Boolean membership,
                                      Boolean starred, Boolean statistics, int itemsPerPage) throws GitLabApiException {

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("archived", archived)
                .withParam("visibility", visibility)
                .withParam("order_by", orderBy)
                .withParam("sort", sort)
                .withParam("search", search)
                .withParam("simple", simple)
                .withParam("owned", owned)
                .withParam("membership", membership)
                .withParam("starred", starred)
                .withParam("statistics", statistics);

        return (new Pager<Project>(this, Project.class, itemsPerPage, formData.asMap(), "projects"));
    }

    /**
     * Get a list of projects accessible by the authenticated user that match the provided search string.
     * <p>
     * GET /projects?search=search
     *
     * @param search the project name search criteria
     * @return a list of projects accessible by the authenticated user that match the provided search string
     * @throws GitLabApiException if any exception occurs
     */
    public List<Project> getProjects(String search) throws GitLabApiException {
        Form formData = new GitLabApiForm().withParam("search", search).withParam(PER_PAGE_PARAM, getDefaultPerPage());
        Response response = get(Response.Status.OK, formData.asMap(), "projects");
        return (response.readEntity(new GenericType<List<Project>>() {
        }));
    }

    /**
     * Get a list of projects accessible by the authenticated user that match the provided search string.
     * <p>
     * GET /projects?search=search
     *
     * @param search  the project name search criteria
     * @param page    the page to get
     * @param perPage the number of projects per page
     * @return a list of projects accessible by the authenticated user that match the provided search string
     * @throws GitLabApiException if any exception occurs
     */
    public List<Project> getProjects(String search, int page, int perPage) throws GitLabApiException {
        Form formData = new GitLabApiForm().withParam("search", search).withParam(PAGE_PARAM, page).withParam(PER_PAGE_PARAM, perPage);
        Response response = get(Response.Status.OK, formData.asMap(), "projects");
        return (response.readEntity(new GenericType<List<Project>>() {
        }));
    }

    /**
     * Get a Pager of projects accessible by the authenticated user that match the provided search string.
     * <p>
     * GET /projects?search=search
     *
     * @param search       the project name search criteria
     * @param itemsPerPage the number of Project instances that will be fetched per page
     * @return a Pager of projects accessible by the authenticated user that match the provided search string
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Project> getProjects(String search, int itemsPerPage) throws GitLabApiException {
        Form formData = new GitLabApiForm().withParam("search", search);
        return (new Pager<Project>(this, Project.class, itemsPerPage, formData.asMap(), "projects"));
    }

    /**
     * Get a list of projects that the authenticated user is a member of.
     * <p>
     * GET /projects
     *
     * @return a list of projects that the authenticated user is a member of
     * @throws GitLabApiException if any exception occurs
     */
    public List<Project> getMemberProjects() throws GitLabApiException {
        Form formData = new GitLabApiForm().withParam("membership", true).withParam(PER_PAGE_PARAM, getDefaultPerPage());
        Response response = get(Response.Status.OK, formData.asMap(), "projects");
        return (response.readEntity(new GenericType<List<Project>>() {
        }));
    }

    /**
     * Get a list of projects that the authenticated user is a member of in the specified page range.
     * <p>
     * GET /projects
     *
     * @param page    the page to get
     * @param perPage the number of projects per page
     * @return a list of projects that the authenticated user is a member of
     * @throws GitLabApiException if any exception occurs
     */
    public List<Project> getMemberProjects(int page, int perPage) throws GitLabApiException {
        Form formData = new GitLabApiForm().withParam("membership", true).withParam(PAGE_PARAM, page).withParam(PER_PAGE_PARAM, perPage);
        Response response = get(Response.Status.OK, formData.asMap(), "projects");
        return (response.readEntity(new GenericType<List<Project>>() {
        }));
    }

    /**
     * Get a Pager of projects that the authenticated user is a member of.
     * <p>
     * GET /projects
     *
     * @param itemsPerPage the number of Project instances that will be fetched per page
     * @return a Pager o Project instances that the authenticated user is a member of
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Project> getMemberProjects(int itemsPerPage) throws GitLabApiException {
        Form formData = new GitLabApiForm().withParam("membership", true);
        return (new Pager<Project>(this, Project.class, itemsPerPage, formData.asMap(), "projects"));
    }

    /**
     * Get a list of all GitLab projects (admin only).
     * <p>
     * GET /projects/all
     *
     * @return a list of all GitLab projects
     * @throws GitLabApiException if any exception occurs
     * @deprecated Will be removed, no longer supported by the GitLab API
     */
    public List<Project> getAllProjects() throws GitLabApiException {

        if (!isApiVersion(ApiVersion.V3)) {
            throw new GitLabApiException("Not supported by GitLab API version " + this.getApiVersion());
        }

        Form formData = new GitLabApiForm().withParam(PER_PAGE_PARAM, getDefaultPerPage());
        Response response = get(Response.Status.OK, formData.asMap(), "projects", "all");
        return (response.readEntity(new GenericType<List<Project>>() {
        }));
    }

    /**
     * Get a list of projects owned by the authenticated user.
     * <p>
     * GET /projects
     *
     * @return a list of projects owned by the authenticated user
     * @throws GitLabApiException if any exception occurs
     */
    public List<Project> getOwnedProjects() throws GitLabApiException {
        Form formData = new GitLabApiForm().withParam("owned", true).withParam(PER_PAGE_PARAM, getDefaultPerPage());
        Response response = get(Response.Status.OK, formData.asMap(), "projects");
        return (response.readEntity(new GenericType<List<Project>>() {
        }));
    }

    /**
     * Get a list of projects owned by the authenticated user in the specified page range.
     * <p>
     * GET /projects
     *
     * @param page    the page to get
     * @param perPage the number of projects per page
     * @return a list of projects owned by the authenticated user
     * @throws GitLabApiException if any exception occurs
     */
    public List<Project> getOwnedProjects(int page, int perPage) throws GitLabApiException {
        Form formData = new GitLabApiForm().withParam("owned", true).withParam(PAGE_PARAM, page).withParam(PER_PAGE_PARAM, perPage);
        Response response = get(Response.Status.OK, formData.asMap(), "projects");
        return (response.readEntity(new GenericType<List<Project>>() {
        }));
    }

    /**
     * Get a Pager of projects owned by the authenticated user.
     * <p>
     * GET /projects
     *
     * @param itemsPerPage the number of Project instances that will be fetched per page
     * @return a list of projects owned by the authenticated user
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Project> getOwnedProjects(int itemsPerPage) throws GitLabApiException {
        Form formData = new GitLabApiForm().withParam("owned", true);
        return (new Pager<Project>(this, Project.class, itemsPerPage, formData.asMap(), "projects"));
    }

    /**
     * Get a list of projects starred by the authenticated user.
     * <p>
     * GET /projects
     *
     * @return a list of projects starred by the authenticated user
     * @throws GitLabApiException if any exception occurs
     */
    public List<Project> getStarredProjects() throws GitLabApiException {
        Form formData = new GitLabApiForm().withParam("starred", true).withParam(PER_PAGE_PARAM, getDefaultPerPage());
        Response response = get(Response.Status.OK, formData.asMap(), "projects");
        return (response.readEntity(new GenericType<List<Project>>() {
        }));
    }

    /**
     * Get a list of projects starred by the authenticated user in the specified page range.
     * <p>
     * GET /projects
     *
     * @param page    the page to get
     * @param perPage the number of projects per page
     * @return a list of projects starred by the authenticated user
     * @throws GitLabApiException if any exception occurs
     */
    public List<Project> getStarredProjects(int page, int perPage) throws GitLabApiException {
        Form formData = new GitLabApiForm().withParam("starred", true).withParam(PAGE_PARAM, page).withParam(PER_PAGE_PARAM, perPage);
        Response response = get(Response.Status.OK, formData.asMap(), "projects");
        return (response.readEntity(new GenericType<List<Project>>() {
        }));
    }

    /**
     * Get a Pager of projects starred by the authenticated user.
     * <p>
     * GET /projects
     *
     * @param itemsPerPage the number of Project instances that will be fetched per page
     * @return a Pager of projects starred by the authenticated user
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Project> getStarredProjects(int itemsPerPage) throws GitLabApiException {
        Form formData = new GitLabApiForm().withParam("starred", true).withParam(PER_PAGE_PARAM, getDefaultPerPage());
        return (new Pager<Project>(this, Project.class, itemsPerPage, formData.asMap(), "projects"));
    }

    /**
     * Get a specific project, which is owned by the authentication user.
     * <p>
     * GET /projects/:id
     *
     * @param projectId the ID of the project to get
     * @return the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public Project getProject(Integer projectId) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, "projects", projectId);
        return (response.readEntity(Project.class));
    }

    /**
     * Get a specific project, which is owned by the authentication user.
     * <p>
     * GET /projects/:id
     *
     * @param namespace the name of the project namespace or group
     * @param project   the name of the project to get
     * @return the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public Project getProject(String namespace, String project) throws GitLabApiException {

        if (namespace == null) {
            throw new RuntimeException("namespace cannot be null");
        }

        if (project == null) {
            throw new RuntimeException("project cannot be null");
        }

        String projectPath = null;
        try {
            projectPath = URLEncoder.encode(namespace + "/" + project, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            throw (new GitLabApiException(uee));
        }

        Response response = get(Response.Status.OK, null, "projects", projectPath);
        return (response.readEntity(Project.class));
    }
    /**
     * Get a specific project, which is owned by the authentication user.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param includeStatistics include project statistics
     * @return the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public Project getProject(Object projectIdOrPath, Boolean includeStatistics) throws GitLabApiException {
        return (getProject(projectIdOrPath, includeStatistics, null, null));
    }

    /**
     * Get a specific project, which is owned by the authentication user.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param includeStatistics include project statistics
     * @param includeLicense include project license data
     * @param withCustomAttributes include custom attributes in response (admins only)
     * @return the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public Project getProject(Object projectIdOrPath, Boolean includeStatistics,
                              Boolean includeLicense, Boolean withCustomAttributes) throws GitLabApiException {
        Form formData = new GitLabApiForm()
                .withParam("statistics", includeStatistics)
                .withParam("license", includeLicense)
                .withParam("with_custom_attributes", withCustomAttributes);
        Response response = get(Response.Status.OK, formData.asMap(),
                "projects", getProjectIdOrPath(projectIdOrPath));
        return (response.readEntity(Project.class));
    }

    /**
     * Create a new project in the specified group.
     *
     * @param groupId     the group ID to create the project under
     * @param projectName the name of the project top create
     * @return the created project
     * @throws GitLabApiException if any exception occurs
     */
    public Project createProject(Integer groupId, String projectName) throws GitLabApiException {

        GitLabApiForm formData = new GitLabApiForm().withParam("namespace_id", groupId).withParam("name", projectName, true);
        Response response = post(Response.Status.CREATED, formData, "projects");
        return (response.readEntity(Project.class));
    }

    /**
     * Create a new project with the current user's namespace.
     *
     * @param projectName the name of the project top create
     * @return the created project
     * @throws GitLabApiException if any exception occurs
     */
    public Project createProject(String projectName) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm().withParam("name", projectName, true);
        Response response = post(Response.Status.CREATED, formData, "projects");
        return (response.readEntity(Project.class));
    }

    /**
     * Creates new project owned by the current user.
     *
     * @param project the Project instance with the configuration for the new project
     * @return a Project instance with the newly created project info
     * @throws GitLabApiException if any exception occurs
     */
    public Project createProject(Project project) throws GitLabApiException {
        return (createProject(project, null));
    }

    /**
     * Creates new project owned by the current user. The following properties on the Project instance
     * are utilized in the creation of the project:
     * <p>
     * name (name or path are required) - new project name
     * path (name or path are required) - new project path
     * defaultBranch (optional) - master by default
     * description (optional) - short project description
     * visibility (optional) - Limit by visibility public, internal, or private
     * visibilityLevel (optional)
     * issuesEnabled (optional) - Enable issues for this project
     * mergeRequestsEnabled (optional) - Enable merge requests for this project
     * wikiEnabled (optional) - Enable wiki for this project
     * snippetsEnabled (optional) - Enable snippets for this project
     * jobsEnabled (optional) - Enable jobs for this project
     * containerRegistryEnabled (optional) - Enable container registry for this project
     * sharedRunnersEnabled (optional) - Enable shared runners for this project
     * publicJobs (optional) - If true, jobs can be viewed by non-project-members
     * onlyAllowMergeIfPipelineSucceeds (optional) - Set whether merge requests can only be merged with successful jobs
     * onlyAllowMergeIfAllDiscussionsAreResolved (optional) - Set whether merge requests can only be merged when all the discussions are resolved
     * lLfsEnabled (optional) - Enable LFS
     * requestAccessEnabled (optional) - Allow users to request member access
     * repositoryStorage (optional) - Which storage shard the repository is on. Available only to admins
     * approvalsBeforeMerge (optional) - How many approvers should approve merge request by default
     *
     * @param project   the Project instance with the configuration for the new project
     * @param importUrl the URL to import the repository from
     * @return a Project instance with the newly created project info
     * @throws GitLabApiException if any exception occurs
     */
    public Project createProject(Project project, String importUrl) throws GitLabApiException {

        if (project == null) {
            return (null);
        }

        String name = project.getName();
        String path = project.getPath();

        if ((name == null || name.trim().length() == 0) && (path == null || path.trim().length() == 0)) {
            return (null);
        }

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("name", name)
                .withParam("path", path)
                .withParam("default_branch", project.getDefaultBranch())
                .withParam("description", project.getDescription())
                .withParam("issues_enabled", project.getIssuesEnabled())
                .withParam("merge_requests_enabled", project.getMergeRequestsEnabled())
                .withParam("jobs_enabled", project.getJobsEnabled())
                .withParam("wiki_enabled", project.getWikiEnabled())
                .withParam("container_registry_enabled", project.getContainerRegistryEnabled())
                .withParam("snippets_enabled", project.getSnippetsEnabled())
                .withParam("shared_runners_enabled", project.getSharedRunnersEnabled())
                .withParam("public_jobs", project.getPublicJobs())
                .withParam("visibility_level", project.getVisibilityLevel())
                .withParam("only_allow_merge_if_pipeline_succeeds", project.getOnlyAllowMergeIfPipelineSucceeds())
                .withParam("only_allow_merge_if_all_discussions_are_resolved", project.getOnlyAllowMergeIfAllDiscussionsAreResolved())
                .withParam("lfs_enabled", project.getLfsEnabled())
                .withParam("request_access_enabled", project.getRequestAccessEnabled())
                .withParam("repository_storage", project.getRepositoryStorage())
                .withParam("approvals_before_merge", project.getApprovalsBeforeMerge())
                .withParam("import_url", importUrl);

        if (isApiVersion(ApiVersion.V3)) {
            boolean isPublic = (project.getPublic() != null ? project.getPublic() : project.getVisibility() == Visibility.PUBLIC);
            formData.withParam("public", isPublic);
        } else {
            Visibility visibility = (project.getVisibility() != null ? project.getVisibility() :
                    project.getPublic() == Boolean.TRUE ? Visibility.PUBLIC : null);
            formData.withParam("visibility", visibility);
        }

        if (project.getNamespace() != null) {
            formData.withParam("namespace_id", project.getNamespace().getId());
        }

        Response response = post(Response.Status.CREATED, formData, "projects");
        return (response.readEntity(Project.class));
    }

    /**
     * Creates a Project
     *
     * @param name                 The name of the project
     * @param namespaceId          The Namespace for the new project, otherwise null indicates to use the GitLab default (user)
     * @param description          A description for the project, null otherwise
     * @param issuesEnabled        Whether Issues should be enabled, otherwise null indicates to use GitLab default
     * @param mergeRequestsEnabled Whether Merge Requests should be enabled, otherwise null indicates to use GitLab default
     * @param wikiEnabled          Whether a Wiki should be enabled, otherwise null indicates to use GitLab default
     * @param snippetsEnabled      Whether Snippets should be enabled, otherwise null indicates to use GitLab default
     * @param visibility           The visibility of the project, otherwise null indicates to use GitLab default
     * @param visibilityLevel      The visibility level of the project, otherwise null indicates to use GitLab default
     * @param importUrl            The Import URL for the project, otherwise null
     * @return the GitLab Project
     * @throws GitLabApiException if any exception occurs
     */
    public Project createProject(String name, Integer namespaceId, String description, Boolean issuesEnabled, Boolean mergeRequestsEnabled,
                                 Boolean wikiEnabled, Boolean snippetsEnabled, Visibility visibility, Integer visibilityLevel, String importUrl) throws GitLabApiException {

        if (isApiVersion(ApiVersion.V3)) {
            Boolean isPublic = Visibility.PUBLIC == visibility;
            return (createProject(name, namespaceId, description, issuesEnabled, mergeRequestsEnabled,
                    wikiEnabled, snippetsEnabled, isPublic, visibilityLevel, importUrl));
        }

        if (name == null || name.trim().length() == 0) {
            return (null);
        }

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("name", name, true)
                .withParam("namespace_id", namespaceId)
                .withParam("description", description)
                .withParam("issues_enabled", issuesEnabled)
                .withParam("merge_requests_enabled", mergeRequestsEnabled)
                .withParam("wiki_enabled", wikiEnabled)
                .withParam("snippets_enabled", snippetsEnabled)
                .withParam("visibility_level", visibilityLevel)
                .withParam("visibility", visibility)
                .withParam("import_url", importUrl);

        Response response = post(Response.Status.CREATED, formData, "projects");
        return (response.readEntity(Project.class));
    }

    /**
     * Creates a Project
     *
     * @param name                 The name of the project
     * @param namespaceId          The Namespace for the new project, otherwise null indicates to use the GitLab default (user)
     * @param description          A description for the project, null otherwise
     * @param issuesEnabled        Whether Issues should be enabled, otherwise null indicates to use GitLab default
     * @param mergeRequestsEnabled Whether Merge Requests should be enabled, otherwise null indicates to use GitLab default
     * @param wikiEnabled          Whether a Wiki should be enabled, otherwise null indicates to use GitLab default
     * @param snippetsEnabled      Whether Snippets should be enabled, otherwise null indicates to use GitLab default
     * @param isPublic             Whether the project is public or private, if true same as setting visibilityLevel = 20, otherwise null indicates to use GitLab default
     * @param visibilityLevel      The visibility level of the project, otherwise null indicates to use GitLab default
     * @param importUrl            The Import URL for the project, otherwise null
     * @return the GitLab Project
     * @throws GitLabApiException if any exception occurs
     * @deprecated As of release 4.2.0, replaced by {@link #createProject(String, Integer, String, Boolean, Boolean,
     * Boolean, Boolean, Visibility, Integer, String)}
     */
    @Deprecated
    public Project createProject(String name, Integer namespaceId, String description, Boolean issuesEnabled, Boolean mergeRequestsEnabled,
                                 Boolean wikiEnabled, Boolean snippetsEnabled, Boolean isPublic, Integer visibilityLevel, String importUrl) throws GitLabApiException {

        if (name == null || name.trim().length() == 0) {
            return (null);
        }

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("name", name, true)
                .withParam("namespace_id", namespaceId)
                .withParam("description", description)
                .withParam("issues_enabled", issuesEnabled)
                .withParam("merge_requests_enabled", mergeRequestsEnabled)
                .withParam("wiki_enabled", wikiEnabled)
                .withParam("snippets_enabled", snippetsEnabled)
                .withParam("visibility_level", visibilityLevel)
                .withParam("import_url", importUrl);

        if (isApiVersion(ApiVersion.V3)) {
            formData.withParam("public", isPublic);
        } else if (isPublic) {
            formData.withParam("visibility", Visibility.PUBLIC);
        }

        Response response = post(Response.Status.CREATED, formData, "projects");
        return (response.readEntity(Project.class));
    }

    /**
     * Updates a project. The following properties on the Project instance
     * are utilized in the edit of the project, null values are not updated:
     * <p>
     * id (required) - existing project id
     * name (required) - project name
     * path (optional) - project path
     * defaultBranch (optional) - master by default
     * description (optional) - short project description
     * visibility (optional) - Limit by visibility public, internal, or private
     * issuesEnabled (optional) - Enable issues for this project
     * mergeRequestsEnabled (optional) - Enable merge requests for this project
     * wikiEnabled (optional) - Enable wiki for this project
     * snippetsEnabled (optional) - Enable snippets for this project
     * jobsEnabled (optional) - Enable jobs for this project
     * containerRegistryEnabled (optional) - Enable container registry for this project
     * sharedRunnersEnabled (optional) - Enable shared runners for this project
     * publicJobs (optional) - If true, jobs can be viewed by non-project-members
     * onlyAllowMergeIfPipelineSucceeds (optional) - Set whether merge requests can only be merged with successful jobs
     * onlyAllowMergeIfAllDiscussionsAreResolved (optional) - Set whether merge requests can only be merged when all the discussions are resolved
     * lLfsEnabled (optional) - Enable LFS
     * requestAccessEnabled (optional) - Allow users to request member access
     * repositoryStorage (optional) - Which storage shard the repository is on. Available only to admins
     * approvalsBeforeMerge (optional) - How many approvers should approve merge request by default
     * <p>
     * NOTE: The following parameters specified by the GitLab API edit project are not supported:
     * import_url
     * tag_list array
     * avatar
     * ci_config_path
     *
     * @param project the Project instance with the configuration for the new project
     * @return a Project instance with the newly updated project info
     * @throws GitLabApiException if any exception occurs
     */
    public Project updateProject(Project project) throws GitLabApiException {

        if (project == null) {
            throw new RuntimeException("Project instance cannot be null.");
        }

        // This will throw an exception if both id and path are not present
        Object projectIdentifier = getProjectIdOrPath(project);

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("name", project.getName())
                .withParam("path", project.getPath())
                .withParam("default_branch", project.getDefaultBranch())
                .withParam("description", project.getDescription())
                .withParam("issues_enabled", project.getIssuesEnabled())
                .withParam("merge_requests_enabled", project.getMergeRequestsEnabled())
                .withParam("jobs_enabled", project.getJobsEnabled())
                .withParam("wiki_enabled", project.getWikiEnabled())
                .withParam("snippets_enabled", project.getSnippetsEnabled())
                .withParam("container_registry_enabled", project.getContainerRegistryEnabled())
                .withParam("shared_runners_enabled", project.getSharedRunnersEnabled())
                .withParam("public_jobs", project.getPublicJobs())
                .withParam("only_allow_merge_if_pipeline_succeeds", project.getOnlyAllowMergeIfPipelineSucceeds())
                .withParam("only_allow_merge_if_all_discussions_are_resolved", project.getOnlyAllowMergeIfAllDiscussionsAreResolved())
                .withParam("lfs_enabled", project.getLfsEnabled())
                .withParam("request_access_enabled", project.getRequestAccessEnabled())
                .withParam("repository_storage", project.getRepositoryStorage())
                .withParam("approvals_before_merge", project.getApprovalsBeforeMerge())
                .withParam("ci_config_path", project.getCiConfigPath());

        if (isApiVersion(ApiVersion.V3)) {
            formData.withParam("visibility_level", project.getVisibilityLevel());
            boolean isPublic = (project.getPublic() != null ? project.getPublic() : project.getVisibility() == Visibility.PUBLIC);
            formData.withParam("public", isPublic);

            if (project.getTagList() != null && !project.getTagList().isEmpty()) {
                throw new IllegalArgumentException("GitLab API v3 does not support tag lists when updating projects");
            }
        } else {
            Visibility visibility = (project.getVisibility() != null ? project.getVisibility() :
                    project.getPublic() == Boolean.TRUE ? Visibility.PUBLIC : null);
            formData.withParam("visibility", visibility);

            if (project.getTagList() != null && !project.getTagList().isEmpty()) {
                formData.withParam("tag_list", String.join(",", project.getTagList()));
            }
        }

        Response response = putWithFormData(Response.Status.OK, formData, "projects", projectIdentifier);
        return (response.readEntity(Project.class));
    }

    /**
     * Removes project with all resources(issues, merge requests etc).
     * <p>
     * DELETE /projects/:id
     *
     * @param projectId the project ID to remove
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteProject(Integer projectId) throws GitLabApiException {

        if (projectId == null) {
            throw new RuntimeException("projectId cannot be null");
        }

        Response.Status expectedStatus = (isApiVersion(ApiVersion.V3) ? Response.Status.OK : Response.Status.ACCEPTED);
        delete(expectedStatus, null, "projects", projectId);
    }

    /**
     * Removes project with all resources(issues, merge requests etc).
     * <p>
     * DELETE /projects/:id
     *
     * @param project the Project instance to remove
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteProject(Project project) throws GitLabApiException {
        deleteProject(project.getId());
    }

    /**
     * Get a list of project team members.
     * <p>
     * GET /projects/:id/members
     *
     * @param projectId the project ID to get team members for
     * @return the members belonging to the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public List<Member> getMembers(Integer projectId) throws GitLabApiException {
        Response response = get(Response.Status.OK, this.getDefaultPerPageParam(), "projects", projectId, "members");
        return (response.readEntity(new GenericType<List<Member>>() {
        }));
    }

    /**
     * Get a list of project team members in the specified page range.
     * <p>
     * GET /projects/:id/members
     *
     * @param projectId the project ID to get team members for
     * @param page      the page to get
     * @param perPage   the number of Member instances per page
     * @return the members belonging to the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public List<Member> getMembers(Integer projectId, int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage), "projects", projectId, "members");
        return (response.readEntity(new GenericType<List<Member>>() {
        }));
    }

    /**
     * Get a Pager of project team members.
     * <p>
     * GET /projects/:id/members
     *
     * @param projectId    the project ID to get team members for
     * @param itemsPerPage the number of Project instances that will be fetched per page
     * @return the members belonging to the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Member> getMembers(Integer projectId, int itemsPerPage) throws GitLabApiException {
        return (new Pager<Member>(this, Member.class, itemsPerPage, null, "projects", projectId, "members"));
    }

    /**
     * Gets a project team member.
     * <p>
     * GET /projects/:id/members/:user_id
     *
     * @param projectId the project ID to get team member for
     * @param userId    the user ID of the member
     * @return the member specified by the project ID/user ID pair
     * @throws GitLabApiException if any exception occurs
     */
    public Member getMember(Integer projectId, Integer userId) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, "projects", projectId, "members", userId);
        return (response.readEntity(Member.class));
    }

    /**
     * Adds a user to a project team. This is an idempotent method and can be called multiple times
     * with the same parameters. Adding team membership to a user that is already a member does not
     * affect the existing membership.
     *
     * <pre><code>GitLab Endpoint: POST /projects/:id/members</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param userId          the user ID of the member to add, required
     * @param accessLevel     the access level for the new member, required
     * @return the added member
     * @throws GitLabApiException if any exception occurs
     */
    public Member addMember(Object projectIdOrPath, Integer userId, Integer accessLevel) throws GitLabApiException {
        return (addMember(projectIdOrPath, userId, accessLevel, null));
    }

    /**
     * Adds a user to a project team. This is an idempotent method and can be called multiple times
     * with the same parameters. Adding team membership to a user that is already a member does not
     * affect the existing membership.
     *
     * <pre><code>GitLab Endpoint: POST /projects/:id/members</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param userId          the user ID of the member to add
     * @param accessLevel     the access level for the new member
     * @param expiresAt       the date the membership in the group will expire
     * @return the added member
     * @throws GitLabApiException if any exception occurs
     */
    public Member addMember(Object projectIdOrPath, Integer userId, Integer accessLevel, Date expiresAt) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm()
                .withParam("user_id", userId, true)
                .withParam("access_level", accessLevel, true)
                .withParam("expires_at", expiresAt, false);
        Response response = post(Response.Status.CREATED, formData, "projects", getProjectIdOrPath(projectIdOrPath), "members");
        return (response.readEntity(Member.class));
    }

    /**
     * Adds a user to a project team. This is an idempotent method and can be called multiple times
     * with the same parameters. Adding team membership to a user that is already a member does not
     * affect the existing membership.
     * <p>
     * POST /projects/:id/members
     *
     * @param projectId   the project ID to add the team member to
     * @param userId      the user ID of the member to add
     * @param accessLevel the access level for the new member
     * @return the added member
     * @throws GitLabApiException if any exception occurs
     */
    public Member updateMember(Integer projectId, Integer userId, Integer accessLevel) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm()
                .withParam("access_level", accessLevel, true)
                .withParam("expires_at", "", false);
        Response response = put(Response.Status.OK, formData.asMap(), "projects", projectId, "members", userId);
        return (response.readEntity(Member.class));
    }

    /**
     * Updates a member of a project.
     *
     * <pre><code>GitLab Endpoint: PUT /projects/:projectId/members/:userId</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance, required
     * @param userId          the user ID of the member to update, required
     * @param accessLevel     the new access level for the member, required
     * @param expiresAt       the date the membership in the group will expire, optional
     * @return the updated member
     * @throws GitLabApiException if any exception occurs
     */
    public Member updateMember(Object projectIdOrPath, Integer userId, Integer accessLevel, Date expiresAt) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm()
                .withParam("access_level", accessLevel, true)
                .withParam("expires_at", expiresAt, false);
        Response response = put(Response.Status.OK, formData.asMap(), "projects", getProjectIdOrPath(projectIdOrPath), "members", userId);
        return (response.readEntity(Member.class));
    }

    /**
     * Removes user from project team.
     * <p>
     * DELETE /projects/:id/members/:user_id
     *
     * @param projectId the project ID to remove the team member from
     * @param userId    the user ID of the member to remove
     * @throws GitLabApiException if any exception occurs
     */
    public void removeMember(Integer projectId, Integer userId) throws GitLabApiException {
        Response.Status expectedStatus = (isApiVersion(ApiVersion.V3) ? Response.Status.OK : Response.Status.NO_CONTENT);
        delete(expectedStatus, null, "projects", projectId, "members", userId);
    }

    /**
     * Get a list of project users. This list includes all project members and all users assigned to project parent groups.
     * <p>
     * GET /projects/:id/users
     *
     * @param projectId the project ID to get users for
     * @return the users belonging to the specified project and its parent groups
     * @throws GitLabApiException if any exception occurs
     */
    public List<ProjectUser> getProjectUsers(Integer projectId) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "projects", projectId, "users");
        return (response.readEntity(new GenericType<List<ProjectUser>>() {
        }));
    }

    /**
     * Get a list of project users matching the specified search string. This list includes all project members and all users assigned to project parent groups.
     * <p>
     * GET /projects/:id/users
     *
     * @param projectId the project ID to get users for
     * @param search    the string to match specific users
     * @return the users matching the search string and belonging to the specified project and its parent groups
     * @throws GitLabApiException if any exception occurs
     */
    public List<ProjectUser> getProjectUsers(Integer projectId, String search) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm()
                .withParam("search", search)
                .withParam(PER_PAGE_PARAM, getDefaultPerPage());
        Response response = get(Response.Status.OK, formData.asMap(), "projects", projectId, "users");
        return (response.readEntity(new GenericType<List<ProjectUser>>() {
        }));
    }

    /**
     * Get the project events for specific project. Sorted from newest to latest.
     * <p>
     * GET /projects/:id/events
     *
     * @param projectId the project ID to get events for
     * @return the project events for the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public List<Event> getProjectEvents(Integer projectId) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "projects", projectId, "events");
        return (response.readEntity(new GenericType<List<Event>>() {
        }));
    }

    /**
     * Get the project events for specific project. Sorted from newest to latest in the specified page range.
     * <p>
     * GET /projects/:id/events
     *
     * @param projectId the project ID to get events for
     * @param page      the page to get
     * @param perPage   the number of Event instances per page
     * @return the project events for the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public List<Event> getProjectEvents(Integer projectId, int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage), "projects", projectId, "events");
        return (response.readEntity(new GenericType<List<Event>>() {
        }));
    }

    /**
     * Get a Pager of project events for specific project. Sorted from newest to latest.
     * <p>
     * GET /projects/:id/events
     *
     * @param projectId    the project ID to get events for
     * @param itemsPerPage the number of Project instances that will be fetched per page
     * @return a Pager of project events for the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Event> getProjectEvents(Integer projectId, int itemsPerPage) throws GitLabApiException {
        return (new Pager<Event>(this, Event.class, itemsPerPage, null, "projects", projectId, "events"));
    }

    /**
     * Get list of project hooks.
     * <p>
     * GET /projects/:id/hooks
     *
     * @param projectId the project ID to get project hooks for
     * @return a list of project hooks for the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public List<ProjectHook> getHooks(Integer projectId) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "projects", projectId, "hooks");
        return (response.readEntity(new GenericType<List<ProjectHook>>() {
        }));
    }

    /**
     * Get list of project hooks in the specified page range.
     * <p>
     * GET /projects/:id/hooks
     *
     * @param projectId the project ID to get project hooks for
     * @param page      the page to get
     * @param perPage   the number of ProjectHook instances per page
     * @return a list of project hooks for the specified project in the specified page range
     * @throws GitLabApiException if any exception occurs
     */
    public List<ProjectHook> getHooks(Integer projectId, int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage), "projects", projectId, "hooks");
        return (response.readEntity(new GenericType<List<ProjectHook>>() {
        }));
    }

    /**
     * Get Pager of project hooks.
     * <p>
     * GET /projects/:id/hooks
     *
     * @param projectId    the project ID to get project hooks for
     * @param itemsPerPage the number of Project instances that will be fetched per page
     * @return a Pager of project hooks for the specified project
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<ProjectHook> getHooks(Integer projectId, int itemsPerPage) throws GitLabApiException {
        return (new Pager<ProjectHook>(this, ProjectHook.class, itemsPerPage, null, "projects", projectId, "hooks"));
    }

    /**
     * Get a specific hook for project.
     * <p>
     * GET /projects/:id/hooks/:hook_id
     *
     * @param projectId the project ID to get the hook for
     * @param hookId    the ID of the hook to get
     * @return the project hook for the specified project ID/hook ID pair
     * @throws GitLabApiException if any exception occurs
     */
    public ProjectHook getHook(Integer projectId, Integer hookId) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, "projects", projectId, "hooks", hookId);
        return (response.readEntity(ProjectHook.class));
    }

    /**
     * Adds a hook to project.
     * <p>
     * POST /projects/:id/hooks
     *
     * @param projectName           the name of the project
     * @param url                   the callback URL for the hook
     * @param enabledHooks          a ProjectHook instance specifying which hooks to enable
     * @param enableSslVerification enable SSL verification
     * @param secretToken           the secret token to pass back to the hook
     * @return the added ProjectHook instance
     * @throws GitLabApiException if any exception occurs
     */
    public ProjectHook addHook(String projectName, String url, ProjectHook enabledHooks, boolean enableSslVerification, String secretToken) throws GitLabApiException {

        if (projectName == null) {
            return (null);
        }

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("url", url, true)
                .withParam("push_events", enabledHooks.getPushEvents(), false)
                .withParam("issues_events", enabledHooks.getIssuesEvents(), false)
                .withParam("merge_requests_events", enabledHooks.getMergeRequestsEvents(), false)
                .withParam("tag_push_events", enabledHooks.getTagPushEvents(), false)
                .withParam("note_events", enabledHooks.getNoteEvents(), false)
                .withParam("job_events", enabledHooks.getJobEvents(), false)
                .withParam("pipeline_events", enabledHooks.getPipelineEvents(), false)
                .withParam("wiki_events", enabledHooks.getWikiPageEvents(), false)
                .withParam("enable_ssl_verification", enabledHooks.getEnableSslVerification())
                .withParam("token", secretToken, false);
        Response response = post(Response.Status.CREATED, formData, "projects", projectName, "hooks");
        return (response.readEntity(ProjectHook.class));
    }

    /**
     * Adds a hook to project.
     * <p>
     * POST /projects/:id/hooks
     *
     * @param projectId             the project ID to add the project hook to
     * @param url                   the callback URL for the hook
     * @param enabledHooks          a ProjectHook instance specifying which hooks to enable
     * @param enableSslVerification enable SSL verification
     * @param secretToken           the secret token to pass back to the hook
     * @return the added ProjectHook instance
     * @throws GitLabApiException if any exception occurs
     */
    public ProjectHook addHook(Integer projectId, String url, ProjectHook enabledHooks, boolean enableSslVerification, String secretToken) throws GitLabApiException {

        if (projectId == null) {
            return (null);
        }

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("url", url, true)
                .withParam("push_events", enabledHooks.getPushEvents(), false)
                .withParam("issues_events", enabledHooks.getIssuesEvents(), false)
                .withParam("merge_requests_events", enabledHooks.getMergeRequestsEvents(), false)
                .withParam("tag_push_events", enabledHooks.getTagPushEvents(), false)
                .withParam("note_events", enabledHooks.getNoteEvents(), false)
                .withParam("job_events", enabledHooks.getJobEvents(), false)
                .withParam("pipeline_events", enabledHooks.getPipelineEvents(), false)
                .withParam("wiki_events", enabledHooks.getWikiPageEvents(), false)
                .withParam("enable_ssl_verification", enabledHooks.getEnableSslVerification())
                .withParam("token", secretToken, false);
        Response response = post(Response.Status.CREATED, formData, "projects", projectId, "hooks");
        return (response.readEntity(ProjectHook.class));
    }

    /**
     * Adds a hook to project.
     * <p>
     * POST /projects/:id/hooks
     *
     * @param project               the Project instance to add the project hook to
     * @param url                   the callback URL for the hook
     * @param enabledHooks          a ProjectHook instance specifying which hooks to enable
     * @param enableSslVerification enable SSL verification
     * @param secretToken           the secret token to pass back to the hook
     * @return the added ProjectHook instance
     * @throws GitLabApiException if any exception occurs
     */
    public ProjectHook addHook(Project project, String url, ProjectHook enabledHooks, boolean enableSslVerification, String secretToken) throws GitLabApiException {

        if (project == null) {
            return (null);
        }

        return (addHook(project.getId(), url, enabledHooks, enableSslVerification, secretToken));
    }

    /**
     * Adds a hook to project.
     * <p>
     * POST /projects/:id/hooks
     *
     * @param project               the Project instance to add the project hook to
     * @param url                   the callback URL for the hook
     * @param doPushEvents          flag specifying whether to do push events
     * @param doIssuesEvents        flag specifying whether to do issues events
     * @param doMergeRequestsEvents flag specifying whether to do merge requests events
     * @return the added ProjectHook instance
     * @throws GitLabApiException if any exception occurs
     */
    public ProjectHook addHook(Project project, String url, boolean doPushEvents, boolean doIssuesEvents, boolean doMergeRequestsEvents) throws GitLabApiException {

        if (project == null) {
            return (null);
        }

        return (addHook(project.getId(), url, doPushEvents, doIssuesEvents, doMergeRequestsEvents));
    }

    /**
     * Adds a hook to project.
     * <p>
     * POST /projects/:id/hooks
     *
     * @param projectId             the project ID to add the project hook to
     * @param url                   the callback URL for the hook
     * @param doPushEvents          flag specifying whether to do push events
     * @param doIssuesEvents        flag specifying whether to do issues events
     * @param doMergeRequestsEvents flag specifying whether to do merge requests events
     * @return the added ProjectHook instance
     * @throws GitLabApiException if any exception occurs
     */
    public ProjectHook addHook(Integer projectId, String url, boolean doPushEvents, boolean doIssuesEvents, boolean doMergeRequestsEvents) throws GitLabApiException {

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("url", url)
                .withParam("push_events", doPushEvents)
                .withParam("issues_enabled", doIssuesEvents)
                .withParam("merge_requests_events", doMergeRequestsEvents);

        Response response = post(Response.Status.CREATED, formData, "projects", projectId, "hooks");
        return (response.readEntity(ProjectHook.class));
    }

    /**
     * Deletes a hook from the project.
     * <p>
     * DELETE /projects/:id/hooks/:hook_id
     *
     * @param projectId the project ID to delete the project hook from
     * @param hookId    the project hook ID to delete
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteHook(Integer projectId, Integer hookId) throws GitLabApiException {
        Response.Status expectedStatus = (isApiVersion(ApiVersion.V3) ? Response.Status.OK : Response.Status.NO_CONTENT);
        delete(expectedStatus, null, "projects", projectId, "hooks", hookId);
    }

    /**
     * Deletes a hook from the project.
     * <p>
     * DELETE /projects/:id/hooks/:hook_id
     *
     * @param hook the ProjectHook instance to remove
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteHook(ProjectHook hook) throws GitLabApiException {
        deleteHook(hook.getProjectId(), hook.getId());
    }

    /**
     * Modifies a hook for project.
     * <p>
     * PUT /projects/:id/hooks/:hook_id
     *
     * @param hook the ProjectHook instance that contains the project hook info to modify
     * @return the modified project hook
     * @throws GitLabApiException if any exception occurs
     */
    public ProjectHook modifyHook(ProjectHook hook) throws GitLabApiException {

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("url", hook.getUrl(), true)
                .withParam("push_events", hook.getPushEvents(), false)
                .withParam("issues_events", hook.getIssuesEvents(), false)
                .withParam("merge_requests_events", hook.getMergeRequestsEvents(), false)
                .withParam("tag_push_events", hook.getTagPushEvents(), false)
                .withParam("note_events", hook.getNoteEvents(), false)
                .withParam("job_events", hook.getJobEvents(), false)
                .withParam("pipeline_events", hook.getPipelineEvents(), false)
                .withParam("wiki_events", hook.getWikiPageEvents(), false)
                .withParam("enable_ssl_verification", hook.getEnableSslVerification(), false)
                .withParam("token", hook.getToken(), false);

        Response response = put(Response.Status.OK, formData.asMap(), "projects", hook.getProjectId(), "hooks", hook.getId());
        return (response.readEntity(ProjectHook.class));
    }

    /**
     * Get a list of project's issues. Only returns the first page
     * <p>
     * GET /projects/:id/issues
     *
     * @param projectId the project ID to get the issues for
     * @return a list of project's issues
     * @throws GitLabApiException if any exception occurs
     * @deprecated Will be removed in version 5.0, replaced by {@link IssuesApi#getIssues(Integer)}
     */
    public List<Issue> getIssues(Integer projectId) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "projects", projectId, "issues");
        return (response.readEntity(new GenericType<List<Issue>>() {
        }));
    }

    /**
     * Get a list of project's issues using the specified page and per page settings.
     * <p>
     * GET /projects/:id/issues
     *
     * @param projectId the project ID to get the issues for
     * @param page      the page to get
     * @param perPage   the number of issues per page
     * @return the list of issues in the specified range
     * @throws GitLabApiException if any exception occurs
     * @deprecated Will be removed in version 5.0, replaced by {@link IssuesApi#getIssues(Integer, int, int)}
     */
    public List<Issue> getIssues(Integer projectId, int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage), "projects", projectId, "issues");
        return (response.readEntity(new GenericType<List<Issue>>() {
        }));
    }

    /**
     * Get a Pager of project's issues.
     * <p>
     * GET /projects/:id/issues
     *
     * @param projectId    the project ID to get the issues for
     * @param itemsPerPage the number of issues per page
     * @return the list of issues in the specified range
     * @throws GitLabApiException if any exception occurs
     * @deprecated Will be removed in version 5.0, replaced by {@link IssuesApi#getIssues(Integer, int)}
     */
    public Pager<Issue> getIssues(Integer projectId, int itemsPerPage) throws GitLabApiException {
        return (new Pager<Issue>(this, Issue.class, itemsPerPage, null, "projects", projectId, "issues"));
    }

    /**
     * Get a single project issues.
     * <p>
     * GET /projects/:id/issues/:issue_iid
     *
     * @param projectId the project ID to get the issue for
     * @param issueId   the internal ID of a project's issue
     * @return the specified Issue instance
     * @throws GitLabApiException if any exception occurs
     * @deprecated Will be removed in version 5.0, replaced by {@link IssuesApi#getIssue(Integer, Integer)}
     */
    public Issue getIssue(Integer projectId, Integer issueId) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "projects", projectId, "issues", issueId);
        return (response.readEntity(Issue.class));
    }

    /**
     * Delete a project issue.
     * <p>
     * DELETE /projects/:id/issues/:issue_iid
     *
     * @param projectId the project ID to delete the issue from
     * @param issueId   the internal ID of a project's issue
     * @throws GitLabApiException if any exception occurs
     * @deprecated Will be removed in version 5.0, replaced by {@link IssuesApi#deleteIssue(Integer, Integer)}
     */
    public void deleteIssue(Integer projectId, Integer issueId) throws GitLabApiException {
        Response.Status expectedStatus = (isApiVersion(ApiVersion.V3) ? Response.Status.OK : Response.Status.NO_CONTENT);
        delete(expectedStatus, getDefaultPerPageParam(), "projects", projectId, "issues", issueId);
    }

    /**
     * Get a list of project snippets.  This only returns the first page of snippets.
     * <p>
     * GET /projects/:id/snippets
     *
     * @param projectId the project ID to get the snippets for
     * @return a list of project's snippets
     * @throws GitLabApiException if any exception occurs
     */
    public List<Snippet> getSnippets(Integer projectId) throws GitLabApiException {
        return (getSnippets(projectId, 1, this.getDefaultPerPage()));
    }

    /**
     * Get a list of project snippets.  This only returns the first page of snippets.
     * <p>
     * GET /projects/:id/snippets
     *
     * @param projectId the project ID to get the snippets for
     * @param page      the page to get
     * @param perPage   the number of snippets per page
     * @return a list of project's snippets for the specified range
     * @throws GitLabApiException if any exception occurs
     */
    public List<Snippet> getSnippets(Integer projectId, int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage), "projects", projectId, "snippets");
        return (response.readEntity(new GenericType<List<Snippet>>() {
        }));
    }

    /**
     * Get a Pager of project's snippets.
     * <p>
     * GET /projects/:id/snippets
     *
     * @param projectId    the project ID to get the issues for
     * @param itemsPerPage the number of snippets per page
     * @return the Pager of snippets
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Snippet> getSnippets(Integer projectId, int itemsPerPage) throws GitLabApiException {
        return (new Pager<Snippet>(this, Snippet.class, itemsPerPage, null, "projects", projectId, "snippets"));
    }

    /**
     * Get a single of project snippet.
     * <p>
     * GET /projects/:id/snippets/:snippet_id
     *
     * @param projectId the project ID to get the snippet for
     * @param snippetId the ID of the project's snippet
     * @return the specified project Snippet
     * @throws GitLabApiException if any exception occurs
     */
    public Snippet getSnippet(Integer projectId, Integer snippetId) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, "projects", projectId, "snippets", snippetId);
        return (response.readEntity(Snippet.class));
    }

    /**
     * Creates a new project snippet. The user must have permission to create new snippets.
     * <p>
     * POST /projects/:id/snippets
     *
     * @param projectId   the ID of the project owned by the authenticated user, required
     * @param title       the title of a snippet, required
     * @param filename    the name of a snippet file, required
     * @param description the description of a snippet, optional
     * @param code        the content of a snippet, required
     * @param visibility  the snippet's visibility, required
     * @return a Snippet instance with info on the created snippet
     * @throws GitLabApiException if any exception occurs
     */
    public Snippet createSnippet(Integer projectId, String title, String filename, String description,
                                 String code, Visibility visibility) throws GitLabApiException {

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("title", title, true)
                .withParam("file_name", filename, true)
                .withParam("description", description)
                .withParam("code", code, true)
                .withParam("visibility", visibility, true);

        Response response = post(Response.Status.CREATED, formData, "projects", projectId, "snippets");
        return (response.readEntity(Snippet.class));
    }

    /**
     * Updates an existing project snippet. The user must have permission to change an existing snippet.
     * <p>
     * PUT /projects/:id/snippets/:snippet_id
     *
     * @param projectId   the ID of the project owned by the authenticated user, required
     * @param snippetId   the ID of a project's snippet, required
     * @param title       the title of a snippet, optional
     * @param filename    the name of a snippet file, optional
     * @param description the description of a snippet, optioptionalonal
     * @param code        the content of a snippet, optional
     * @param visibility  the snippet's visibility, reqoptionaluired
     * @return a Snippet instance with info on the updated snippet
     * @throws GitLabApiException if any exception occurs
     */
    public Snippet updateSnippet(Integer projectId, Integer snippetId, String title, String filename, String description,
                                 String code, Visibility visibility) throws GitLabApiException {

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("title", title)
                .withParam("file_name", filename)
                .withParam("description", description)
                .withParam("code", code)
                .withParam("visibility", visibility);

        Response response = put(Response.Status.OK, formData.asMap(), "projects", projectId, "snippets", snippetId);
        return (response.readEntity(Snippet.class));
    }

    /*
     * Deletes an existing project snippet. This is an idempotent function and deleting a
     * non-existent snippet does not cause an error.
     *
     * DELETE /projects/:id/snippets/:snippet_id
     *
     * @param projectId the project ID of the snippet
     * @param snippetId the ID of the project's snippet
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteSnippet(Integer projectId, Integer snippetId) throws GitLabApiException {
        delete(Response.Status.NO_CONTENT, null, "projects", projectId, "snippets", snippetId);
    }

    /*
     * Get the raw project snippet as plain text.
     *
     * GET /projects/:id/snippets/:snippet_id/raw
     *
     * @param projectId the project ID of the snippet
     * @param snippetId the ID of the project's snippet
     * @throws GitLabApiException if any exception occurs
     */
    public String getRawSnippetContent(Integer projectId, Integer snippetId) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, "projects", projectId, "snippets", snippetId, "raw");
        return (response.readEntity(String.class));
    }

    /**
     * Create a new project variable
     * <p>
     * POST /projects/:id/variables
     *
     * @param id         The ID of a project or urlencoded NAMESPACE/PROJECT_NAME of the project owned by the authenticated user
     * @param key        The key of a variable; must have no more than 255 characters; only A-Z, a-z, 0-9, and _ are allowed
     * @param value      The value of a variable
     * @param protecteds the access level for the new member
     * @return Create a new project variable.
     * @throws GitLabApiException if any exception occurs
     */
    public Map<String, Object> addVariable(Integer id, String key, String value, boolean protecteds) throws GitLabApiException {

        Form formData = new Form();
        formData.param("key", key);
        formData.param("value", value);
        formData.param("protected", String.valueOf(protecteds));
        Response response = post(Response.Status.CREATED, formData, "projects", id, "variables");
        return (response.readEntity(Map.class));
    }


    /**
     * Update a new project variable
     * <p>
     * POST /projects/:id/variables
     *
     * @param id The ID of a project or urlencoded NAMESPACE/PROJECT_NAME of the project owned by the authenticated user
     * @return get project variable.
     * @throws GitLabApiException if any exception occurs
     */
    public Map<String, Object> updateVariable(Integer id, String key, String value, boolean protecteds) throws GitLabApiException {
        Form formData = new Form();
        formData.param("key", key);
        formData.param("value", value);
        formData.param("protected", String.valueOf(protecteds));
        Response response = put(Response.Status.OK, formData.asMap(), "projects", id, "variables", key);
        return (response.readEntity(Map.class));
    }

    /**
     * Deletes a project variable.
     *
     * <pre><code>GitLab Endpoint: DELETE /projects/:id/variables/:key</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance, required
     * @param key the key of an existing variable, required
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteVariable(Object projectIdOrPath, String key) throws GitLabApiException {
        delete(Response.Status.NO_CONTENT, null, "projects", getProjectIdOrPath(projectIdOrPath), "variables", key);
    }

    /**
     * Create a new project variable
     * <p>
     * POST /projects/:id/variables
     *
     * @param id The ID of a project or urlencoded NAMESPACE/PROJECT_NAME of the project owned by the authenticated user
     * @return get project variable.
     * @throws GitLabApiException if any exception occurs
     */
    public List<Variable> getVariable(Integer id) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, "projects", id, "variables");
        return (response.readEntity(new GenericType<List<Variable>>() {
        }));
    }

    /**
     * Protects a single repository branch or several project repository branches using a wildcard protected branch.
     * <p>
     * POST /projects/:id/protected_branches
     *
     * @param id                 The ID or URL-encoded path of the project owned by the authenticated user
     * @param name               The name of the branch or wildcard
     * @param push_access_level  Access levels allowed to push (defaults: 40, master access level)
     * @param merge_access_level Access levels allowed to merge (defaults: 40, master access level)
     * @return Create a  project protected branches.
     * @throws GitLabApiException if any exception occurs
     */
    public Map<String, Object> protectedBranches(Integer id, String name, String merge_access_level, String push_access_level) throws GitLabApiException {
        Form formData = new Form();
        formData.param("name", name);
        formData.param("merge_access_level", merge_access_level);
        formData.param("push_access_level", push_access_level);
        Response response = post(Response.Status.CREATED, formData, "projects", id, "protected_branches");
        return (response.readEntity(Map.class));
    }

    /**
     * Gets a single protected branch or wildcard protected branch.
     * <p>
     * GET /projects/:id/protected_branches/:name
     *
     * @param id   The ID or URL-encoded path of the project owned by the authenticated user
     * @param name The name of the branch or wildcard
     * @return Get a project protected branches.
     * @throws GitLabApiException if any exception occurs
     */
    public Map<String, Object> getBranch(Integer id, String name) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, "projects", id, "protected_branches", name);
        return (response.readEntity(Map.class));
    }

    /**
     * Gets a list of protected branches from a project.
     * <p>
     * GET /projects/:id/protected_branches
     *
     * @param id The ID or URL-encoded path of the project owned by the authenticated user
     * @return Get a list project protected branches.
     * @throws GitLabApiException if any exception occurs
     */
    public List<Map<String, Object>> getBranchs(Integer id) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "projects", id, "protected_branches");
        return (response.readEntity(List.class));
    }

    /**
     * Unprotects the given protected branch or wildcard protected branch.
     * <p>
     * DELETE /projects/:id/protected_branches/:name
     *
     * @param id   The ID or URL-encoded path of the project owned by the authenticated user
     * @param name The name of the branch or wildcard
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteBranch(Integer id, String name) throws GitLabApiException {
        delete(Response.Status.NO_CONTENT, null, "projects", id, "protected_branches", name);
    }

    /**
     * Get a List of the project access requests viewable by the authenticated user.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/access_requests</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @return a List of project AccessRequest instances accessible by the authenticated user
     * @throws GitLabApiException if any exception occurs
     */
    public List<AccessRequest> getAccessRequests(Object projectIdOrPath) throws GitLabApiException {
        return (getAccessRequests(projectIdOrPath, getDefaultPerPage()).all());
    }

    /**
     * Get a Pager of the project access requests viewable by the authenticated user.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/access_requests</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param itemsPerPage    the number of AccessRequest instances that will be fetched per page
     * @return a Pager of project AccessRequest instances accessible by the authenticated user
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<AccessRequest> getAccessRequests(Object projectIdOrPath, int itemsPerPage) throws GitLabApiException {
        return (new Pager<>(this, AccessRequest.class, itemsPerPage, null, "projects", getProjectIdOrPath(projectIdOrPath), "access_requests"));
    }

    /**
     * Returns the project ID or path from the provided Integer, String, or Project instance.
     *
     * @param obj the object to determine the ID or path from
     * @return the project ID or path from the provided Integer, String, or Project instance
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Object getProjectIdOrPath(Object obj) throws GitLabApiException {

        if (obj == null) {
            throw (new RuntimeException("Cannot determine ID or path from null object"));
        } else if (obj instanceof Integer) {
            return (obj);
        } else if (obj instanceof String) {
            return (urlEncode(((String) obj).trim()));
        } else if (obj instanceof Project) {

            Integer id = ((Project) obj).getId();
            if (id != null && id.intValue() > 0) {
                return (id);
            }

            String path = ((Project) obj).getPathWithNamespace();
            if (path != null && path.trim().length() > 0) {
                return (urlEncode(path.trim()));
            }

            throw (new RuntimeException("Cannot determine ID or path from provided Project instance"));

        } else {
            throw (new RuntimeException("Cannot determine ID or path from provided " + obj.getClass().getSimpleName() +
                    " instance, must be Integer, String, or a Project instance"));
        }
    }

    /**
     * Deny access for the specified user to the specified project.
     *
     * <pre><code>GitLab Endpoint: DELETE /projects/:id/access_requests/:user_id</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param userId          the user ID to deny access for
     * @throws GitLabApiException if any exception occurs
     */
    public void denyAccessRequest(Object projectIdOrPath, Integer userId) throws GitLabApiException {
        delete(Response.Status.NO_CONTENT, null,
                "projects", getProjectIdOrPath(projectIdOrPath), "access_requests", userId);
    }

    /**
     * Gets a list of project members viewable by the authenticated user,
     * including inherited members through ancestor groups. Returns multiple
     * times the same user (with different member attributes) when the user is
     * a member of the project/group and of one or more ancestor group.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/members/all</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @return the project members viewable by the authenticated user, including inherited members through ancestor groups
     * @throws GitLabApiException if any exception occurs
     */
    public List<Member> getAllMembers(Object projectIdOrPath, String query) throws GitLabApiException {
        return (getAllMembers(projectIdOrPath, getDefaultPerPage(), query).all());
    }

    /**
     * Gets a list of project members viewable by the authenticated user,
     * including inherited members through ancestor groups. Returns multiple
     * times the same user (with different member attributes) when the user is
     * a member of the project/group and of one or more ancestor group.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/members</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param page            the page to get
     * @param perPage         the number of Member instances per page
     * @return the project members viewable by the authenticated user, including inherited members through ancestor groups
     * @throws GitLabApiException if any exception occurs
     */
    public List<Member> getAllMembers(Object projectIdOrPath, int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage),
                "projects", getProjectIdOrPath(projectIdOrPath), "members", "all");
        return (response.readEntity(new GenericType<List<Member>>() {
        }));
    }

    /**
     * Gets a Pager of project members viewable by the authenticated user,
     * including inherited members through ancestor groups. Returns multiple
     * times the same user (with different member attributes) when the user is
     * a member of the project/group and of one or more ancestor group.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/members/all</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param itemsPerPage    the number of Project instances that will be fetched per page
     * @return a Pager of the project members viewable by the authenticated user,
     * including inherited members through ancestor groups
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Member> getAllMembers(Object projectIdOrPath, int itemsPerPage, String query) throws GitLabApiException {
        MultivaluedMap<String, String> map = new GitLabApiForm().withParam(QUERY, query).asMap();
        return (new Pager<Member>(this, Member.class, itemsPerPage, map,
                "projects", getProjectIdOrPath(projectIdOrPath), "members", "all"));
    }


    /**
     * Gets a member of a group or project, including members inherited through ancestor groups.
     * See the corresponding endpoint to list all inherited members for details.
     *
     * <pre><code>GitLab Endpoint: GET  /projects/:id/members/all/:user_id</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param userId The user ID of the member
     *
     * @return Gets a member of a group or project.
     * @throws GitLabApiException if any exception occurs
     */
//    public Member getAllMember(Object projectIdOrPath, int userId) throws GitLabApiException {
//        Response response = get(Response.Status.OK, null,
//                "projects", getProjectIdOrPath(projectIdOrPath), "members", "all", userId);
//        return (response.readEntity(Member.class));
//    }

}
