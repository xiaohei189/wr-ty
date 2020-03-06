package com.wr.ty.grpc.handler.client;


import com.wr.ty.grpc.SubscriberFluxSinkWrap;
import com.wr.ty.grpc.core.channel.*;
import com.xh.demo.grpc.WrTy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.retry.DefaultContext;
import reactor.retry.Retry;

import java.time.Duration;
import java.util.Objects;

/**
 * @author xiaohei
 * @date 2020/2/13 13:59
 */

public class RetryableChannelPipeline implements ChannelPipeline {

    private final static Logger logger = LoggerFactory.getLogger(RetryableChannelPipeline.class);
    private final ChannelPipelineFactory pipelineFactory;
    private final Duration retryDelayMs;
    private final Scheduler scheduler;
    private final String pipelineId;


    public RetryableChannelPipeline(String pipelineId, ChannelPipelineFactory pipelineFactory,
                                    Duration retryDelayMs,
                                    Scheduler scheduler) {
        this.pipelineId = pipelineId;
        Objects.requireNonNull(scheduler, "scheduler is null");
        this.pipelineFactory = pipelineFactory;
        this.retryDelayMs = retryDelayMs;
        this.scheduler = scheduler;
    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream) {

        Flux<WrTy.ProtocolMessageEnvelope> flux = Flux.create(fluxSink -> {
            logger.debug("Subscription to RetryableRegistrationClientHandler started");
            SubscriberFluxSinkWrap subscriberFluxSinkWrap = new SubscriberFluxSinkWrap(fluxSink);
            Retry<Object> retry = Retry.any().fixedBackoff(retryDelayMs).retryMax(10).withBackoffScheduler(scheduler).doOnRetry(value -> {
                DefaultContext context = (DefaultContext) value;
                Throwable exception = value.exception();
                logger.debug(" Reconnecting times {} internal pipeline terminated earlier with an error ({})", context.iteration(), exception.getMessage());
            });
            pipelineFactory.createPipeline()
                    .block()
                    .handle(inputStream)
                    .retryWhen(retry)
                    .doOnCancel(() -> logger.debug("UnSubscribing from RetryableClientConnectHandler innerPipeline"))
                    .subscribe(subscriberFluxSinkWrap);

        });
        return flux.doOnCancel(() -> logger.debug("UnSubscribing from RetryableRegistrationClientHandler"));
    }

    @Override
    public String pipelineId() {
        return this.pipelineId;
    }

}
