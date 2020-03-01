package com.wr.ty.grpc.core.channel;

import com.xh.demo.grpc.WrTy;
import reactor.core.publisher.Flux;

/**
 * @author weirui (xiao hei)
 * @date 2019/4/7 17:26
 */
public interface ChannelHandler {

    void init(ChannelContext channelContext);

    Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream);
}
