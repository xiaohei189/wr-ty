package com.wr.ty.grpc.register;

import com.wr.ty.grpc.RegistrySubscriber;
import com.wr.ty.grpc.SubscriberFluxSinkWrap;
import com.wr.ty.grpc.handler.server.ServerSubscribeHandler;
import com.xh.demo.grpc.WrTy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

/**
 * instance registry
 *
 * @author xiaohei
 * @date 2020/3/1 14:28
 */
public interface Registry {

    /**
     * update local registry
     *
     * @param changeNotification
     */
    void register(WrTy.ChangeNotification changeNotification);

    /**
     * subscribe registry
     * note: ChangeNotification is sequential ,but is possible repetition
     *
     * @param interest
     * @return
     */
    void subscribe(RegistrySubscriber subscriber, WrTy.Interest interest);

    /**
     * @return
     */
    int observers();

    void shutDown();
}
