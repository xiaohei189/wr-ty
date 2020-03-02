package com.wr.ty.grpc.core;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author xiaohei
 * @date 2020/2/20 23:03
 */
public class DefaultServerResolver implements ServerResolver {
    @Override
    public Mono<Server> resolve() {
        Server server = new Server();
        server.setHostName("127.0.0.1");
        server.setPort(50051);
        return Mono.just(server);
    }
}
