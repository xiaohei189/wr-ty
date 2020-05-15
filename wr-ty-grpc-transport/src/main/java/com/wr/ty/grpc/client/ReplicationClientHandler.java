package com.wr.ty.grpc.client;

import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.api.Registry;
import com.xh.demo.grpc.WrTy;
import reactor.core.publisher.Flux;

import static com.xh.demo.grpc.WrTy.ProtocolMessageEnvelope.ItemCase.CHANGENOTIFICATION;

/**
 * @author xiaohei
 * @date 2020/3/1 22:23
 */
public class ReplicationClientHandler implements ChannelHandler {
    private final Registry registry;

    public ReplicationClientHandler(Registry registry) {
        this.registry = registry;
    }


    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream, ChannelPipeline pipeline) {
        Flux<WrTy.ProtocolMessageEnvelope> envelopeFlux = pipeline.handle(inputStream).doOnNext(value -> {
            WrTy.ProtocolMessageEnvelope.ItemCase itemCase = value.getItemCase();
            if (itemCase == CHANGENOTIFICATION) {
                WrTy.ChangeNotification changeNotification = value.getChangeNotification();
                registry.register(changeNotification);
            }
        });
        return envelopeFlux;
    }
}
