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

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.gitlab4j.api.models.Session;

/**
 * This class implements the client side API for the GitLab login call.
 */
public class SessionApi extends AbstractApi {

    public SessionApi(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    /**
     * Login to get private token.
     *
     * POST /session
     *
     * @param username the username to login
     * @param email the email address to login
     * @param password the password of the user
     * @return a Session instance with info on the logged in user
     * @throws GitLabApiException if any exception occurs
     */
    public Session login(String username, String email, String password) throws GitLabApiException {

        if ((username == null || username.trim().length() == 0) && (email == null || email.trim().length() == 0)) {
            throw new IllegalArgumentException("both username and email cannot be empty or null");
        }

        Form formData = new Form();
        addFormParam(formData, "email", email, false);
        addFormParam(formData, "password", password, true);
        addFormParam(formData, "login", username, false);

        Response response = post(Response.Status.CREATED, formData, "session");
        return (response.readEntity(Session.class));
    }
}
