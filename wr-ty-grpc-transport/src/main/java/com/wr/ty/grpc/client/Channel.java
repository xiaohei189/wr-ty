package com.wr.ty.grpc.client;

//import com.xh.demo.grpc.ReactorWrTyChannelGrpc;
import com.xh.demo.grpc.WrTy;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.FluxSink;
import reactor.retry.DefaultContext;
import reactor.retry.Retry;

import java.time.Duration;

import static com.xh.demo.grpc.WrTy.Response.ItemCase.SERVERHELLO;

/**
 * @author xiaohei
 * @date 2020/3/11 23:57
 */
public class Channel {

    private Logger logger = LoggerFactory.getLogger(Channel.class);
//    private final FluxProcessor<WrTy.Request, WrTy.Request> request;
//    private final FluxProcessor<WrTy.Response, WrTy.Response> response;
//    private Disposable heartbeatTask;
//    private final Duration retryDelayMs;

    public Channel(ManagedChannel channel, Duration retryDelayMs) {
//        this.retryDelayMs = retryDelayMs;
//        request = EmitterProcessor.create();
//        response = EmitterProcessor.create();
//        ReactorWrTyChannelGrpc.ReactorWrTyChannelStub stub = ReactorWrTyChannelGrpc.newReactorStub(channel);
//        FluxSink<WrTy.Response> sink = response.sink();
//
//        response.doOnNext(value -> {
//            WrTy.Response.ItemCase itemCase = value.getItemCase();
//            if (itemCase == SERVERHELLO) {
//                heartbeatTask = Flux.interval(Duration.ofSeconds(5)).subscribe(t -> request.onNext(null));
//            }
//        });
//        Retry<Object> retry = Retry.any().fixedBackoff(this.retryDelayMs).retryMax(10).doOnRetry(value -> {
//            DefaultContext context = (DefaultContext) value;
//            Throwable exception = value.exception();
//            if (heartbeatTask != null) {
//                heartbeatTask.dispose();
//            }
//            logger.debug(" Reconnecting times {} internal pipeline terminated earlier with an error ({})", context.iteration(), exception.getMessage());
//        });
//
//        stub.connect(request).retryWhen(retry).subscribe(sink::next, sink::error, sink::complete);
    }

}
