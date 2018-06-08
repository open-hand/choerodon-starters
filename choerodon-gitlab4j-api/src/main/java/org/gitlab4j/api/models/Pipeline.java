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

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Pipeline {

    private Integer id;
    private PipelineStatus status;
    private String ref;
    private String sha;
    private String beforeSha;
    private Boolean tag;
    private String yamlErrors;
    private User user;
    private Date createdAt;
    private Date updated_at;
    private Date started_at;
    private Date finished_at;
    private Date committed_at;
    private String coverage;
    private Integer duration;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PipelineStatus getStatus() {
        return status;
    }

    public void setStatus(PipelineStatus status) {
        this.status = status;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getBeforeSha() {
        return beforeSha;
    }

    public void setBeforeSha(String beforeSha) {
        this.beforeSha = beforeSha;
    }

    public Boolean getTag() {
        return tag;
    }

    public void setTag(Boolean tag) {
        this.tag = tag;
    }

    public String getYamlErrors() {
        return yamlErrors;
    }

    public void setYamlErrors(String yamlErrors) {
        this.yamlErrors = yamlErrors;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public Date getStarted_at() {
        return started_at;
    }

    public void setStarted_at(Date started_at) {
        this.started_at = started_at;
    }

    public Date getFinished_at() {
        return finished_at;
    }

    public void setFinished_at(Date finished_at) {
        this.finished_at = finished_at;
    }

    public Date getCommitted_at() {
        return committed_at;
    }

    public void setCommitted_at(Date committed_at) {
        this.committed_at = committed_at;
    }

    public String getCoverage() {
        return coverage;
    }

    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
