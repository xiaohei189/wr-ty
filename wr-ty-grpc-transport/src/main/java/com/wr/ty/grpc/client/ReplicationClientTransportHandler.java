package com.wr.ty.grpc.client;

import com.wr.ty.grpc.core.channel.ChannelContext;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.util.ProtocolMessageEnvelopes;
import com.xh.demo.grpc.RegistrationServiceGrpc;
import com.xh.demo.grpc.ReplicationServiceGrpc;
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
public class ReplicationClientTransportHandler implements ChannelHandler {
    private final static Logger logger = LoggerFactory.getLogger(InterestClientTransportHandler.class);

    final private ReplicationServiceGrpc.ReplicationServiceStub replicationServiceStub;

    public ReplicationClientTransportHandler(Channel channel) {
        Objects.requireNonNull(channel);
        this.replicationServiceStub = ReplicationServiceGrpc.newStub(channel);
    }

    @Override
    public void init(ChannelContext channelContext) {


    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream) {
        return Flux.create(fluxSink -> {
            logger.debug("Subscribed to GrpcRegistrationClientTransportHandler handler");

            StreamObserver<WrTy.ReplicationRequest> submittedRegistrationsObserver = replicationServiceStub.subscribe(
                    new StreamObserver<WrTy.ReplicationResponse>() {
                        @Override
                        public void onNext(WrTy.ReplicationResponse response) {
                            logger.debug("Received response of type {}", response.getItemCase());
                            switch (response.getItemCase()) {
                                case HEARTBEAT:
                                    fluxSink.next(ProtocolMessageEnvelopes.HEART_BEAT);
                                    break;
                                case SERVERHELLO:
                                    fluxSink.next(ProtocolMessageEnvelopes.SERVER_HELLO);
                                    break;
//                                case Chan:
//                                    WrTy.ProtocolMessageEnvelope confirmedUpdate = updatesQueue.poll();
//                                    if (confirmedUpdate != null) {
//                                        fluxSink.next(confirmedUpdate);
//                                    } else {
//                                        fluxSink.error(new IllegalStateException("Received unexpected acknowledgement in the registration channel"));
//                                    }
//                                    break;
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

            Disposable subscription = inputStream
                    .doOnCancel(() -> {
                        logger.debug("Unsubscribing from registration update input stream");
                        submittedRegistrationsObserver.onCompleted();
                    })
                    .subscribe(
                            registrationNotification -> {
                                WrTy.ProtocolMessageEnvelope.MessageOneOfCase messageOneOfCase = registrationNotification.getMessageOneOfCase();

//                                submittedRegistrationsObserver.onNext(toRegistrationRequest(registrationNotification));
                            },
                            e -> {
                                logger.debug("Forwarding error from registration update stream to transport ({})", e.getMessage());
                                submittedRegistrationsObserver.onError(e);
                            },
                            () -> {
                                logger.debug("Forwarding onComplete from registration update stream to transport");
                                submittedRegistrationsObserver.onCompleted();
                            }
                    );
            fluxSink.onCancel(subscription);
        });
    }


    private static WrTy.RegistrationRequest toRegistrationRequest(WrTy.ProtocolMessageEnvelope protocolMessageEnvelope) {
        WrTy.ProtocolMessageEnvelope.MessageOneOfCase messageOneOfCase = protocolMessageEnvelope.getMessageOneOfCase();
        switch (messageOneOfCase) {
            case HEARTBEAT:
                return WrTy.RegistrationRequest.newBuilder().setHeartbeat(WrTy.Heartbeat.getDefaultInstance()).build();
            case CLIENTHELLO:
                return WrTy.RegistrationRequest.newBuilder().setClientHello(WrTy.ClientHello.getDefaultInstance()).build();
            case INSTANCEINFO:
                return WrTy.RegistrationRequest.newBuilder().setInstanceInfo(protocolMessageEnvelope.getInstanceInfo()).build();
        }
        throw new IllegalStateException("Unrecognized channel notification type " + messageOneOfCase);
    }
}
