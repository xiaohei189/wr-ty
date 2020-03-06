package com.wr.ty.grpc.service;

import com.wr.ty.grpc.Session;
import com.wr.ty.grpc.TransportConfig;
import com.wr.ty.grpc.client.RegistrationClientTransportHandler;
import com.wr.ty.grpc.core.PipelineNameGenerator;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.grpc.core.channel.ChannelPipelineFactory;
import com.wr.ty.grpc.core.channel.DefaultChannelPipeline;
import com.wr.ty.grpc.handler.client.ClientHandshakeHandler;
import com.wr.ty.grpc.handler.client.ClientHeartbeatHandler;
import com.wr.ty.grpc.handler.server.RegistrationProcessorHandler;
import com.wr.ty.grpc.handler.server.ServerHandshakeHandler;
import com.wr.ty.grpc.handler.server.ServerHeartbeatHandler;
import com.wr.ty.grpc.register.Registry;
import com.xh.demo.grpc.RegistrationServiceGrpc;
import com.xh.demo.grpc.WrTy;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.wr.ty.grpc.util.ProtocolMessageEnvelopes.*;
import static com.wr.ty.grpc.util.Registrations.convertHeartbeat;
import static com.wr.ty.grpc.util.Registrations.convertServerHello;
import static com.wr.ty.grpc.util.Registrations.*;

/**
 * @author xiaohei
 * @date 2020/2/12 22:54
 */
public class RegistrationServerImpl extends RegistrationServiceGrpc.RegistrationServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationServerImpl.class);

    private static final Function<WrTy.RegistrationRequest, WrTy.ProtocolMessageEnvelope> inputMap = value -> {
        WrTy.RegistrationRequest.ItemCase itemCase = value.getItemCase();
        switch (itemCase) {
            case CLIENTHELLO:
                return fromClientHello(value.getClientHello());
            case HEARTBEAT:
                return fromHeartbeat(value.getHeartbeat());
            case INSTANCEINFO:
                return fromInstance(value.getInstanceInfo());
            default:
                throw new RuntimeException("unknown message type");
        }
    };
    private static final Function<WrTy.ProtocolMessageEnvelope, WrTy.RegistrationResponse> outMap = value -> {
        WrTy.ProtocolMessageEnvelope.ItemCase itemCase = value.getItemCase();
        WrTy.RegistrationResponse.Builder builder = WrTy.RegistrationResponse.newBuilder();
        switch (itemCase) {
            case SERVERHELLO:
                return convertServerHello(value.getServerHello());
            case HEARTBEAT:
                return convertHeartbeat(value.getHeartbeat());
            case INSTANCEINFO:
                return convertAck(value.getInstanceInfo());
            default:
                throw new RuntimeException("unknown message type");

        }
    };
    private final ChannelPipelineFactory pipelineFactory;
    private final Registry register;

    public RegistrationServerImpl(Registry registry,
                                  TransportConfig config,
                                  PipelineNameGenerator pipelineNameGenerator,
                                  Scheduler scheduler) {
        Objects.requireNonNull(scheduler);
        Objects.requireNonNull(registry);
        this.register = registry;
        this.pipelineFactory = () -> Mono.create(fluxSink -> {
            String pipelineId = pipelineNameGenerator.generate("registrationServer@");
            List<ChannelHandler> handlers = new ArrayList<>();
            handlers.add(new ServerHeartbeatHandler(config.heartbeatTimeout(), scheduler));
            handlers.add(new ServerHandshakeHandler());
            handlers.add(new RegistrationProcessorHandler(RegistrationServerImpl.this.register));
            DefaultChannelPipeline pipeline = new DefaultChannelPipeline(pipelineId, 0, handlers);
            fluxSink.success(pipeline);
        });

    }

    @Override
    public StreamObserver<WrTy.RegistrationRequest> register(StreamObserver<WrTy.RegistrationResponse> responseObserver) {
        Session<WrTy.RegistrationRequest, WrTy.RegistrationResponse> registrationSession = new Session<>(responseObserver, pipelineFactory, inputMap, outMap);
        return registrationSession;
    }

}
