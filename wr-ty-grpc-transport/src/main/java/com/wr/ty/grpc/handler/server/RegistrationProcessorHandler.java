

package com.wr.ty.grpc.handler.server;


import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.grpc.register.Registry;
import com.wr.ty.grpc.util.ChangeNotifications;
import com.xh.demo.grpc.WrTy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


public class RegistrationProcessorHandler implements ChannelHandler {

    private Logger logger = LoggerFactory.getLogger(RegistrationProcessorHandler.class);
    private final Registry registry;

    public RegistrationProcessorHandler(Registry registry) {
        Objects.requireNonNull(registry);
        this.registry = registry;
    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream, ChannelPipeline pipeline) {
        return Flux.create(fluxSink -> {

            AtomicReference<WrTy.InstanceInfo> lastInstance = new AtomicReference();
            Disposable disposable = inputStream
                    .filter(value -> value.getItemCase() == WrTy.ProtocolMessageEnvelope.ItemCase.INSTANCEINFO)
                    .doOnNext(value -> {
                        fluxSink.next(value);// reply
                        if (lastInstance.get() == null) {
                            lastInstance.set(value.getInstanceInfo());
                        }
                    })
                    .materialize()
                    .doOnCancel(() -> {
                        logger.debug("Unsubscribing registration input");
                    }).doOnNext(value -> {
                        SignalType signalType = value.getType();
                        switch (signalType) {
                            case CANCEL:
                            case ON_COMPLETE:
                                WrTy.ChangeNotification deleteNotification = ChangeNotifications.newDeleteNotification(lastInstance.get());
                                registry.register(deleteNotification);
                                break;
                            case ON_NEXT:
                                WrTy.ChangeNotification addNotification = ChangeNotifications.newAddNotification(lastInstance.get());
                                registry.register(addNotification);
                                break;
                        }
                    }).subscribe();
            fluxSink.onDispose(disposable);
        });
    }


}