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

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.gitlab4j.api.GitLabApi.ApiVersion;
import org.gitlab4j.api.models.*;

/**
 * This class implements the client side API for the GitLab groups calls.
 */
public class GroupApi extends AbstractApi {

    public GroupApi(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    /**
     * Get a list of groups. (As user: my groups, as admin: all groups)
     * <p>
     * GET /groups
     *
     * @return the list of groups viewable by the authenticated user
     * @throws GitLabApiException if any exception occurs
     */
    public List<Group> getGroups() throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "groups");
        return (response.readEntity(new GenericType<List<Group>>() {
        }));
    }

    /**
     * Get a list of groups (As user: my groups, as admin: all groups) and in the specified page range.
     * <p>
     * GET /groups
     *
     * @param page    the page to get
     * @param perPage the number of Group instances per page
     * @return the list of groups viewable by the authenticated userin the specified page range
     * @throws GitLabApiException if any exception occurs
     */
    public List<Group> getGroups(int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage), "groups");
        return (response.readEntity(new GenericType<List<Group>>() {
        }));
    }

    /**
     * Get a Pager of groups. (As user: my groups, as admin: all groups)
     * <p>
     * GET /groups
     *
     * @param itemsPerPage the number of Group instances that will be fetched per page
     * @return the list of groups viewable by the authenticated user
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Group> getGroups(int itemsPerPage) throws GitLabApiException {
        return (new Pager<Group>(this, Group.class, itemsPerPage, null, "groups"));
    }

    /**
     * Get all groups that match your string in their name or path.
     *
     * @param search the group name or path search criteria
     * @return a List containing matching Group instances
     * @throws GitLabApiException if any exception occurs
     */
    public List<Group> getGroups(String search) throws GitLabApiException {
        Form formData = new GitLabApiForm().withParam("search", search).withParam(PER_PAGE_PARAM, getDefaultPerPage());
        Response response = get(Response.Status.OK, formData.asMap(), "groups");
        return (response.readEntity(new GenericType<List<Group>>() {
        }));
    }

    /**
     * Get all groups that match your string in their name or path.
     *
     * @param search  the group name or path search criteria
     * @param page    the page to get
     * @param perPage the number of Group instances per page
     * @return a List containing matching Group instances
     * @throws GitLabApiException if any exception occurs
     */
    public List<Group> getGroups(String search, int page, int perPage) throws GitLabApiException {
        Form formData = new GitLabApiForm().withParam("search", search).withParam(PAGE_PARAM, page).withParam(PER_PAGE_PARAM, perPage);
        Response response = get(Response.Status.OK, formData.asMap(), "groups");
        return (response.readEntity(new GenericType<List<Group>>() {
        }));
    }

    /**
     * Get all groups that match your string in their name or path.
     *
     * @param search       the group name or path search criteria
     * @param itemsPerPage the number of Group instances that will be fetched per page
     * @return a List containing matching Group instances
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Group> getGroups(String search, int itemsPerPage) throws GitLabApiException {
        Form formData = new GitLabApiForm().withParam("search", search);
        return (new Pager<Group>(this, Group.class, itemsPerPage, formData.asMap(), "groups"));
    }

    /**
     * Get a list of visible groups for the authenticated user using the provided filter.
     *
     * <pre><code>GitLab Endpoint: GET /groups</code></pre>
     *
     * @param filter the GroupFilter to match against
     * @return a List&lt;Group&gt; of the matching groups
     * @throws GitLabApiException if any exception occurs
     */
    public List<Group> getGroups(GroupFilter filter) throws GitLabApiException {
        return (getGroups(filter, getDefaultPerPage()).all());
    }

    /**
     * Get a Pager of visible groups for the authenticated user using the provided filter.
     *
     * <pre><code>GitLab Endpoint: GET /groups</code></pre>
     *
     * @param filter       the GroupFilter to match against
     * @param itemsPerPage the number of Group instances that will be fetched per page
     * @return a Pager containing matching Group instances
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Group> getGroups(GroupFilter filter, int itemsPerPage) throws GitLabApiException {
        GitLabApiForm formData = filter.getQueryParams();
        return (new Pager<Group>(this, Group.class, itemsPerPage, formData.asMap(), "groups"));
    }

    /**
     * Get a Pager of visible groups for the authenticated user using the provided filter.
     *
     * <pre><code>GitLab Endpoint: GET /groups</code></pre>
     *
     * @param filter the GroupFilter to match against
     * @return a Pager containing matching Group instances
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Group> getGroups(GroupFilter filter, int page, int size) throws GitLabApiException {
        GitLabApiForm formData = filter.getQueryParams();
        return (new Pager<Group>(this, Group.class, page, size, formData.asMap(), "groups"));
    }

    /**
     * Get a list of projects belonging to the specified group ID.
     * <p>
     * GET /groups/:id/projects
     *
     * @param groupId the group ID to list the projects for
     * @return a list of projects belonging to the specified group ID
     * @throws GitLabApiException if any exception occurs
     */
    public List<Project> getProjects(int groupId) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "groups", groupId, "projects");
        return (response.readEntity(new GenericType<List<Project>>() {
        }));
    }

    /**
     * Get a list of projects belonging to the specified group ID in the specified page range.
     * <p>
     * GET /groups/:id/projects
     *
     * @param groupId the group ID to list the projects for
     * @param page    the page to get
     * @param perPage the number of Project instances per page
     * @return a list of projects belonging to the specified group ID in the specified page range
     * @throws GitLabApiException if any exception occurs
     */
    public List<Project> getProjects(int groupId, int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage), "groups", groupId, "projects");
        return (response.readEntity(new GenericType<List<Project>>() {
        }));
    }

    /**
     * Get a Pager of projects belonging to the specified group ID and filter.
     *
     * <pre><code>GitLab Endpoint: GET /groups/:id/projects</code></pre>
     *
     * @param groupId the group ID, path of the group, or a Group instance holding the group ID or path
     * @param filter  the GroupProjectsFilter instance holding the filter values for the query
     * @return a Pager containing Project instances that belong to the group and match the provided filter
     * @throws GitLabApiException if any exception occurs
     */
    public List<Project> getProjects(int groupId, GroupProjectsFilter filter) throws GitLabApiException {
        GitLabApiForm formData = filter.getQueryParams();

        Response response = get(Response.Status.OK, formData.asMap(), "groups", groupId, "projects");
        return (response.readEntity(new GenericType<List<Project>>() {
        }));
    }

    /**
     * Get a Pager of projects belonging to the specified group ID.
     * <p>
     * GET /groups/:id/projects
     *
     * @param groupId      the group ID to list the projects for
     * @param itemsPerPage the number of Project instances that will be fetched per page
     * @return a Pager of projects belonging to the specified group ID
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Project> getProjects(int groupId, int itemsPerPage) throws GitLabApiException {
        return (new Pager<Project>(this, Project.class, itemsPerPage, null, "groups", groupId, "projects"));
    }

    /**
     * Get all details of a group.
     * <p>
     * GET /groups/:id
     *
     * @param groupId the group ID to get
     * @return the Group instance for the specified group ID
     * @throws GitLabApiException if any exception occurs
     */
    public Group getGroup(Integer groupId) throws GitLabApiException {
        return getGroup(groupId.toString());
    }

    /**
     * Get all details of a group.
     * <p>
     * GET /groups/:id
     *
     * @param groupPath the path of the group to get details for
     * @return the Group instance for the specified group path
     * @throws GitLabApiException if any exception occurs
     */
    public Group getGroup(String groupPath) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, "groups", urlEncode(groupPath));
        return (response.readEntity(Group.class));
    }

    public List<Group> getGroup(String groupPath, Boolean statistics) throws GitLabApiException {
        Form formData = new GitLabApiForm()
                .withParam("search", groupPath)
                .withParam("statistics", statistics);
        Response response = get(Response.Status.OK, formData.asMap(),"groups");
        return (response.readEntity(new GenericType<List<Group>>() {
        }));
    }

    /**
     * Creates a new project group. Available only for users who can create groups.
     * <p>
     * POST /groups
     *
     * @param name the name of the group to add
     * @param path the path for the group
     * @throws GitLabApiException if any exception occurs
     */
    public void addGroup(String name, String path) throws GitLabApiException {

        Form formData = new Form();
        formData.param("name", name);
        formData.param("path", path);
        post(Response.Status.CREATED, formData, "groups");
    }

    /**
     * Creates a new project group. Available only for users who can create groups.
     * <p>
     * POST /groups
     *
     * @param name                      the name of the group to add
     * @param path                      the path for the group
     * @param description               (optional) - The group's description
     * @param membershipLock            (optional, boolean) - Prevent adding new members to project membership within this group
     * @param shareWithGroupLock        (optional, boolean) - Prevent sharing a project with another group within this group
     * @param visibility                (optional) - The group's visibility. Can be private, internal, or public.
     * @param lfsEnabled                (optional) - Enable/disable Large File Storage (LFS) for the projects in this group
     * @param requestAccessEnabled      (optional) - Allow users to request member access.
     * @param parentId                  (optional) - The parent group id for creating nested group.
     * @param sharedRunnersMinutesLimit (optional) - (admin-only) Pipeline minutes quota for this group
     * @throws GitLabApiException if any exception occurs
     */
    public void addGroup(String name, String path, String description, Boolean membershipLock,
                         Boolean shareWithGroupLock, Visibility visibility, Boolean lfsEnabled, Boolean requestAccessEnabled,
                         Integer parentId, Integer sharedRunnersMinutesLimit) throws GitLabApiException {

        Form formData = new GitLabApiForm()
                .withParam("name", name)
                .withParam("path", path)
                .withParam("description", description)
                .withParam("membership_lock", membershipLock)
                .withParam("share_with_group_lock", shareWithGroupLock)
                .withParam("visibility", visibility)
                .withParam("lfs_enabled", lfsEnabled)
                .withParam("request_access_enabled", requestAccessEnabled)
                .withParam("parent_id", parentId)
                .withParam("shared_runners_minutes_limit", sharedRunnersMinutesLimit);
        post(Response.Status.CREATED, formData, "groups");
    }

    /**
     * Creates a new project group. Available only for users who can create groups.
     * <p>
     * PUT /groups
     *
     * @param groupId                   the ID of the group to update
     * @param name                      the name of the group to add
     * @param path                      the path for the group
     * @param description               (optional) - The group's description
     * @param membershipLock            (optional, boolean) - Prevent adding new members to project membership within this group
     * @param shareWithGroupLock        (optional, boolean) - Prevent sharing a project with another group within this group
     * @param visibility                (optional) - The group's visibility. Can be private, internal, or public.
     * @param lfsEnabled                (optional) - Enable/disable Large File Storage (LFS) for the projects in this group
     * @param requestAccessEnabled      (optional) - Allow users to request member access.
     * @param parentId                  (optional) - The parent group id for creating nested group.
     * @param sharedRunnersMinutesLimit (optional) - (admin-only) Pipeline minutes quota for this group
     * @return the updated Group instance
     * @throws GitLabApiException if any exception occurs
     */
    public Group updateGroup(Integer groupId, String name, String path, String description, Boolean membershipLock,
                             Boolean shareWithGroupLock, Visibility visibility, Boolean lfsEnabled, Boolean requestAccessEnabled,
                             Integer parentId, Integer sharedRunnersMinutesLimit) throws GitLabApiException {

        Form formData = new GitLabApiForm()
                .withParam("name", name)
                .withParam("path", path)
                .withParam("description", description)
                .withParam("membership_lock", membershipLock)
                .withParam("share_with_group_lock", shareWithGroupLock)
                .withParam("visibility", visibility)
                .withParam("lfs_enabled", lfsEnabled)
                .withParam("request_access_enabled", requestAccessEnabled)
                .withParam("parent_id", parentId)
                .withParam("shared_runners_minutes_limit", sharedRunnersMinutesLimit);

        Response response = put(Response.Status.OK, formData.asMap(), "groups", groupId);
        return (response.readEntity(Group.class));
    }

    /**
     * Removes group with all projects inside.
     * <p>
     * DELETE /groups/:id
     *
     * @param groupId the group ID to delete
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteGroup(Integer groupId) throws GitLabApiException {

        if (groupId == null) {
            throw new RuntimeException("groupId cannot be null");
        }

        Response.Status expectedStatus = (isApiVersion(ApiVersion.V3) ? Response.Status.OK : Response.Status.NO_CONTENT);
        delete(expectedStatus, null, "groups", groupId);
    }

    /**
     * Removes group with all projects inside.
     * <p>
     * DELETE /groups/:id
     *
     * @param group the Group instance to delete
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteGroup(Group group) throws GitLabApiException {
        deleteGroup(group.getId());
    }

    /**
     * Get a list of group members viewable by the authenticated user.
     * <p>
     * GET /groups/:id/members
     *
     * @param groupId the group ID to list the members for
     * @return a list of group members viewable by the authenticated user
     * @throws GitLabApiException if any exception occurs
     */
    public List<Member> getMembers(int groupId) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "groups", groupId, "members");
        return (response.readEntity(new GenericType<List<Member>>() {
        }));
    }

    /**
     * Get a specific group members, which is owned by the authentication user.
     * <p>
     * GET /groups/:id/members/:user_id
     *
     * @param groupId the group ID to get team member for
     * @param userId  the user ID of the member
     * @return the member specified by the group ID/user ID pair
     * @throws GitLabApiException if any exception occurs
     */
    public Member getMember(int groupId, int userId) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, "groups", groupId, "members", userId);
        return (response.readEntity(Member.class));
    }

    /**
     * Gets a list of group members viewable by the authenticated user, including inherited members
     * through ancestor groups. Returns multiple times the same user (with different member attributes)
     * when the user is a member of the group and of one or more ancestor group.
     *
     * <pre><code>GitLab Endpoint: GET /groups/:id/members/all</code></pre>
     *
     * @param groupIdOrPath the group ID, path of the group, or a Group instance holding the group ID or path
     * @param query a query string to search for members
     * @param userIds filter the results on the given user IDs
     * @return the group members viewable by the authenticated user, including inherited members through ancestor groups
     * @throws GitLabApiException if any exception occurs
     */
    public List<Member> getAllMembers(Object groupIdOrPath, String query, List<Long> userIds) throws GitLabApiException {
        return (getAllMembers(groupIdOrPath, query, userIds, getDefaultPerPage()).all());
    }
    /**
     * Gets a Pager of group members viewable by the authenticated user, including inherited members
     * through ancestor groups. Returns multiple times the same user (with different member attributes)
     * when the user is a member of the group and of one or more ancestor group.
     *
     * <pre><code>GitLab Endpoint: GET /groups/:id/members/all</code></pre>
     *
     * @param groupIdOrPath the group ID, path of the group, or a Group instance holding the group ID or path
     * @param query a query string to search for members
     * @param userIds filter the results on the given user IDs
     * @param itemsPerPage the number of Project instances that will be fetched per page
     * @return a Pager of the group members viewable by the authenticated user,
     * including inherited members through ancestor groups
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Member> getAllMembers(Object groupIdOrPath, String query, List<Long> userIds, int itemsPerPage) throws GitLabApiException {
        GitLabApiForm form = new GitLabApiForm().withParam("query", query).withParam("user_ids", userIds);
        return (new Pager<Member>(this, Member.class, itemsPerPage, form.asMap(),
                "groups", getGroupIdOrPath(groupIdOrPath), "members", "all"));
    }

    /**
     * Gets a Pager of group members viewable by the authenticated user, including inherited members
     * through ancestor groups. Returns multiple times the same user (with different member attributes)
     * when the user is a member of the group and of one or more ancestor group.
     *
     * <pre><code>GitLab Endpoint: GET /groups/:id/members/all</code></pre>
     *
     * @param groupIdOrPath the group ID, path of the group, or a Group instance holding the group ID or path
     * @param query a query string to search for members
     * @param userIds filter the results on the given user IDs
     * @param itemsPerPage the number of Project instances that will be fetched per page
     * @return a Pager of the group members viewable by the authenticated user,
     * including inherited members through ancestor groups
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Member> getAllMembers(Object groupIdOrPath, String query, List<Long> userIds, int page, int size) throws GitLabApiException {
        GitLabApiForm form = new GitLabApiForm().withParam("query", query).withParam("user_ids", userIds);
        return (new Pager<Member>(this, Member.class, page, size, form.asMap(),
                "groups", getGroupIdOrPath(groupIdOrPath), "members", "all"));
    }

    /**
     * Adds a user to the list of group members.
     * <p>
     * POST /groups/:id/members
     *
     * @param groupId     the project ID to add the member to
     * @param userId      the user ID of the member to add
     * @param accessLevel the access level for the new member
     * @param expires_at  a date string in the format YEAR-MONTH-DAY
     * @return a Member instance for the added user
     * @throws GitLabApiException if any exception occurs
     */
    public Member addMember(Integer groupId, Integer userId, Integer accessLevel, String expires_at) throws GitLabApiException {
        return (addMember(groupId, userId, accessLevel, strToDateLong(expires_at)));
    }

    /**
     * Adds a user to the list of group members.
     *
     * <pre><code>GitLab Endpoint: POST /groups/:id/members</code></pre>
     *
     * @param groupIdOrPath the group ID, path of the group, or a Group instance holding the group ID or path, required
     * @param userId        the user ID of the member to add, required
     * @param accessLevel   the access level for the new member, required
     * @param expiresAt     the date the membership in the group will expire, optional
     * @return a Member instance for the added user
     * @throws GitLabApiException if any exception occurs
     */
    public Member addMember(Object groupIdOrPath, Integer userId, Integer accessLevel, Date expiresAt) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm()
                .withParam("user_id", userId, true)
                .withParam("access_level", accessLevel, true)
                .withParam("expires_at", expiresAt, false);
        Response response = post(Response.Status.CREATED, formData, "groups", getGroupIdOrPath(groupIdOrPath), "members");
        return (response.readEntity(Member.class));
    }


    /**
     * update a specific group members, which is owned by the authentication user.
     * <p>
     * PUT /groups/:id/members/:user_id
     *
     * @param groupId      the ID of the group to update
     * @param userId       the user ID of the member to update
     * @param access_level a valid access level
     * @param expires_at   a date string in the format YEAR-MONTH-DAY
     * @return a Member instance with the newly updated group members info
     * @throws GitLabApiException if any exception occurs
     */
    public Member updateMember(int groupId, int userId, int access_level, String expires_at) throws GitLabApiException {
        return (updateMember(groupId, userId, access_level, strToDateLong(expires_at)));
    }

    /**
     * Updates a member of a group.
     *
     * <pre><code>GitLab Endpoint: PUT /groups/:groupId/members/:userId</code></pre>
     *
     * @param groupIdOrPath the group ID, path of the group, or a Group instance holding the group ID or path, required
     * @param userId        the user ID of the member to update, required
     * @param accessLevel   the new access level for the member, required
     * @param expiresAt     the date the membership in the group will expire, optional
     * @return the updated member
     * @throws GitLabApiException if any exception occurs
     */
    public Member updateMember(Object groupIdOrPath, Integer userId, Integer accessLevel, Date expiresAt) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm()
                .withParam("access_level", accessLevel, true)
                .withParam("expires_at", expiresAt, false);
        Response response = put(Response.Status.OK, formData.asMap(), "groups", getGroupIdOrPath(groupIdOrPath), "members", userId);
        return (response.readEntity(Member.class));
    }

    public Member updateMember(Object groupIdOrPath, Integer userId, Integer accessLevel) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm()
                .withParam("access_level", accessLevel, true)
                .withParam("expires_at", "", false);
        Response response = put(Response.Status.OK, formData.asMap(), "groups", getGroupIdOrPath(groupIdOrPath), "members", userId);
        return (response.readEntity(Member.class));
    }

    /**
     * Get a list of group members viewable by the authenticated user in the specified page range.
     * <p>
     * GET /groups/:id/members
     *
     * @param groupId the group ID to list the members for
     * @param page    the page to get
     * @param perPage the number of Member instances per page
     * @return a list of group members viewable by the authenticated user in the specified page range
     * @throws GitLabApiException if any exception occurs
     */
    public List<Member> getMembers(int groupId, int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage), "groups", groupId, "members");
        return (response.readEntity(new GenericType<List<Member>>() {
        }));
    }

    /**
     * Get a Pager of group members viewable by the authenticated user.
     * <p>
     * GET /groups/:id/members
     *
     * @param groupId      the group ID to list the members for
     * @param itemsPerPage the number of Member instances that will be fetched per page
     * @return a list of group members viewable by the authenticated user
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Member> getMembers(int groupId, int itemsPerPage) throws GitLabApiException {
        return (new Pager<Member>(this, Member.class, itemsPerPage, null, "groups", groupId, "members"));
    }

    /**
     * Adds a user to the list of group members.
     * <p>
     * POST /groups/:id/members
     *
     * @param groupId     the project ID to add the member to
     * @param userId      the user ID of the member to add
     * @param accessLevel the access level for the new member
     * @return a Member instance for the added user
     * @throws GitLabApiException if any exception occurs
     */
    public Member addMember(Integer groupId, Integer userId, Integer accessLevel) throws GitLabApiException {

        Form formData = new Form();
        formData.param("user_id", userId.toString());
        formData.param("access_level", accessLevel.toString());
        Response response = post(Response.Status.CREATED, formData, "groups", groupId, "members");
        return (response.readEntity(Member.class));
    }

    /**
     * Removes member from the group team.
     * <p>
     * DELETE /groups/:id/members/:user_id
     *
     * @param projectId the project ID to remove the member from
     * @param userId    the user ID of the member to remove
     * @throws GitLabApiException if any exception occurs
     */
    public void removeMember(Integer projectId, Integer userId) throws GitLabApiException {
        Response.Status expectedStatus = (isApiVersion(ApiVersion.V3) ? Response.Status.OK : Response.Status.NO_CONTENT);
        delete(expectedStatus, null, "groups", projectId, "members", userId);
    }

    /**
     * Get a List of the group access requests viewable by the authenticated user.
     *
     * <pre><code>GET /group/:id/access_requests</code></pre>
     *
     * @param groupIdOrPath the group ID, path of the group, or a Group instance holding the group ID or path
     * @return a List of project AccessRequest instances accessible by the authenticated user
     * @throws GitLabApiException if any exception occurs
     */
    public List<AccessRequest> getAccessRequests(Object groupIdOrPath) throws GitLabApiException {
        return (getAccessRequests(groupIdOrPath, getDefaultPerPage()).all());
    }

    /**
     * Get a Pager of the group access requests viewable by the authenticated user.
     *
     * <pre><code>GET /groups/:id/access_requests</code></pre>
     *
     * @param groupIdOrPath the group ID, path of the group, or a Group instance holding the group ID or path
     * @param itemsPerPage  the number of AccessRequest instances that will be fetched per page
     * @return a Pager of group AccessRequest instances accessible by the authenticated user
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<AccessRequest> getAccessRequests(Object groupIdOrPath, int itemsPerPage) throws GitLabApiException {
        return (new Pager<>(this, AccessRequest.class, itemsPerPage, null,
                "groups", getGroupIdOrPath(groupIdOrPath), "access_requests"));
    }

    /**
     * Returns the group ID or path from the provided Integer, String, or Group instance.
     *
     * @param obj the object to determine the ID or path from
     * @return the group ID or path from the provided Integer, String, or Group instance
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Object getGroupIdOrPath(Object obj) throws GitLabApiException {

        if (obj == null) {
            throw (new RuntimeException("Cannot determine ID or path from null object"));
        } else if (obj instanceof Integer) {
            return (obj);
        } else if (obj instanceof String) {
            return (urlEncode(((String) obj).trim()));
        } else if (obj instanceof Group) {

            Integer id = ((Group) obj).getId();
            if (id != null && id.intValue() > 0) {
                return (id);
            }

            String path = ((Group) obj).getFullPath();
            if (path != null && path.trim().length() > 0) {
                return (urlEncode(path.trim()));
            }

            throw (new RuntimeException("Cannot determine ID or path from provided Group instance"));

        } else {
            throw (new RuntimeException("Cannot determine ID or path from provided " + obj.getClass().getSimpleName() +
                    " instance, must be Integer, String, or a Group instance"));
        }
    }

    /**
     * Deny access for the specified user to the specified group.
     *
     * <pre><code>DELETE /groups/:id/access_requests/:user_id</code></pre>
     *
     * @param groupIdOrPath the group ID, path of the group, or a Group instance holding the group ID or path
     * @param userId        the user ID to deny access for
     * @throws GitLabApiException if any exception occurs
     */
    public void denyAccessRequest(Object groupIdOrPath, Integer userId) throws GitLabApiException {
        delete(Response.Status.NO_CONTENT, null,
                "groups", getGroupIdOrPath(groupIdOrPath), "access_requests", userId);
    }

    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        return formatter.parse(strDate, pos);
    }

    /**
     * Get list of a group’s variables.
     *
     * <pre><code>GitLab Endpoint: GET /groups/:id/variables</code></pre>
     *
     * @param groupIdOrPath the group ID, path of the group, or a Group instance holding the group ID or path
     * @return a list of variables belonging to the specified group
     * @throws GitLabApiException if any exception occurs
     */
    public List<Variable> getVariables(Object groupIdOrPath) throws GitLabApiException {
        return (getVariables(groupIdOrPath, getDefaultPerPage()).all());
    }


    /**
     * Get a Pager of variables belonging to the specified group.
     *
     * <pre><code>GitLab Endpoint: GET /groups/:id/variables</code></pre>
     *
     * @param groupIdOrPath the group ID, path of the group, or a Group instance holding the group ID or path
     * @param itemsPerPage  the number of Variable instances that will be fetched per page
     * @return a Pager of variables belonging to the specified group
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Variable> getVariables(Object groupIdOrPath, int itemsPerPage) throws GitLabApiException {
        return (new Pager<Variable>(this, Variable.class, itemsPerPage, null, "groups", getGroupIdOrPath(groupIdOrPath), "variables"));
    }

    /**
     * Create a new group variable.
     *
     * <pre><code>GitLab Endpoint: POST /groups/:id/variables</code></pre>
     *
     * @param groupIdOrPath the group ID, path of the group, or a Group instance holding the group ID or path, required
     * @param key           the key of a variable; must have no more than 255 characters; only A-Z, a-z, 0-9, and _ are allowed, required
     * @param value         the value for the variable, required
     * @param isProtected   whether the variable is protected, optional
     * @return a Variable instance with the newly created variable
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Variable createVariable(Object groupIdOrPath, String key, String value, Boolean isProtected) throws GitLabApiException {

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("key", key, true)
                .withParam("value", value, false)
                .withParam("protected", isProtected);
        Response response = post(Response.Status.CREATED, formData, "groups", getGroupIdOrPath(groupIdOrPath), "variables");
        return (response.readEntity(Variable.class));
    }

    /**
     * Update a group variable.
     *
     * <pre><code>GitLab Endpoint: PUT /groups/:id/variables/:key</code></pre>
     *
     * @param groupIdOrPath the group ID, path of the group, or a Group instance holding the group ID or path, required
     * @param key           the key of an existing variable, required
     * @param value         the value for the variable, required
     * @param isProtected   whether the variable is protected, optional
     * @return a Variable instance with the updated variable
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Variable updateVariable(Object groupIdOrPath, String key, String value, Boolean isProtected) throws GitLabApiException {

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("value", value, false)
                .withParam("protected", isProtected);
        Response response = putWithFormData(Response.Status.OK, formData, "groups", getGroupIdOrPath(groupIdOrPath), "variables", key);
        return (response.readEntity(Variable.class));
    }

    /**
     * Deletes a group variable.
     *
     * <pre><code>GitLab Endpoint: DELETE /groups/:id/variables/:key</code></pre>
     *
     * @param groupIdOrPath the group ID, path of the group, or a Group instance holding the group ID or path, required
     * @param key           the key of an existing variable, required
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteVariable(Object groupIdOrPath, String key) throws GitLabApiException {
        delete(Response.Status.NO_CONTENT, null, "groups", getGroupIdOrPath(groupIdOrPath), "variables", key);
    }
}
