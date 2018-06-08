package io.choerodon.websocket.session;

public class SessionListenerFactory{
    private AgentSessionListener agentSessionListener;
    private PipeSessionListener pipeSessionListener;
    private SimpleSessionListener simpleSessionListener;


    public SessionListenerFactory(SimpleSessionListener simpleSessionListener,
                                  AgentSessionListener agentSessionListener,
                                  PipeSessionListener pipeSessionListener) {
        this.agentSessionListener = agentSessionListener;
        this.pipeSessionListener = pipeSessionListener;
        this.simpleSessionListener = simpleSessionListener;
    }

    public SessionListener sessionListener(int type){
        if(type == Session.AGENT){
            return agentSessionListener;
        }else if (type == Session.LOG || type == Session.EXEC){
            return pipeSessionListener;
        }else {
            return simpleSessionListener;
        }
    }
}
