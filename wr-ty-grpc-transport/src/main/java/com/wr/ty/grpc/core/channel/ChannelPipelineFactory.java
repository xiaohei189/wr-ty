package com.wr.ty.grpc.core.channel;

import reactor.core.publisher.Flux;

/**
 * @author xiaohei
 * @date 2020/2/12 14:00
 */
public interface ChannelPipelineFactory {
    Flux<ChannelPipeline> createPipeline();
}