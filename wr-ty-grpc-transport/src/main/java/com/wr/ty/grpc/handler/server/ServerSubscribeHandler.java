package com.wr.ty.grpc.handler.server;

import com.wr.ty.grpc.core.channel.ChannelContext;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.register.Registry;
import com.xh.demo.grpc.WrTy;
import reactor.core.publisher.BaseSubscriber;
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

    private SwitchSubscriber subscriber;

    public ServerSubscribeHandler(Registry registry) {
        this.registry = registry;
    }

    @Override
    public void init(ChannelContext channelContext) {
        if (channelContext.hasNext()) {
            throw new RuntimeException("ServerSubscribeHandler must be at the end");
        }
    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream) {
        EmitterProcessor response = EmitterProcessor.create();
        FluxSink fluxSink = response.sink();
        inputStream.filter(value -> value.getItemCase() == INTERESTREGISTRATION).subscribe(value -> {
            Flux<WrTy.ChangeNotification> notificationFlux = registry.subscribe(null);
            subscriber = new SwitchSubscriber(fluxSink);
            notificationFlux.subscribe(subscriber);
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

    static class SwitchSubscriber extends BaseSubscriber<WrTy.ChangeNotification> {

        private final FluxSink<WrTy.ProtocolMessageEnvelope> fluxSink;

        SwitchSubscriber(FluxSink fluxSink) {
            this.fluxSink = fluxSink;
        }

        @Override
        protected void hookOnNext(WrTy.ChangeNotification value) {
            WrTy.ProtocolMessageEnvelope messageEnvelope = WrTy.ProtocolMessageEnvelope.newBuilder().setChangeNotification(value).build();
            fluxSink.next(messageEnvelope);
        }

        @Override
        protected void hookOnComplete() {
            fluxSink.complete();
        }

        @Override
        protected void hookOnError(Throwable throwable) {
            fluxSink.error(throwable);
        }
    }
}
