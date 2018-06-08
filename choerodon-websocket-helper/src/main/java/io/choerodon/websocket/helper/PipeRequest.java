package io.choerodon.websocket.helper;

public class PipeRequest {
    private String podName;
    private String containerName;
    private String pipeID;

    public PipeRequest(String podName, String containerName, String pipeID) {
        this.podName = podName;
        this.containerName = containerName;
        this.pipeID = pipeID;
    }

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getPipeID() {
        return pipeID;
    }

    public void setPipeID(String pipeID) {
        this.pipeID = pipeID;
    }
}
