package io.choerodon.websocket.send;

/**
 * Created by hailuo.liu@choerodon.io on 2019-08-23.
 */
public class SendBinaryMessagePayload extends SendMessagePayload<byte[]> {
    public SendBinaryMessagePayload() {
    }

    public SendBinaryMessagePayload(String type, String key, byte[] data) {
        super(type, key, data);
    }
}
