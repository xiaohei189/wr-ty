package com.wr.ty.grpc.core;

import reactor.core.publisher.Flux;

/**
 * @author xiaohei
 * @date 2020/2/20 22:59
 */
public interface ServerResolver {
    Flux<Server> resolve();
}
