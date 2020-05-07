package com.wr.ty.grpc;

import reactor.core.publisher.BaseSubscriber;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author xiaohei
 * @date 2020/3/14 20:12
 */
public class InterestSubscriber extends BaseSubscriber<ServiceInstanceNotification> {
    private List<Predicate<ServiceInstanceNotification>> predicate;

    public InterestSubscriber(List<Predicate<ServiceInstanceNotification>> predicates) {
        this.predicate = predicates;
    }

    @Override
    protected void hookOnNext(ServiceInstanceNotification value) {

        super.hookOnNext(value);
    }
}
