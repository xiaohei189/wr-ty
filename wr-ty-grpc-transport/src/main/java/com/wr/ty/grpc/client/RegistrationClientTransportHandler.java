package com.wr.ty.grpc.client;

import com.wr.ty.grpc.ChannelLogger;
import com.wr.ty.grpc.core.channel.ChannelContext;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.util.ProtocolMessageEnvelopes;
import com.xh.demo.grpc.RegistrationServiceGrpc;
import com.xh.demo.grpc.WrTy;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author xiaohei
 * @date 2020/2/13 14:21
 */
public class RegistrationClientTransportHandler implements ChannelHandler {
    private final static Logger logger = LoggerFactory.getLogger(RegistrationClientTransportHandler.class);

    final private RegistrationServiceGrpc.RegistrationServiceStub registrationService;
    private ChannelLogger channelLogger;

    public RegistrationClientTransportHandler(Channel channel) {
        Objects.requireNonNull(channel);
        this.registrationService = RegistrationServiceGrpc.newStub(channel);
    }

    @Override
    public void init(ChannelContext channelContext) {
        channelLogger = new ChannelLogger(logger, channelContext.getPipeline().getPipelineId());
    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream) {
        return Flux.create(fluxSink -> {
            channelLogger.debug("Subscription to GrpcRegistrationClientTransportHandler start");

            Queue<WrTy.ProtocolMessageEnvelope> updatesQueue = new ConcurrentLinkedQueue<WrTy.ProtocolMessageEnvelope>();

            StreamObserver<WrTy.RegistrationRequest> submittedRegistrationsObserver = registrationService.register(
                    new StreamObserver<WrTy.RegistrationResponse>() {
                        @Override
                        public void onNext(WrTy.RegistrationResponse response) {
                            channelLogger.debug("Received response of type {}", response.getItemCase());
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
                            channelLogger.debug("Received onError from reply channel", t);
                            fluxSink.error(t);
                        }

                        @Override
                        public void onCompleted() {
                            channelLogger.debug("Received onCompleted from reply channel");
                            fluxSink.complete();
                        }
                    });

            Disposable subscription = inputStream
                    .doOnCancel(() -> {
                        channelLogger.debug("Unsubscribing from registration update input stream");
                        submittedRegistrationsObserver.onCompleted();
                    })
                    .subscribe(
                            value -> {
                                WrTy.ProtocolMessageEnvelope.ItemCase itemCase = value.getItemCase();
                                channelLogger.debug("Forwarding notification of type {} over the transport", itemCase);
                                if (itemCase == WrTy.ProtocolMessageEnvelope.ItemCase.INSTANCEINFO) {
                                    updatesQueue.add(value);
                                }
                                submittedRegistrationsObserver.onNext(toRegistrationRequest(value));
                            },
                            e -> {
                                channelLogger.debug("Forwarding error from registration update stream to transport ({})", e.getMessage());
                                submittedRegistrationsObserver.onError(e);
                            },
                            () -> {
                                channelLogger.debug("Forwarding onComplete from registration update stream to transport");
                                submittedRegistrationsObserver.onCompleted();
                            }
                    );
            fluxSink.onCancel(subscription);
        });
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
