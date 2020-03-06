package com.wr.ty.grpc;

import com.wr.ty.grpc.util.ProtocolMessageEnvelopes;
import com.xh.demo.grpc.WrTy;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.FluxSink;

final public class RegistrySubscriber extends BaseSubscriber<WrTy.ChangeNotification> {

    private final FluxSink<WrTy.ProtocolMessageEnvelope> fluxSink;

    public RegistrySubscriber(FluxSink fluxSink) {
        this.fluxSink = fluxSink;
        fluxSink.onDispose(this);
    }

    @Override
    protected void hookOnNext(WrTy.ChangeNotification value) {
        WrTy.ProtocolMessageEnvelope messageEnvelope = ProtocolMessageEnvelopes.fromChangeNotification(value);
        fluxSink.next(messageEnvelope);
    }

    @Override
    protected void hookOnComplete() {
        fluxSink.complete();
    }

    @Override
    protected void hookOnError(Throwable throwable) {
        fluxSink.error(throwable);
    }
}