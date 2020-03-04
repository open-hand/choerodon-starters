package org.gitlab4j.api;

import java.util.List;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.ProtectedTag;

/**
 * This class provides an entry point to all the GitLab Tags and Protected Tags API calls.
 * @see <a href="https://docs.gitlab.com/ce/api/tags.html">Tags API at GitLab</a>
 * @see <a href="https://docs.gitlab.com/ce/api/protected_tags.html">Protected Tags API at GitLab</a>
 */
public class TagsApi extends AbstractApi {

    public TagsApi(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    /**
     * Gets a list of protected tags from a project.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/protected_tags</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @return a List of protected tags for the specified project ID
     * @throws GitLabApiException if any exception occurs
     */
    public List<ProtectedTag> getProtectedTags(Object projectIdOrPath) throws GitLabApiException {
        return (getProtectedTags(projectIdOrPath, getDefaultPerPage()).all());
    }

    /**
     * Gets a list of protected tags from a project and in the specified page range.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/protected_tags</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param page the page to get
     * @param perPage the number of Tag instances per page
     * @return a List of tags for the specified project ID and page range
     * @throws GitLabApiException if any exception occurs
     */
    public List<ProtectedTag> getProtectedTags(Object projectIdOrPath, int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage),
                "projects", getProjectIdOrPath(projectIdOrPath), "protected_tags");
        return (response.readEntity(new GenericType<List<ProtectedTag>>() { }));
    }

    /**
     * Get a Pager of protected tags for a project.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/protected_tags</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param itemsPerPage the number of Project instances that will be fetched per page
     * @return the Pager of protected tags for the specified project ID
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<ProtectedTag> getProtectedTags(Object projectIdOrPath, int itemsPerPage) throws GitLabApiException {
        return (new Pager<ProtectedTag>(this, ProtectedTag.class, itemsPerPage, null, "projects", getProjectIdOrPath(projectIdOrPath), "protected_tags"));
    }

    /**
     * Gets a single protected tag or wildcard protected tag
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/protected_tags/:name</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param name the name of the tag or wildcard
     * @return a ProtectedTag instance with info on the specified protected tag
     * @throws GitLabApiException if any exception occurs
     */
    public ProtectedTag getProtectedTag(Object projectIdOrPath, String name) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, "projects", getProjectIdOrPath(projectIdOrPath), "protected_tags", urlEncode(name));
        return (response.readEntity(ProtectedTag.class));
    }

    /**
     * Protects a single repository tag or several project repository tags using a wildcard protected tag.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/protected_tags</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param name the name of the tag or wildcard
     * @param createAccessLevel the access level allowed to create
     * @return a ProtectedTag instance
     * @throws GitLabApiException if any exception occurs
     */
    public ProtectedTag protectTag(Object projectIdOrPath, String name, AccessLevel createAccessLevel) throws GitLabApiException {
        Form formData = new GitLabApiForm().withParam("name", name, true).withParam("create_access_level", createAccessLevel);
        Response response = post(Response.Status.CREATED, formData, "projects", getProjectIdOrPath(projectIdOrPath), "protected_tags");
        return (response.readEntity(ProtectedTag.class));
    }

    /**
     * Unprotects the given protected tag or wildcard protected tag.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/protected_tags/:name</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param name the name of the tag or wildcard
     * @throws GitLabApiException if any exception occurs
     */
    public void unprotectTag(Object projectIdOrPath, String name) throws GitLabApiException {
        delete(Response.Status.NO_CONTENT, null, "projects", getProjectIdOrPath(projectIdOrPath), "protected_tags", urlEncode(name));
    }
}
