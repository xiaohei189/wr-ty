package com.wr.ty.grpc.handler.client;

import com.wr.ty.grpc.ChannelLogger;
import com.wr.ty.grpc.core.channel.ChannelContext;
import com.wr.ty.grpc.core.channel.ChannelHandler;
import com.xh.demo.grpc.WrTy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

/**
 * @author xiaohei
 * @date 2020/2/13 0:50
 */
public class LoggingChannelHandler implements ChannelHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoggingChannelHandler.class);


    private ChannelContext channelContext;

    private String prefix;
    private ChannelLogger channelLogger;

    @Override
    public void init(ChannelContext channelContext) {
        if (!channelContext.hasNext()) {
            throw new IllegalStateException("Expected next handler in the pipeline");
        }
        this.channelContext = channelContext;
        channelLogger = new ChannelLogger(logger, channelContext.getPipeline().getPipelineId());
        this.channelContext = channelContext;
    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream) {
        Flux<WrTy.ProtocolMessageEnvelope> messageEnvelopeFlux = inputStream
                .doOnSubscribe(value -> channelLogger.debug("Subscribed to input stream"))
                .doOnCancel(() -> channelLogger.debug("Unsubscribed from input stream"))
                .doOnNext(value -> {
                    WrTy.ProtocolMessageEnvelope.ItemCase itemCase = value.getItemCase();
                    switch (itemCase) {
                        case HEARTBEAT:
                            channelLogger.debug("Sending Heartbeat");
                            break;
                        case CLIENTHELLO:
                            channelLogger.debug("Sending Hello");
                            break;
                        case INSTANCEINFO:
                            channelLogger.debug("Sending Data");
                            break;
                    }
                })
                .doOnError(e -> channelLogger.debug("Input terminated with an error", e))
                .doOnComplete(() -> channelLogger.debug("Input onCompleted"));
        return channelContext.next()
                .handle(messageEnvelopeFlux)
                .doOnSubscribe(value -> channelLogger.debug("Subscribed to reply stream"))
                .doOnCancel(() -> channelLogger.debug("Unsubscribed from reply stream"))
                .doOnNext(value -> {
                    switch (value.getItemCase()) {
                        case HEARTBEAT:
                            channelLogger.debug(" Received Heartbeat");
                            break;
                        case CLIENTHELLO:
                            channelLogger.debug(" Received Hello");
                            break;
                        case INSTANCEINFO:
                            channelLogger.debug(" Received Data={}", value.toString());
                            break;
                    }
                })
                .doOnError(e -> channelLogger.debug("Reply stream terminated with an error", e))
                .doOnComplete(() -> channelLogger.debug("Reply stream onCompleted"));
    }
}
