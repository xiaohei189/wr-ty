package com.wr.ty.grpc.handler.client;

import com.wr.ty.grpc.ChannelLogger;
import com.wr.ty.grpc.core.channel.ChannelContext;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.util.FluxUtil;
import com.xh.demo.grpc.WrTy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static com.wr.ty.grpc.util.ProtocolMessageEnvelopes.HEART_BEAT;
import static com.xh.demo.grpc.WrTy.ProtocolMessageEnvelope.ItemCase.HEARTBEAT;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ClientHeartbeatHandler implements ChannelHandler {

    private static final Logger logger = LoggerFactory.getLogger(ClientHeartbeatHandler.class);
    private ChannelLogger channelLogger;


    private static final Throwable HEARTBEAT_TIMEOUT = new IOException("Heartbeat timeout");

    private final Duration heartbeatIntervalMs;
    private final Duration heartbeatTimeoutMs;
    private final Scheduler scheduler;

    private ChannelContext channelContext;


    public ClientHeartbeatHandler(Duration heartbeatIntervalMs, Duration heartbeatTimeoutMs, Scheduler scheduler) {
        this.heartbeatIntervalMs = heartbeatIntervalMs;
        this.heartbeatTimeoutMs = heartbeatTimeoutMs;
        this.scheduler = scheduler;
    }

    @Override
    public void init(ChannelContext channelContext) {
        if (!channelContext.hasNext()) {
            throw new IllegalStateException("Expected next handler in the pipeline");
        }
        channelLogger = new ChannelLogger(logger, channelContext.getPipeline().getPipelineId());
        this.channelContext = channelContext;
    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream) {
        Flux<WrTy.ProtocolMessageEnvelope> flux = Flux.create(fluxSink -> {
            channelLogger.debug("Subscription to ClientHeartbeatHandler start");
            AtomicLong lastHeartbeatReply = new AtomicLong(-1);
            Flux<WrTy.ProtocolMessageEnvelope> heartbeatTask = Flux.interval(heartbeatIntervalMs, heartbeatIntervalMs, scheduler)
                    .flatMap(tick -> {
                        if (isLate(lastHeartbeatReply)) {
                            channelLogger.debug("No heartbeat reply from server received in {} ms", heartbeatTimeoutMs);
                            return Flux.error(HEARTBEAT_TIMEOUT);
                        }
                        return Flux.just(HEART_BEAT);
                    });
            Flux<WrTy.ProtocolMessageEnvelope> intercepted = FluxUtil.mergeWhenAllActive(inputStream, heartbeatTask);
            Disposable disposable = channelContext.next()
                    .handle(intercepted)
                    .subscribe(
                            next -> {
                                if (next.getItemCase() == HEARTBEAT) {
                                    channelLogger.debug("Heartbeat reply from server received");
                                    lastHeartbeatReply.set(scheduler.now(MILLISECONDS));
                                } else {
                                    fluxSink.next(next);
                                }
                            },
                            e -> {
                                channelLogger.debug("Subscription to ClientHeartbeatHandler completed with an error ({})", e.getMessage());
                                fluxSink.error(e);
                            },
                            () -> {
                                channelLogger.debug("Subscription to ClientHeartbeatHandler onCompleted");
                                fluxSink.complete();
                            }
                    );

            fluxSink.onDispose(disposable);
        });

        return flux.doOnCancel(() -> channelLogger.debug("UnSubscribing from ClientHeartbeatHandler"));

    }

    private boolean isLate(AtomicLong lastHeartbeatReply) {
        if (lastHeartbeatReply.get() < 0) {
            lastHeartbeatReply.set(scheduler.now(MILLISECONDS));
            return false;
        }
        long currentTime = scheduler.now(MILLISECONDS);
        long lastTime = lastHeartbeatReply.get();
        long delay = currentTime - lastTime;
        channelLogger.debug("heartbeat current time {}, lastTime {}, delay {}", currentTime, lastTime, delay);
        return delay > heartbeatTimeoutMs.toMillis();
    }
}
