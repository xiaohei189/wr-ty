package com.wr.ty.grpc.handler.server;

import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.grpc.transport.ChannelHandlerStub;
import com.wr.ty.grpc.util.ProtocolMessageEnvelopes;
import com.xh.demo.grpc.WrTy;
import org.junit.Before;
import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

import static org.junit.Assert.assertTrue;

/**
 * @author xiaohei
 * @date 2020/2/13 20:46
 */
public class ServerHeartbeatHandlerTest {
    private static final long HEARTBEAT_TIMEOUT = 5 * 1000;
    private final ChannelHandler nextHandler = new ChannelHandlerStub();
    private VirtualTimeScheduler testScheduler = VirtualTimeScheduler.create();
    private final ServerHeartbeatHandler handler = new ServerHeartbeatHandler(HEARTBEAT_TIMEOUT, testScheduler);
    private ChannelPipeline channelPipeline;
    TestPublisher<WrTy.ProtocolMessageEnvelope> testPublisher;
    Flux<WrTy.ProtocolMessageEnvelope> reply;

    @Before
    public void setup() {
        channelPipeline = new ChannelPipeline("heartbeat", handler, nextHandler);
        testPublisher = TestPublisher.create();
        reply = channelPipeline.getFirst().handle(testPublisher.flux());
    }


    @Test
    public void testHeartbeat() throws Exception {

        StepVerifier.create(reply)
                .then(() -> testPublisher.next(ProtocolMessageEnvelopes.HEART_BEAT)).as("Send heartbeat")
                .then(() -> testPublisher.next(ProtocolMessageEnvelopes.DEFAULT_INSTANCE)).as("Send instance information")
                .then(() -> testPublisher.complete()).as("Send complete")
                .expectNext(ProtocolMessageEnvelopes.HEART_BEAT).as("Receive heart data")
                .expectNext(ProtocolMessageEnvelopes.DEFAULT_INSTANCE).as("Receive instance information")
                .expectComplete()
                .log()
                .verify(Duration.ofSeconds(5));
        testPublisher.assertNoSubscribers();
    }

    @Test
    public void testTimeout() throws Exception {
        StepVerifier.create(reply)
                .then(() -> testPublisher.next(ProtocolMessageEnvelopes.HEART_BEAT)).as("Send heartbeat")
                .then(() -> testScheduler.advanceTimeBy(Duration.ofSeconds(3))).as("Launch heartbeat check,result should be ok")
                .then(() -> testScheduler.advanceTimeBy(Duration.ofSeconds(8))).as("trigger timeout")
                .expectNext(ProtocolMessageEnvelopes.HEART_BEAT).as("receive heart data")
                .expectError()
                .log()
                .verify(Duration.ofSeconds(5));
        testPublisher.assertNoSubscribers();
    }

    @Test
    public void testInputUnsubscribeCompletesOutput() throws Exception {

        Disposable disposable = reply.subscribe();
        testPublisher.complete();
        assertTrue("when input completed,disposable resource", disposable.isDisposed());

    }


}