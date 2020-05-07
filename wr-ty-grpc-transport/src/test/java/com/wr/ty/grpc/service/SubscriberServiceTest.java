package com.wr.ty.grpc.service;

import com.xh.demo.grpc.ReactorSubscriberServiceGrpc;
import com.xh.demo.grpc.WrTy;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static com.wr.ty.grpc.util.SubscribeMessages.*;

/**
 * @author xiaohei
 * @date 2020/3/14 20:52
 */
public class SubscriberServiceTest {

    Logger logger = LoggerFactory.getLogger(SubscriberServiceTest.class);

    @Test
    public void testHello() throws IOException, InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        InProcessServerBuilder hello = InProcessServerBuilder.forName("hello");
        Server server = hello.addService(new SubscriberService(registry)).build().start();
        ManagedChannel channel = InProcessChannelBuilder.forName("hello").usePlaintext().build();
        ReactorSubscriberServiceGrpc.ReactorSubscriberServiceStub stub = ReactorSubscriberServiceGrpc.newReactorStub(channel);
        EmitterProcessor<WrTy.SubscribeRequest> request = EmitterProcessor.create();
        Flux<WrTy.SubscribeResponse> responseFlux = stub.subscriber(request);
        responseFlux.subscribe(value -> {
            logger.debug("received {} message {}", value.getItemCase(), value.toString());
        });
        request.onNext(CLIENT_HELLO);
        request.onNext(CLIENT_HEART);
        request.onComplete();
        logger.info("channel state {}", channel.isShutdown());
        server.shutdownNow();
        latch.await();


    }
}