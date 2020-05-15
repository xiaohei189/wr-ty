//package com.wr.ty.grpc.service;
//
//import com.wr.ty.grpc.InstanceIdPredicate;
//import com.wr.ty.grpc.InterestSubscriber;
//import com.wr.ty.grpc.ServiceInstanceNotification;
//import com.wr.ty.api.Registry;
//import com.wr.ty.grpc.util.SubscribeMessages;
//import com.xh.demo.grpc.ReactorSubscriberServiceGrpc;
//import com.xh.demo.grpc.WrTy;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import reactor.core.publisher.EmitterProcessor;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.SignalType;
//
//import java.util.List;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
//import static com.xh.demo.grpc.WrTy.SubscribeRequest.ItemCase.*;
//
///**
// * @author xiaohei
// * @date 2020/3/14 20:47
// */
//public class SubscriberService extends ReactorSubscriberServiceGrpc.SubscriberServiceImplBase {
//
//    final private Registry registry;
//    private Logger logger = LoggerFactory.getLogger(SubscriberService.class);
//
//    public SubscriberService(Registry registry) {
//        this.registry = registry;
//    }
//
//    @Override
//    public Flux<WrTy.SubscribeResponse> subscriber(Flux<WrTy.SubscribeRequest> request) {
//
//        EmitterProcessor<WrTy.SubscribeResponse> response = EmitterProcessor.create();
////        AtomicReference<Disposable> heartbeatTask = new AtomicReference<>();
////        AtomicReference<ServiceInstance> serviceInstance = new AtomicReference<>();
////        AtomicLong lastHeartbeatTime = new AtomicLong(0);
//        request.doOnNext(value -> {
//            if (value.getItemCase() == CLIENTHELLO) {
//                response.onNext(SubscribeMessages.SERVER_HELLO);
//                logger.debug("response handshake message");
//            }
//        })
//                .doOnNext(value -> {
//                    if (value.getItemCase() == HEARTBEAT) {
////                        if (heartbeatTask.get() == null) {
////                            logger.debug("start heartbeat check");
////                            Disposable disposable = Flux.interval(Duration.ofSeconds(3)).subscribe(t -> {
////                                logger.debug("heartbeat check");
////                                // when heartbeat is timeout,the instance should be added to queue of eviction
////
////                            });
////                            heartbeatTask.set(disposable);
////                        }
////                        lastHeartbeatTime.set(System.currentTimeMillis());
//                        response.onNext(SubscribeMessages.SERVER_HEART);
//                        logger.debug("response server hello message");
//
//                    }
//                })
//                .doOnNext(value -> {
//                    if (value.getItemCase() == SUBSCRIPTIONS) {
//                        List<Predicate<ServiceInstanceNotification>> predicates = value.getSubscriptions().getSubscriptionsList().stream().map(subscription -> {
//                            switch (subscription.getMatchType()) {
//                                case InstanceId:
//                                    Predicate<ServiceInstanceNotification> predicate = new InstanceIdPredicate(subscription.getPattern());
//                                    return predicate;
//                                default:
//                                    throw new RuntimeException("unsupported type  ");
//                            }
//                        }).collect(Collectors.toList());
//
//                        InterestSubscriber interestSubscriber = new InterestSubscriber(predicates);
//                        registry.subscribe(interestSubscriber);
//                        // subscribe
//
//                    }
//                })
//                .materialize().
//                subscribe(value -> {
//                    SignalType type = value.getType();
//                    switch (type) {
//                        case CANCEL:
//                        case ON_COMPLETE:
//                        case ON_ERROR:
//                            // unsubscribe
//                            break;
//                    }
//                }, e -> {
//                    // client's error don't be propagated to here
//                }, () -> {
//                    logger.debug("completed");
//                });
//        return response;
//    }
//}
