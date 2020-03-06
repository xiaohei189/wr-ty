package com.wr.ty.grpc.handler.client;

import com.wr.ty.grpc.SubscriberFluxSinkWrap;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
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
import java.util.logging.Level;

import static com.wr.ty.grpc.util.ProtocolMessageEnvelopes.HEART_BEAT;
import static com.xh.demo.grpc.WrTy.ProtocolMessageEnvelope.ItemCase.HEARTBEAT;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ClientHeartbeatHandler implements ChannelHandler {

    private static final Throwable HEARTBEAT_TIMEOUT = new IOException("Heartbeat timeout");

    private final Duration heartbeatIntervalMs;
    private final Duration heartbeatTimeoutMs;
    private final Scheduler scheduler;
    private String prefix;

    private Logger logger = LoggerFactory.getLogger(ClientHeartbeatHandler.class);

    public ClientHeartbeatHandler(Duration heartbeatIntervalMs, Duration heartbeatTimeoutMs, Scheduler scheduler) {
        this.heartbeatIntervalMs = heartbeatIntervalMs;
        this.heartbeatTimeoutMs = heartbeatTimeoutMs;
        this.scheduler = scheduler;
    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream, ChannelPipeline pipeline) {
        return Flux.create(fluxSink -> {
            prefix = pipeline.pipelineId();
            AtomicLong lastHeartbeatReply = new AtomicLong(-1);
            Flux<WrTy.ProtocolMessageEnvelope> heartbeatTask = Flux.interval(heartbeatIntervalMs, heartbeatIntervalMs, scheduler)
                    .flatMap(tick -> {
                        if (isLate(lastHeartbeatReply)) {
                            logger.debug("No heartbeat reply from server received in {} ms", heartbeatTimeoutMs);
                            return Flux.error(HEARTBEAT_TIMEOUT);
                        }
                        return Flux.just(HEART_BEAT);
                    });
            Flux<WrTy.ProtocolMessageEnvelope> intercepted = FluxUtil.mergeWhenAllActive(inputStream, heartbeatTask);
            pipeline.handle(intercepted)
                    .doOnNext(value -> {
                        if (value.getItemCase() == HEARTBEAT) {
                            logger.debug("{} Heartbeat reply from server received", prefix);
                            lastHeartbeatReply.set(scheduler.now(MILLISECONDS));
                        }
                    })
                    .filter(value -> value.getItemCase() != HEARTBEAT)
                    .subscribe(new SubscriberFluxSinkWrap(fluxSink));

        });


    }


    private boolean isLate(AtomicLong lastHeartbeatReply) {
        if (lastHeartbeatReply.get() < 0) {
            lastHeartbeatReply.set(scheduler.now(MILLISECONDS));
            return false;
        }
        long currentTime = scheduler.now(MILLISECONDS);
        long lastTime = lastHeartbeatReply.get();
        long delay = currentTime - lastTime;
//        logger.debug("{} heartbeat current time {}, lastTime {}, delay {}", prefix, currentTime, lastTime, delay);
        return delay > heartbeatTimeoutMs.toMillis();
    }
}
