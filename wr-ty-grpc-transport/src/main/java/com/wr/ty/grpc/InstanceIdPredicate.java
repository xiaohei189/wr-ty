package com.wr.ty.grpc;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.util.AntPathMatcher;

/**
 * @author xiaohei
 * @date 2020/3/15 0:05
 */
public class InstanceIdPredicate implements ServiceInstancePredicate {
    final private AntPathMatcher matcher;

    final private String pattern;

    public InstanceIdPredicate(String pattern) {
        this.pattern = pattern;
        this.matcher = new AntPathMatcher();
    }

    @Override
    public boolean test(ServiceInstanceNotification notification) {
        return matcher.match(pattern, notification.getInstance().getInstanceId());
    }
}
