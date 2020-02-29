package com.wr.ty.grpc.core.channel;

/**
 * @author xiaohei
 * @date 2020/2/12 13:56
 */
public class ChannelContext {

    private final ChannelPipeline pipeline;
    private final ChannelHandler next;

    public ChannelContext(ChannelPipeline pipeline, ChannelHandler next) {
        this.pipeline = pipeline;
        this.next = next;
    }

    public ChannelPipeline getPipeline() {
        return pipeline;
    }

    public boolean hasNext() {
        return next != null;
    }

    public ChannelHandler next() {
        return next;
    }
}
