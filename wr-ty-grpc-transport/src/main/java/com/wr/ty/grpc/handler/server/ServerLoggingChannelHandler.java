package com.wr.ty.grpc.handler.server;

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
public class ServerLoggingChannelHandler implements ChannelHandler {

    private static final Logger logger = LoggerFactory.getLogger(ServerLoggingChannelHandler.class);

    private ChannelContext channelContext;

    @Override
    public void init(ChannelContext channelContext) {
        if (!channelContext.hasNext()) {
            throw new IllegalStateException("Expected next handler in the pipeline");
        }
        this.channelContext = channelContext;
        String prefix = '[' + channelContext.getPipeline().getPipelineId() + "] ";
    }

    @Override
    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream) {
        return channelContext.next()
                .handle(
                        inputStream
                                .doOnSubscribe(value -> logger.debug("Subscribed to input stream"))
                                .doOnCancel(() -> logger.debug("Unsubscribed from input stream"))
                                .doOnNext(value -> {
                                    WrTy.ProtocolMessageEnvelope.ItemCase itemCase = value.getItemCase();
                                    switch (itemCase) {
                                        case HEARTBEAT:
                                            logger.debug("Received Heartbeat");
                                            break;
                                        case CLIENTHELLO:
                                            logger.debug("Received Client Hello");
                                            break;
                                        case INSTANCEINFO:
                                            logger.debug("Received Data {}", value.toString());
                                            break;
                                    }
                                })
                                .doOnError(e -> logger.debug("Input terminated with an error", e))
                                .doOnComplete(() -> logger.debug("Input onCompleted"))
                )
                .doOnSubscribe(value -> logger.debug("Subscribed to reply stream"))
                .doOnCancel(() -> logger.debug("Unsubscribed from reply stream"))
                .doOnNext(value -> {
                    switch (value.getItemCase()) {
                        case HEARTBEAT:
                            logger.debug("Send Heartbeat");
                            break;
                        case CLIENTHELLO:
                            logger.debug("Send Server Hello");
                            break;
                        case INSTANCEINFO:
                            logger.debug("Send Data ", value.getInstanceInfo().toString());
                            break;
                    }
                })
                .doOnError(e -> logger.debug("Reply stream terminated with an error", e))
                .doOnComplete(() -> logger.debug("Reply stream onCompleted"));
    }
}
