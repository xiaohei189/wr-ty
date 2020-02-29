package com.wr.ty.grpc.client;

import com.wr.ty.grpc.core.channel.ChannelContext;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.register.Registry;
import com.wr.ty.grpc.util.ProtocolMessageEnvelopes;
import com.xh.demo.grpc.InterestServiceGrpc;
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
public class InterestClientTransportHandler implements ChannelHandler {
    private final static Logger logger = LoggerFactory.getLogger(InterestClientTransportHandler.class);

    final private InterestServiceGrpc.InterestServiceStub interestServiceStub;

    public InterestClientTransportHandler(Channel channel, Registry registry) {
        Objects.requireNonNull(channel);
        this.interestServiceStub = InterestServiceGrpc.newStub(channel);
    }

    @Override
    public void init(ChannelContext channelContext) {


    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream) {
        return Flux.create(fluxSink -> {
            logger.debug("Subscription to InterestClientTransportHandler start");

            Queue<WrTy.ProtocolMessageEnvelope> updatesQueue = new ConcurrentLinkedQueue<WrTy.ProtocolMessageEnvelope>();

            StreamObserver<WrTy.InterestRequest> submittedRegistrationsObserver = interestServiceStub.subscribe(
                    new StreamObserver<WrTy.InterestResponse>() {
                        @Override
                        public void onNext(WrTy.InterestResponse response) {
                            logger.debug("Received response of type {}", response.getItemCase());
                            switch (response.getItemCase()) {
                                case HEARTBEAT:
                                    fluxSink.next(ProtocolMessageEnvelopes.HEART_BEAT);
                                    break;
                                case SERVERHELLO:
                                    fluxSink.next(ProtocolMessageEnvelopes.SERVER_HELLO);
                                    break;
                                case CHANGENOTIFICATION:
                                    WrTy.ChangeNotification changeNotification = response.getChangeNotification();
                                    WrTy.ProtocolMessageEnvelope messageEnvelope = convertChangeNotification2MessageEnvelope(changeNotification);
                                    if (messageEnvelope != null) {
                                        fluxSink.next(messageEnvelope);
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

            Disposable subscription = inputStream
                    .doOnCancel(() -> {
                        logger.debug("Unsubscribing from interest update input stream");
                        submittedRegistrationsObserver.onCompleted();
                    })
                    .subscribe(
                            value -> {
                                logger.debug("Forwarding notification {} over the transport", value.toString());
                                submittedRegistrationsObserver.onNext(toRegistrationRequest(value));
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

    private WrTy.ProtocolMessageEnvelope convertChangeNotification2MessageEnvelope(WrTy.ChangeNotification changeNotification) {

        WrTy.ChangeNotification.NotificationOneofCase oneofCase = changeNotification.getNotificationOneofCase();
        WrTy.ProtocolMessageEnvelope messageEnvelope = null;
        switch (oneofCase) {
            case ADD:
                WrTy.InstanceInfo instanceInfo = changeNotification.getAdd().getInstanceInfo();
                messageEnvelope = WrTy.ProtocolMessageEnvelope.newBuilder()
                        .setAddInstance(WrTy.AddInstance.newBuilder().setInstanceInfo(instanceInfo).build()).build();
                break;
            case DELETE:
                String instanceId = changeNotification.getDelete().getInstanceId();
                messageEnvelope = WrTy.ProtocolMessageEnvelope.newBuilder()
                        .setDeleteInstance(WrTy.DeleteInstance.newBuilder().setInstanceId(instanceId).build()).build();
                break;
            default:
                throw new RuntimeException("unknown message type");
        }
        return messageEnvelope;
    }


    private static WrTy.InterestRequest toRegistrationRequest(WrTy.ProtocolMessageEnvelope protocolMessageEnvelope) {
        WrTy.ProtocolMessageEnvelope.MessageOneOfCase messageOneOfCase = protocolMessageEnvelope.getMessageOneOfCase();
        switch (messageOneOfCase) {
            case HEARTBEAT:
                return WrTy.InterestRequest.newBuilder().setHeartbeat(WrTy.Heartbeat.getDefaultInstance()).build();
            case CLIENTHELLO:
                return WrTy.InterestRequest.newBuilder().setClientHello(WrTy.ClientHello.getDefaultInstance()).build();
            case INTERESTREGISTRATION:
                return WrTy.InterestRequest.newBuilder().setInterestRegistration(protocolMessageEnvelope.getInterestRegistration()).build();
        }
        throw new IllegalStateException("Unrecognized channel notification type " + messageOneOfCase);
    }
}
