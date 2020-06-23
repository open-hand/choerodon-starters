
package org.gitlab4j.api.models;

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

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectHook {

    private Boolean buildEvents;
    private Date createdAt;
    private Boolean enableSslVerification;
    private Integer id;
    private Boolean issuesEvents;
    private Boolean mergeRequestsEvents;
    private Boolean noteEvents;
    private Boolean jobEvents;
    private Boolean pipelineEvents;
    private Integer projectId;
    private Boolean pushEvents;
    private Boolean tagPushEvents;
    private String url;
    private Boolean wikiPageEvents;
    private String token;


    public Boolean getBuildEvents() {
        return buildEvents;
    }

    public void setBuildEvents(Boolean buildEvents) {
        this.buildEvents = buildEvents;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getEnableSslVerification() {
        return enableSslVerification;
    }

    public void setEnableSslVerification(Boolean enableSslVerification) {
        this.enableSslVerification = enableSslVerification;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIssuesEvents() {
        return issuesEvents;
    }

    public void setIssuesEvents(Boolean issuesEvents) {
        this.issuesEvents = issuesEvents;
    }

    public Boolean getMergeRequestsEvents() {
        return mergeRequestsEvents;
    }

    public void setMergeRequestsEvents(Boolean mergeRequestsEvents) {
        this.mergeRequestsEvents = mergeRequestsEvents;
    }

    public Boolean getNoteEvents() {
        return noteEvents;
    }

    public void setNoteEvents(Boolean noteEvents) {
        this.noteEvents = noteEvents;
    }

    public Boolean getJobEvents() {
        return jobEvents;
    }

    public void setJobEvents(Boolean jobEvents) {
        this.jobEvents = jobEvents;
    }

    public Boolean getPipelineEvents() {
        return pipelineEvents;
    }

    public void setPipelineEvents(Boolean pipelineEvents) {
        this.pipelineEvents = pipelineEvents;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Boolean getPushEvents() {
        return pushEvents;
    }

    public void setPushEvents(Boolean pushEvents) {
        this.pushEvents = pushEvents;
    }

    public Boolean getTagPushEvents() {
        return tagPushEvents;
    }

    public void setTagPushEvents(Boolean tagPushEvents) {
        this.tagPushEvents = tagPushEvents;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getWikiPageEvents() {
        return wikiPageEvents;
    }

    public void setWikiPageEvents(Boolean wikiPageEvents) {
        this.wikiPageEvents = wikiPageEvents;
    }

    /**
     * @return the do build events flag
     * @deprecated As of release 4.1.0, replaced by {@link #getBuildEvents()}
     */
    @Deprecated
    @JsonIgnore
    public Boolean getBuild_events() {
        return buildEvents;
    }

    /**
     * @param buildEvents the do build events flag
     * @deprecated As of release 4.1.0, replaced by {@link #setBuildEvents(Boolean)}
     */
    @Deprecated
    @JsonIgnore
    public void setBuild_events(Boolean buildEvents) {
        this.buildEvents = buildEvents;
    }

    /**
     * @return the enable SSL verification flag
     * @deprecated As of release 4.1.0, replaced by {@link #getEnableSslVerification()}
     */
    @Deprecated
    @JsonIgnore
    public Boolean getEnable_ssl_verification() {
        return enableSslVerification;
    }

    /**
     * @param enableSslVerification the enable SSL verification flag
     * @deprecated As of release 4.1.0, replaced by {@link #setEnableSslVerification(Boolean)}
     */
    @Deprecated
    @JsonIgnore
    public void setEnable_ssl_verification(Boolean enableSslVerification) {
        this.enableSslVerification = enableSslVerification;
    }

    /**
     * @return the do note events flag
     * @deprecated As of release 4.1.0, replaced by {@link #getNoteEvents()}
     */
    @Deprecated
    @JsonIgnore
    public Boolean getNote_events() {
        return noteEvents;
    }

    /**
     * @param noteEvents the do note events flag
     * @deprecated As of release 4.1.0, replaced by {@link #setNoteEvents(Boolean)}
     */
    @Deprecated
    @JsonIgnore
    public void setNote_events(Boolean noteEvents) {
        this.noteEvents = noteEvents;
    }

    /**
     * @return the do pipeline events flag
     * @deprecated As of release 4.1.0, replaced by {@link #getPipelineEvents()}
     */
    @Deprecated
    @JsonIgnore
    public Boolean getPipeline_events() {
        return pipelineEvents;
    }

    /**
     * @param pipelineEvents the do pipeline events flag
     * @deprecated As of release 4.1.0, replaced by {@link #setPipelineEvents(Boolean)}
     */
    @Deprecated
    @JsonIgnore
    public void setPipeline_events(Boolean pipelineEvents) {
        this.pipelineEvents = pipelineEvents;
    }

    /**
     * @return the do tag push events flag
     * @deprecated As of release 4.1.0, replaced by {@link #getTagPushEvents()}
     */
    @Deprecated
    @JsonIgnore
    public Boolean getTag_push_events() {
        return tagPushEvents;
    }

    /**
     * @param tagPushEvents the do tag push events flag
     * @deprecated As of release 4.1.0, replaced by {@link #setTagPushEvents(Boolean)}
     */
    @Deprecated
    @JsonIgnore
    public void setTag_push_events(Boolean tagPushEvents) {
        this.tagPushEvents = tagPushEvents;
    }

    /**
     * @return the do wiki page events flag
     * @deprecated As of release 4.1.0, replaced by {@link #getWikiPageEvents()}
     */
    @Deprecated
    @JsonIgnore
    public Boolean getWiki_page_events() {
        return wikiPageEvents;
    }

    /**
     * @param wikiPageEvents the do wiki page events flag
     * @deprecated As of release 4.1.0, replaced by {@link #setWikiPageEvents(Boolean)}
     */
    @Deprecated
    @JsonIgnore
    public void setWiki_page_events(Boolean wikiPageEvents) {
        this.wikiPageEvents = wikiPageEvents;
    }
}
