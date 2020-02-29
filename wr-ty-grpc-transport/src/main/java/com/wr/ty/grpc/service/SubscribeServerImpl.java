package com.wr.ty.grpc.service;

import com.wr.ty.grpc.Session;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.grpc.core.channel.ChannelPipelineFactory;
import com.wr.ty.grpc.handler.server.RegistrationProcessorHandler;
import com.wr.ty.grpc.handler.server.ServerHandshakeHandler;
import com.wr.ty.grpc.handler.server.ServerHeartbeatHandler;
import com.wr.ty.grpc.handler.server.ServerLoggingChannelHandler;
import com.wr.ty.grpc.register.Registry;
import com.wr.ty.grpc.util.ProtocolMessageEnvelopes;
import com.wr.ty.grpc.util.SourceIdGenerator;
import com.xh.demo.grpc.SubscribeServiceGrpc.SubscribeServiceImplBase;
import com.xh.demo.grpc.WrTy;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

import static com.wr.ty.grpc.util.ProtocolMessageEnvelopes.*;
import static com.wr.ty.grpc.util.ProtocolMessageEnvelopes.convertChangeNotification;

/**
 * @author xiaohei
 * @date 2020/2/24 13:41
 */
public class SubscribeServerImpl extends SubscribeServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(SubscribeServerImpl.class);

    private final ChannelPipelineFactory channelPipelineFactory;
    private final Registry register;

    private Function<WrTy.SubscribeRequest, WrTy.ProtocolMessageEnvelope> inputMap = value -> {
        WrTy.SubscribeRequest.ItemCase itemCase = value.getItemCase();
        switch (itemCase) {
            case HEARTBEAT:
                WrTy.Heartbeat heartbeat = value.getHeartbeat();
                return ProtocolMessageEnvelopes.fromHeartbeat(heartbeat);
            case CLIENTHELLO:
                return ProtocolMessageEnvelopes.fromClientHello(value.getClientHello());
            case INTERESTREGISTRATION:
                return fromInterestRegistration(value.getInterestRegistration());
            default:
                throw new RuntimeException("unknown message type");
        }
    };
    private Function<WrTy.ProtocolMessageEnvelope, WrTy.SubscribeResponse> outMap = value -> {
        WrTy.ProtocolMessageEnvelope.ItemCase itemCase = value.getItemCase();
        switch (itemCase) {
            case SERVERHELLO:
                return convertServerHello(value.getServerHello());
            case HEARTBEAT:
                return convertHeartbeat(value.getHeartbeat());
            case CHANGENOTIFICATION:
                return convertChangeNotification(value.getChangeNotification());
            default:
                throw new RuntimeException("unknown message type");
        }
    };

    public SubscribeServerImpl(Registry registry, Scheduler scheduler) {
        Objects.requireNonNull(scheduler);
        Objects.requireNonNull(registry);
        this.register = registry;
        SourceIdGenerator idGenerator = new SourceIdGenerator();
        this.channelPipelineFactory = () -> Mono.create(fluxSink -> {
            fluxSink.success(new ChannelPipeline("registrationServer@" + "test",
                    new ServerLoggingChannelHandler(),
                    new ServerHeartbeatHandler(1000 * 30, scheduler),
                    new ServerHandshakeHandler(idGenerator),
                    new RegistrationProcessorHandler(SubscribeServerImpl.this.register)
            ));

        });

    }

    @Override
    public StreamObserver<WrTy.SubscribeRequest> subscribe(StreamObserver<WrTy.SubscribeResponse> responseObserver) {
        Session<WrTy.SubscribeRequest, WrTy.SubscribeResponse> session = new Session(
                responseObserver,
                channelPipelineFactory,
                inputMap,
                outMap

        );
        return session;
    }
}
