package com.wr.ty.grpc.service;

import com.wr.ty.grpc.util.RegistrationMessages;
import com.wr.ty.grpc.util.SubscribeMessages;
import com.xh.demo.grpc.SubscribeServiceGrpc;
import com.xh.demo.grpc.WrTy;
import io.grpc.stub.StreamObserver;
import org.junit.Before;
import org.junit.Test;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

/**
 * @author xiaohei
 * @date 2020/3/1 13:34
 */
public class SubscribeServerImplTest extends ServerTestBase<WrTy.SubscribeRequest, WrTy.SubscribeResponse> {
    SubscribeServiceGrpc.SubscribeServiceStub serviceStub;

    WrTy.ChangeNotification changeNotification = WrTy.ChangeNotification.getDefaultInstance();

    @Before
    public void setup() {
        SubscribeServerImpl subscribeServer = new SubscribeServerImpl(registry, config, nameGenerator, scheduler);
        serviceRegistry.addService(subscribeServer);
        serviceStub = SubscribeServiceGrpc.newStub(channel);
        requestPublish = serviceStub.subscribe(new StreamObserver<WrTy.SubscribeResponse>() {
            @Override
            public void onNext(WrTy.SubscribeResponse value) {
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
    public void testSubscribe() {

        TestPublisher testPublisher = registry.getTestPublisher();
        StepVerifier.create(responseFlux)
                .then(() -> {
                    requestPublish.onNext(SubscribeMessages.CLIENT_HELLO);
                    requestPublish.onNext(SubscribeMessages.CLIENT_HEART);
                    requestPublish.onNext(SubscribeMessages.INTEREST_REGISTRATION);
                    testPublisher.next(changeNotification);
                    responseFlux.complete();
                })
                .expectNext(SubscribeMessages.SERVER_HELLO)
                .expectNext(SubscribeMessages.SERVER_HEART)
                .expectNext(WrTy.SubscribeResponse.newBuilder().setChangeNotification(changeNotification).build())
                .expectComplete()
                .verify();

        testPublisher.assertSubscribers(1);
        requestPublish.onCompleted();
        testPublisher.assertNoSubscribers();


    }


}