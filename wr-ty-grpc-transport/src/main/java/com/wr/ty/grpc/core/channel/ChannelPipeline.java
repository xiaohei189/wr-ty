package com.wr.ty.grpc.core.channel;

import com.xh.demo.grpc.WrTy;
import reactor.core.publisher.Flux;

/**
 * @author xiaohei
 * @date 2020/2/12 13:56
 */
public interface ChannelPipeline {

    String pipelineId();

    Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream);

}
