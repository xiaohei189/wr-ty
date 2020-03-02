package com.wr.ty.grpc;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.FluxSink;
import reactor.util.context.Context;

/**
 * Subscriber wrap FluxSink ,forward signal to download stream fluxSink
 *
 * @author xiaohei
 * @date 2020/2/19 18:19
 */
public class SubscriberFluxSinkWrap<T> extends BaseSubscriber<T> {

    final FluxSink<T> fluxSink;

    public SubscriberFluxSinkWrap(FluxSink<T> fluxSink) {
        this.fluxSink = fluxSink;
        fluxSink.onDispose(this);
    }

    @Override
    protected void hookOnNext(T value) {
        fluxSink.next(value);
    }

    @Override
    protected void hookOnComplete() {
        fluxSink.complete();
    }

    @Override
    protected void hookOnError(Throwable throwable) {
        fluxSink.error(throwable);
    }

//    @Override
//    public Context currentContext() {
//        return fluxSink.currentContext();
//    }
}
