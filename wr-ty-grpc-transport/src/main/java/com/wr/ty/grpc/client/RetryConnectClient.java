package com.wr.ty.grpc.client;

import com.wr.ty.grpc.SubscriberFluxSinkWrap;
import com.wr.ty.grpc.TransportConfig;
import com.wr.ty.grpc.core.Server;
import com.wr.ty.grpc.core.ServerResolver;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.grpc.core.channel.ChannelPipelineFactory;
import com.xh.demo.grpc.WrTy;
import io.grpc.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.retry.DefaultContext;
import reactor.retry.Retry;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author xiaohei
 * @date 2020/3/3 12:32
 */
public abstract class RetryConnectClient {

    private static final Logger logger = LoggerFactory.getLogger(RetryConnectClient.class);
    private final Scheduler scheduler;
    Disposable subscriber;
    TransportConfig transportConfig;

    public RetryConnectClient(Scheduler scheduler,
                              TransportConfig transportConfig
    ) {
        Objects.requireNonNull(scheduler);
        Objects.requireNonNull(transportConfig);
        this.transportConfig = transportConfig;
        this.scheduler = scheduler;
    }

    final protected Flux<WrTy.ProtocolMessageEnvelope> subscribe(Flux<WrTy.ProtocolMessageEnvelope> inputStream) {
        Flux<WrTy.ProtocolMessageEnvelope> flux = Flux.create(fluxSink -> {
            logger.debug("Subscription to RetryConnectClient started");
            SubscriberFluxSinkWrap subscriberFluxSinkWrap = new SubscriberFluxSinkWrap(fluxSink);
            Retry<Object> retry = Retry.any()
                    .fixedBackoff(transportConfig.autoConnectInterval())
                    .retryMax(transportConfig.maxRetryConnectTimes())
                    .withBackoffScheduler(scheduler)
                    .doOnRetry(value -> {
                        DefaultContext context = (DefaultContext) value;
                        Throwable exception = value.exception();
                        logger.debug(" Reconnecting times {} internal pipeline terminated earlier with an error ({})", context.iteration(), exception.getMessage());
                    });
            ChannelPipeline pipeline = channelPipelineFactory().createPipeline().block();
            pipeline.handle(inputStream)
                    .retryWhen(retry)
                    .doOnCancel(() -> logger.debug("UnSubscribing from RetryConnectClient innerPipeline"))
                    .subscribe(subscriberFluxSinkWrap);
            subscriber = subscriberFluxSinkWrap;

        });
        return flux.doOnCancel(() -> logger.debug("UnSubscribing from RetryConnectClient"));
    }

    abstract protected ChannelPipelineFactory channelPipelineFactory();

    public void shutDown() {
        if (subscriber != null) {
            subscriber.dispose();
        }
    }

}

