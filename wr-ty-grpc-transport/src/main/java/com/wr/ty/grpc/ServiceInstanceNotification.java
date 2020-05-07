package com.wr.ty.grpc;

import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.cloud.client.ServiceInstance;

/**
 * @author xiaohei
 * @date 2020/3/15 0:22
 */
public class ServiceInstanceNotification {
    private ServiceInstance instance;
    private NotificationType type;

    public ServiceInstance getInstance() {
        return instance;
    }

    static enum NotificationType {
        ADD, UPDATE, DELETE
    }

}
