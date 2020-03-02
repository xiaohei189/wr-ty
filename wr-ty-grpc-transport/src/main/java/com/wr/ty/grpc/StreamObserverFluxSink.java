package com.wr.ty.grpc;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.FluxSink;

import java.util.function.Function;

/**
 * @author xiaohei
 * @date 2020/3/1 21:41
 */
public class StreamObserverFluxSink<IN, OUT> implements StreamObserver<IN> {
    private final static Logger logger = LoggerFactory.getLogger(StreamObserverFluxSink.class);
    private final FluxSink<OUT> fluxSink;
    private final Function<IN, OUT> mapper;

    public StreamObserverFluxSink(FluxSink<OUT> fluxSink, Function<IN, OUT> mapper) {
        this.fluxSink = fluxSink;
        this.mapper = mapper;
    }

    @Override
    public void onNext(IN o) {
        try {
            OUT apply = mapper.apply(o);
            fluxSink.next(apply);
        } catch (Throwable throwable) {
            logger.error("in mapper convert error", throwable);
            fluxSink.error(throwable);
        }

    }

    @Override
    public void onError(Throwable throwable) {
        fluxSink.error(throwable);
        logger.error("", throwable);

    }

    @Override
    public void onCompleted() {
        logger.debug("complete");

        fluxSink.complete();
    }
}
