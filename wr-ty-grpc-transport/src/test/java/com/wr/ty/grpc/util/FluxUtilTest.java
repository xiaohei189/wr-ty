package com.wr.ty.grpc.util;

import com.xh.demo.grpc.WrTy;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import java.time.Duration;

import static org.junit.Assert.*;

/**
 * @author xiaohei
 * @date 2020/2/19 1:09
 */
public class FluxUtilTest {

    @Test
    public void mergeWhenAllActive() {

        TestPublisher<Long> publisher1 = TestPublisher.create();
        Flux<Long> publisher2 = Flux.interval(Duration.ofSeconds(1));
        TestPublisher<Integer> publisher3 = TestPublisher.create();
        Flux<Long> integerFlux = FluxUtil.mergeWhenAllActive(publisher1.flux(), publisher2);
//        Flux<Integer> flux = FluxUtil.mergeWhenAllActive(integerFlux, publisher3.flux());
        StepVerifier.create(integerFlux.doOnComplete(() -> {
            System.out.println("1111111111111");
        })).then(() -> {
            publisher1.complete();
        }).expectComplete().verify(Duration.ofSeconds(5));
    }

    @Test
    public void propagateCommonError() {

        TestPublisher<Long> publisher1 = TestPublisher.create();
        Flux<Long> publisher2 = Flux.interval(Duration.ofSeconds(1));
        Flux<Long> integerFlux = FluxUtil.mergeWhenAllActive(publisher1.flux(), publisher2);
        StepVerifier.create(integerFlux)
                .then(() -> publisher1.error(new Exception("test exception")))
                .expectError()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    public void loggerTest() {
        Flux.create(fluxSink -> {
            Flux.create(emitter -> {
                fluxSink.next(1);
                fluxSink.next(1);
                fluxSink.next(1);
                fluxSink.complete();
            }).log("add").subscribe(fluxSink::next, fluxSink::error, fluxSink::complete);
        }).log("category").subscribe(value -> {

            System.out.println(value);
        });
    }


}