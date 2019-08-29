package io.choerodon.websocket.send;

/**
 * Created by hailuo.liu@choerodon.io on 2019-08-23.
 */
public class SendPlaintextMessagePayload extends SendMessagePayload<String> {
    public SendPlaintextMessagePayload() {
    }

    public SendPlaintextMessagePayload(String data) {
        super("", "", data);
    }
}
