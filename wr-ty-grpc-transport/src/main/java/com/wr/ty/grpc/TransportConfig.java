package com.wr.ty.grpc;

import java.time.Duration;

/**
 * @author xiaohei
 * @date 2020/2/20 23:26
 */
public interface TransportConfig {

    default Duration heartbeatInterval() {
        return Duration.ofMillis(15 * 1000);
    }

    default Duration heartbeatTimeout() {
        return Duration.ofMillis(30 * 1000);
    }

    default Duration autoConnectInterval() {
        return Duration.ofMillis(15 * 1000);
    }

}
