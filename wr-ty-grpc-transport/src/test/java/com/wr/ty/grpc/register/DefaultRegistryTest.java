package com.wr.ty.grpc.register;

import com.xh.demo.grpc.WrTy;
import org.junit.Before;
import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.TopicProcessor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author xiaohei
 * @date 2020/2/23 1:52
 */
public class DefaultRegistryTest {

    static WrTy.ChangeNotification.AddChangeNotification addChangeNotification = WrTy.ChangeNotification.AddChangeNotification.newBuilder()
            .setInstanceInfo(WrTy.InstanceInfo.newBuilder().setId("instanceId").build()).build();
    static WrTy.ChangeNotification addNotification = WrTy.ChangeNotification.newBuilder().setAdd(addChangeNotification).build();
    Scheduler scheduler;
    Registry registry;

    @Before
    public void setup() {
        scheduler = Schedulers.newElastic("register-test");
        registry = new DefaultRegistry(scheduler);
    }

    @Test
    public void testMultipleSubscriber() throws InterruptedException {
        EmitterProcessor<Integer> emitter = EmitterProcessor.<Integer>create();
        Scheduler scheduler = Schedulers.newElastic("test");
        AtomicInteger count = new AtomicInteger(0);
        scheduler.schedulePeriodically(() -> {
            emitter.onNext(count.incrementAndGet());
        },1,1, TimeUnit.MILLISECONDS);
        Flux<Integer> flux = emitter.publishOn(scheduler);

        Disposable subscribe1 = flux.subscribe(value -> {
            String name = Thread.currentThread().getName();
            System.out.println(name + "----1----" + value);
        });
        CountDownLatch latch = new CountDownLatch(1);

        Disposable subscribe2 = flux.subscribe(value -> {
            String name = Thread.currentThread().getName();
            System.out.println(name + "----2----" + value);
        });

        Thread.sleep(3000);

        System.out.println("stop all subscriber");
        subscribe1.dispose();
        subscribe2.dispose();


        System.out.println("start third subscriber");
        flux.subscribe(value -> {
            String name = Thread.currentThread().getName();
            System.out.println(name + "----3----" + value);
        },e->{},()->{
            System.out.println("3completed");
        });
        latch.await();

//        CountDownLatch latch = new CountDownLatch(2);
//        scheduler.schedule(() -> {
//            Flux<WrTy.ChangeNotification> flux = registry.subscribe(null);
//            StepVerifier.create(flux)
//                    .then(() -> registry.register(addNotification))
//                    .expectNext(addNotification)
//                    .expectComplete()
//                    .log()
//                    .verify();
//            latch.countDown();
//        });
//        scheduler.schedule(() -> {
//            Flux<WrTy.ChangeNotification> flux = registry.subscribe(null);
//            StepVerifier.create(flux)
//                    .expectNext(addNotification)
//                    .then(() -> registry.shutDown())
//                    .expectComplete()
//                    .log()
//                    .verify();
//            latch.countDown();
//        });
//        latch.await();

    }

    @Test
    public void testSubscriberNoComplete() throws InterruptedException {
        EmitterProcessor<Integer> emitterProcessor1 = EmitterProcessor.create();
        TopicProcessor<Integer> emitterProcessor = TopicProcessor.create();
        ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();
        map.put(1, 1);
        map.put(2, 2);
        map.put(3, 3);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        List<Integer> collect = map.values().stream().collect(Collectors.toList());
        Disposable subscribe = emitterProcessor.subscribe();
        subscribe.dispose();
//        System.out.println("emitterProcessor cancelled--------"+emitterProcessor.i);
        Flux.fromIterable(collect).log("init").concatWith(emitterProcessor.log("hadd")).log().subscribe(value -> {
            System.out.println("value :" + value);
        }, error -> {
            System.out.println("error");
        }, () -> {
            System.out.println("completed");
            countDownLatch.countDown();
        });

        countDownLatch.await();
    }

}