package com.wr.ty.grpc.client;

import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.grpc.util.ProtocolMessageEnvelopes;
import com.xh.demo.grpc.RegistrationServiceGrpc;
import com.xh.demo.grpc.WrTy;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

/**
 * @author xiaohei
 * @date 2020/2/13 14:21
 */
public class RegistrationClientTransportHandler implements ChannelHandler {
    private final static Logger logger = LoggerFactory.getLogger(RegistrationClientTransportHandler.class);

    final private RegistrationServiceGrpc.RegistrationServiceStub registrationService;

    private static Function<com.xh.demo.grpc.WrTy.ProtocolMessageEnvelope, com.xh.demo.grpc.WrTy.RegistrationRequest> outMapper = value -> {
        WrTy.ProtocolMessageEnvelope.ItemCase itemCase = value.getItemCase();
        switch (itemCase) {
            case HEARTBEAT:
                return WrTy.RegistrationRequest.newBuilder().setHeartbeat(WrTy.Heartbeat.getDefaultInstance()).build();
            case CLIENTHELLO:
                return WrTy.RegistrationRequest.newBuilder().setClientHello(WrTy.ClientHello.getDefaultInstance()).build();
            case INSTANCEINFO:
                return WrTy.RegistrationRequest.newBuilder().setInstanceInfo(value.getInstanceInfo()).build();
        }
        throw new IllegalStateException("Unrecognized channel notification type " + itemCase);
    };

    //    private static Function<com.xh.demo.grpc.WrTy.RegistrationResponse, com.xh.demo.grpc.WrTy.ProtocolMessageEnvelope> inMapper = value -> {
//        WrTy.RegistrationResponse.ItemCase itemCase = value.getItemCase();
//        switch (itemCase) {
//            case HEARTBEAT:
//                return ProtocolMessageEnvelopes.HEART_BEAT;
//            case CLIENTHELLO:
//                return WrTy.RegistrationRequest.newBuilder().setClientHello(WrTy.ClientHello.getDefaultInstance()).build();
//            case INSTANCEINFO:
//                return WrTy.RegistrationRequest.newBuilder().setInstanceInfo(value.getInstanceInfo()).build();
//        }
//        throw new IllegalStateException("Unrecognized channel notification type " + itemCase);
//    };
    public RegistrationClientTransportHandler(Channel channel) {
        Objects.requireNonNull(channel);
        this.registrationService = RegistrationServiceGrpc.newStub(channel);
    }


    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream, ChannelPipeline pipeline) {
        Flux<WrTy.ProtocolMessageEnvelope> flux = Flux.create(fluxSink -> {
            logger.debug("Subscription to RegistrationClientTransportHandler start");

            Queue<WrTy.ProtocolMessageEnvelope> updatesQueue = new ConcurrentLinkedQueue<WrTy.ProtocolMessageEnvelope>();

            StreamObserver<WrTy.RegistrationRequest> submittedRegistrationsObserver = registrationService.register(
                    new StreamObserver<WrTy.RegistrationResponse>() {
                        @Override
                        public void onNext(WrTy.RegistrationResponse response) {
                            switch (response.getItemCase()) {
                                case HEARTBEAT:
                                    fluxSink.next(ProtocolMessageEnvelopes.HEART_BEAT);
                                    break;
                                case SERVERHELLO:
                                    fluxSink.next(ProtocolMessageEnvelopes.SERVER_HELLO);
                                    break;
                                case ACK:
                                    WrTy.ProtocolMessageEnvelope confirmedUpdate = updatesQueue.poll();
                                    if (confirmedUpdate != null) {
                                        fluxSink.next(confirmedUpdate);
                                    } else {
                                        fluxSink.error(new IllegalStateException("Received unexpected acknowledgement in the registration channel"));
                                    }
                                    break;
                                default:
                                    fluxSink.error(new IllegalStateException("Unexpected response kind " + response.getItemCase()));
                            }
                        }

                        @Override
                        public void onError(Throwable t) {
                            logger.debug("Received onError from reply channel", t);
                            fluxSink.error(t);
                        }

                        @Override
                        public void onCompleted() {
                            logger.debug("Received onCompleted from reply channel");
                            fluxSink.complete();
                        }
                    });

            inputStream.doOnCancel(() -> {
                logger.debug("UnSubscribing from RegistrationClientTransportHandler");
                submittedRegistrationsObserver.onCompleted();
            })
                    .subscribe(
                            value -> {
                                WrTy.ProtocolMessageEnvelope.ItemCase itemCase = value.getItemCase();
                                if (itemCase == WrTy.ProtocolMessageEnvelope.ItemCase.INSTANCEINFO) {
                                    updatesQueue.add(value);
                                }
                                submittedRegistrationsObserver.onNext(toRegistrationRequest(value));
                            },
                            e -> {
                                logger.debug("Forwarding error from registration update stream to transport ({})", e.getMessage());
                                submittedRegistrationsObserver.onError(e);
                            },
                            () -> {
                                submittedRegistrationsObserver.onCompleted();
                            }
                    );
        });

        return flux.doOnCancel(() -> logger.debug("UnSubscribing from RegistrationClientTransportHandler"));
    }


    private static WrTy.RegistrationRequest toRegistrationRequest(WrTy.ProtocolMessageEnvelope protocolMessageEnvelope) {
        WrTy.ProtocolMessageEnvelope.ItemCase itemCase = protocolMessageEnvelope.getItemCase();
        switch (itemCase) {
            case HEARTBEAT:
                return WrTy.RegistrationRequest.newBuilder().setHeartbeat(WrTy.Heartbeat.getDefaultInstance()).build();
            case CLIENTHELLO:
                return WrTy.RegistrationRequest.newBuilder().setClientHello(WrTy.ClientHello.getDefaultInstance()).build();
            case INSTANCEINFO:
                return WrTy.RegistrationRequest.newBuilder().setInstanceInfo(protocolMessageEnvelope.getInstanceInfo()).build();
        }
        throw new IllegalStateException("Unrecognized channel notification type " + itemCase);
    }
}
