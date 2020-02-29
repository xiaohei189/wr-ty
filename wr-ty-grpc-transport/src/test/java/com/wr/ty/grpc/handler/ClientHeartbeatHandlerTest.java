package com.wr.ty.grpc.handler;

import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.grpc.handler.client.ClientHeartbeatHandler;
import com.wr.ty.grpc.transport.ChannelHandlerStub;
import com.xh.demo.grpc.WrTy;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

import static com.wr.ty.grpc.util.ProtocolMessageEnvelopes.SERVER_HELLO;

/**
 * @author xiaohei
 * @date 2020/2/16 23:00
 */
public class ClientHeartbeatHandlerTest {

    VirtualTimeScheduler scheduler = VirtualTimeScheduler.create();
    private ChannelPipeline channelPipeline;
    private TestPublisher<WrTy.ProtocolMessageEnvelope> inputPublic;
    private Flux<WrTy.ProtocolMessageEnvelope> reply;
    ClientHeartbeatHandler handler;

    @Before
    public void setup() {
        Duration duration = Duration.ofMillis(1000 * 5);
        handler = new ClientHeartbeatHandler(duration,duration, scheduler);
        channelPipeline = new ChannelPipeline("client heart", handler, new ChannelHandlerStub());
        inputPublic = TestPublisher.create();
        reply = channelPipeline.getFirst().handle(inputPublic.flux());
    }

    @Test
    public void testHeartbeat() {
        StepVerifier.create(reply)
                .then(() -> scheduler.advanceTimeBy(Duration.ofSeconds(5)))
//                .expectNext(HEART_BEAT)  should not receive heartbeat message , deal with heartbeat in inner
                .then(() -> {
                    inputPublic.complete();
                })
                .expectComplete()
                .log()
                .verify(Duration.ofSeconds(5));

    }

    @Test
    public void testPassHandshakeMessage() {
        StepVerifier.create(reply)
                .then(() -> inputPublic.emit(SERVER_HELLO))
                .expectNext(SERVER_HELLO)
                .then(() -> {
                    inputPublic.complete();
                })
                .expectComplete()
                .log()
                .verify(Duration.ofSeconds(5));

    }


}