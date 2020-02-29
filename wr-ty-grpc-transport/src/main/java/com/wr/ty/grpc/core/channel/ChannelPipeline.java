package com.wr.ty.grpc.core.channel;

import reactor.core.publisher.Flux;

/**
 * @author xiaohei
 * @date 2020/2/12 13:56
 */
public class ChannelPipeline {

    private final String pipelineId;
    private final ChannelHandler[] handlers;
    private final ChannelContext[] contexts;

    public ChannelPipeline(String pipelineId, ChannelHandler... handlers) {
        this.pipelineId = pipelineId;
        this.handlers = handlers;
        this.contexts = initializeContexts();
    }

    public String getPipelineId() {
        return pipelineId;
    }

    public ChannelHandler getFirst() {
        return handlers[0];
    }

    public Flux<Void> lifecycle() {
        return null;
    }

    private ChannelContext[] initializeContexts() {
        ChannelContext[] contexts = new ChannelContext[handlers.length];
        for (int i = 0; i < handlers.length; i++) {
            contexts[i] = new ChannelContext(this, i + 1 < handlers.length ? handlers[i + 1] : null);
        }
        // Do not merge these two loops into one. Channel initialization may require access to next handler
        // in pipeline
        for (int i = 0; i < handlers.length; i++) {
            handlers[i].init(contexts[i]);
        }
        return contexts;
    }
}
