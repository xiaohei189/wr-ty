package com.wr.ty.grpc.client;

import com.wr.ty.grpc.TransportConfig;
import com.wr.ty.grpc.core.Server;
import com.wr.ty.grpc.core.ServerResolver;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.grpc.core.channel.ChannelPipelineFactory;
import com.wr.ty.grpc.handler.client.ClientHandshakeHandler;
import com.wr.ty.grpc.handler.client.ClientHeartbeatHandler;
import com.wr.ty.grpc.handler.client.LoggingChannelHandler;
import com.wr.ty.grpc.handler.client.RetryableClientConnectHandler;
import com.wr.ty.grpc.register.Registry;
import com.xh.demo.grpc.WrTy;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;


/**
 * @author xiaohei
 * @date 2020/2/13 13:18
 */
public class InterestClient {

    private static final Logger logger = LoggerFactory.getLogger(InterestClient.class);

    private final ChannelPipelineFactory transportPipelineFactory;
    private final Scheduler scheduler;
    Disposable disposable;
    Function<Server, Channel> channelSupplier;
    TransportConfig transportConfig;

    public InterestClient(@Nonnull Scheduler scheduler,
                          @Nonnull ServerResolver serverResolver,
                          @Nonnull TransportConfig transportConfig,
                          @Nonnull Registry registry,
                          @Nullable Function<Server, Channel> channelFunction) {
        Objects.requireNonNull(scheduler);
        Objects.requireNonNull(serverResolver);
        Objects.requireNonNull(registry);
        Objects.requireNonNull(transportConfig);
        this.channelSupplier = channelFunction;
        this.transportConfig = transportConfig;
        this.scheduler = scheduler;
        this.transportPipelineFactory = () -> {
            Flux<ChannelPipeline> channelPipeline = serverResolver.resolve().map(server -> {
                String pipelineId = createPipelineId(server);
                Channel channel;
                if (channelFunction == null) {
                    channel = createChannel.apply(server);
                } else {
                    channel = channelSupplier.apply(server);
                }
                return new ChannelPipeline(pipelineId,
                        new ClientHandshakeHandler(),
                        new ClientHeartbeatHandler(transportConfig.heartbeatInterval(), transportConfig.heartbeatTimeout(), InterestClient.this.scheduler),
                        new LoggingChannelHandler(),
                        new InterestClientTransportHandler(channel, registry)
                );
            });
            return channelPipeline;
        };
    }

    private static String createPipelineId(Server server) {
        return "interest pipelineId-" + server.getHostName() + ":" + server.getPort();
    }

    private static Function<Server, Channel> createChannel = value -> {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(value.getHostName(), value.getPort()).usePlaintext().build();
        return channel;
    };

    public Flux<String> subscribe(WrTy.Interest interest) {

        WrTy.InterestRegistration interestRegistration = WrTy.InterestRegistration.newBuilder().addInterests(interest).build();
        WrTy.ProtocolMessageEnvelope protocolMessageEnvelope = WrTy.ProtocolMessageEnvelope.newBuilder().setInterestRegistration(interestRegistration).build();
        return Flux.create(fluxSink -> {
            ChannelPipeline retryablePipeline = new ChannelPipeline("interest",
                    new RetryableClientConnectHandler(transportPipelineFactory, this.transportConfig.autoConnectInterval(), scheduler)
            );
            disposable = retryablePipeline.getFirst()
                    .handle(Flux.just(protocolMessageEnvelope))
                    .doOnCancel(() -> {
                        logger.debug("UnSubscribing registration client");
                    })
                    .subscribe();
            fluxSink.onCancel(disposable);
        });
    }

    public void shutDown() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

}
