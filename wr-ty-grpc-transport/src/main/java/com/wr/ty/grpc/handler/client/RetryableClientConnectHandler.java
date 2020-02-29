package com.wr.ty.grpc.handler.client;

/**
 * @author xiaohei
 * @date 2020/2/13 13:59
 */


import com.wr.ty.grpc.SubscriberFluxSinkWrap;
import com.wr.ty.grpc.core.channel.ChannelContext;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.core.channel.ChannelPipelineFactory;
import com.xh.demo.grpc.WrTy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Scheduler;
import reactor.retry.DefaultContext;
import reactor.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 */
public class RetryableClientConnectHandler implements ChannelHandler {

    private static final Logger logger = LoggerFactory.getLogger(RetryableClientConnectHandler.class);

    public static final IOException NOT_RECOGNIZED_INSTANCE = new IOException("Registration reply for not recognized instance");

    private final ChannelPipelineFactory factory;
    private final Duration retryDelayMs;
    private final Scheduler scheduler;


    public RetryableClientConnectHandler(ChannelPipelineFactory factory,
                                         Duration retryDelayMs,
                                         Scheduler scheduler) {

        Objects.requireNonNull(scheduler, "scheduler is null");
        this.factory = factory;
        this.retryDelayMs = retryDelayMs;
        this.scheduler = scheduler;
    }

    @Override
    public void init(ChannelContext channelContext) {
        if (channelContext.hasNext()) {
            throw new IllegalStateException("RetryableRegistrationClientHandler must be single handler pipeline");
        }
    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> registrationUpdates) {
        return Flux.create(fluxSink -> {
            logger.debug("Subscription to RetryableRegistrationClientHandler started");
            new RegistrationSession(registrationUpdates, fluxSink);
        });
    }

    class RegistrationSession {
        private final Queue<WrTy.ProtocolMessageEnvelope> unreportedUpdates = new LinkedBlockingQueue<>();
        private final EmitterProcessor<WrTy.ProtocolMessageEnvelope> replySubject = EmitterProcessor.create();
        private final Flux<WrTy.ProtocolMessageEnvelope> trackedUpdates;

        private final Disposable pipelineSubscription;

        RegistrationSession(Flux<WrTy.ProtocolMessageEnvelope> registrationUpdates, FluxSink<WrTy.ProtocolMessageEnvelope> fluxSink) {

            this.trackedUpdates = registrationUpdates.doOnNext(next -> unreportedUpdates.add(next));
            replySubject.subscribe(new SubscriberFluxSinkWrap(fluxSink));

            Retry<Object> retry = Retry.any().fixedBackoff(retryDelayMs).retryMax(10).withBackoffScheduler(scheduler).doOnRetry(value -> {
                DefaultContext context = (DefaultContext) value;
                Throwable exception = value.exception();
                logger.debug(" Reconnecting times {} internal pipeline terminated earlier with an error ({})", context.iteration(), exception.getMessage());
            });
            pipelineSubscription = connectPipeline()
                    .doOnError(e -> logger.info("Registration pipeline terminated due to an error", e))
//                    .retryWhen(retry)
                    .doOnCancel(() -> logger.debug("RetryableRegistrationClientHandler internal pipeline unsubscribed"))
                    .subscribe();
            fluxSink.onCancel(pipelineSubscription);
        }

        private Flux<Void> connectPipeline() {
            return factory.createPipeline().flatMap(newPipeline -> {
                return newPipeline.getFirst()
                        .handle(trackedUpdates)
                        .flatMap(next -> drainUntilCurrentFound(next));
            }).doOnSubscribe(value -> logger.debug("Creating new internal pipeline"));
        }

        private Flux<Void> drainUntilCurrentFound(WrTy.ProtocolMessageEnvelope next) {
            while (!unreportedUpdates.isEmpty()) {
                WrTy.ProtocolMessageEnvelope head = unreportedUpdates.poll();
                replySubject.onNext(head);
                if (head.hasInstanceInfo()) {
                    logger.debug("Received reply from internal pipeline matched with tracked instance {}", next.getInstanceInfo().getId());
                    return Flux.empty();
                }
            }
            logger.debug("Received reply from internal pipeline ({}) not matched with any tracked instance", next.getInstanceInfo().getId());
            return Flux.error(NOT_RECOGNIZED_INSTANCE);
        }
    }
}
