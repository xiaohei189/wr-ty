package com.wr.ty.grpc.core.channel;

import com.xh.demo.grpc.WrTy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author xiaohei
 * @date 2020/3/2 23:18
 */
public class DefaultChannelPipeline implements ChannelPipeline {
    private final int index;

    private final List<ChannelHandler> handlers;
    private final String pipelineId;

    public DefaultChannelPipeline(String pipelineId, int index, List<ChannelHandler> handlers) {
        this.pipelineId = pipelineId;
        this.index = index;
        this.handlers = handlers;
    }

    public DefaultChannelPipeline(DefaultChannelPipeline parent, int index) {
        this(parent.pipelineId(), index, parent.getHandlers());
    }

    @Override
    public String pipelineId() {
        return pipelineId;
    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream) {
        return Flux.defer(() -> {
            if (this.index < handlers.size()) {
                ChannelHandler handler = handlers.get(this.index);
                DefaultChannelPipeline pipeline = new DefaultChannelPipeline(this,
                        this.index + 1);
                return handler.handle(inputStream, pipeline);
            } else {
                return Mono.empty();
            }
        });
    }

    public List<ChannelHandler> getHandlers() {
        return handlers;
    }
}
