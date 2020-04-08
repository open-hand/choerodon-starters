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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.gitlab4j.api.models.ErrorMessage;

/**
 * This is the exception that will be thrown if any exception occurs while communicating
 * with a GitLab API endpoint.
 */
public class GitLabApiException extends Exception {

    private static final long serialVersionUID = 1L;
    private StatusType statusInfo;
    private int httpStatus;
    private String message;

    /**
     * Create a GitLabApiException instance with the specified message.
     *
     * @param message the message for the exception
     */
    public GitLabApiException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * Create a GitLabApiException instance based on the ClientResponse.
     *
     * @param response the JAX-RS response that caused the exception
     */
    public GitLabApiException(Response response) {

        super();
        statusInfo = response.getStatusInfo();
        httpStatus = response.getStatus();

        if (response.hasEntity()) {
            try {

                ErrorMessage errorMessage = response.readEntity(ErrorMessage.class);
                message = errorMessage.getMessage();

            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Create a GitLabApiException instance based on the exception.
     *
     * @param e the Exception to wrap
     */
    public GitLabApiException(Exception e) {
        super(e);
        message = e.getMessage();
    }

    /**
     * Get the message associated with the exception.
     *
     * @return the message associated with the exception
     */
    @Override
    public final String getMessage() {
        return (message != null ? message : getReason());
    }

    /**
     * Returns the HTTP status reason message, returns null if the
     * causing error was not an HTTP related exception.
     *
     * @return the HTTP status reason message
     */
    public final String getReason() {
        return (statusInfo != null ? statusInfo.getReasonPhrase() : null);
    }

    /**
     * Returns the HTTP status code that was the cause of the exception. returns 0 if the
     * causing error was not an HTTP related exception.
     *
     * @return the HTTP status code, returns 0 if the causing error was not an HTTP related exception
     */
    public final int getHttpStatus() {
        return (httpStatus);
    }
}
