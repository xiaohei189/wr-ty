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
import com.wr.ty.grpc.util.ProtocolMessageEnvelopes;
import com.xh.demo.grpc.WrTy;
import reactor.core.publisher.Flux;

import static com.xh.demo.grpc.WrTy.ProtocolMessageEnvelope.ItemCase.CLIENTHELLO;


/**
 *
 */
public class ServerHandshakeHandler  implements ChannelHandler {


    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream, ChannelPipeline pipeline) {
        return Flux.create(fluxSink -> {
            //fluxSink order sent
            Flux<WrTy.ProtocolMessageEnvelope> interceptedInput = inputStream.flatMap(value -> {
                if (value.getItemCase() != CLIENTHELLO) {
                    return Flux.just(value);
                }
                WrTy.ClientHello clientHello = value.getClientHello();
                // reply hello
                fluxSink.next(ProtocolMessageEnvelopes.SERVER_HELLO);
                return Flux.empty();
            });
            pipeline.handle(interceptedInput).subscribe(new SubscriberFluxSinkWrap<>(fluxSink));
        });
    }


}
