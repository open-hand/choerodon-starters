package org.gitlab4j.api.models;

/**
 * Created by wangxiang on 2021/11/9
 */
public class GroupStatistics {

    long storageSize;
    long repositorySize;
    long wikiSize;
    long lfsObjectsSize;
    long jobArtifactsSize;
    long packagesSize;
    long snippetsSize;

    public long getStorageSize() {
        return storageSize;
    }

    public void setStorageSize(long storageSize) {
        this.storageSize = storageSize;
    }

    public long getRepositorySize() {
        return repositorySize;
    }

    public void setRepositorySize(long repositorySize) {
        this.repositorySize = repositorySize;
    }

    public long getWikiSize() {
        return wikiSize;
    }

    public void setWikiSize(long wikiSize) {
        this.wikiSize = wikiSize;
    }

    public long getLfsObjectsSize() {
        return lfsObjectsSize;
    }

    public void setLfsObjectsSize(long lfsObjectsSize) {
        this.lfsObjectsSize = lfsObjectsSize;
    }

    public long getJobArtifactsSize() {
        return jobArtifactsSize;
    }

    public void setJobArtifactsSize(long jobArtifactsSize) {
        this.jobArtifactsSize = jobArtifactsSize;
    }

    public long getPackagesSize() {
        return packagesSize;
    }

    public void setPackagesSize(long packagesSize) {
        this.packagesSize = packagesSize;
    }

    public long getSnippetsSize() {
        return snippetsSize;
    }

    public void setSnippetsSize(long snippetsSize) {
        this.snippetsSize = snippetsSize;
    }
}
