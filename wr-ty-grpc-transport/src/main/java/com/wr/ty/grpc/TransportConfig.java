package com.wr.ty.grpc;

import java.time.Duration;

/**
 * @author xiaohei
 * @date 2020/2/20 23:26
 */
public interface TransportConfig {

    default Duration heartbeatInterval() {
        return Duration.ofSeconds(15);
    }

    default Duration heartbeatTimeout() {
        return Duration.ofSeconds(30);
    }

    default Duration autoConnectInterval() {
        return Duration.ofSeconds(15);
    }

}
