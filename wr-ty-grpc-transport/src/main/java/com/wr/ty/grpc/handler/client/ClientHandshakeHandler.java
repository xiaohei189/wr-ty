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

package com.wr.ty.grpc.handler.client;

import com.wr.ty.grpc.ChannelLogger;
import com.wr.ty.grpc.SubscriberFluxSinkWrap;
import com.wr.ty.grpc.core.channel.ChannelContext;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.util.ProtocolMessageEnvelopes;
import com.xh.demo.grpc.WrTy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 *
 */
public class ClientHandshakeHandler implements ChannelHandler {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandshakeHandler.class);

    protected static final IllegalStateException UNEXPECTED_HANDSHAKE_REPLY = new IllegalStateException("Unexpected handshake reply");
    protected static final IllegalStateException DATA_BEFORE_HANDSHAKE_REPLY = new IllegalStateException("Data before handshake reply");
    private ChannelLogger channelLogger;
    protected ChannelContext channelContext;

    @Override
    public void init(ChannelContext channelContext) {
        if (!channelContext.hasNext()) {
            throw new IllegalStateException("Expected next element in the pipeline");
        }
        channelLogger = new ChannelLogger(logger, channelContext.getPipeline().getPipelineId());

        this.channelContext = channelContext;
    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream) {
        return Flux.create(fluxSink -> {
            channelLogger.debug("Subscription to ClientHandshakeHandler start");
            AtomicBoolean handshakeCompleted = new AtomicBoolean(false);
            channelContext.next().handle(Flux.just(ProtocolMessageEnvelopes.CLIENT_HELLO).concatWith(inputStream))
                    .flatMap(handshakeVerifier(handshakeCompleted))
                    .doOnCancel(() -> channelLogger.debug("Unsubscribing from ClientHandshakeHandler"))
                    .subscribe(new SubscriberFluxSinkWrap(fluxSink));

        });
    }

    protected Function<WrTy.ProtocolMessageEnvelope, Flux<WrTy.ProtocolMessageEnvelope>> handshakeVerifier(AtomicBoolean handshakeCompleted) {
        return replyNotification -> {
            if (replyNotification.getMessageOneOfCase() == WrTy.ProtocolMessageEnvelope.MessageOneOfCase.SERVERHELLO) {
                if (!handshakeCompleted.getAndSet(true)) {
                    channelLogger.debug("Handshake has completed");

                    return Flux.empty();
                }
            }
            if (replyNotification.getMessageOneOfCase() == WrTy.ProtocolMessageEnvelope.MessageOneOfCase.INSTANCEINFO && !handshakeCompleted.get()) {
                channelLogger.error("Data sent from server before handshake has completed");
                return Flux.error(DATA_BEFORE_HANDSHAKE_REPLY);
            }
            return Flux.just(replyNotification);
        };
    }
}
