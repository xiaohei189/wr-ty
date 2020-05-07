package com.wr.ty.grpc;

import com.wr.ty.grpc.interest.Interest;
import org.springframework.cloud.client.ServiceInstance;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author xiaohei
 * @date 2020/3/12 1:25
 */
public interface SubscriberClient {

    Flux<ServiceInstance> subscriber(List<Interest> interests);

}
