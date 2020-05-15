package com.wr.ty.api;

import com.wr.ty.grpc.InterestSubscriber;
import com.wr.ty.grpc.RegistrySubscriber;
import com.wr.ty.grpc.SubscriberFluxSinkWrap;
import com.wr.ty.grpc.handler.server.ServerSubscribeHandler;
import com.xh.demo.grpc.WrTy;
import reactor.core.CoreSubscriber;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * instance registry
 *
 * @author xiaohei
 * @date 2020/3/1 14:28
 */
public interface Registry<T> {

    /**
     * update local registry
     *
     * @param notification
     */
    void update(Notification<T> notification);

    /**
     * 订阅满足指定条件的Registry 信息，当predicate 为null时订阅所有registry信息
     *
     * @param subscriber
     * @param predicate
     * @return Disposable
     */
    Disposable subscribe(CoreSubscriber subscriber, @Nullable Predicate predicate);

    /**
     * 延迟清理registry 中信息
     *
     * @param notification
     */
    void delayClear(Notification<T> notification);

    /**
     * @return
     */
    int observers();

    /**
     * 查询当前registry 状态
     *
     * @return
     */
    Status getStatus();

    /**
     * registry是否已经被关闭
     *
     * @return
     */
    boolean isTerminated();

    /**
     * 关闭当前registry
     *
     * @return
     */
    boolean shutDown();

    enum Status {
        //        启动中
        START,
        //        回复数据中
        RECOVER,
        //        正在运行
        GOING,
        //        结束
        END
    }
}
