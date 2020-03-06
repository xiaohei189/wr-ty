package com.wr.ty.grpc.handler.server;

import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.grpc.transport.ChannelHandlerStub;
import com.wr.ty.grpc.util.ProtocolMessageEnvelopes;
import com.wr.ty.grpc.util.SourceIdGenerator;
import com.xh.demo.grpc.WrTy;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import java.time.Duration;

/**
 * @author xiaohei
 * @date 2020/2/16 20:26
 */
public class ServerHandshakeHandlerTest {
    private ChannelPipeline channelPipeline;
    TestPublisher<WrTy.ProtocolMessageEnvelope> testPublisher;
    private final ChannelHandler nextHandler = new ChannelHandlerStub();
    ServerHandshakeHandler handler = new ServerHandshakeHandler();
    Flux<WrTy.ProtocolMessageEnvelope> reply;

    @Before
    public void setup() {
//        channelPipeline = new ChannelPipeline("handshake", handler, nextHandler);
        testPublisher = TestPublisher.create();
        reply = channelPipeline.handle(testPublisher.flux());
    }

    @Test
    public void testHandshake() throws Exception {

        StepVerifier.create(reply)
                .then(() -> testPublisher.emit(ProtocolMessageEnvelopes.CLIENT_HELLO)).as("Send client hello")
                .expectNext(ProtocolMessageEnvelopes.SERVER_HELLO).as("Receive server hello")
                .thenCancel()
                .log()
                .verify(Duration.ofSeconds(5));
        testPublisher.assertNoSubscribers();
    }
}