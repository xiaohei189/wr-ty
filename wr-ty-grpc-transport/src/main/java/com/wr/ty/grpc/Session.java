package com.wr.ty.grpc;

import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.grpc.core.channel.ChannelPipelineFactory;
import com.xh.demo.grpc.WrTy;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.FluxSink;

import java.util.function.Function;

public class Session<IN, OUT> implements StreamObserver<IN> {
    private final Logger logger = LoggerFactory.getLogger(Session.class);
    private final StreamObserver<OUT> responseObserver;
    private final ChannelPipeline pipeline;
    private final EmitterProcessor<IN> inputStream = EmitterProcessor.create();
    private final FluxSink<IN> inFluxSink = inputStream.sink();
    private final Disposable subscription;
    private final Function<IN, WrTy.ProtocolMessageEnvelope> inputMap;
    private final Function<WrTy.ProtocolMessageEnvelope, OUT> outMap;

    public Session(StreamObserver<OUT> responseObserver,
            ChannelPipelineFactory pipelineFactory,
            Function<IN, WrTy.ProtocolMessageEnvelope> inputMap,
            Function<WrTy.ProtocolMessageEnvelope, OUT> outMap) {
        this.responseObserver = responseObserver;
        this.pipeline = pipelineFactory.createPipeline().block();
        this.inputMap = inputMap;
        this.outMap = outMap;
        this.subscription = connectPipeline();
    }

    @Override
    public void onNext(IN in) {
        inFluxSink.next(in);

    }

    @Override
    public void onError(Throwable throwable) {
        inFluxSink.error(throwable);
        subscription.dispose();
    }

    @Override
    public void onCompleted() {
        inputStream.onComplete();
        subscription.dispose();
    }

    private Disposable connectPipeline() {
        return pipeline.handle(inputStream.map(inputMap)).map(outMap)
                .doOnCancel(() -> logger.debug("session pipeline cancel"))
                .subscribe(value -> {
                            responseObserver.onNext(value);
                        },
                        e -> {
                            logger.debug("Send onError to transport ({})", e.getMessage());
                            responseObserver.onError(e);
                        },
                        () -> {
                            logger.debug("Send onCompleted to transport ({})");
                            responseObserver.onCompleted();
                        }
                );
    }
}
