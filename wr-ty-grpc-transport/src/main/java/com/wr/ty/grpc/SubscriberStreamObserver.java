package com.wr.ty.grpc;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;

import java.util.function.Function;

/**
 * @author xiaohei
 * @date 2020/3/1 21:26
 */
public class SubscriberStreamObserver<IN, OUT> extends BaseSubscriber<IN> {
   private final static Logger logger= LoggerFactory.getLogger(SubscriberStreamObserver.class);
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
            logger.error("",throwable);
            hookOnError(throwable);
        }
    }

    @Override
    protected void hookOnComplete() {
        logger.debug("completed");
        stream.onCompleted();
    }

    @Override
    protected void hookOnError(Throwable throwable) {
        logger.error("",throwable);
        stream.onError(throwable);
    }
}
