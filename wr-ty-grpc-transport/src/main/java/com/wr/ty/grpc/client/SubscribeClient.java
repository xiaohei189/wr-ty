package com.wr.ty.grpc.client;

import com.wr.ty.grpc.TransportConfig;
import com.wr.ty.grpc.core.PipelineNameGenerator;
import com.wr.ty.grpc.core.Server;
import com.wr.ty.grpc.core.ServerResolver;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.grpc.core.channel.ChannelPipelineFactory;
import com.wr.ty.grpc.core.channel.DefaultChannelPipeline;
import com.wr.ty.grpc.handler.client.ClientHandshakeHandler;
import com.wr.ty.grpc.handler.client.ClientHeartbeatHandler;
import com.xh.demo.grpc.WrTy;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
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
public class SubscribeClient extends RetryConnectClient {

    private static final Logger logger = LoggerFactory.getLogger(SubscribeClient.class);

    private final ChannelPipelineFactory pipelineFactory;
    private final Scheduler scheduler;
    FluxSink<WrTy.ProtocolMessageEnvelope> fluxSink;
    Function<Server, Channel> channelSupplier;
    TransportConfig transportConfig;

    public SubscribeClient(Scheduler scheduler,
                           ServerResolver serverResolver,
                           TransportConfig transportConfig,
                           PipelineNameGenerator pipelineNameGenerator) {
        this(scheduler, serverResolver, transportConfig, pipelineNameGenerator, null);
    }

    public SubscribeClient(Scheduler scheduler,
                           ServerResolver serverResolver,
                           TransportConfig transportConfig,
                           PipelineNameGenerator pipelineNameGenerator,
                           Function<Server, Channel> channelFunction) {
        super(scheduler, transportConfig);
        Objects.requireNonNull(scheduler);
        Objects.requireNonNull(serverResolver);
        Objects.requireNonNull(transportConfig);
        this.channelSupplier = channelFunction;
        this.transportConfig = transportConfig;
        this.scheduler = scheduler;
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
                handlers.add(new SubscribeClientTransportHandler(channel));
                return new DefaultChannelPipeline(pipelineId, 0, handlers);
            });
            return channelPipeline;
        };
    }

    private static String createPipelineId(Server server) {
        return "Subscribe pipelineId-" + server.getHostName() + ":" + server.getPort();
    }

    private static Function<Server, Channel> createChannel = value -> {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(value.getHostName(), value.getPort()).usePlaintext().build();
        return channel;
    };

    public Flux<WrTy.ProtocolMessageEnvelope> subscribe(WrTy.Interest... interests) {
        WrTy.InterestRegistration.Builder builder = WrTy.InterestRegistration.newBuilder();
        for (int i = 0; i < interests.length; i++) {
            builder.addInterests(interests[i]);
        }
        WrTy.InterestRegistration interestRegistration = builder.build();
        WrTy.ProtocolMessageEnvelope protocolMessageEnvelope = WrTy.ProtocolMessageEnvelope.newBuilder()
                .setInterestRegistration(interestRegistration).build();
        Flux<WrTy.ProtocolMessageEnvelope> inputStream = Flux.create(fluxSink -> {
            fluxSink.next(protocolMessageEnvelope);
            this.fluxSink = fluxSink;
        });
        return Flux.defer(() -> subscribe(inputStream));
    }

    @Override
    protected ChannelPipelineFactory channelPipelineFactory() {
        return this.pipelineFactory;
    }


}
