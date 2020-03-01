package com.wr.ty.grpc.core;

import reactor.core.publisher.Mono;

/**
 * @author xiaohei
 * @date 2020/2/20 22:59
 */
public interface ServerResolver {
    Mono<Server> resolve();
}
