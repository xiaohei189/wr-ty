package com.wr.ty.grpc.client;

import com.wr.ty.grpc.core.PipelineNameGenerator;
import com.wr.ty.grpc.core.Server;
import com.wr.ty.grpc.core.ServerResolver;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.grpc.core.channel.ChannelPipelineFactory;
import com.wr.ty.grpc.core.channel.DefaultChannelPipeline;
import com.wr.ty.grpc.handler.client.ClientHandshakeHandler;
import com.wr.ty.grpc.handler.client.ClientHeartbeatHandler;
import com.wr.ty.grpc.TransportConfig;
import com.xh.demo.grpc.WrTy;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author xiaohei
 * @date 2020/2/13 13:18
 */
public class RegistrationClient extends RetryConnectClient {

    private final ChannelPipelineFactory pipelineFactory;
    private final Function<Server, Channel> channelSupplier;

    public RegistrationClient(Scheduler scheduler,
                              ServerResolver serverResolver,
                              TransportConfig transportConfig,
                              PipelineNameGenerator pipelineNameGenerator) {
        this(scheduler, serverResolver, transportConfig, pipelineNameGenerator, null);
    }

    public RegistrationClient(Scheduler scheduler,
                              ServerResolver serverResolver,
                              TransportConfig transportConfig,
                              PipelineNameGenerator pipelineNameGenerator,
                              Function<Server, Channel> channelFunction) {
        super(scheduler, transportConfig);
        this.channelSupplier = channelFunction;
        this.pipelineFactory = () -> {
            Mono<ChannelPipeline> channelPipeline = serverResolver.resolve().map(server -> {
                String pipelineId = pipelineNameGenerator.generate(createPipelineId(server));
                Channel channel;
                if (channelFunction == null) {
                    channel = createChannel.apply(server);
                } else {
                    channel = channelSupplier.apply(server);
                }

                List<ChannelHandler> handlers = new ArrayList<>();
                handlers.add(new ClientHandshakeHandler());
                handlers.add(new ClientHeartbeatHandler(transportConfig.heartbeatInterval(), transportConfig.heartbeatTimeout(), scheduler));
//                handlers.add(new RegistrationClientTransportHandler(channel));
                return new DefaultChannelPipeline(pipelineId, 0, handlers);
            });
            return channelPipeline;
        };
    }

    private static String createPipelineId(Server server) {
        return "Registration pipelineId-" + server.getHostName() + ":" + server.getPort();
    }

    private static Function<Server, Channel> createChannel = value -> {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(value.getHostName(), value.getPort()).usePlaintext().build();
        return channel;
    };

    public Mono<WrTy.ProtocolMessageEnvelope> register(Flux<WrTy.InstanceInfo> registrant) {
        Flux<WrTy.ProtocolMessageEnvelope> messageEnvelopeFlux = registrant.map(instance -> WrTy.ProtocolMessageEnvelope.newBuilder().setInstanceInfo(instance).build());
        return subscribe(messageEnvelopeFlux).ignoreElements();
    }

    @Override
    protected ChannelPipelineFactory channelPipelineFactory() {
        return pipelineFactory;
    }


}
