package com.wr.ty.grpc.client;

import com.wr.ty.grpc.TransportConfig;
import com.wr.ty.grpc.core.Server;
import com.wr.ty.grpc.core.ServerResolver;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.grpc.core.channel.ChannelPipelineFactory;
import com.wr.ty.grpc.core.channel.DefaultChannelPipeline;
import com.wr.ty.grpc.handler.client.ClientHandshakeHandler;
import com.wr.ty.grpc.handler.client.ClientHeartbeatHandler;
import com.wr.ty.grpc.register.Registry;
import com.xh.demo.grpc.WrTy;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author xiaohei
 * @date 2020/2/13 13:18
 */
public class ReplicationClient extends RetryConnectClient {

    private static final Logger logger = LoggerFactory.getLogger(ReplicationClient.class);

    private final ChannelPipelineFactory pipelineFactory;
    Function<Server, Channel> channelSupplier;
    TransportConfig transportConfig;

    public ReplicationClient(Scheduler scheduler,
                             Registry registry,
                             ServerResolver serverResolver,
                             TransportConfig transportConfig,
                             Function<Server, Channel> channelFunction) {
        super(scheduler, transportConfig);
        Objects.requireNonNull(scheduler);
        Objects.requireNonNull(serverResolver);
        Objects.requireNonNull(transportConfig);
        this.channelSupplier = channelFunction;
        this.transportConfig = transportConfig;
        this.pipelineFactory = () -> {
            Mono<ChannelPipeline> pipelineMono = serverResolver.resolve().map(server -> {
                String pipelineId = createPipelineId(server);
                Channel channel;
                if (channelFunction == null) {
                    channel = createChannel.apply(server);
                } else {
                    channel = channelSupplier.apply(server);
                }
                List<ChannelHandler> handlers = new ArrayList<>();
                handlers.add(new ClientHandshakeHandler());
                handlers.add(new ClientHeartbeatHandler(transportConfig.heartbeatInterval(), transportConfig.heartbeatTimeout(), scheduler));
                handlers.add(new ReplicationClientHandler(registry));
                handlers.add(new ReplicationClientTransportHandler(channel));
                return new DefaultChannelPipeline(pipelineId, 0, handlers);

            });
            return pipelineMono;
        };
    }

    private static String createPipelineId(Server server) {
        return "registration pipelineId-" + server.getHostName() + ":" + server.getPort();
    }

    private static Function<Server, Channel> createChannel = value -> {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(value.getHostName(), value.getPort()).usePlaintext().build();
        return channel;
    };

    public Flux<WrTy.ProtocolMessageEnvelope> register(Flux<WrTy.InstanceInfo> registrant) {
        Flux<WrTy.ProtocolMessageEnvelope> envelopeFlux = registrant.map(instance -> WrTy.ProtocolMessageEnvelope.newBuilder().setInstanceInfo(instance).build());

        return Flux.defer(() -> subscribe(envelopeFlux));
    }


    @Override
    protected ChannelPipelineFactory channelPipelineFactory() {
        return this.pipelineFactory;
    }
}
