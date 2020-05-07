package com.wr.ty.grpc;

import org.springframework.cloud.client.ServiceInstance;

import java.util.function.Predicate;

/**
 * @author xiaohei
 * @date 2020/3/14 20:27
 */
public interface ServiceInstancePredicate extends Predicate<ServiceInstanceNotification> {
}
