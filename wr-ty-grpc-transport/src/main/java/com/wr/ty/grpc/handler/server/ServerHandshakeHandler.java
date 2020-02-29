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
import com.wr.ty.grpc.core.channel.ChannelContext;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.util.ProtocolMessageEnvelopes;
import com.wr.ty.grpc.util.SourceIdGenerator;
import com.xh.demo.grpc.WrTy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import static com.xh.demo.grpc.WrTy.ProtocolMessageEnvelope.MessageOneOfCase.CLIENTHELLO;


/**
 *
 */
public class ServerHandshakeHandler implements ChannelHandler {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandshakeHandler.class);

    private final SourceIdGenerator idGenerator;

    private ChannelContext channelContext;

    public ServerHandshakeHandler(SourceIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public void init(ChannelContext channelContext) {
        if (!channelContext.hasNext()) {
            throw new IllegalStateException("ServerHandshakeHandler cannot be last handler in the pipeline");
        }
        this.channelContext = channelContext;
    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream) {
        return Flux.create(fluxSink -> {
            //fluxSink order sent
            Flux<WrTy.ProtocolMessageEnvelope> interceptedInput = inputStream.flatMap(inputNotification -> {
                if (inputNotification.getMessageOneOfCase() != CLIENTHELLO) {
                    return Flux.just(inputNotification);
                }
                WrTy.ClientHello clientHello = inputNotification.getClientHello();
                // reply hello
                fluxSink.next(ProtocolMessageEnvelopes.SERVER_HELLO);
                return Flux.empty();
            });
            channelContext.next().handle(interceptedInput).subscribe(new SubscriberFluxSinkWrap<>(fluxSink));
        });
    }
}
