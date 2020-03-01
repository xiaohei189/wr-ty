package com.wr.ty.grpc.handler.server;

import com.wr.ty.grpc.core.channel.ChannelContext;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.xh.demo.grpc.WrTy;
import reactor.core.publisher.Flux;

import static com.wr.ty.grpc.util.ProtocolMessageEnvelopes.createSubscribeAllRegistration;

/**
 * @author xiaohei
 * @date 2020/3/1 15:17
 */
public class ServerReplicationHandler implements ChannelHandler {
    private ChannelContext channelContext;

    @Override
    public void init(ChannelContext channelContext) {
        this.channelContext=channelContext;
    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream) {

        Flux<WrTy.ProtocolMessageEnvelope> envelopeFlux = inputStream.map(value -> {
            // subscribe all instance
            WrTy.ProtocolMessageEnvelope allRegistration = createSubscribeAllRegistration();
            return allRegistration;
        });

        return channelContext.next().handle(envelopeFlux);
    }
}
