/*
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wr.ty.grpc.handler.server;

import com.wr.ty.grpc.SubscriberFluxSinkWrap;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.grpc.util.FluxUtil;
import com.xh.demo.grpc.WrTy;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.wr.ty.grpc.util.ProtocolMessageEnvelopes.HEART_BEAT;
import static com.xh.demo.grpc.WrTy.ProtocolMessageEnvelope.ItemCase.HEARTBEAT;


/**
 *
 */

public class ServerHeartbeatHandler implements ChannelHandler {

    private static final IOException HEARTBEAT_TIMEOUT = new IOException("Heartbeat timeout");

    private final Duration heartbeatTimeoutMs;
    private final Scheduler scheduler;


    /**
     * @param heartbeatTimeoutMs
     * @param scheduler
     */
    public ServerHeartbeatHandler(Duration heartbeatTimeoutMs, Scheduler scheduler) {
        this.heartbeatTimeoutMs = heartbeatTimeoutMs;
        this.scheduler = scheduler;
    }


    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream, ChannelPipeline pipeline) {
        return Flux.create(fluxSink -> {
            EmitterProcessor<WrTy.ProtocolMessageEnvelope> heartbeatReplies = EmitterProcessor.create();
            AtomicLong lastTimeoutUpdate = new AtomicLong(scheduler.now(TimeUnit.MILLISECONDS));

            // Intercept heartbeats from input
            Flux<WrTy.ProtocolMessageEnvelope> interceptedInput = inputStream
                    .flatMap(inputNotification -> {
                        if (inputNotification.getItemCase() == HEARTBEAT) {
                            heartbeatReplies.onNext(HEART_BEAT); // Send back heartbeat
                            lastTimeoutUpdate.set(scheduler.now(TimeUnit.MILLISECONDS));
                            return Flux.empty();
                        }
                        return Flux.just(inputNotification);
                    });

            // Create heartbeat timeout trigger
            Flux<WrTy.ProtocolMessageEnvelope> timeoutTrigger = Flux.interval(heartbeatTimeoutMs, heartbeatTimeoutMs, scheduler)
                    .flatMap(tick -> {
                        long delay = scheduler.now(TimeUnit.MILLISECONDS) - lastTimeoutUpdate.get();
                        if (delay >= heartbeatTimeoutMs.toMillis()) {
                            return Flux.error(HEARTBEAT_TIMEOUT);
                        }
                        return Flux.empty();
                    });

//            Flux<WrTy.ProtocolMessageEnvelope> flux = Flux.merge(
//                    channelContext.next().handle(interceptedInput).materialize(),
//                    heartbeatReplies.materialize()
//                    , timeoutTrigger.materialize()
////                 when inner flux has one complete,complete all flux ,not wait for all flux complete
//            ).takeWhile(value -> value.hasValue() || value.hasError()).dematerialize();

            FluxUtil.mergeWhenAllActive(
                    pipeline.handle(interceptedInput),
                    heartbeatReplies
                    , timeoutTrigger
//                 when inner flux has one complete,complete all flux ,not wait for all flux complete
            ).subscribe(new SubscriberFluxSinkWrap<>(fluxSink));
        });
    }
}
