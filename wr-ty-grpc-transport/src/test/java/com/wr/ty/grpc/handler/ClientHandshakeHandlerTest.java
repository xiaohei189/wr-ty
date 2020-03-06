package com.wr.ty.grpc.handler;

import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.grpc.handler.client.ClientHandshakeHandler;
import com.wr.ty.grpc.util.FluxUtil;
import com.xh.demo.grpc.WrTy;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import java.time.Duration;

import static com.wr.ty.grpc.util.ProtocolMessageEnvelopes.*;

/**
 * @author xiaohei
 * @date 2020/2/16 22:40
 */
public class ClientHandshakeHandlerTest {
    private ChannelPipeline channelPipeline;
    private TestPublisher<WrTy.ProtocolMessageEnvelope> publish;
    private Flux<WrTy.ProtocolMessageEnvelope> reply;
    TestPublisher<WrTy.ProtocolMessageEnvelope> replayPublisher;
    ClientHandshakeHandler handler;

    @Before
    public void setup() {
        handler = new ClientHandshakeHandler();
        replayPublisher = TestPublisher.create();
//        channelPipeline = new ChannelPipeline("client handshake", handler, new PublisherChannelHandler(replayPublisher));
        publish = TestPublisher.create();
        reply = channelPipeline.handle(publish.flux());
    }
    @Test
    public void testHandshake() throws InterruptedException {
        StepVerifier.create(reply)
                .expectNext(CLIENT_HELLO)
                .then(() -> replayPublisher.next(SERVER_HELLO))
                .then(() -> replayPublisher.emit(DEFAULT_INSTANCE))
                .expectNext(DEFAULT_INSTANCE)
                .expectComplete()
                .verify(Duration.ofSeconds(5));

    }


    @Test
    public void testThrowWhenSendDataBeforeReplyServerHello() throws InterruptedException {
        StepVerifier.create(reply)
                .expectNext(CLIENT_HELLO)
                .then(() -> replayPublisher.emit(DEFAULT_INSTANCE))
                .expectErrorMessage("Data before handshake reply")
                .verify(Duration.ofSeconds(5));

    }


    static class PublisherChannelHandler implements ChannelHandler {

        final Flux<WrTy.ProtocolMessageEnvelope> flux;

        PublisherChannelHandler(TestPublisher<WrTy.ProtocolMessageEnvelope> testPublisher) {
            this.flux = testPublisher.flux();
        }


        @Override
        public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream,ChannelPipeline pipeline) {
            return FluxUtil.mergeWhenAllActive(inputStream, flux);
        }
    }

}