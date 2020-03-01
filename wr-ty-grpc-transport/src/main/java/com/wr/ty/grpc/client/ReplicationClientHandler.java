package com.wr.ty.grpc.client;

import com.wr.ty.grpc.core.channel.ChannelContext;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.register.Registry;
import com.xh.demo.grpc.WrTy;
import reactor.core.publisher.Flux;

import static com.xh.demo.grpc.WrTy.ProtocolMessageEnvelope.ItemCase.CHANGENOTIFICATION;

/**
 * @author xiaohei
 * @date 2020/3/1 22:23
 */
public class ReplicationClientHandler implements ChannelHandler {
    private ChannelContext channelContext;
    private final Registry registry;

    public ReplicationClientHandler(Registry registry) {
        this.registry = registry;
    }

    @Override
    public void init(ChannelContext channelContext) {
        this.channelContext = channelContext;
    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream) {
        Flux<WrTy.ProtocolMessageEnvelope> envelopeFlux = channelContext.next().handle(inputStream).doOnNext(value -> {
            WrTy.ProtocolMessageEnvelope.ItemCase itemCase = value.getItemCase();
            if (itemCase == CHANGENOTIFICATION) {
                WrTy.ChangeNotification changeNotification = value.getChangeNotification();
                registry.register(changeNotification);
            }
        });
        return envelopeFlux;
    }
}
