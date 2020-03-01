package com.wr.ty.grpc.register;

import com.xh.demo.grpc.WrTy;
import reactor.core.publisher.Flux;

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
    Flux<WrTy.ChangeNotification> subscribe(WrTy.Interest interest);

    /**
     * @return
     */
    int observers();

    void shutDown();
}
