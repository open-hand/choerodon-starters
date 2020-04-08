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

import org.gitlab4j.api.models.Namespace;

/**
 * This class implements the client side API for the GitLab namespace calls.
 */
public class NamespaceApi extends AbstractApi {

    public NamespaceApi(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    /**
     * Get a list of the namespaces of the authenticated user. If the user is an administrator,
     * a list of all namespaces in the GitLab instance is created.
     * <p>
     * GET /namespaces
     *
     * @return a List of Namespace instances
     * @throws GitLabApiException if any exception occurs
     */
    public List<Namespace> getNamespaces() throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "namespaces");
        return (response.readEntity(new GenericType<List<Namespace>>() {
        }));
    }

    /**
     * Get a list of the namespaces of the authenticated user. If the user is an administrator,
     * a list of all namespaces in the GitLab instance is created.
     * <p>
     * GET /namespaces
     *
     * @param page    the page to get
     * @param perPage the number of Namespace instances per page
     * @return a List of Namespace instances in the specified page range
     * @throws GitLabApiException if any exception occurs
     */
    public List<Namespace> getNamespaces(int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage), "namespaces");
        return (response.readEntity(new GenericType<List<Namespace>>() {
        }));
    }

    /**
     * Get a Pager of the namespaces of the authenticated user. If the user is an administrator,
     * a Pager of all namespaces in the GitLab instance is created.
     * <p>
     * GET /namespaces
     *
     * @param itemsPerPage the number of Project instances that will be fetched per page
     * @return a Pager of Namespace instances
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Namespace> getNamespaces(int itemsPerPage) throws GitLabApiException {
        return (new Pager<Namespace>(this, Namespace.class, itemsPerPage, null, "namespaces"));
    }

    /**
     * Get all namespaces that match a string in their name or path.
     * <p>
     * GET /namespaces?search=:query
     *
     * @param query the search string
     * @return the Namespace List with the matching namespaces
     * @throws GitLabApiException if any exception occurs
     */
    public List<Namespace> findNamespaces(String query) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm().withParam("search", query, true).withParam(PER_PAGE_PARAM, getDefaultPerPage());
        Response response = get(Response.Status.OK, formData.asMap(), "namespaces");
        return (response.readEntity(new GenericType<List<Namespace>>() {
        }));
    }

    /**
     * Get all namespaces that match a string in their name or path in the specified page range.
     * <p>
     * GET /namespaces?search=:query
     *
     * @param query   the search string
     * @param page    the page to get
     * @param perPage the number of Namespace instances per page
     * @return the Namespace List with the matching namespaces
     * @throws GitLabApiException if any exception occurs
     */
    public List<Namespace> findNamespaces(String query, int page, int perPage) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm().withParam("search", query, true).withParam(PAGE_PARAM, page).withParam(PER_PAGE_PARAM, perPage);
        Response response = get(Response.Status.OK, formData.asMap(), "namespaces");
        return (response.readEntity(new GenericType<List<Namespace>>() {
        }));
    }

    /**
     * Get a Pager of all namespaces that match a string in their name or path.
     * <p>
     * GET /namespaces?search=:query
     *
     * @param query        the search string
     * @param itemsPerPage the number of Project instances that will be fetched per page
     * @return a Pager of Namespace instances with the matching namespaces
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Namespace> findNamespaces(String query, int itemsPerPage) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm().withParam("search", query, true);
        return (new Pager<Namespace>(this, Namespace.class, itemsPerPage, formData.asMap(), "namespaces"));
    }
}
