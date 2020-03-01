

package com.wr.ty.grpc.handler.server;


import com.wr.ty.grpc.core.channel.ChannelContext;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.register.Registry;
import com.wr.ty.grpc.util.ChangeNotifications;
import com.xh.demo.grpc.WrTy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


public class RegistrationProcessorHandler implements ChannelHandler {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationProcessorHandler.class);

    private static final IOException REGISTRATION_ERROR = new IOException("Registration reply stream terminated with an error");

    private final Registry registry;

    public RegistrationProcessorHandler(Registry registry) {

        Objects.requireNonNull(registry);
        this.registry = registry;
    }

    @Override
    public void init(ChannelContext channelContext) {
        if (channelContext.hasNext()) {
            throw new IllegalStateException("RegistrationProcessorBridgeHandler must be the last one in the pipeline");
        }
    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> registrationUpdates) {
        return Flux.create(fluxSink -> {

            AtomicReference<WrTy.InstanceInfo> lastInstance = new AtomicReference();
            Disposable disposable = registrationUpdates
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
                                ChangeNotifications.newDeleteNotification(lastInstance.get());
                                break;
                            case ON_NEXT:
                                ChangeNotifications.newAddNotification(lastInstance.get());
                                break;
                        }
                    }).subscribe();
            fluxSink.onDispose(disposable);
        });
    }


}