package com.wr.ty.grpc;

import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Schedulers;

/**
 * @author weirui (xiao hei)
 * @date 2020/5/16 0:47
 */
public class TestCase {

    @Test
    public void HelloWorld() {

        ReplayProcessor<Integer> processor = ReplayProcessor.create();

        FluxSink<Integer> sink = processor.sink();
        Flux<Integer> source = processor.publishOn(Schedulers.elastic());
        Disposable subscribe = source.subscribe(value -> {
            System.out.println(Thread.currentThread().getName() + "-------- subscriber one:" + value);
        });
        for (int i = 0; i < 10; i++) {
            System.out.println(Thread.currentThread().getName() + "-------- add data source" + i);
            sink.next(i);
        }
        subscribe.dispose();
        source.subscribe(value -> {
            System.out.println(Thread.currentThread().getName() + "-------- subscriber two:" + value);
        });
    }
}
