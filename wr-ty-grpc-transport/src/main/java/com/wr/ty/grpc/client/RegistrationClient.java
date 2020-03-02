package com.wr.ty.grpc.client;

import com.wr.ty.grpc.core.Server;
import com.wr.ty.grpc.core.ServerResolver;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.grpc.core.channel.ChannelPipelineFactory;
import com.wr.ty.grpc.handler.client.ClientHandshakeHandler;
import com.wr.ty.grpc.handler.client.ClientHeartbeatHandler;
import com.wr.ty.grpc.handler.client.RetryableClientConnectHandler;
import com.wr.ty.grpc.TransportConfig;
import com.wr.ty.grpc.handler.client.LoggingChannelHandler;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author xiaohei
 * @date 2020/2/13 13:18
 */
public class RegistrationClient {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationClient.class);

    private final ChannelPipelineFactory pipelineFactory;
    private final Scheduler scheduler;
    Disposable disposable;
    Function<Server, Channel> channelSupplier;
    TransportConfig transportConfig;

    public RegistrationClient(@Nonnull Scheduler scheduler,
                              @Nonnull ServerResolver serverResolver,
                              @Nonnull TransportConfig transportConfig,
                              @Nullable Function<Server, Channel> channelFunction) {
        Objects.requireNonNull(scheduler);
        Objects.requireNonNull(serverResolver);
        Objects.requireNonNull(transportConfig);
        this.channelSupplier = channelFunction;
        this.transportConfig = transportConfig;
        this.scheduler = scheduler;
        this.pipelineFactory = () -> {
            Mono<ChannelPipeline> channelPipeline = serverResolver.resolve().map(server -> {
                String pipelineId = createPipelineId(server);
                Channel channel;
                if (channelFunction == null) {
                    channel = createChannel.apply(server);
                } else {
                    channel = channelSupplier.apply(server);
                }
                return new ChannelPipeline(pipelineId,
                        new LoggingChannelHandler(),
                        new ClientHandshakeHandler(),
                        new ClientHeartbeatHandler(transportConfig.heartbeatInterval(), transportConfig.heartbeatTimeout(), RegistrationClient.this.scheduler),
                        new RegistrationClientTransportHandler(channel)
                );
            });
            return channelPipeline;
        };
    }

    private static String createPipelineId(Server server) {
        return "registration pipelineId-" + server.getHostName() + ":" + server.getPort();
    }

    private static Function<Server, Channel> createChannel = value -> {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(value.getHostName(), value.getPort()).usePlaintext().build();
        return channel;
    };

    public Flux<String> register(Flux<WrTy.InstanceInfo> registrant) {
        Flux<String> flux = Flux.create(fluxSink -> {
            RetryableClientConnectHandler retryableClientConnectHandler = new RetryableClientConnectHandler(pipelineFactory, this.transportConfig.autoConnectInterval(), scheduler);
            disposable = retryableClientConnectHandler.handle(registrant.map(instance -> WrTy.ProtocolMessageEnvelope.newBuilder().setInstanceInfo(instance).build())).subscribe();
            fluxSink.onDispose(disposable);
        });
        return flux.doOnCancel(() -> logger.debug("UnSubscribing from RegistrationClient"));
    }

    public void shutDown() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

}
