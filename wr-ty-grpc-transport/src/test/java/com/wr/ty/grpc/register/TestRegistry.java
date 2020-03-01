package com.wr.ty.grpc.register;

import com.xh.demo.grpc.WrTy;
import reactor.core.publisher.Flux;
import reactor.test.publisher.TestPublisher;

/**
 * @author xiaohei
 * @date 2020/3/1 14:47
 */
public class TestRegistry implements Registry {

    private TestPublisher testPublisher = TestPublisher.create();

    @Override
    public void register(WrTy.ChangeNotification changeNotification) {

    }

    @Override
    public Flux<WrTy.ChangeNotification> subscribe(WrTy.Interest interest) {
        return testPublisher.flux();
    }

    @Override
    public int observers() {
        return 0;
    }

    @Override
    public void shutDown() {

    }

    public TestPublisher getTestPublisher() {
        return this.testPublisher;
    }
}
