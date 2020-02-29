package com.wr.ty.grpc.handler;

import com.wr.ty.grpc.client.RegistrationClientTransportHandler;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.xh.demo.grpc.RegistrationServiceGrpc;
import com.xh.demo.grpc.WrTy;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import io.grpc.util.MutableHandlerRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import reactor.core.publisher.Flux;
import reactor.test.publisher.TestPublisher;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author xiaohei
 * @date 2020/2/19 20:50
 */
@RunWith(JUnit4.class)
public class RegistrationClientTransportHandlerTest {
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private final MutableHandlerRegistry serviceRegistry = new MutableHandlerRegistry();

    private RegistrationServiceGrpc.RegistrationServiceStub registrationStub;
    ManagedChannel channel;

    @Before
    public void setup() throws IOException {
        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();
        // Use a mutable service registry for later registering the service impl for each test case.
        grpcCleanup.register(InProcessServerBuilder.forName(serverName)
                .fallbackHandlerRegistry(serviceRegistry).directExecutor().build().start());
        channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
        registrationStub = RegistrationServiceGrpc.newStub(channel);
    }

    @After
    public void tearDown() throws Exception {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    public void handle() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<StreamObserver<WrTy.RegistrationResponse>> responseObserverRef = new AtomicReference();
        RegistrationServiceGrpc.RegistrationServiceImplBase registrationImpl = new RegistrationServiceGrpc.RegistrationServiceImplBase() {
            @Override
            public StreamObserver<WrTy.RegistrationRequest> register(StreamObserver<WrTy.RegistrationResponse> responseObserver) {
                responseObserverRef.set(responseObserver);
                StreamObserver requestStream = new StreamObserver<WrTy.RegistrationRequest>() {
                    @Override
                    public void onNext(WrTy.RegistrationRequest value) {
                        System.out.printf("received " + value.toString());
                    }

                    @Override
                    public void onError(Throwable t) {
                    }

                    @Override
                    public void onCompleted() {

                    }
                };
                return requestStream;
            }
        };
        serviceRegistry.addService(registrationImpl);
        ChannelHandler handler = new RegistrationClientTransportHandler(channel);
        TestPublisher<WrTy.ProtocolMessageEnvelope> publisher = TestPublisher.create();
        Flux<WrTy.ProtocolMessageEnvelope> response = handler.handle(publisher.flux());
        StreamObserver<WrTy.RegistrationRequest> request = registrationStub.register(new StreamObserver<WrTy.RegistrationResponse>() {
            @Override
            public void onNext(WrTy.RegistrationResponse value) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
        });
        request.onNext(WrTy.RegistrationRequest.getDefaultInstance());
//        latch.await();

    }
}