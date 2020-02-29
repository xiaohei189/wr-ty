package com.wr.ty.grpc.register;

import com.wr.ty.grpc.util.ChangeNotifications;
import com.xh.demo.grpc.WrTy;
import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

/**
 * @author xiaohei
 * @date 2020/2/23 1:52
 */
public class RegistryTest {
    @Test
    public void testHello() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Registry registry = new Registry(Schedulers.newElastic("register-------"));
        Disposable subscribe = Flux.interval(Duration.ofSeconds(3)).doOnNext(value -> {
            WrTy.InstanceInfo instanceInfo = WrTy.InstanceInfo.newBuilder().setId(value + "").setTimeStamp(System.currentTimeMillis()).build();
            WrTy.ChangeNotification notification = ChangeNotifications.newAddNotification(instanceInfo);
            registry.register(notification);
        }).subscribe();

        Flux<WrTy.ChangeNotification> changeNotificationFlux = registry.interest(null);
        Disposable subscribe1 = changeNotificationFlux.subscribe(value -> {
            System.out.println("subscribe1 receive change notification: " + value);
        });

        Thread.sleep(1000*10);

        Disposable subscribe2 = registry.interest(null).subscribe(value -> {
            System.out.println("subscribe2 receive change notification: " + value);
        });
        Thread.sleep(1000*10);
        subscribe2.dispose();
        latch.await();
    }


}