//package com.wr.ty.grpc.handler.client;
//
//import com.wr.ty.grpc.core.channel.ChannelContext;
//import com.wr.ty.grpc.core.channel.ChannelHandler;
//import com.xh.demo.grpc.WrTy;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import reactor.core.publisher.Flux;
//
///**
// * @author xiaohei
// * @date 2020/2/13 0:50
// */
//public class LoggingChannelHandler implements ChannelHandler {
//
//    private static final Logger logger = LoggerFactory.getLogger(LoggingChannelHandler.class);
//
//
//    private ChannelContext channelContext;
//
//    private String prefix;
//
//    @Override
//    public void init(ChannelContext channelContext) {
//        if (!channelContext.hasNext()) {
//            throw new IllegalStateException("Expected next handler in the pipeline");
//        }
//        this.channelContext = channelContext;
//
//        prefix = '[' + channelContext.getPipeline().getPipelineId() + "] ";
//    }
//
//    @Override
//    public Flux<WrTy.ProtocolMessageEnvelope> handle(Flux<WrTy.ProtocolMessageEnvelope> inputStream) {
//        return channelContext.next()
//                .handle(
//                        inputStream
//                                .doOnSubscribe(value -> logger.debug("{} Subscribed to input stream", prefix))
//                                .doOnCancel(() -> logger.debug("{} Unsubscribed from input stream", prefix))
//                                .doOnNext(next -> {
//                                    WrTy.ProtocolMessageEnvelope.MessageOneOfCase kind = next.getMessageOneOfCase();
//                                    switch (kind) {
//                                        case HEARTBEAT:
//                                            logger.debug("{} Sending Heartbeat", prefix);
//                                            break;
//                                        case CLIENTHELLO:
//                                            logger.debug("{} Sending Hello", prefix);
//                                            break;
//                                        case INSTANCEINFO:
//                                            logger.debug("{} Sending Data", prefix);
//                                            break;
//                                    }
//                                })
//                                .doOnError(e -> logger.debug("{} Input terminated with an error", prefix, e))
//                                .doOnComplete(() -> logger.debug("{} Input onCompleted", prefix, null))
//                )
//                .doOnSubscribe(value -> logger.debug("{} Subscribed to reply stream", prefix, null))
//                .doOnCancel(() -> logger.debug("{} Unsubscribed from reply stream", prefix, null))
//                .doOnNext(next -> {
//                    switch (next.getMessageOneOfCase()) {
//                        case HEARTBEAT:
//                            logger.debug("{} Received Heartbeat", prefix, null);
//                            break;
//                        case CLIENTHELLO:
//                            logger.debug("{} Received Hello", prefix);
//                            break;
//                        case INSTANCEINFO:
//                            logger.debug("{} Received Data={}", prefix, next.toString());
//                            break;
//                    }
//                })
//                .doOnError(e -> logger.debug("{} Reply stream terminated with an error", prefix, e))
//                .doOnComplete(() -> logger.debug("{} Reply stream onCompleted", prefix));
//    }
//}
