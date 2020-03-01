package com.wr.ty.grpc.client;

import com.wr.ty.grpc.StreamObserverFluxSink;
import com.wr.ty.grpc.SubscriberStreamObserver;
import com.wr.ty.grpc.core.channel.ChannelContext;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.register.Registry;
import com.wr.ty.grpc.util.ProtocolMessageEnvelopes;
import com.xh.demo.grpc.ReplicationServiceGrpc;
import com.xh.demo.grpc.WrTy;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author xiaohei
 * @date 2020/2/13 14:21
 */
public class ReplicationClientTransportHandler implements ChannelHandler {
    private final static Logger logger = LoggerFactory.getLogger(ReplicationClientTransportHandler.class);

    final private ReplicationServiceGrpc.ReplicationServiceStub replicationServiceStub;
    private static final Function<WrTy.SubscribeRequest, WrTy.ProtocolMessageEnvelope> outMapper = value -> {
        switch (value.getItemCase()) {
            case HEARTBEAT:
                return ProtocolMessageEnvelopes.HEART_BEAT;
            case CLIENTHELLO:
                return ProtocolMessageEnvelopes.SERVER_HELLO;
            case INTERESTREGISTRATION:
                return ProtocolMessageEnvelopes.fromInterestRegistration(value.getInterestRegistration());
            default:
                throw new RuntimeException("Unexpected response kind");
        }
    };

    private static final Function<WrTy.SubscribeResponse, WrTy.ProtocolMessageEnvelope> inMapper = value -> {
        switch (value.getItemCase()) {
            case HEARTBEAT:
                return ProtocolMessageEnvelopes.HEART_BEAT;
            case SERVERHELLO:
                return ProtocolMessageEnvelopes.SERVER_HELLO;
            case CHANGENOTIFICATION:
                return ProtocolMessageEnvelopes.fromChangeNotification(value.getChangeNotification());
            default:
                throw new RuntimeException("Unexpected response kind");
        }
    };

    public ReplicationClientTransportHandler(Channel channel) {
        Objects.requireNonNull(channel);
        this.replicationServiceStub = ReplicationServiceGrpc.newStub(channel);
    }

    @Override
    public void init(ChannelContext channelContext) {
        if (channelContext.hasNext()) {
            throw new IllegalStateException("ReplicationClientTransportHandler must be the last one in the pipeline");
        }
    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream) {

        return Flux.create(fluxSink -> {
            logger.debug("Subscription to ReplicationClientTransportHandler start");
            StreamObserverFluxSink<WrTy.SubscribeResponse, WrTy.ProtocolMessageEnvelope> response = new StreamObserverFluxSink(fluxSink, inMapper);
            StreamObserver<WrTy.SubscribeRequest> requestStream = replicationServiceStub.subscribe(response);
            SubscriberStreamObserver<WrTy.ProtocolMessageEnvelope, WrTy.SubscribeRequest> subscriber = new SubscriberStreamObserver(requestStream, outMapper);
            inputStream.subscribe(subscriber);
            fluxSink.onDispose(subscriber);
        });

    }
}
