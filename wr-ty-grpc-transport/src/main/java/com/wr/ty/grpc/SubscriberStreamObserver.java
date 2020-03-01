package com.wr.ty.grpc;

import io.grpc.stub.StreamObserver;
import reactor.core.publisher.BaseSubscriber;

import java.util.function.Function;

/**
 * @author xiaohei
 * @date 2020/3/1 21:26
 */
public class SubscriberStreamObserver<IN, OUT> extends BaseSubscriber<IN> {
    private final StreamObserver<OUT> stream;
    private final Function<IN, OUT> mapper;

    public SubscriberStreamObserver(StreamObserver<OUT> stream, Function<IN, OUT> mapper) {
        this.stream = stream;
        this.mapper = mapper;
    }

    @Override
    protected void hookOnNext(IN value) {
        try {
            OUT apply = mapper.apply(value);
            stream.onNext(apply);
        } catch (Throwable throwable) {
            hookOnError(throwable);
        }
    }

    @Override
    protected void hookOnComplete() {
        stream.onCompleted();
    }

    @Override
    protected void hookOnError(Throwable throwable) {
        stream.onError(throwable);
    }
}
