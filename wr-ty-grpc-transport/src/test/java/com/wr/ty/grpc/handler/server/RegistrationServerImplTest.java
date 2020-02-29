package com.wr.ty.grpc.handler.server;

import com.wr.ty.grpc.register.Registry;
import com.wr.ty.grpc.service.RegistrationServerImpl;
import com.wr.ty.grpc.util.RegistrationMessages;
import com.xh.demo.grpc.RegistrationServiceGrpc;
import com.xh.demo.grpc.WrTy;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.util.MutableHandlerRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;
import reactor.test.scheduler.VirtualTimeScheduler;
import sun.rmi.registry.RegistryImpl;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author xiaohei
 * @date 2020/2/20 17:18
 */
public class RegistrationServerImplTest {
    Logger logger = LoggerFactory.getLogger(RegistrationServerImplTest.class);

    private final MutableHandlerRegistry serviceRegistry = new MutableHandlerRegistry();
    final AtomicReference<StreamObserver<WrTy.RegistrationResponse>> responseObserverRef = new AtomicReference();
    Server server;
    ManagedChannel channel;
    RegistrationServiceGrpc.RegistrationServiceStub registrationStub;
    TestPublisher<Object> responsePublisher = TestPublisher.create();
    StreamObserver<WrTy.RegistrationRequest> requestStreamObserver;
    VirtualTimeScheduler scheduler = VirtualTimeScheduler.create();

    @Before
    public void setup() throws IOException {
        String serverName = InProcessServerBuilder.generateName();
        server = InProcessServerBuilder.forName(serverName)
                .fallbackHandlerRegistry(serviceRegistry).directExecutor().build().start();
        channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
        Registry registry = new Registry(scheduler);
        RegistrationServiceGrpc.RegistrationServiceImplBase registrationImpl = new RegistrationServerImpl(registry, scheduler);
        serviceRegistry.addService(registrationImpl);
        registrationStub = RegistrationServiceGrpc.newStub(channel);
        requestStreamObserver = registrationStub.register(new StreamObserver<WrTy.RegistrationResponse>() {
            @Override
            public void onNext(WrTy.RegistrationResponse value) {
                responsePublisher.next(value);
            }

            @Override
            public void onError(Throwable t) {
                responsePublisher.error(t);
            }

            @Override
            public void onCompleted() {
                responsePublisher.complete();
            }
        });
    }

    @Test
    public void testRegister() {
        StepVerifier.create(responsePublisher)
                .then(() -> {
                    requestStreamObserver.onNext(RegistrationMessages.CLIENT_HELLO);
                    requestStreamObserver.onNext(RegistrationMessages.CLIENT_HEART);
                    requestStreamObserver.onNext(RegistrationMessages.INSTANCE);
                    responsePublisher.complete();
                })
                .expectNext(RegistrationMessages.SERVER_HELLO)
                .expectNext(RegistrationMessages.SERVER_HEART)
                .expectNext(RegistrationMessages.ACK)
                .expectComplete()
                .verify();
    }

    @Test
    public void testRegisterTimeout() {
        StepVerifier.create(responsePublisher)
                .then(() -> {
                    requestStreamObserver.onNext(RegistrationMessages.CLIENT_HELLO);
                })
                .expectNext(RegistrationMessages.SERVER_HELLO)
                .then(() -> scheduler.advanceTimeBy(Duration.ofSeconds(40)))
                .expectError()
                .verify();
    }

    @After
    public void tearDown() throws Exception {
        if (server != null && !server.isShutdown()) {
            server.shutdown();
        }
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }


}