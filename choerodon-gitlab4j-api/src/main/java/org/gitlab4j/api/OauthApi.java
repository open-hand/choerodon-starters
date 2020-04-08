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

import org.gitlab4j.api.models.AccessToken;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URL;

public class OauthApi extends AbstractApi {
    public OauthApi(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    public AccessToken oauthLogin(String hostUrl, String username, String email, String password) throws GitLabApiException, IOException {
        if ((username == null || username.trim().length() == 0) && (email == null || email.trim().length() == 0)) {
            throw new IllegalArgumentException("both username and email cannot be empty or null");
        }
        Form formData = new Form();
        addFormParam(formData, "email", email, false);
        addFormParam(formData, "password", password, true);
        addFormParam(formData, "username", username, false);
        addFormParam(formData, "grant_type", "password");
        StringBuilder url = new StringBuilder();
        url.append(hostUrl.endsWith("/") ? hostUrl.replaceAll("/$", "") : hostUrl);
        url.append("/oauth/token");
        Response response = post(Response.Status.OK, formData, new URL(url.toString()));
        return (response.readEntity(AccessToken.class));
    }
}
