package com.wr.ty.grpc.register;

import com.wr.ty.grpc.util.ChangeNotifications;
import com.xh.demo.grpc.WrTy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

/**
 * @author xiaohei
 * @date 2020/2/23 1:52
 */
public class DefaultRegistryTest {

    WrTy.ChangeNotification addNotification = WrTy.ChangeNotification.newBuilder().setAdd(WrTy.ChangeNotification.AddChangeNotification.getDefaultInstance()).build();

    Scheduler scheduler;

    Registry registry;

    @Before
    public void setup() {
        scheduler = Schedulers.newElastic("register-test");
        registry = new DefaultRegistry(scheduler);
    }

    @Test
    public void testHello() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        scheduler.schedule(() -> {
            Flux<WrTy.ChangeNotification> flux = registry.subscribe(null);
            StepVerifier.create(flux)
                    .then(() -> registry.register(addNotification))
                    .expectNext(addNotification)
                    .expectComplete()
                    .log()
                    .verify();
            latch.countDown();
        });
        scheduler.schedule(() -> {
            Flux<WrTy.ChangeNotification> flux = registry.subscribe(null);
            StepVerifier.create(flux)
                    .expectNext(addNotification)
                    .then(() -> registry.shutDown())
                    .expectComplete()
                    .log()
                    .verify();
            latch.countDown();
        });
        latch.await();

    }


}