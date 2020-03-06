package com.wr.ty.grpc.transport;

import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.xh.demo.grpc.WrTy;
import reactor.core.publisher.Flux;

/**
 * @author xiaohei
 * @date 2020/2/19 17:58
 */
public class ChannelHandlerStub implements ChannelHandler {



    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream, ChannelPipeline pipeline) {
        return inputStream;
    }
}