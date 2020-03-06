package com.wr.ty.grpc.core.channel;

import com.xh.demo.grpc.WrTy;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import reactor.core.publisher.Flux;

/**
 * @author weirui (xiao hei)
 * @date 2019/4/7 17:26
 */
public interface ChannelHandler {
    Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream, ChannelPipeline pipeline);
}
