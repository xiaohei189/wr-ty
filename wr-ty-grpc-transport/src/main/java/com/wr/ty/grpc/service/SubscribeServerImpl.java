package com.wr.ty.grpc.service;

import com.wr.ty.grpc.Session;
import com.wr.ty.grpc.TransportConfig;
import com.wr.ty.grpc.core.PipelineNameGenerator;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.grpc.core.channel.ChannelPipelineFactory;
import com.wr.ty.grpc.handler.server.ServerHandshakeHandler;
import com.wr.ty.grpc.handler.server.ServerHeartbeatHandler;
import com.wr.ty.grpc.handler.server.ServerLoggingChannelHandler;
import com.wr.ty.grpc.handler.server.ServerSubscribeHandler;
import com.wr.ty.grpc.register.Registry;
import com.wr.ty.grpc.util.ProtocolMessageEnvelopes;
import com.xh.demo.grpc.SubscribeServiceGrpc.SubscribeServiceImplBase;
import com.xh.demo.grpc.WrTy;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.Objects;
import java.util.function.Function;

import static com.wr.ty.grpc.util.ProtocolMessageEnvelopes.*;

/**
 * @author xiaohei
 * @date 2020/2/24 13:41
 */
public class SubscribeServerImpl extends SubscribeServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(SubscribeServerImpl.class);

    private final ChannelPipelineFactory pipelineFactory;
    private final Registry register;

    private static Function<WrTy.SubscribeRequest, WrTy.ProtocolMessageEnvelope> inputMap = value -> {
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
    private static Function<WrTy.ProtocolMessageEnvelope, WrTy.SubscribeResponse> outMap = value -> {
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

    public SubscribeServerImpl(Registry registry,
                               TransportConfig config,
                               PipelineNameGenerator pipelineNameGenerator,
                               Scheduler scheduler) {
        Objects.requireNonNull(scheduler);
        Objects.requireNonNull(registry);
        this.register = registry;
        this.pipelineFactory = () -> Mono.create(fluxSink -> {
            String generate = pipelineNameGenerator.generate("subscribeServer@");
            fluxSink.success(new ChannelPipeline(generate,
                    new ServerLoggingChannelHandler(),
                    new ServerHeartbeatHandler(config.heartbeatTimeout(), scheduler),
                    new ServerHandshakeHandler(),
                    new ServerSubscribeHandler(register)
            ));

        });

    }

    @Override
    public StreamObserver<WrTy.SubscribeRequest> subscribe(StreamObserver<WrTy.SubscribeResponse> responseObserver) {
        Session<WrTy.SubscribeRequest, WrTy.SubscribeResponse> session = new Session(responseObserver, pipelineFactory, inputMap, outMap);
        return session;
    }
}
