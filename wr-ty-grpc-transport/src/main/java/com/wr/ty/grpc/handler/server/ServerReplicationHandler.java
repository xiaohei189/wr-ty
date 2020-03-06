package com.wr.ty.grpc.handler.server;

import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.xh.demo.grpc.WrTy;
import reactor.core.publisher.Flux;

import static com.wr.ty.grpc.util.ProtocolMessageEnvelopes.createSubscribeAllRegistration;

/**
 * @author xiaohei
 * @date 2020/3/1 15:17
 */
public class ServerReplicationHandler  implements ChannelHandler {

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream, ChannelPipeline pipeline) {

        Flux<WrTy.ProtocolMessageEnvelope> envelopeFlux = inputStream.map(value -> {
            // subscribe all instance
            WrTy.ProtocolMessageEnvelope allRegistration = createSubscribeAllRegistration();
            return allRegistration;
        });

        return pipeline.handle(envelopeFlux);
    }
}
