package com.wr.ty.grpc.service;

import com.wr.ty.grpc.util.RegistrationMessages;
import com.xh.demo.grpc.RegistrationServiceGrpc;
import com.xh.demo.grpc.WrTy;
import io.grpc.stub.StreamObserver;
import org.junit.Before;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;

/**
 * @author xiaohei
 * @date 2020/2/20 17:18
 */
public class RegistrationServerImplTest extends ServerTestBase<WrTy.RegistrationRequest, WrTy.RegistrationResponse> {
    private RegistrationServiceGrpc.RegistrationServiceStub registrationStub;

    @Before
    public void setup() {
        RegistrationServiceGrpc.RegistrationServiceImplBase registrationImpl = new RegistrationServerImpl(registry, config, nameGenerator, scheduler);
        serviceRegistry.addService(registrationImpl);
        registrationStub = RegistrationServiceGrpc.newStub(channel);
        requestPublish = registrationStub.register(new StreamObserver<WrTy.RegistrationResponse>() {
            @Override
            public void onNext(WrTy.RegistrationResponse value) {
                responseFlux.next(value);
            }

            @Override
            public void onError(Throwable t) {
                responseFlux.error(t);
            }

            @Override
            public void onCompleted() {
                responseFlux.complete();
            }
        });
    }

    @Test
    public void testRegister() {
        StepVerifier.create(responseFlux)
                .then(() -> {
                    requestPublish.onNext(RegistrationMessages.CLIENT_HELLO);
                    requestPublish.onNext(RegistrationMessages.CLIENT_HEART);
                    requestPublish.onNext(RegistrationMessages.INSTANCE);
                    responseFlux.complete();
                })
                .expectNext(RegistrationMessages.SERVER_HELLO)
                .expectNext(RegistrationMessages.SERVER_HEART)
                .expectNext(RegistrationMessages.ACK)
                .expectComplete()
                .verify();
    }

    @Test
    public void testRegisterTimeout() {
        StepVerifier.create(responseFlux)
                .then(() -> {
                    requestPublish.onNext(RegistrationMessages.CLIENT_HELLO);
                })
                .expectNext(RegistrationMessages.SERVER_HELLO)
                .then(() -> scheduler.advanceTimeBy(Duration.ofSeconds(40)))
                .expectError()
                .verify();
    }

}