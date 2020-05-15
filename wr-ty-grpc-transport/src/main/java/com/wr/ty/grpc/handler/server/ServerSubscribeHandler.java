package com.wr.ty.grpc.handler.server;

import com.wr.ty.grpc.RegistrySubscriber;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.api.Registry;
import com.xh.demo.grpc.WrTy;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import static com.xh.demo.grpc.WrTy.ProtocolMessageEnvelope.ItemCase.INTERESTREGISTRATION;

/**
 * @author xiaohei
 * @date 2020/3/1 14:57
 */
public class ServerSubscribeHandler implements ChannelHandler {

    private final Registry registry;

    private RegistrySubscriber subscriber;

    public ServerSubscribeHandler(Registry registry) {
        this.registry = registry;
    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream, ChannelPipeline pipeline) {
        EmitterProcessor response = EmitterProcessor.create();
        FluxSink fluxSink = response.sink();
        inputStream.filter(value -> value.getItemCase() == INTERESTREGISTRATION).subscribe(value -> {
            subscriber = new RegistrySubscriber(fluxSink);
            registry.subscribe(subscriber, null);
        }, error -> {
            if (subscriber != null) {
                subscriber.dispose();
            }

        }, () -> {
            if (subscriber != null) {
                subscriber.dispose();
            }
        });

        return response;
    }

}
